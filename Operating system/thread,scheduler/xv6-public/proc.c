#include "types.h"
#include "defs.h"
#include "param.h"
#include "memlayout.h"
#include "mmu.h"
#include "x86.h"
#include "proc.h"
#include "spinlock.h"

struct {
  struct spinlock lock;
  struct proc proc[NPROC];
} ptable;

static struct proc *initproc;


extern void forkret(void);
extern void trapret(void);


static void wakeup1(void *chan);

int nexttid=1;

int nextpid = 1;

void
pinit(void)
{
  initlock(&ptable.lock, "ptable");
}

// Must be called with interrupts disabled
int
cpuid() {
  return mycpu()-cpus;
}

// Must be called with interrupts disabled to avoid the caller being
// rescheduled between reading lapicid and running through the loop.
struct cpu*
mycpu(void)
{
  int apicid, i;
  
  if(readeflags()&FL_IF)
    panic("mycpu called with interrupts enabled\n");
  
  apicid = lapicid();
  // APIC IDs are not guaranteed to be contiguous. Maybe we should have
  // a reverse map, or reserve a register to store &cpus[i].
  for (i = 0; i < ncpu; ++i) {
    if (cpus[i].apicid == apicid)
      return &cpus[i];
  }
  panic("unknown apicid\n");
}

// Disable interrupts so that we are not rescheduled
// while reading proc from the cpu structure
struct proc*
myproc(void) {
  struct cpu *c;
  struct proc *p;
  pushcli();
  c = mycpu();
  p = c->proc;
  popcli();
  return p;
}

//PAGEBREAK: 32
// Look in the process table for an UNUSED proc.
// If found, change state to EMBRYO and initialize
// state required to run in the kernel.
// Otherwise return 0.
static struct proc*
allocproc(void)
{
  struct proc *p;
  char *sp;
 //cprintf("aa\n");

  acquire(&ptable.lock);//on lock
  //cprintf("cc\n");
  for(p = ptable.proc; p < &ptable.proc[NPROC]; p++)//check porc[] have space for new process
    if(p->state == UNUSED)//if find not used one
      goto found;//move to found
 // cpirntf("dd\n");
  release(&ptable.lock);//off lock

  //cprintf("bb\n");
  return 0;

found:
  //cprintf("rr\n");
  p->state = EMBRYO;
  p->pid = nextpid++;
  p->last_out=0;
  p->out=0;
  p->ticks=0;
  p->level=0;
  setpriority(p->pid,0);
  release(&ptable.lock);
  
  // Allocate kernel stack.
  if((p->kstack = kalloc()) == 0){
    p->state = UNUSED;
    return 0;
  }
  sp = p->kstack + KSTACKSIZE;

  // Leave room for trap frame.
  sp -= sizeof *p->tf;
  p->tf = (struct trapframe*)sp;

  // Set up new context to start executing at forkret,
  // which returns to trapret.
  sp -= 4;
  *(uint*)sp = (uint)trapret;

  sp -= sizeof *p->context;
  p->context = (struct context*)sp;
  memset(p->context, 0, sizeof *p->context);
  p->context->eip = (uint)forkret;

  //project03
  
  p->master=0;// shyswy
  p->tid=0;

  // Init data of stride & mlfq
  
  
  memset(&p->empty_mem, 0, sizeof p->empty_mem);


  //cprintf("tt\n");
  return p;
}

//PAGEBREAK: 32
// Set up first user process.
void

userinit(void)
{
  struct proc *p;
  extern char _binary_initcode_start[], _binary_initcode_size[];
  p = allocproc();
  initproc = p;
  if((p->pgdir = setupkvm()) == 0)
    panic("userinit: out of memory?");
  inituvm(p->pgdir, _binary_initcode_start, (int)_binary_initcode_size);
  p->sz = PGSIZE;
  memset(p->tf, 0, sizeof(*p->tf));
  p->tf->cs = (SEG_UCODE << 3) | DPL_USER;
  p->tf->ds = (SEG_UDATA << 3) | DPL_USER;
  p->tf->es = p->tf->ds;
  p->tf->ss = p->tf->ds;
  p->tf->eflags = FL_IF;
  p->tf->esp = PGSIZE;
  p->tf->eip = 0;  // beginning of initcode.S

  safestrcpy(p->name, "initcode", sizeof(p->name));
  p->cwd = namei("/");

  // this assignment to p->state lets other cores
  // run this process. the acquire forces the above
  // writes to be visible, and the lock is also needed
  // because the assignment might not be atomic.
  acquire(&ptable.lock);

  p->state = RUNNABLE;

  release(&ptable.lock);
}

