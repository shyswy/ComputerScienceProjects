import glfw
from OpenGL.GL import *
from OpenGL.GLU import *
import numpy as np

gCamAng = 0.
gCamHeight = 1.

M = np.identity(3)


    

def renders(T):
    glClear(GL_COLOR_BUFFER_BIT)
    glLoadIdentity()
    # draw cooridnate
    glBegin(GL_LINES)
    glColor3ub(255, 0, 0)
    glVertex2fv(np.array([0.,0.]))
    glVertex2fv(np.array([1.,0.]))
    glColor3ub(0, 255, 0)
    glVertex2fv(np.array([0.,0.]))
    glVertex2fv(np.array([0.,1.]))
    glEnd()
# draw triangle
    glBegin(GL_TRIANGLES)
    glColor3ub(255, 255, 255)
    glVertex2fv( (T @ np.array([.0,.5,1.]))[:-1] )
    glVertex2fv( (T @ np.array([.0,.0,1.]))[:-1] )
    glVertex2fv( (T @ np.array([.5,.0,1.]))[:-1] )
    glEnd()




def key_callback(window, key, scancode, action, mods):
    global gCamAng, gCamHeight,T, M
    deg=np.radians(10)
    if action==glfw.PRESS or action==glfw.REPEAT:
        if key==glfw.KEY_Q:
            T=[[1,0,-0.1],
               [0,1,0],
               [0,0,1]]
            M =  T@M
        elif key==glfw.KEY_E:
             T=[[1,0,0.1],
               [0,1,0],
               [0,0,1]]
             M =  T@M
        elif key==glfw.KEY_A:
             T=[[np.cos(deg),-np.sin(deg),0.],[np.sin(deg),np.cos(deg),0.],[0.,0.,1.]]
             M = M@T
        elif key==glfw.KEY_D:
            T=[[np.cos(-deg),-np.sin(-deg),0.],[np.sin(-deg),np.cos(-deg),0.],[0.,0.,1.]]
            M = M@T
        elif key==glfw.KEY_1:
            M=np.array([[1.,0.,.0], [0.,1.,.0],[0.,0.,1.]])
        elif key==glfw.KEY_W:
             T=[[.9,0,0],
               [0,1,0],
               [0,0,1]]
             M = T@M

        elif key==glfw.KEY_S:
             T=[[np.cos(deg),-np.sin(deg),0.],[np.sin(deg),np.cos(deg),0.],[0.,0.,1.]]
             M = T@M

def main():
    global M
    if not glfw.init():
        return
    window = glfw.create_window(480,480,'glOrtho()', None,None)
    if not window:
        glfw.terminate()
        return
    glfw.make_context_current(window)
    glfw.set_key_callback(window, key_callback)
    T=np.array([[1.,0.,.1], [0.,1.,.1],[0.,0.,1.]])
    while not glfw.window_should_close(window):
        glfw.poll_events()
        renders(M)
        glfw.swap_buffers(window)

    glfw.terminate()

if __name__ == "__main__":
    main()
