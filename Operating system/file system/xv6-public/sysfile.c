//
// File-system system calls.
// Mostly argument checking, since we don't trust
// user code, and calls into file.c and fs.c.
//

#include "types.h"
#include "defs.h"
#include "param.h"
#include "stat.h"
#include "mmu.h"
#include "proc.h"
#include "fs.h"
#include "spinlock.h"
#include "sleeplock.h"
#include "file.h"
#include "fcntl.h"

// Fetch the nth word-sized system call argument as a file descriptor
// and return both the descriptor and the corresponding struct file.
static int
argfd(int n, int *pfd, struct file **pf)
{
  int fd;
  struct file *f;

  if(argint(n, &fd) < 0)
    return -1;
  if(fd < 0 || fd >= NOFILE || (f=myproc()->ofile[fd]) == 0)
    return -1;
  if(pfd)
    *pfd = fd;
  if(pf)
    *pf = f;
  return 0;
}

// Allocate a file descriptor for the given file.
// Takes over file reference from caller on success.
static int
fdalloc(struct file *f)
{
  int fd;
  struct proc *curproc = myproc();

  for(fd = 0; fd < NOFILE; fd++){
    if(curproc->ofile[fd] == 0){
      curproc->ofile[fd] = f;
      return fd;
    }
  }
  return -1;
}

int
sys_dup(void)
{
  struct file *f;
  int fd;

  if(argfd(0, 0, &f) < 0)
    return -1;
  if((fd=fdalloc(f)) < 0)
    return -1;
  filedup(f);
  return fd;
}

int
sys_read(void)
{
  struct file *f;
  int n;
  char *p;

  if(argfd(0, 0, &f) < 0 || argint(2, &n) < 0 || argptr(1, &p, n) < 0)
    return -1;
  return fileread(f, p, n);
}

int
sys_write(void)
{
  struct file *f;
  int n;
  char *p;

  if(argfd(0, 0, &f) < 0 || argint(2, &n) < 0 || argptr(1, &p, n) < 0)
    return -1;
  return filewrite(f, p, n);
}

int
sys_close(void)
{
  int fd;
  struct file *f;

  if(argfd(0, &fd, &f) < 0)
    return -1;
  myproc()->ofile[fd] = 0;
  fileclose(f);
  return 0;
}

int
sys_fstat(void)
{
  struct file *f;
  struct stat *st;

  if(argfd(0, 0, &f) < 0 || argptr(1, (void*)&st, sizeof(*st)) < 0)
    return -1;
  return filestat(f, st);
}

// Create the path new as a link to the same inode as old.
int
sys_link(void)
{
  char name[DIRSIZ], *new, *old;
  struct inode *dp, *ip;

  if(argstr(0, &old) < 0 || argstr(1, &new) < 0)
    return -1;

  begin_op();
  if((ip = namei(old)) == 0){
    end_op();
    return -1;
  }

  ilock(ip);
  if(ip->type == T_DIR){
    iunlockput(ip);
    end_op();
    return -1;
  }

  ip->nlink++;
  iupdate(ip);
  iunlock(ip);

  if((dp = nameiparent(new, name)) == 0)
    goto bad;
  ilock(dp);
  if(dp->dev != ip->dev || dirlink(dp, name, ip->inum) < 0){
    iunlockput(dp);
    goto bad;
  }
  iunlockput(dp);
  iput(ip);

  end_op();

  return 0;

bad:
  ilock(ip);
  ip->nlink--;
  iupdate(ip);
  iunlockput(ip);
  end_op();
  return -1;
}

// Is the directory dp empty except for "." and ".." ?
static int
isdirempty(struct inode *dp)
{
  int off;
  struct dirent de;

  for(off=2*sizeof(de); off<dp->size; off+=sizeof(de)){
    if(readi(dp, (char*)&de, off, sizeof(de)) != sizeof(de))
      panic("isdirempty: readi");
    if(de.inum != 0)
      return 0;
  }
  return 1;
}