// Grow current process's memory by n bytes.
// Return 0 on success, -1 on failure.


//project03
int
growproc(int n)
{
  uint prev_sz,sz;
  struct proc *curproc = myproc();
  struct proc *p;

  acquire(&ptable.lock);

  // if curporc==thread size up master

  if(!curproc->master)
	  p=curproc;
  else
	  p=curproc->master;
 

  sz = p->sz;
  prev_sz = sz;
  if(n > 0){
    if((sz = allocuvm(curproc->pgdir, sz, sz + n)) == 0)
      goto bad;
  } else if(n < 0){
    if((sz = deallocuvm(curproc->pgdir, sz, sz + n)) == 0)
      goto bad;
  }
  p->sz = sz;
  release(&ptable.lock);

  switchuvm(curproc);
  return prev_sz;

bad:
  release(&ptable.lock);
  return -1;
}



/*
int
growproc(int n)
{
  uint sz;
  struct proc *curproc = myproc();

  sz = curproc->sz;
  if(n > 0){
    if((sz = allocuvm(curproc->pgdir, sz, sz + n)) == 0)
      return -1;
  } else if(n < 0){
    if((sz = deallocuvm(curproc->pgdir, sz, sz + n)) == 0)
      return -1;
  }
  curproc->sz = sz;
  cprintf("3\n");
  switchuvm(curproc);
  return 0;
}
*/




// Create a new process copying p as the parent.
// Sets up stack to return as if from system call.
// Caller must set state of returned proc to RUNNABLE.
/*
int
fork(void)
{
  int i, pid;
  struct proc *np;
  struct proc *curproc = myproc();

  // Allocate process.
  if((np = allocproc()) == 0){
    return -1;
  }

  // Copy process state from proc.
  if((np->pgdir = copyuvm(curproc->pgdir, curproc->sz)) == 0){
    kfree(np->kstack);
    np->kstack = 0;
    np->state = UNUSED;
    return -1;
  }
  np->sz = curproc->sz;
  np->parent = curproc;
  *np->tf = *curproc->tf;

  // Clear %eax so that fork returns 0 in the child.
  np->tf->eax = 0;

  for(i = 0; i < NOFILE; i++)
    if(curproc->ofile[i])
      np->ofile[i] = filedup(curproc->ofile[i]);
  np->cwd = idup(curproc->cwd);

  safestrcpy(np->name, curproc->name, sizeof(curproc->name));

  pid = np->pid;

  acquire(&ptable.lock);

  np->state = RUNNABLE;

  release(&ptable.lock);

  return pid;
}
*/


int
fork(void)
{
  int i, pid; 
  struct proc *np; 
  struct proc *curproc = myproc();

  // Allocate process.
  if((np = allocproc()) == 0){
    return -1;
  }
  
  // master control memory
  // use master's sz on slave's thread
  if(curproc->tid > 0){
    np->pgdir = copyuvm(curproc->pgdir, curproc->master->sz);
  }else{
    np->pgdir = copyuvm(curproc->pgdir, curproc->sz);
  }

  // debug
  if(np->pgdir == 0){
    kfree(np->kstack); 
    np->state = UNUSED;
	np->kstack=0;
    return -1;
  }
  np->sz = curproc->sz;
  *np->tf = *curproc->tf;
  np->parent = curproc;
  

  
  np->tf->eax = 0; 

  for(i = 0; i < NOFILE; i++) 
    if(curproc->ofile[i])
      np->ofile[i] = filedup(curproc->ofile[i]);
  np->cwd = idup(curproc->cwd);

  safestrcpy(np->name, curproc->name, sizeof(curproc->name));

  pid = np->pid;

  acquire(&ptable.lock);

  np->state = RUNNABLE;

  release(&ptable.lock);
  return pid; 
}


