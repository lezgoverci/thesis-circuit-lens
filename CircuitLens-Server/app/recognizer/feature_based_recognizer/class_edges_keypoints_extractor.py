import class_feature_processable_data_extractor as fpde
import numpy as np
import cv2

class EdgesKeypointsExtractor(fpde.FeatureProcessableDataExtractor):

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

        img = cv2.Canny(img,100,200)
        h, w = img.shape[:2]
        
        edges = []
        
        newImg = np.zeros(img.shape, np.uint8)
        y = 0
        while y < h:
            x = 0
            while x < w:
                if 0 != img[y][x]:
                    edges.append(np.array([float(x), float(y), 0.0]))
                    cv2.line(newImg, (int(centroid[0]), int(centroid[1])), (x, y), 255, 2)
                x += 1
            y += 1

        self.__extractedData = (edges, newImg)
        
        return self
    
    def argumentsMet(self):
        return self.__arguments is not None and all(neededArg in self.__arguments for neededArg in self.__neededArguments)