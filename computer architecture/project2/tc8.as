 lw 0 1 one      reg1 = 1
    lw 0 2 two      reg2 =2
    noop
    noop
    add 1 1 1       reg1+reg1 =2   aboid data hazard with 1 noop, correct ans return 
    add 2 2 2       reg2+reg2=2
    noop
    noop
    sw 0 1 one      one <- 2
    sw 0 2 two      two <- 4
    halt
one .fill 1
two .fill 2
three .fill 3
four .fill 4
