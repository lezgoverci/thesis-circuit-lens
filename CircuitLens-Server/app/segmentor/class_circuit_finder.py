import cv2
import numpy as np
import math

class CircuitFinder:
    __mLineThickness = 0
    __mKernelSide = 0
    __mDetectedCircuit = None
    
    def getLineThickness(self):
        return self.__mLineThickness
    
    def getKernelSide(self):
        return self.__mKernelSide
    
    def getDetectedCircuit(self):
        return self.__mDetectedCircuit
    
    def find(self, img):
        self.__mLineThickness = self.__getLineThickness(img)
        self.__mKernelSide = self.__approximateKernelSide(self.__mLineThickness)

        connectedObjects = self.__attemptConnectionOfCircuitElements(img, self.__mKernelSide)
        tempDetectedCircuit = self.__detectCircuit(connectedObjects.copy())
        self.__mDetectedCircuit = self.__attemptIncludeNeighbouringTexts(connectedObjects, tempDetectedCircuit, self.__mLineThickness)
        return self.__mDetectedCircuit
        
    def __approximateElementDim(self, lineThickness):
        return lineThickness / 0.08

    def __approximateKernelSide(self, lineThickness):
        elementDim = self.__approximateElementDim(lineThickness)
        approxTotalElemSpace = elementDim - (4 * lineThickness)
        
        return int(math.floor(approxTotalElemSpace / 6))
    
    def __getLineThickness(self, img):
        h, w = img.shape[:2]

        maxDim = h if h > w else w
        
        return int(math.ceil(maxDim * 0.005))
    
    def __attemptConnectionOfCircuitElements(self, img, kernelSide=5):
        kernel = np.ones((kernelSide, kernelSide),np.uint8)
        img = cv2.morphologyEx(img,cv2.MORPH_CLOSE,kernel,iterations=2)
        
        return img
        
    def __detectCircuit(self, img):
        _,contours,_ = cv2.findContours(img,cv2.RETR_TREE,cv2.CHAIN_APPROX_SIMPLE)
        
        if not contours or len(contours) == 0:
            return None
        
        area = 0
        f_x, f_y, f_w, f_h = 0, 0, 0, 0
        for cnt in contours:
            x,y,w,h = cv2.boundingRect(cnt)
            t_area = w * h
            if area < t_area:
                area = t_area
                f_x, f_y, f_w, f_h = x, y, w, h
        
        return (f_x, f_y, f_w, f_h)
    
    def __attemptIncludeNeighbouringTexts(self, img, circuitArea, lineThickness, intensity=255, thickness=2):
        acceptable_dst = lineThickness * 2
        
        src_h, src_w = img.shape[:2]
        
        x, y, w, h = circuitArea
        
        x -= acceptable_dst
        y -= acceptable_dst
        w += acceptable_dst
        h += acceptable_dst
        
        x = 0 if x < 0 else x
        y = 0 if y < 0 else y
        w = w if src_w > x + w else src_w - x
        h = h if src_h > y + h else src_h - y
        
        cv2.rectangle(img, (x, y), (x + w, y + h), intensity, thickness)
        
        return self.__detectCircuit(img)