/*
// Exit the current process.  Does not return.
// An exited process remains in the zombie state
// until its parent calls wait() to find out it exited.
void
exit(void)
{
  struct proc *curproc = myproc();
  struct proc *p;
  int fd;

  if(curproc == initproc)
    panic("init exiting");

  // Close all open files.
  for(fd = 0; fd < NOFILE; fd++){
    if(curproc->ofile[fd]){
      fileclose(curproc->ofile[fd]);
      curproc->ofile[fd] = 0;

    }
  }

  begin_op();
  iput(curproc->cwd);
  end_op();
  curproc->cwd = 0;

  acquire(&ptable.lock);

  // Parent might be sleeping in wait().
  wakeup1(curproc->parent);

  // Pass abandoned children to init.
  for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
    if(p->parent == curproc){
      p->parent = initproc;
      if(p->state == ZOMBIE)
        wakeup1(initproc);
    }
  }

  // Jump into the scheduler, never to return.
  curproc->state = ZOMBIE;
  sched();
  panic("zombie exit");
}

// Wait for a child process to exit and return its pid.
// Return -1 if this process has no children.

*/





void
exit(void)
{

  struct proc *p;

  struct proc *curproc = myproc();
  
  int fd, thread_exist;

  if(curproc == initproc)
    panic("init exiting");

  // If curproc ==  master process and there are  alive thread, clean all lower thread
  
  if(curproc->tid == 0){
    acquire(&ptable.lock);

    for(;;){
      thread_exist = 0;
      for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
        if(p->master == curproc){
          // If lower  thread is already zombie, clean-up it
          // Else, kill lower thread and wait for it
          if(p->state == ZOMBIE){
             kfree(p->kstack);
	        p->kstack = 0;
    	    p->master->empty_mem.data[p->master->empty_mem.size++] = p->vir_add;
       		p->killed = 0;
        	p->state = UNUSED;
        	p->pid = 0;
        	p->parent = 0;
        	p->master = 0;
        	p->name[0] = 0;


          }else{
            thread_exist=1;
            p->killed = 1;
            wakeup1(p);
          }
        }
      }
      if(!thread_exist ){
        release(&ptable.lock);
        break;
      }
      // Wait for lower thread to exit.  
      sleep(curproc, &ptable.lock);
    }
  }

  for(fd = 0; fd < NOFILE; fd++){
    if(curproc->ofile[fd]){
      fileclose(curproc->ofile[fd]);
      curproc->ofile[fd] = 0;
    }
  }
  begin_op();
  iput(curproc->cwd);
  end_op();
  curproc->cwd = 0;

  acquire(&ptable.lock);

  if(curproc->tid == 0){
    // Parent might be sleeping in wait().
    wakeup1(curproc->parent);

   

  }else{
    // If master is alive
    if(curproc->master != 0){
      curproc->master->killed = 1;
      wakeup1(curproc->master);
    }
  }

  // Pass abandoned children to init.
    for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
      if(p->parent == curproc){
        p->parent = initproc;
        if(p->state == ZOMBIE)
          wakeup1(initproc);
      }
    }

  
  curproc->state = ZOMBIE;
  sched();
  panic("zombie exit");
}


int
wait(void)
{
  struct proc *p;
  int havekids, pid;
  struct proc *curproc = myproc();
  
  acquire(&ptable.lock);
  for(;;){
    havekids = 0;
    for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
      if(p->parent != curproc)
        continue;
      havekids = 1;
      if(p->state == ZOMBIE){
      
        pid = p->pid;
        kfree(p->kstack);
        p->kstack = 0;
        freevm(p->pgdir);
        p->pid = 0;
        p->parent = 0;
        p->name[0] = 0;
        p->killed = 0;
        p->state = UNUSED;
        release(&ptable.lock);
        return pid;
      }
    }

    // No point waiting if we don't have any children.
    if(!havekids || curproc->killed){
      release(&ptable.lock);
      return -1;
    }

    // Wait for children to exit.  (See wakeup1 call in proc_exit.)
    sleep(curproc, &ptable.lock);  //DOC: wait-sleep
  }
}






