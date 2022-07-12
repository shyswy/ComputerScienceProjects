#include "types.h"
#include "x86.h"
#include "defs.h"
#include "date.h"
#include "param.h"
#include "memlayout.h"
#include "mmu.h"
#include "proc.h"

int
sys_fork(void)
{
  return fork();
}

int
sys_exit(void)
{
  exit();
  return 0;  // not reached
}

int
sys_wait(void)
{
  return wait();
}

int
sys_kill(void)
{
  int pid;

  if(argint(0, &pid) < 0)
    return -1;
  return kill(pid);
}

int
sys_getpid(void)
{
  return myproc()->pid;
}
int
sys_getppid(void)//by using getpid() i figure out how to make getppid method which brings back process id of parent process
{
	return myproc()->parent->pid;
}

int
sys_yield(void)
{
	yield();
	return 0;
}

int
sys_getlev(void){
	int gt=getlev();
	return gt;
}


int
sys_sbrk(void)
{
  int addr;
  int n;

  if(argint(0, &n) < 0)
    return -1;
  //addr = myproc()->sz;   //project03 
  if( (addr=growproc(n)) <0 )
	  return -1;
  if(growproc(n) < 0)
    return -1;
  return addr;
}

int
sys_sleep(void)
{
  int n;
  uint ticks0;

  if(argint(0, &n) < 0)
    return -1;
  acquire(&tickslock);
  ticks0 = ticks;
  while(ticks - ticks0 < n){
    if(myproc()->killed){
      release(&tickslock);
      return -1;
    }
    sleep(&ticks, &tickslock);
  }
  release(&tickslock);
  return 0;
}

// return how many clock tick interrupts have occurred
// since start.
int
sys_uptime(void)
{
  uint xticks;

  acquire(&tickslock);
  xticks = ticks;
  release(&tickslock);
  return xticks;
}

sys_setpriority(void){
	int in_pid,new_priority;
	if(argint(0,&in_pid)<0||argint(1,&new_priority)<0)
		return -1;
	return setpriority(in_pid,new_priority);
}



int sys_thread_create(void)
{
  int thread, routine, arg;
  

  if(argint(2, &arg) < 0)
    return -1;

  if(argint(0, &thread) < 0)
    return -1; 

  if(argint(1, &routine) < 0)
    return -1; 

  return thread_create((thread_t*)thread, (void*)routine, (void*)arg);
}


int sys_thread_exit(void)
{
  int ret_val;

  if(argint(0, &ret_val) < 0)
    return -1;

  thread_exit((void*)ret_val);
  return 0;
}


int sys_thread_join(void)
{
  int thread, retval;

  if(argint(0, &thread) < 0)
    return -1;

  if(argint(1, &retval) < 0)
    return -1;

  return thread_join((thread_t)thread, (void**)retval);
}

