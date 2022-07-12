    lw 0 1 five Load reg1 with 5
    lw 0 2 neg1    load reg2 with -1 
    lw 0 3 one load reg3 with 1
    lw 0 4 zero load reg4 with 0
start   add 1 2 1   decrement reg1
    beq 0 1 done   goto end of program when reg1==0
    add 4 3 4 reg4+=1
    beq 0 0 start go back to the beginning of the loop
    noop
done halt end of prog
zero .fill 0
one .fill 1
five .fill 5
neg1 .fill -1
stAddr .fill dup
