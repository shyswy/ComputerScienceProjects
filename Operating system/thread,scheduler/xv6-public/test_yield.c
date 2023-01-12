#include "types.h"
#include "stat.h"
#include "user.h"
 
 int
 main(int argc,char* argv[]){
    while(1){
	int pid=fork();
	if(pid<0){
		printf(1,"failed\n");
		exit();
	}
	else if( pid==0){
		printf(1,"child\n");
	}
	else{
		printf(1,"parent\n");
	}
	yield();
	}

exit();
 }

