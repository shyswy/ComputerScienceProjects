import glfw
from OpenGL.GL import *
import numpy as np
x=9

arr=[GL_POINTS,GL_LINES,GL_LINE_STRIP,GL_LINE_LOOP,GL_TRIANGLES,GL_TRIANGLE_STRIP,GL_TRIANGLE_FAN,GL_QUADS,GL_QUAD_STRIP,GL_POLYGON]

def render(T):
    glClear(GL_COLOR_BUFFER_BIT)
    glLoadIdentity()
    global x
    global arr
    glBegin(arr[int(x)])
    glColor3ub(255,255,255)
   
    arrs=np.linspace(0,(2*np.pi),13)
   
    
    
    for i in range(0,13):    
        glVertex2f(np.cos(arrs[i]),np.sin(arrs[i]))
  
    glEnd()




def key_callback(window, key, scancode, action, mods):
    global x
    
    if action==glfw.PRESS or action==glfw.REPEAT:
        if key==glfw.KEY_1:
            x=0
           
        elif key==glfw.KEY_2:
            x=1
        elif key==glfw.KEY_3:
             x=2
        elif key==glfw.KEY_4:
            x=3
            
        elif key==glfw.KEY_5:
            x=4
           
        elif key==glfw.KEY_6:
             x=5

        elif key==glfw.KEY_7:
           x=6

        elif key==glfw.KEY_8:
            x=7
        elif key==glfw.KEY_9:
            x=8

        elif key==glfw.KEY_0:
            x=9
        




def main():
               
    if not glfw.init():
        return
    window=glfw.create_window(480,480,"2dtrans",None,None)
    if not window:
        glfw.terminate()
        return
    

    glfw.make_context_current(window)
    glfw.set_key_callback(window, key_callback)
    T=np.array([[2.,0.],[0.,2.]])
    while not glfw.window_should_close(window):
        glfw.poll_events()
        render(T)
        glfw.swap_buffers(window)

    glfw.terminate()

if __name__=="__main__":
    main()
