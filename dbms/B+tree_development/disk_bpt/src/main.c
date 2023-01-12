#include "bpt.h"
#define I_MAX 100000;
#define D_MAX 100000;
#include<math.h>
#include <stdlib.h>
int main(){
    int64_t input;
    char instruction;
    char buf[120];
    char *result;
    open_table("test.db");
    
    
    
    for(int j=0;j<10;j++){
	    for(int i=0;i<10000;i++){
	    	
	    	buf[0]='1';
	    	db_insert(i, buf);
	    
	    }
	    for(int i=0;i<10000;i++){
	    	if(i%2==0)
	    		db_delete(i);
	    }
    }
    printf("end\n");
     return EXIT_SUCCESS;
     printf("\n");
    
    /*
    
    while(scanf("%c", &instruction) != EOF){
        switch(instruction){
            case 'i':
                scanf("%ld %s", &input, buf);
                db_insert(input, buf);
                break;
            case 'f':
                scanf("%ld", &input);
                result = db_find(input);
                if (result) {
                    printf("Key: %ld, Value: %s\n", input, result);
                }
                else
                    printf("Not Exists\n");

                fflush(stdout);
                break;
            case 'd':
                scanf("%ld", &input);
                db_delete(input);
                break;
            case 'q':
                while (getchar() != (int)'\n');
                return EXIT_SUCCESS;
                break;   

        }
        while (getchar() != (int)'\n');
    }
    printf("\n");
    */
    return 0;
}



