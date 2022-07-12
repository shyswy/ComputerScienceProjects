/* LC-2K Instruction-level simulator */
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#define NUMMEMORY 65536 /* maximum number of words in memory */
#define NUMREGS 8 /* number of machine registers */
#define MAXLINELENGTH 1000


#define ADD			0
#define NOR			1
#define LW			2
#define SW			3
#define BEQ			4
#define JALR		5
#define HALT		6
#define NOOP		7

int opcode, r1, r2, offset,rd;
int convertNum(int num)
{
	/* convert a 16-bit number into a 32-bit Linux integer */
	if (num & (1 << 15)) {
		num -= (1 << 16);
	}
	return(num);
}


void cvt(int mc)
{
	opcode = (mc >> 22) & 0x00000007;//  0~~~0  0111
	r1 = (mc >> 19) & 0x00000007;
	r2 = (mc >> 16) & 0x00000007;
	rd = mc & 0x00000007;
	offset = mc&0x0000FFFF;
}


typedef struct stateStruct {
	int pc;
	int mem[NUMMEMORY];
	int reg[NUMREGS];
	int numMemory;
} stateType;
void printState(stateType*);



int main(int argc, char* argv[])
{
	char line[MAXLINELENGTH];
	stateType state;
	FILE* filePtr;
	if (argc != 2) {
		printf("error: usage: %s <machine-code file>\n", argv[0]);
		exit(1);
	}
	filePtr = fopen(argv[1], "r");
	if (filePtr == NULL) {
		printf("error: can't open file %s", argv[1]);
		perror("fopen");
		exit(1);
	}
	int endline = 0;
	/* read in the entire machine-code file into memory */
	for (state.numMemory = 0; fgets(line, MAXLINELENGTH, filePtr) != NULL;
		state.numMemory++) {
		if (sscanf(line, "%d", state.mem + state.numMemory) != 1) {
			printf("error in reading address %d\n", state.numMemory);
			exit(1);
		}
		endline = state.numMemory;
		printf("memory[%d]=%d\n", state.numMemory, state.mem[state.numMemory]);
		//flag=1;
	
	}

	memset(state.reg, 0, (sizeof(int) * NUMREGS));
	state.pc=0;
	int ex = 0;

	while (!ex) {
		if (state.pc > endline)break;
		printState(&state);

		cvt(state.mem[state.pc]);
		
	//	state.pc++;//pc first increase
		switch (opcode) {

		case ADD:
			state.reg[rd] = state.reg[r1] + state.reg[r2];
			break;

		case NOR:
			state.reg[rd] = ~(state.reg[r1] | state.reg[r2]);
			break;
		case  LW:
			state.reg[r2] = state.mem[convertNum(state.reg[r1] + offset)];
			break;
		case SW:
			state.reg[convertNum(state.reg[r1] + offset)] = state.reg[r2];
			break;
		case BEQ:
			if (state.reg[r1] == state.reg[r2]) state.pc += (convertNum(offset) );//pc already +1 ??
			break;
		case JALR:
			state.reg[r2] = state.pc+1;//pc already increase
			state.pc = state.reg[r1]-1;//pc increase at first so -1.
			break;
		case HALT:
			state.pc++;
			ex = 1;
			printf("halt!\n");
			printState(&state);
			break;
		case NOOP:
			break;

		default:
			printf("unknown opcode\n");
			break;


		}
		state.pc++;//pc first increase



	}

	if(!ex) printState(&state);
	fclose(filePtr);
	exit(0);


	
}
void printState(stateType* statePtr)
{
	int i;
	printf("\n@@@\nstate:\n");
	printf("\tpc %d\n", statePtr->pc);
	printf("\tmemory:\n");
	for (i = 0; i < statePtr->numMemory; i++) {
		printf("\t\tmem[ %d ] %d\n", i, statePtr->mem[i]);
	}
	printf("\tregisters:\n");
	for (i = 0; i < NUMREGS; i++) {
		printf("\t\treg[ %d ] %d\n", i, statePtr->reg[i]);
	}
	printf("end state\n");
}
