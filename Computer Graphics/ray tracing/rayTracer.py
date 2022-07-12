#!/usr/bin/env python3
# -*- coding: utf-8 -*
# see examples below
# also read all the comments below.

import os
import sys
import pdb  # use pdb.set_trace() for debugging
import code # or use code.interact(local=dict(globals(), **locals()))  for debugging.
import xml.etree.ElementTree as ET
import numpy as np
from PIL import Image 

def scalar(vector):
    return np.sqrt(np.sum(vector**2))


def normalize(vector):
    size = np.sqrt(np.sum(vector**2))
    return vector / scalar(vector)



class Color:
    def __init__(self, R, G, B):
        self.color=np.array([R,G,B]).astype(np.float)

    # Gamma corrects this color.
    # @param gamma the gamma value to use (2.2 is generally used).
    def gammaCorrect(self, gamma):
        inverseGamma = 1.0 / gamma;
        self.color=np.power(self.color, inverseGamma)

    def toUINT8(self):
        return (np.clip(self.color, 0,1)*255).astype(np.uint8)

class Shader:
    def __init__(self, diffuseColor, specularColor=np.array([0.,0.,0.]), exponent=0):
        self.diffuseColor=diffuseColor
        self.specularColor=specularColor
        self.exponent=exponent



class Light:
    def __init__(self, position, intensity):
        self.position=position
        self.intensity=intensity



class Camera:              
    def __init__(self, point, direction, up, projDist, viewW, viewH, imgSize):
        self.e=point
        self.w=-direction/scalar(direction)
        self.u=np.cross(up,self.w)
        self.u=np.cross(up,self.w)/scalar(self.u)
        self.v=np.cross(self.w,self.u)
        self.v=self.v/scalar(self.v)

        # viewD = projD. this project assume projNormal = =viewDir
        self.viewD=projDist
        self.viewW=viewW
        self.viewH=viewH
        self.imgW=imgSize[0]
        self.imgH=imgSize[1]

    def Ray(self, ix, iy):
        U=iy-self.imgW/2
        V=-ix+self.imgH/2
        W=(self.viewD/self.viewW)*self.imgW
        move=U*self.u+V*self.v
        s=self.e+move-W*self.w # s+ (uu+vv)-dw
        rayPoint=self.e
        d=s-self.e   #s-e 
        return rayPoint, d






 


class Sphere:
    def __init__(self, center, radius, shader):
        self.radius=radius
        self.center=center
        
        self.shader=shader

    def intersect(self, rayPoint, rayVec):
        p=rayPoint-self.center
        d=normalize(rayVec)
        in_root= pow(( d@p),2 ) -p@p+self.radius**2 
        #in_root=(d@p)**2-p@p+self.radius**2 
        t=np.inf
        FirstMeetPoint = np.array([0., 0., 0.])
        intersect_vec = np.array([0., 0., 0.])
        
        if in_root>=0:  #   + - 
            if -d@p-np.sqrt(in_root)>=0:#only need first point 
                t= -d@p-np.sqrt(in_root)
               

            #elif -d@p+np.sqrt(in_root)>=0:
             #   t=-d@p+np.sqrt(in_root)
            
            if t<0:
                t=np.inf
            
               #else, no intersect
            FirstMeetPoint=rayPoint+t*d  # by ray, get first meet point 
            intersect_vec=rayPoint+t*d-self.center   #get intersected vector
        return FirstMeetPoint, intersect_vec, t
           

                



        

