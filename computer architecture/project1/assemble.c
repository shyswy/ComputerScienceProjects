/* Assembler code fragment for LC-2K */
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#define MAXLINELENGTH 1000
#define MAXLABEL 987654

// R-type                      
#define ADD             0x00000000
#define NOR             0x00400000

// I-type
#define LW              0x00800000
#define SW              0x00C00000
#define BEQ             0x01000000

// J-type
#define JALR            0x01400000

// O-type
#define HALT            0x01800000
#define NOOP            0x01C00000

char tmp[MAXLINELENGTH];
 char labels[MAXLABEL][7];
int vm[987654];


int find_idx(char* l_name, int ed){//search label 1~ ed and return idx. if not found, -1 returned.
	for(int i=0;i<ed;i++){
		if(!strcmp(labels[i],l_name))
			return i;


	}

	return -1;



}



int readAndParse(FILE *, char *, char *, char *, char *, char *);
int isNumber(char *);
int main(int argc, char *argv[])
{ char *inFileString, *outFileString;
 FILE *inFilePtr, *outFilePtr;
 char label[MAXLINELENGTH], opcode[MAXLINELENGTH], arg0[MAXLINELENGTH],
 arg1[MAXLINELENGTH], arg2[MAXLINELENGTH];
 if (argc != 3) {
 printf("error: usage: %s <assembly-code-file> <machine-code-file>\n",
 	argv[0]);
	 exit(1);
 }
 	inFileString = argv[1];
 	outFileString = argv[2];
 	inFilePtr = fopen(inFileString, "r");
 if (inFilePtr == NULL) {
 	printf("error in opening %s\n", inFileString);
 	exit(1);
 }
 outFilePtr = fopen(outFileString, "w");
 if (outFilePtr == NULL) {
 	printf("error in opening %s\n", outFileString);
 	exit(1);
 }


 /* here is an example for how to use readAndParse to read a line from
 inFilePtr */

 	int line=0;
 	while(1){

		if(!readAndParse(inFilePtr, label, opcode, arg0, arg1, arg2)) break;

 		if(strlen(label)>6){ 
			printf("error: label length over 6\n"); 
			exit(1);
		}

	



		if(!strcmp(label,"")){//no label, skip and go nxt line
			line++;
			continue;
		}	
		
		
		 if(find_idx(label,MAXLABEL-1)!=-1){//duplicate label
                        printf("error: duplicate labels: %s\n",label);
                        exit(1);
                }


		strcpy(labels[line++],label);//or strcpy_s

	}
      

	




		
		//	if (! readAndParse(inFilePtr, label, opcode, arg0, arg1, arg2) ){
//		break;
 //	}
 /* this is how to rewind the file ptr so that you start reading from the
 beginning of the file */
 rewind(inFilePtr);
 line=0;
 int pc=0;
int op,r1,r2,offset,result,type;
	while(1){
		if(!readAndParse(inFilePtr, label, opcode, arg0, arg1, arg2))break;

		result=0;
		op=0;

		//printf("%d ",op);
		if(!strcmp(opcode,"add")){
			op=ADD;
			type=1;

		}
		else if(!strcmp(opcode,"nor")){
                        op=NOR;
			type=1;

                }

		else if(!strcmp(opcode,"lw")){
                        op=LW;
			type=2;

                }
		else if(!strcmp(opcode,"sw")){
                        op=SW;
			type=2;

                }

		else if(!strcmp(opcode,"beq")){
                        op=BEQ;
			type=2;

                }

		else if(!strcmp(opcode,"jalr")){
                       op=JALR;
		       type=3;

                }

		else if(!strcmp(opcode,"noop")){
                        op=NOOP;
			type=4;
                }

		else if(!strcmp(opcode,"halt")){
                        op=HALT;
			type=4;

                }
		else if(!strcmp(opcode,".fill")){
			type=5;
			
		}
		else{
			printf("invalid opcode\n");
			exit(1);
		}

		//R types
		if(type==1){//add, nor
			
			if( (!isNumber(arg0))|| (!isNumber(arg1))|| (!isNumber(arg2)) ){//last isnumver check arg2 is "" or not>>check all 3 arg exist


				printf("error: invalid arg at Rtype\n");
				exit(1);

			}
		 	r1=atoi(arg0)<<19;//16+3 bit for offset, r2
			r2=(atoi(arg1)<<16);//3bit is space for offset + 13 unused 0 bit >> 16bit space require
			offset=atoi(arg2);
			result=r1|r2|offset;


		


		}	
		//I type
		else if(type==2){
		//	printf("bb\n");
			   if( (!isNumber(arg0))|| (!isNumber(arg1))|| !strcmp(arg2,"") ){//check invalid input 
				   printf("error2: invalid arg in I type\n");
				   exit(1);
			}

			   r1=atoi(arg0)<<19;//16+3 bit for offset, r2
                           r2=(atoi(arg1)<<16);//3bit is space for offset + 13 unused 0 bit >> 16bit space require
			   

			 
			   if(isNumber(arg2)){
				offset=atoi(arg2);

			   }
			   else{   //label is address
				offset=find_idx(arg2,MAXLABEL-1);
				if(offset==-1){
					printf("error: I type, no label match to given %s\n",arg2);
					exit(1);

				}
				if(op==BEQ)offset-=(pc+1);//beq is pc relative address so minus pc

			   }

			  if (offset < -32768 || offset > 32767) {
                                printf("error: range of offset field is invalid\n");
                                exit(1);
                                }
				
			   if(op==BEQ){

				offset=( (offset)& (0x0000FFFF) );//by minus, it can contaminate upper 16bit, so clear.

			   }
			   


			   result=r1|r2|offset;
			   	




		}	
		//J type
		else if(type==3){
			 if( (!isNumber(arg0))|| (!isNumber(arg1))|| !strcmp(arg2,"") ){//check invalid input 
                                   printf("error2: invalid arg in J type\n");
                                   exit(1);
                        }

                           r1=atoi(arg0)<<19;//16+3 bit for offset, r2     jump regi
                           r2=((pc+1)<<16);//3bit is space for offset + 13 unused 0 bit >> 16bit space require  ;; save pc+1
		   	   result=r1|r2;	   
	
		}

		//O type
		else if(type==4){
			//if(op==HALT){
			//	pc++;
			//	op=0;
		//	}
			//noop do nothing
			result=0;

		}

		else if(type==5){
			if(isNumber(arg0)){
				result=atoi(arg0);
			}
			else{
				result=find_idx(arg0,line);
				if(result==-1){
					printf("error: no such label, .fill\n");
				}
			}

		}	
		else {
             		printf("error: unrecognized opcode\n");
          
          	  	exit(1);
        	}
		// if (offset < -32768 || offset > 32767) {
        	//	printf("error: range of offset field is invalid\n");
        	//	exit(1);
    	//	}
        	vm[pc] = (op | result);
		 line++;
                pc++;
        	}
		for (int i = 0; i < line; i++) {
         	   printf("(address %d): %d (hex 0x%x)\n", i, vm[i], vm[i]);
           	   if (outFilePtr != NULL)fprintf(outFilePtr, "%d\n", vm[i]);
       		}
		//printf("aa\n");
		exit(0);


	}







 /* after doing a readAndParse, you may want to do the following to test the
 opcode */
