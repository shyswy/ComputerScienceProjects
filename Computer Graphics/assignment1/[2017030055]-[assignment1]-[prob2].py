import numpy as np
M=np.arange(2,27,1)
print(M)
M=M.reshape(5,5)
print(M)
M[0::,0]=0
print(M)
M=M@M
print(M)
v=M[0,0::]
v_mag=np.sqrt(v.dot(v))
print(v_mag)