//PAGEBREAK!
int
sys_unlink(void)
{
  struct inode *ip, *dp;
  struct dirent de;
  char name[DIRSIZ], *path;
  uint off;

  if(argstr(0, &path) < 0)
    return -1;

  begin_op();
  if((dp = nameiparent(path, name)) == 0){
    end_op();
    return -1;
  }

  ilock(dp);
  //project4
  
  int bit=get_modeBit(dp,MODE_WUSR,MODE_WOTH);//check w auth when unlink
  if(bit==0){
     iunlockput(dp);
     end_op();
     return -1;
 }
 
 	 // Cannot unlink "." or "..".
  if(namecmp(name, ".") == 0 || namecmp(name, "..") == 0)
    goto bad;
 

  
  if((ip = dirlookup(dp, name, &off)) == 0)
    goto bad;
  ilock(ip);

  if(ip->nlink < 1)
    panic("unlink: nlink < 1");
  if(ip->type == T_DIR && !isdirempty(ip)){
    iunlockput(ip);
    goto bad;
  }

  memset(&de, 0, sizeof(de));
  writei(dp, (char*)&de, off, sizeof(de));
  if(ip->type == T_DIR){
    dp->nlink--;
    iupdate(dp);
  }
  iunlockput(dp);

  ip->nlink--;
  iupdate(ip);
  iunlockput(ip);

  end_op();

  return 0;

bad:
  iunlockput(dp);
  end_op();
  return -1;
}

static struct inode*
create(char *path, short type, short major, short minor)
{
  struct inode *ip, *dp;
  char name[DIRSIZ];

  if((dp = nameiparent(path, name)) == 0)
    return 0;
  ilock(dp);

  if((ip = dirlookup(dp, name, 0)) != 0){
    iunlockput(dp);
    ilock(ip);
    if(type == T_FILE && ip->type == T_FILE){
      	//project4
		
		int bit=get_modeBit(ip,MODE_WUSR,MODE_WOTH);//write ok?
		if(bit!=1){
			iunlockput(ip);
			return 0;

			
		}
		
		return ip;

	 }
    iunlockput(ip);
    return 0;
  }
  //project4
  
  int bit=get_modeBit(dp,MODE_WUSR,MODE_WOTH);//write ok?
  if(bit!=1){
		iunlockput(dp);
		return 0;

  }
  

  if((ip = ialloc(dp->dev, type)) == 0)
    panic("create: ialloc");

  ilock(ip);
  ip->major = major;
  ip->minor = minor;
  ip->nlink = 1;
  iupdate(ip);

  if(type == T_DIR){  // Create . and .. entries.
    dp->nlink++;  // for ".."
    iupdate(dp);
    // No ip->nlink++ for ".": avoid cyclic ref count.
    if(dirlink(ip, ".", ip->inum) < 0 || dirlink(ip, "..", dp->inum) < 0)
      panic("create dots");
  }

  if(dirlink(dp, name, ip->inum) < 0)
    panic("create: dirlink");

  iunlockput(dp);

  return ip;
}

int
sys_open(void)
{
  char *path;
  int fd, omode;
  struct file *f;
  struct inode *ip;
  //cprintf("openning!!!\n"); 
  if(argstr(0, &path) < 0 || argint(1, &omode) < 0)
    return -1;
  //cprintf("a\n");
  begin_op();

  if(omode & O_CREATE){
    ip = create(path, T_FILE, 0, 0);
    if(ip == 0){
      end_op();
      return -1;
    }
//	cprintf("b\n");
	//project4

	strncpy(ip->owner, get_now(), 16);
	ip->permission=MODE_RUSR|MODE_WUSR|MODE_ROTH;//make with open>> have no execute auth
	iupdate(ip);
  } else {
    if((ip = namei(path)) == 0){
      end_op();
      return -1;
    }
//	cprintf("c\n");
    ilock(ip);
    if(ip->type == T_DIR && omode != O_RDONLY){
      iunlockput(ip);
      end_op();
      return -1;
    }
  }
 // cprintf("d\n");
  int rd=0,wr=0,bit;
  if(omode==O_WRONLY||omode==O_RDWR) wr=1;
  if(omode==O_RDONLY||omode==O_RDWR)rd=1;
// cprintf("wr: %d, rd: %d\n",wr,rd);
  if(wr==1){
	  
	bit=get_modeBit(ip,MODE_WUSR,MODE_WOTH);
	if(bit==0){
		iunlockput(ip);
		end_op();
	//	cprintf("wr!\n");
		return -1;


	}
  }

  if(rd==1){
	  bit=get_modeBit(ip,MODE_RUSR,MODE_ROTH);//ls error!
//	  cprintf("bit: %d\n",bit);
	  if(bit==0){
		iunlockput(ip);
        end_op();
//		cprintf("rd!\n");
		return -1;


	  }

   }
  
  // cprintf("e\n");


  if((f = filealloc()) == 0 || (fd = fdalloc(f)) < 0){
    if(f)
      fileclose(f);
    iunlockput(ip);
    end_op();
    return -1;
  }
  iunlock(ip);
  end_op();
 // cprintf("f\n");

  f->type = FD_INODE;
  f->ip = ip;
  f->off = 0;
  f->readable = !(omode & O_WRONLY);
  f->writable = (omode & O_WRONLY) || (omode & O_RDWR);
  return fd;
}

