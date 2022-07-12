// Shell.

#include "types.h"
#include "user.h"
#include "fcntl.h"
#include "fs.h"
#include "syscall.h"

void
panic(char* s){//get form sh.c 
    printf(2, "%s\n", s); 
    exit();
}

int
fork1(void)//get from sh.c
{
    int pid;
    pid = fork();
    if(pid == -1) 
        panic("fork");
    return pid;
}



int main(void){
    static char id[30];
    static char password[30];
    char* argv[] = {"sh", 0};

 while(1){
	 	memset(id, 0, sizeof(id));
		memset(password, 0, sizeof(password));

        printf(2, "User: ");
        gets(id, sizeof(id));
        id[strlen(id)-1] = '\0';
        printf(2, "password: ");
        gets(password, sizeof(password));
        password[strlen(password)-1] = '\0';

        if(login(id, password)==0){
            if(fork1() == 0){
			
                exec("sh", argv);
            }
            wait();
        }else{
            printf(2, "wrong information!\n");
        }
    }



  exit();
}

