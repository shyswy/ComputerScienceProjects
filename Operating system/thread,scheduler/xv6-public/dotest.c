#include "types.h"
#include "stat.h"
#include "user.h"

int main(int argc, char* argv[]){
	char*buf="check";
	int ret_val=testfunc(buf);
	printf(1,"get value: 0x%x\n",ret_val);
	exit();
	
};