int
sys_mkdir(void)//project4
{
  char *path;
  struct inode *ip;

  begin_op();
  if(argstr(0, &path) < 0 || (ip = create(path, T_DIR, 0, 0)) == 0){
    end_op();
    return -1;
  }

//project4
  ip->permission = MODE_RUSR|MODE_WUSR|MODE_XUSR|MODE_ROTH|MODE_XOTH;
  strncpy(ip->owner, get_now(), 16);
  iupdate(ip);




  iunlockput(ip);
  end_op();
  return 0;
}

int
sys_mknod(void)
{
  struct inode *ip;
  char *path;
  int major, minor;

  begin_op();
  if((argstr(0, &path)) < 0 ||
     argint(1, &major) < 0 ||
     argint(2, &minor) < 0 ||
     (ip = create(path, T_DEV, major, minor)) == 0){
    end_op();
    return -1;
  }

//project4
   ip->permission = MODE_RUSR|MODE_WUSR|MODE_XUSR|MODE_ROTH|MODE_WOTH|MODE_XOTH;//
  iupdate(ip);//



  iunlockput(ip);
  end_op();
  return 0;
}

int
sys_chdir(void)
{
  char *path;
  struct inode *ip;
  struct proc *curproc = myproc();
  
  begin_op();
  if(argstr(0, &path) < 0 || (ip = namei(path)) == 0){
    end_op();
    return -1;
  }
  ilock(ip);
  if(ip->type != T_DIR){
    iunlockput(ip);
    end_op();
    return -1;
  }

  //project4
  
  int bit=get_modeBit(ip,MODE_XUSR,MODE_XOTH);
  if(bit!=1){
	  iunlockput(ip);
	  end_op();
	  return-1;

	  }

  


  iunlock(ip);
  iput(curproc->cwd);
  end_op();
  curproc->cwd = ip;
  return 0;
}

int
sys_exec(void)
{
  char *path, *argv[MAXARG];
  int i;
  uint uargv, uarg;

  if(argstr(0, &path) < 0 || argint(1, (int*)&uargv) < 0){
    return -1;
  }
  memset(argv, 0, sizeof(argv));
  for(i=0;; i++){
    if(i >= NELEM(argv))
      return -1;
    if(fetchint(uargv+4*i, (int*)&uarg) < 0)
      return -1;
    if(uarg == 0){
      argv[i] = 0;
      break;
    }
    if(fetchstr(uarg, &argv[i]) < 0)
      return -1;
  }
  return exec(path, argv);
}

int
sys_pipe(void)
{
  int *fd;
  struct file *rf, *wf;
  int fd0, fd1;

  if(argptr(0, (void*)&fd, 2*sizeof(fd[0])) < 0)
    return -1;
  if(pipealloc(&rf, &wf) < 0)
    return -1;
  fd0 = -1;
  if((fd0 = fdalloc(rf)) < 0 || (fd1 = fdalloc(wf)) < 0){
    if(fd0 >= 0)
      myproc()->ofile[fd0] = 0;
    fileclose(rf);
    fileclose(wf);
    return -1;
  }
  fd[0] = fd0;
  fd[1] = fd1;
  return 0;
}