class Box:
    def __init__(self, minPoint, maxPoint, shader):
        
      
        
        self.shader=shader
       

        self.normX=np.array([-1,0,0])

        self.normY=np.array([0,-1,0])

        self.normZ=np.array([0,0,-1])
        
        self.maxPoint=maxPoint

        self.minPoint=minPoint


        
    def intersect(self, rayPoint, rayVec):
        p=rayPoint
        d=normalize(rayVec)

        tXmin, tXmax,tYmin, tYmax, tZmin, tZmax = np.inf, np.inf, np.inf, np.inf, np.inf, np.inf
        self.normX,self.normY,self.normZ=np.array([-1,0,0]),np.array([0,-1,0]),np.array([0,0,-1])
        
        #check with each axis

        
        
        
        if d[0]!=0:
            if (self.minPoint[0]*d[0])>(self.maxPoint[0]*d[0]):# base on w(or d) change can occur
                
                tXmin=(self.maxPoint[0]-p[0])/d[0]
                tXmax=(self.minPoint[0]-p[0])/d[0]
                self.normX[0]=1
                
            else:
                tXmax=(self.maxPoint[0]-p[0])/d[0]
                tXmin=(self.minPoint[0]-p[0])/d[0]


        if d[1]!=0:
            if (self.minPoint[1]*d[1])>(self.maxPoint[1]*d[1]):# base on w(or d) change can occur
                
                tYmin=(self.maxPoint[1]-p[1])/d[1]
                tYmax=(self.minPoint[1]-p[1])/d[1]
                self.normY[1]=1
                
            else:
                tYmax=(self.maxPoint[1]-p[1])/d[1]
                tYmin=(self.minPoint[1]-p[1])/d[1]


        if d[2]!=0:
            if (self.minPoint[2]*d[2])>(self.maxPoint[2]*d[2]):# base on w(or d) change can occur
                
                tZmin=(self.maxPoint[2]-p[2])/d[2]
                tZmax=(self.minPoint[2]-p[2])/d[2]
                self.normZ[2]=1
                
            else:
                tZmax=(self.maxPoint[2]-p[2])/d[2]
                tZmin=(self.minPoint[2]-p[2])/d[2]

        tminIdx=0
        tmins=np.array([tXmin,tYmin,tZmin])


        if tYmin>tmins[tminIdx]:
            tminIdx=1
        if tZmin>tmins[tminIdx]:
            tminIdx=2



        
        tMin=-np.inf
        idxMin=-1
        idx=-1


        
        arrTmin=np.array([tXmin,tYmin,tZmin])
        for i in arrTmin:    #find biggest among smallest for ranging
            idx=idx+1
            if tMin<i:
                tMin=i
                idxMin=idx
    



        
        tMax=np.inf
        idxMax=-1
        idx=-1
        arrTmax=np.array([tXmax,tYmax,tZmax])
        for i in arrTmax:     
            idx=idx+1
            if tMax>i:
                tMax=i
                idxMax=idx
            





        hitPoint = np.array([0., 0., 0.])
        hitVector = np.array([0., 0., 0.])
        T=np.inf


        
        if tMax>tMin:
            
                
            T=tMin

            
                
            if idxMin==0:
                normal=self.normX
            elif idxMin==1:
                normal=self.normY
            elif idxMin==2:
                normal=self.normZ
    
            hitPoint=p+T*d
            hitVector=normal
            
        if T<0:
            T=np.inf 
            
        return hitPoint, hitVector, T
   

def nextIntersect(rayPoint, rayVec, SphereList, BoxList):
    tmin=np.inf
    FirstIntersectVec = np.array([0., 0., 0.])
    FirstMeetPoint = np.array([0., 0., 0.])
   
    for object in SphereList:
        p, v, t = object.intersect(rayPoint, rayVec)
        if t<tmin: 
            tmin=t
            FirstMeetPoint, FirstIntersectVec = p, v
            meetObj=object
    for object in BoxList:
        p, v, t = object.intersect(rayPoint, rayVec)
        if t<tmin:
            tmin=t
            FirstMeetPoint, FirstIntersectVec = p, v
            meetObj=object

    if tmin == np.inf:
        meetObj=-1#no meeting
    #return first meet object
    return tmin, FirstMeetPoint, FirstIntersectVec, meetObj #intersect vec: first intersect one