//set_priority syscall ass1_195423
int setpriority(int in_pid, int new_priority)
{

	//prio range error!
  if(new_priority<0||10<new_priority)
	  return -2;
 
  
  //scan process table and find process with 
  struct proc *p;
  for (p = ptable.proc; p < &ptable.proc[NPROC]; p++)
  {
    if (p->pid == in_pid)// find process
    {
      
		//cprintf("%d",p->ppid);
		if(myproc()&&( (p->parent)->pid )==myproc()->pid){//if calle is caller's children, it work
			//acquire(&ptable.lock);
		//if(myproc()){
			p->priority=new_priority;
			//release(&ptable.lock);
			return 0;

		}
		
    }
  }
 

  return -1;// error
 



}

int getlev(void){
	int gt=myproc()->level;
	return gt;
}



//add end




//PAGEBREAK: 42
// Per-CPU process scheduler.
// Each CPU calls scheduler() after setting itself up.
// Scheduler never returns.  It loops, doing:
//  - choose a process to run
//  - swtch to start running that process
//  - eventually that process transfers control
//      via swtch back to the scheduler.

//add
struct proc* arr[81];
int front=0,rear=0;





int in_qu(struct proc* a){
	for(int i=front;i<rear;i++){
		if(arr[i]->pid==a->pid)
			return 1;
	}
	return 0;
}

void push(struct proc* a){
	if(in_qu(a)==0){//not in qu
		arr[(rear)%80]=a;
		rear=(rear+1)%80;
	}
	
}

void pop(){
	if(front==rear)
		return;
	else{
	arr[front]=0;
	front=(front+1)%80;
	}
	
}


struct proc*  get(){
	if(front==rear)
		return 0;
	return arr[front];

}

int emp(){
	if(front==rear)
		return 1;
	else
		return 0;

}
int cchhkk=0;
void scheduler(void)
{
  struct proc *p=0;//add 0 for unused error ass1_195423
	


  
  struct proc *now=0;
  struct proc* prev=0;

  struct proc *p2=0;
  struct cpu *c = mycpu();
  c->proc = 0;
  
 
  for (;;)
  {
    // Enable interrupts on this processor.
    sti();
	//acquire(&ptable.lock);
	
#ifdef MULTILEVEL_SCHED 
    // Loop over process table looking for process to run.
    acquire(&ptable.lock);
  	 
 	int arr[100];		
    struct proc *tmp_process = 0;// it will contain process go to cpu
	struct proc *min_odd=0;     //it will save minimum odd num in case no even(RR) exist
    
	
	int even_exist=0;
	
	
	 for (p2 = ptable.proc; p2 < &ptable.proc[NPROC]; p2++){ 
             if( (p2->pid!=2) && (p2->pid)%2==0&&p2->state==RUNNABLE ){
                 even_exist=1;
 
             }
            
     }
	//cprintf("as: %d/",even_exist);
   	
  

    for (p = ptable.proc; p < &ptable.proc[NPROC]; p++)//hear, find process with  minimum pid
    {
		//cause it mean faster in, which means  come in first
      
		
	  if (p->state != RUNNABLE)continue; 
	  if( (p->pid)==1||p->pid==2){//exception. shell and init is essential
		  tmp_process=p;
		  
	  }	  
		//}
	  else if( even_exist==1){//even process in so even must in
			if( (p->pid)%2==0){
				tmp_process=p;
			}
			else {
				
					push(p);
					continue;
			}
		}
	  	else if (even_exist==0){
			if(p->pid%2==1){
					push(p);
			}
			continue;
		} 					
	  
		c->proc =tmp_process;//put process in cpu
        switchuvm(tmp_process);//context switch
        tmp_process->state = RUNNING;//make state running
        swtch(&(c->scheduler), tmp_process->context);
        switchkvm();
		//if(cchhkk==1)	cprintf("edd\n");	
  
        c->proc=0;
		//cprintf("ed\n");

	  
	}




	
  	if(even_exist==0&&emp()==0&&get()!=0){
         struct proc * nxt_odd=0;
     	nxt_odd=get();
		
        if(nxt_odd->state!=RUNNABLE){
			pop();
		}
		else{
         c->proc = nxt_odd;//put process in cpu
         switchuvm(nxt_odd);//context switch
         nxt_odd->state = RUNNING;//make state running
         swtch(&(c->scheduler), nxt_odd->context);
         switchkvm();
		}

		
		/*
		if(now!=0&&(now->state==RUNNABLE)){//if prev run odd process still run, do not alloc
             //must give cpu when yeild!! 
             //cprintf("%d first!\n",now->pid);
             now=min_odd;
 
            }
*/

		
        //cprintf("EADF");
	}

	

	
		
	release(&ptable.lock);
    	


	
    
#endif


	

#ifdef MLFQ_SCHED
	acquire(&ptable.lock);
    // Multilevel feedback queue scheduling
    //See which processes expire their time 

struct proc *tmp=0;//tmp contain process with highest level(low num) queue with highest priority
     int chk=0,mn_qu=99,mx_prio=-1; //minimum num queue == highest priority so find min to get highest prio queu
	
	 if(prev!=0){
		tmp=prev;
	 }	
  else{
     for (p = ptable.proc; p < &ptable.proc[NPROC]; p++){
        // if(p->last_out==1)  >> trouble!
		//	 p->state=SLEEPING;

		 if( (p->state) != RUNNABLE)continue;
		
	    	
		if((p->pid)==1){
			tmp=p;
			
		}

		else if(!tmp){//initialize
			tmp=p;
		 }

		else if (p->last_out==1)continue;// if it happen, means it run out all quantum at last queue

		 else if( (p->level) ==tmp->level){// find process with same level queue
			 if(p->priority>tmp->priority){// find process with highest prio same high level queue. 
				 tmp=p;
			 }
				    
          }
		  else if( (p->level) <tmp->level){// find process with high level queue
                  tmp=p;
           }               
        }

	 }
	   
 		if (tmp&&tmp->state==RUNNABLE){//double check
	 	int prev_lev=tmp->level;
		c->proc = tmp;
        switchuvm(tmp);
        tmp->state = RUNNING;
        swtch(&(c->scheduler), tmp->context);
  	
        switchkvm();
	    c->proc = 0;
		
		if(prev_lev!=tmp->level||tmp->last_out==1||tmp->state!=RUNNABLE){
			
			prev=0;
		}
		else{
			prev=tmp;

		}

		}
         
    release(&ptable.lock);

#endif


#ifdef DEFAULT
    // Loop over process table looking for process to run.
    acquire(&ptable.lock);
    for (p = ptable.proc; p < &ptable.proc[NPROC]; p++)
    {
      if (p->state != RUNNABLE)
        continue;

      // Switch to chosen process.  It is the process's job
      // to release ptable.lock and then reacquire it
      // before jumping back to us.
      c->proc = p;
      switchuvm(p);
      p->state = RUNNING;

      swtch(&(c->scheduler), p->context);
      switchkvm();

      // Process is done running for now.
	  
      // It should have changed its p->state before coming back.
      c->proc = 0;
    }
    release(&ptable.lock);

#endif
	}
}
//add end


