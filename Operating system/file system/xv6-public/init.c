// init: The initial user-level program

#include "types.h"
#include "stat.h"
#include "user.h"
#include "fcntl.h"

//#include"chk.h"


char *argv[] = { "sh", 0 };

int
main(void)
{
 // *initial=0;
  int pid, wpid;

  if(open("console", O_RDWR) < 0){
    mknod("console", 1, 1);
    open("console", O_RDWR);
  }
  dup(0);  // stdout
  dup(0);  // stderr

  myopen();
  ///printf(1,"aaaaaa\n"); 
 // *initial=1;
  for(;;){
    printf(1, "login:" );
    pid = fork();
    if(pid < 0){
      printf(1, "init: fork failed\n");
      exit();
    }
    if(pid == 0){
      exec("login", argv);//open login instead of sh
      printf(1, "init: exec login failed\n");
      exit();
    }
    while((wpid=wait()) >= 0 && wpid != pid)
      printf(1, "zombie!\n");
  }
}
