    lw 0 1 five Load reg1 with 5 (symbolic addr)
    lw 0 2 neg5    load reg2 with -1 (numeric addr)
start   add 1 2 1   decrement reg1
    beq 0 1 done   goto end of program when reg1==0
    beq 0 0 start go back to the beginning of the loop
    noop
done halt end of prog
five .fill 5
neg5 .fill -5
stAddr .fill start
