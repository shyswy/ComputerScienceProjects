 lw 0 1 one      $1 = 1
    lw 0 2 two      $2 = 2
    lw 0 3 three    $3 = 3
    lw 0 4 four     $4 = 4
    add 1 1 1       reg1= reg1+reg1=2 no data hazard as 3 cycle before lw
    add 2 2 2       4   below 3 inst also no data hazard
    add 3 3 3       6
    add 4 4 4       8
    sw 0 1 one      2    
    sw 0 2 two      4
    sw 0 3 three    6
    sw 0 4 four     8   no hazard, return correct ans!
    halt
one .fill 1
two .fill 2
three .fill 3
four .fill 4
