        lw 0 1 input	reg1=input
        lw 0 2 idx  	reg2=idx
        lw 0 3 zero	reg3=zero
        lw 0 4 one	reg4=1
        lw 0 5 adr	reg5=adress
        jalr 5 6   jmp
	sw 0 3 100 
        halt
loop add 3 1 3   plus input to reg3 idx time
        add 2 4 2    
        beq 1 2 done idx==3 goto done
        beq 0 0 loop unconditinal loop
done halt
zero .fill 0
adr .fill loop
idx .fill 0
input .fill 3
one .fill 1
