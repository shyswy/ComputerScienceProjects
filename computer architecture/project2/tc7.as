	lw	0	1	start
	lw	0	2	num
	lw	0	3	neg1
	noop
	add	0	1	4
	noop
	noop
	noop
loop	add 4	5	5
	add	1	4	4
	add 2	3	2
	noop
	noop
	noop
	beq	0	2	7
	noop
	noop
	noop
	beq 0	0	loop
	noop
	noop
	noop
end	sw	0	5	result
	halt
start	.fill	1	
num	.fill	10
neg1	.fill	-1
result	.fill	0
