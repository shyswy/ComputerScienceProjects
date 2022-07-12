 lw 0 2 tmp2      reg2=5
    lw 0 1 tmp1      reg1=1
    lw 0 0 zero  reg0=0    loaduse hazard
    noop
    noop
loop    add 1 3 3       reg3=reg3+reg1 increment 1
    noop
    noop
    noop
    beq 3 2 done    if r3==r2  no control(brach) hazard, so give correct ans 5 
    beq 0 0 loop
done halt
zero .fill 0
tmp1 .fill 1
tmp2 .fill 5