// if (!strcmp(opcode, "add")) {//!strcmp >> same
 /* do whatever you need to do for opcode "add" */
// }

//}
/*
* Read and parse a line of the assembly-language file. Fields are returned
* in label, opcode, arg0, arg1, arg2 (these strings must have memory already
* allocated to them). *
* Return values:
* 0 if reached end of file
* 1 if all went well *
* exit(1) if line is too long.
*/

int
readAndParse(FILE* inFilePtr, char* label, char* opcode, char* arg0, char* arg1, char* arg2)
{
    char line[MAXLINELENGTH];
    char* ptr = line;

    label[0] = opcode[0] = arg0[0] = arg1[0] = arg2[0] = '\0';

    if (fgets(line, MAXLINELENGTH, inFilePtr) == NULL) // end of file
        return 0;

    if (strchr(line, '\n') == NULL) {
        printf("error: line too long\n");
        exit(1);
    }

    // label check
    ptr = line;
    if (sscanf(ptr, "%[^\t\n\r ]", label))
        ptr += strlen(label);

    sscanf(ptr, "%*[\t\n\r ]%[^\t\n\r ]%*[\t\n\r ]%[^\t\n\r ]%*[\t\n\r ]%[^\t\n\r ]%*[\t\n\r ]%[^\t\n\r ]", opcode, arg0, arg1, arg2);

    return 1;
}

int
isNumber(char* str)
{
    int i;
    return ((sscanf(str, "%d", &i)) == 1);
}











