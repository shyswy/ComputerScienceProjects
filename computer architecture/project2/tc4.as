    lw 0 2 tmp2      reg2=5
    lw 0 1 tmp1      reg1=1
    lw 0 0 zero  reg0=0 
    noop
    noop
loop    add 1 3 3       reg3=reg3+reg1 increment 1   
    beq 3 2 done    if r3==r2    branch hazard!!! no prediction and forward make branch hazard  it shoud give 5 (reg3=5) but return 6 
    beq 0 0 loop
done halt
zero .fill 0
tmp1 .fill 1
tmp2 .fill 5