def main(): 
    

    tree = ET.parse(sys.argv[1])
    root = tree.getroot()

    lightList=[]  
    Shaders={} 
    sphereList=[]
    boxList=[]
    

    
    viewDir=np.array([0,0,-1]).astype(np.float)
    viewUp=np.array([0,1,0]).astype(np.float)
    viewProjNormal=-1*viewDir  
    viewWidth=1.0
    viewHeight=1.0
    projDistance=1.0
    intensity=np.array([1,1,1]).astype(np.float)

    imgSize=np.array(root.findtext('image').split()).astype(np.int)

   
    for c in root.findall('camera'):
        if c.find('viewPoint') != None:
            viewPoint=np.array(c.findtext('viewPoint').split()).astype(np.float)
        if c.find('viewWidth') != None:
            viewWidth=np.array(c.findtext('viewWidth').split()).astype(np.float)
        if c.find('viewHeight') != None:
            viewHeight=np.array(c.findtext('viewHeight').split()).astype(np.float)
        if c.find('viewUp') != None:
            viewUp=np.array(c.findtext('viewUp').split()).astype(np.float)
        if c.find('viewDir') != None:
            viewDir=np.array(c.findtext('viewDir').split()).astype(np.float)
        if c.find('viewNormal') != None:
            projNormal=np.array(c.findtext('projNormal').split()).astype(np.float)
        if c.find('projDistance') != None:
            projDistance=np.array(c.findtext('projDistance').split()).astype(np.float)
      
        camera = Camera(viewPoint, viewDir, viewUp, projDistance, viewWidth, viewHeight, imgSize)

    #  shader
    for c in root.findall('shader'):
        diffuseColor=np.array(c.findtext('diffuseColor').split()).astype(np.float)
        
        if c.get('type')=='Phong':
            specularColor=np.array(c.findtext('specularColor').split()).astype(np.float)
            exponent=np.array(c.findtext('exponent').split()).astype(np.float)
            Shaders[c.get('name')]=Shader(diffuseColor, specularColor, exponent)
        elif c.get('type')=='Lambertian':
            Shaders[c.get('name')]=Shader(diffuseColor)

    
    for c in root.findall('surface'):
        
        
        f=c.find('shader')

        sName=f.get('ref')
        
        objShader=Shaders[sName]

        if c.get('type')=='Sphere':
           
            objRadius=np.array(c.findtext('radius').split()).astype(np.float)
            objCenter=np.array(c.findtext('center').split()).astype(np.float)
            tmpObj=Sphere(objCenter, objRadius, objShader)
            sphereList += [tmpObj]

        if c.get('type')=='Box':
           
            objMaxPt=np.array(c.findtext('maxPt').split()).astype(np.float)
            objMinPt=np.array(c.findtext('minPt').split()).astype(np.float)
           
            tmpObj=Box(objMinPt, objMaxPt, objShader)
            boxList += [tmpObj]

    # get light
    for c in root.findall('light'):
        lightIntensity=np.array(c.findtext('intensity').split()).astype(np.float)
        
        lightPosition=np.array(c.findtext('position').split()).astype(np.float)
        
        tmpLight=Light(lightPosition, lightIntensity)
        lightList+= [tmpLight]

    #get materials done



    

    # Create an empty image
    channels=3
    img = np.zeros((imgSize[1], imgSize[0], channels), dtype=np.uint8)
    img[:,:]=0

    for i in np.arange(imgSize[1]):
        for j in np.arange(imgSize[0]):
            imgColor=np.array([0,0,0])
            rayPoint, rayVec = camera.Ray(i,j)
            tmin, FirstMeetPoint, FirstIntersectVec, meetObj = nextIntersect(rayPoint, rayVec, sphereList, boxList)
            if tmin != np.inf: # if inf, no meeting
                normal = normalize(FirstIntersectVec)
                V = normalize(-rayVec)
                imgColor=np.array([0.,0.,0.])# ambient shade , but can't find best so leave it 0
                for light in lightList:
                  
                    I = normalize(light.position-FirstMeetPoint) #use in lember aka diffuse   dir of   eyeray hitpoint >> light   
                    h = normalize(I + V) #use in phong aka specular shade 
                   
                   
                    st,sp , sv, sobj = nextIntersect(FirstMeetPoint, I, sphereList, boxList) # shoot point: ray interect point, direction: eyeray hit point to light, if inf, no meet of light so shade it
                    if st==np.inf: #light is blocked
                       
                        Ls=meetObj.shader.specularColor*light.intensity*1
                        Ld=meetObj.shader.diffuseColor*light.intensity*1    
                        imgColor+= Ld*max([0,I@normal])
                        imgColor+=Ls*(max([0,h@normal])**meetObj.shader.exponent)
        
           
            color = Color(imgColor[0], imgColor[1], imgColor[2]) 
            color.gammaCorrect(2.2)
            img[i][j] = color.toUINT8()

    rawimg = Image.fromarray(img, 'RGB')
    rawimg.save(sys.argv[1]+'.png')
    
if __name__=="__main__":
    main()