/*
	void
scheduler(void)
{
  struct proc *p;
  struct cpu *c = mycpu();
  c->proc = 0;
  
  for(;;){
    // Enable interrupts on this processor.
    sti();

    // Loop over process table looking for process to run.
    acquire(&ptable.lock);
    for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
      if(p->state != RUNNABLE)
        continue;

      // Switch to chosen process.  It is the process's job
      // to release ptable.lock and then reacquire it
      // before jumping back to us.
      c->proc = p;
      switchuvm(p);
      p->state = RUNNING;

      swtch(&(c->scheduler), p->context);
      switchkvm();

      // Process is done running for now.
      // It should have changed its p->state before coming back.
      c->proc = 0;
    }
    release(&ptable.lock);

  }
}
*/
// Enter scheduler.  Must hold only ptable.lock
// and have changed proc->state. Saves and restores
// intena because intena is a property of this
// kernel thread, not this CPU. It should
// be proc->intena and proc->ncli, but that would
// break in the few places where a lock is held but
// there's no process.
void
sched(void)
{
  int intena;
  struct proc *p = myproc();

  if(!holding(&ptable.lock))
    panic("sched ptable.lock");
  if(mycpu()->ncli != 1)
    panic("sched locks");
  if(p->state == RUNNING)
    panic("sched running");
  if(readeflags()&FL_IF)
    panic("sched interruptible");
  intena = mycpu()->intena;
  swtch(&p->context, mycpu()->scheduler);
  mycpu()->intena = intena;
}

// Give up the CPU for one scheduling round.
void
yield(void)
{
	//cprintf("yield!!!\n");
  acquire(&ptable.lock);  //DOC: yieldlock
  myproc()->state = RUNNABLE;
  //myproc()->ticks=0;//add
  //myproc()->level=0;//add
  sched();
  release(&ptable.lock);
}