int
sys_myopen(void){
  begin_op();
  struct inode* userS;
  struct inode *dp;
  struct dirent de;
  char name[DIRSIZ];

  if((userS = namei("/userS"))==0){
    userS = create("/userS", T_FILE, 0, 0);
    strncpy(userS->owner, "root", 16);
    userS->permission = MODE_RUSR|MODE_WUSR;
    iupdate(userS);
    iunlockput(userS);
  }

  
  if(userS == 0){
    end_op();
    return -1;
  }
 



  iput(userS);
  end_op();
  begin_op();
  userS = namei("/userS");
  ilock(userS);
  //cprintf("chk1!!\n");  
  give_userS(userS);
  myopen();
  iupdate(userS);
  iunlockput(userS);
  end_op();
    begin_op();
    struct inode* ip = create("tmp", T_DIR, 0, 0);
    iupdate(ip);
    iunlockput(ip);
    end_op();

  uint off;
//	cprintf("eee\n");
  begin_op();
  if((dp = nameiparent("tmp", name)) == 0){
    end_op();
    return -1;
  }
  ilock(dp);
//cprintf("111\n");
  // exception
 


  if((ip = dirlookup(dp, name, &off)) == 0){
	  iunlockput(dp);
	  end_op();
    //cprintf("444\n");

  	  return -1;

  }
  ilock(ip);
 // cprintf("ttt\n");
  if(ip->nlink<= 0)
    panic("nlink size is low");
  if(ip->type == T_DIR && !isdirempty(ip)){
    

	 iunlockput(dp);
	 end_op();
    //cprintf("444\n");

  	return -1;

  }
//cprintf("222\n");
  memset(&de, 0, sizeof(de));
  if(writei(dp, (char*)&de, off, sizeof(de)) != sizeof(de))
    panic("unlink writei");
  if(ip->type == T_DIR){
    dp->nlink--;
    iupdate(dp);
	 }
    ip->nlink--;
   iupdate(ip);


  iunlockput(dp);

  ip->nlink--; 
  iupdate(ip);
  iunlockput(ip);

  end_op();
  //cprintf("333\n");
  return 0;

}



int sys_addUser(void){
    char* username;
    char* password;
	struct inode* ip;
	struct inode* userS;
    if (strncmp("root", get_now(), 16)!=0 || argstr(0, &username)<0|| argstr(1, &password)<0){
        return -1;
    }

    begin_op();
	userS=namei("/userS");
    if(userS == 0){
        end_op();
        return -1;
    }
    ilock(userS);
    iunlock(userS);
    end_op();
    begin_op();
    ilock(userS);
	give_userS(userS);
    if(addUser(username, password)!=0){
		cprintf("error: addUser\n");
		iupdate(userS);
	    iunlockput(userS);
		end_op();
		return -1;
	}


    iupdate(userS);
    iunlockput(userS);
    end_op();
     
    begin_op();
	ip=namei(username);
	
	if(ip!=0){
		iput(ip);
	}
	else{
		ip = create(username, T_DIR, 0, 0);
        ip->permission = MODE_RUSR|MODE_WUSR|MODE_XUSR|MODE_ROTH|MODE_XOTH;
        strncpy(ip->owner, username, 16);
        iupdate(ip);
        iunlockput(ip);

	}
	

	
    end_op();
    return 0;
}

int sys_deleteUser(void){
    char* username;
	struct inode* userS;
    if (strncmp("root", get_now(), 16)!=0 || argstr(0, &username)<0){
        return -1;
    }
    begin_op();
    userS = namei("/userS");
    end_op();
    begin_op();
    ilock(userS);
	give_userS(userS);
	if(deleteUser(username)!=0){
		cprintf("error: deleteuser\n");

		iupdate(userS);
	    iunlock(userS);
   		end_op();
		return -1;

	}
    iupdate(userS);
    iunlock(userS);
    end_op();
    return 0;
}


int sys_login(void){
	char* pwd;
    char* id;
    if(argstr(0, &id)<0||argstr(1, &pwd)<0)return -1;
    
    return login(id, pwd);
}

int sys_logout(void){
    return logout();
}




int sys_chmod(void){
    char* pathname;
    int mode;
	struct inode* ip;

    if(argstr(0, &pathname)<0||argint(1, &mode)<0)return -1;
    

    begin_op();
    if((ip=namei(pathname)) == 0){
        end_op();
        return -1;
    }
	   ilock(ip);

	if(strncmp(ip->owner, get_now(), 16)!=0&&strncmp("root", get_now(), 16)!=0){
		cprintf("error: only owner and root can do chmod\n");


		iunlockput(ip);
		
		end_op();
		return -1;
	}



   if( (strncmp(ip->owner, get_now(), 16)!=0) && (strncmp("root", get_now(), 16)!=0) ){
        iupdate(ip);//only owner and root  can dp chmod!
   	 	iunlockput(ip);
	    end_op();
		return -1;
    }
    ip->permission=mode;
    iupdate(ip);
    iunlockput(ip);
    end_op();
    return 0;
}


