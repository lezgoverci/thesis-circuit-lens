import cv2
import numpy as np
from class_circuit_element_finder import CircuitElementFinder

class MorphologyUserCircuitElementFinder(CircuitElementFinder):
    __mLineThickness = 0
    __mKernelSide = 0
    
    def setLineThickness(self, lineThickness):
        self.__mLineThickness = lineThickness
        
    def setKernelSide(self, kernelSide):
        self.__mKernelSide = kernelSide
        
    def find(self, img):
        connectKernel = np.ones((self.__mKernelSide, self.__mKernelSide),np.uint8)
        img = cv2.morphologyEx(img,cv2.MORPH_CLOSE,connectKernel,iterations=2)

        disconnectKernel = np.ones((self.__mLineThickness, self.__mLineThickness), np.uint8)
        img = cv2.morphologyEx(img,cv2.MORPH_OPEN,disconnectKernel,iterations=2)

        img = cv2.morphologyEx(img,cv2.MORPH_CLOSE,connectKernel,iterations=2)

        _,contours,_ = cv2.findContours(img,cv2.RETR_TREE,cv2.CHAIN_APPROX_SIMPLE)
        
        if not contours or len(contours) == 0:
            return None
        
        return contours