// A fork child's very first scheduling by scheduler()
// will swtch here.  "Return" to user space.
void
forkret(void)
{
  static int first = 1;
  // Still holding ptable.lock from scheduler.
  release(&ptable.lock);

  if (first) {
    // Some initialization functions must be run in the context
    // of a regular process (e.g., they call sleep), and thus cannot
    // be run from main().
    first = 0;
    iinit(ROOTDEV);
    initlog(ROOTDEV);
  }

  // Return to "caller", actually trapret (see allocproc).
}

// Atomically release lock and sleep on chan.
// Reacquires lock when awakened.
void
sleep(void *chan, struct spinlock *lk)
{
  struct proc *p = myproc();
  
  if(p == 0)
    panic("sleep");

  if(lk == 0)
    panic("sleep without lk");

  // Must acquire ptable.lock in order to
  // change p->state and then call sched.
  // Once we hold ptable.lock, we can be
  // guaranteed that we won't miss any wakeup
  // (wakeup runs with ptable.lock locked),
  // so it's okay to release lk.
  if(lk != &ptable.lock){  //DOC: sleeplock0
    acquire(&ptable.lock);  //DOC: sleeplock1
    release(lk);
  }
  // Go to sleep.
  p->chan = chan;
  p->state = SLEEPING;

  sched();

  // Tidy up.
  p->chan = 0;

  // Reacquire original lock.
  if(lk != &ptable.lock){  //DOC: sleeplock2
    release(&ptable.lock);
    acquire(lk);
  }
}

//PAGEBREAK!
// Wake up all processes sleeping on chan.
// The ptable lock must be held.
static void
wakeup1(void *chan)
{
  struct proc *p;

  for(p = ptable.proc; p < &ptable.proc[NPROC]; p++)
    if(p->state == SLEEPING && p->chan == chan)
      p->state = RUNNABLE;
}

// Wake up all processes sleeping on chan.
void
wakeup(void *chan)
{
  acquire(&ptable.lock);
  wakeup1(chan);
  release(&ptable.lock);
}

// Kill the process with the given pid.
// Process won't exit until it returns
// to user space (see trap in trap.c).
int
kill(int pid)
{
  struct proc *p;

  acquire(&ptable.lock);
  for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
    if(p->pid == pid){
      p->killed = 1;
      // Wake process from sleep if necessary.
      if(p->state == SLEEPING)
        p->state = RUNNABLE;
      release(&ptable.lock);
      return 0;
    }
  }
  release(&ptable.lock);
  return -1;
}

//PAGEBREAK: 36
// Print a process listing to console.  For debugging.
// Runs when user types ^P on console.
// No lock to avoid wedging a stuck machine further.
void
procdump(void)
{
  static char *states[] = {
  [UNUSED]    "unused",
  [EMBRYO]    "embryo",
  [SLEEPING]  "sleep ",
  [RUNNABLE]  "runble",
  [RUNNING]   "run   ",
  [ZOMBIE]    "zombie"
  };
  int i;
  struct proc *p;
  char *state;
  uint pc[10];

  for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
    if(p->state == UNUSED)
      continue;
    if(p->state >= 0 && p->state < NELEM(states) && states[p->state])
      state = states[p->state];
    else
      state = "???";
    cprintf("%d %s %s", p->pid, state, p->name);
    if(p->state == SLEEPING){
      getcallerpcs((uint*)p->context->ebp+2, pc);
      for(i=0; i<10 && pc[i] != 0; i++)
        cprintf(" %p", pc[i]);
    }
    cprintf("\n");
  }
}








