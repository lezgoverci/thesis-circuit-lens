import class_recognizer as r
import class_integrated_features_calculator as ifc
import numpy as np
import cv2
import class_features_using_recognizer_classes_db as db

class FreeFeaturesUsingRecognizer(r.Recognizer):
    
    def __init__(self, img=None):
        self.__class = None
        self.__calculatedFeature = None
        self.__matchPercentage = None
        self.__featuresCalculator = ifc.IntegratedFeaturesCalculator()
        self.__features = None
        self.__img = img
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------

    def setImage(self, img):
        if img is None:
            return
        
        self.__img = img
        return self
    
    def setFeatures(self, features):
        self.__features = features
        
        return self
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------

    def getClass(self, recalculate=False):
        if self.__class is None or recalculate:
            self.recognize(recalculate)
            self.__getOtherArguments()
        
        return self.__class
    
    def getCalculatedFeature(self, recalculate=False):
        if self.__calculatedFeature is None or recalculate:
            self.recognize(recalculate)
        
        return self.__calculatedFeature
    
    def getMatchPercentage(self, recalculate=False):
        if self.__matchPercentage is None or recalculate:
            self.recognize(recalculate)
            self.__getOtherArguments()
        
        return self.__matchPercentage
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    def recognize(self, recalculate=False):
        if self.__features is None:
            return self
        
        self.__featuresCalculator.setFeatures(self.__features)
        
        self.__calculatedFeature = self.__featuresCalculator.get(recalculate)
        
        return self
    
    def train(self, classesImageMap):
        db.instance.train(classesImageMap)
        
        return self
        
    def __getOtherArguments(self):
        if self.__calculatedFeature is None:
            self.recognize(True)
        
        self.__class, self.__matchPercentage = db.instance.match(self.__calculatedFeature)
        