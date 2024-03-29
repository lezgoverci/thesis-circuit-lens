import class_feature_processable_data_extractor as fpde
import numpy as np
import cv2

class CornersKeypointsExtractor(fpde.FeatureProcessableDataExtractor):

    def __init__(self):
        self.__arguments = None
        self.__neededArguments = ['centroid', 'img']
        self.__extractedData = None
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------

    def setArguments(self, args):
        self.__arguments = args
        
        return self

    #-----------------------------------------
    # Getters
    #-----------------------------------------

    def getExtractedData(self, reextract=False):
        if not self.__extractedData or reextract:
            self.extract()
        
        return self.__extractedData

    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    def extract(self):
        if not self.argumentsMet():
            return None
        
        centroid = self.__arguments['centroid']
        img = self.__arguments['img']
        
        img = np.float32(img)
        img = cv2.cornerHarris(img,2,3,0.04)
        
        h, w = img.shape[:2]
        
        corners = []
        
        newImg = np.zeros(img.shape, np.uint8)
        
        y = 0
        while y < h:
            x = 0
            while x < w:
                if 0 != img[y][x] and img[y][x] > 0.01 * img.max():
                    corners.append(np.array([float(x), float(y), 0.0]))
                    cv2.line(newImg, (int(centroid[0]), int(centroid[1])), (x, y), 255, 2)
                x += 1
            y += 1

        self.__extractedData = (corners, newImg)
        
        return self
    
    def argumentsMet(self):
        return self.__arguments is not None and all(neededArg in self.__arguments for neededArg in self.__neededArguments)