// Create thread with process
int
thread_create(thread_t* thread, void* (*start_routine)(void *), void* arg)
{
  pde_t *pgdir;
	
  int i;
  uint sz, sp, vir_add;


  struct proc *curproc = myproc();
  struct proc *np;
  
 
 struct proc *master;
 if(!curproc->master)
	 master=curproc;
 else
	 master=curproc->master;
	 


  // alloc process.
  if((np = allocproc()) == 0){
     return -1;
 }

  

  // set
  np->tid = nexttid++;
  np->master = master;

  np->pid = master->pid;

  acquire(&ptable.lock);
  pgdir = master->pgdir;

 
  // if no empty memeory, grow memory and put new thread located at the top of mem
  
  if(!(master->empty_mem.size)){
    vir_add = master->sz;
    master->sz += 2*PGSIZE;
  }


  else{//get form top of stack(pop)
    vir_add = master->empty_mem.data[--master->empty_mem.size]; 

  }

  // current thread get two page space
  
  sz = allocuvm(pgdir, vir_add, vir_add + 2*PGSIZE);
  release(&ptable.lock);
  
  *np->tf = *master->tf;


  for(i = 0; i < NOFILE; i++)
    if(master->ofile[i])
      np->ofile[i] = filedup(master->ofile[i]);
  np->cwd = idup(master->cwd);

  safestrcpy(np->name, master->name, sizeof(master->name));
  


  
  sp = sz-4;//space for arg
  *((uint*)sp) = (uint)arg; // argument
  sp -= 4;//space for pc. but don't need to put data 
 
  np->sz = sz;
  np->tf->eip = (uint)start_routine; // entry point of this thread
  np->tf->esp = sp; // set stack pointer

  np->pgdir = pgdir;
  np->vir_add = vir_add;
  

  // return tid by arg
  *thread = np->tid;

  acquire(&ptable.lock);

  np->state = RUNNABLE;

  release(&ptable.lock);

  return 0;
}



void
thread_exit(void* ret_val)
{
  struct proc *curproc = myproc();
  int fd;

  // close every  opened file
  for(fd = 0; fd < NOFILE; fd++){
    if(!(curproc->ofile[fd]))continue;
      fileclose(curproc->ofile[fd]);
      curproc->ofile[fd] = 0;
    
  }

  begin_op();
  iput(curproc->cwd);
  end_op();
  curproc->cwd = 0;

  acquire(&ptable.lock);

  // Save return value
  curproc->tmp_retval = ret_val;

  // parent >> masster maybe sleep, so wake
  wakeup1(curproc->master);

  
  curproc->state = ZOMBIE;
  sched();
  panic("zombie exit");
}



thread_join(thread_t thread, void** retval)
{
  struct proc *p;
  struct proc *curproc = myproc();

  // exception. (lower thread cannot join thread.
  if(curproc->master != 0)return -1;
 
  acquire(&ptable.lock);
  for(;;){
    //master  clean zombie thread 
    for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
      if(p->tid == thread&&p->state==ZOMBIE){
     
        *retval = p->tmp_retval;
        //clear thread
		kfree(p->kstack);
 		p->kstack = 0; 
 	    p->master->empty_mem.data[p->master->empty_mem.size++] = p->vir_add;
 	    p->killed = 0; 
 	 	p->state = UNUSED;
 	 	p->pid = 0; 
 	 	p->parent = 0; 
	 	p->master = 0; 
 		p->name[0] = 0; 
 

  		// dellocate  memory of thread
	    deallocuvm(p->pgdir, p->sz, p->vir_add);
        release(&ptable.lock);
        return 0;
      
	  }
    }

    if(curproc->killed){
      release(&ptable.lock);
      return -1;
    }
    // waiting for lower thread to be terminate
    sleep(curproc, &ptable.lock);  
  }
}





// kill except  process from except arg
void
thread_kill(struct proc* pp)
{

  struct proc *p;
  int cur_pid=pp->pid;
  acquire(&ptable.lock);
 
 if(!myproc()->killed){

  for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
	if(p==pp)continue;

    if (cur_pid==p->pid  ){
      p->state = SLEEPING;
	  p->killed = 1;
      p->chan = 0;
	 
      
    }
  }
 
 }
 release(&ptable.lock);

}


// find proecess with  pid, and wake up
// except for one process. They might call exit() this time

void
thread_wakeup(struct proc* pp)
{
  struct proc *p;
  int isemp;
  int cur_pid=pp->pid;
  acquire(&ptable.lock);
  isemp=1;

  for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
	if(p->state!=SLEEPING)continue;
    if (p->pid == cur_pid && p != pp){
		// except process
      if(p->parent){
        p->parent = pp;
        isemp=0;
      }
      p->state = RUNNABLE;

   
    }
  }
  release(&ptable.lock);

  if(!isemp)// if not empty, wait.
    wait();
}

