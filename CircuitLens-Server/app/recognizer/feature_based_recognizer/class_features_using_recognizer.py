import class_recognizer as r
import class_integrated_features_calculator as ifc
import numpy as np
import cv2
import class_features_using_recognizer_classes_db as db

class FeaturesUsingRecognizer(r.Recognizer):
    
    def __init__(self, img=None):
        self.__class = None
        self.__calculatedFeature = None
        self.__matchPercentage = None
        self.__featuresCalculator = ifc.IntegratedFeaturesCalculator()
        self.__db = db.FeaturesUsingRecognizerClassesDB()
        
        self.setImage(img)
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------

    def setImage(self, img):
        self.__img = img
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------

    def getClass(self, recalculate=False):
        if None == self.__class or recalculate:
            self.recognize(recalculate)
        
        return self.__class
    
    def getCalculatedFeature(self, recalculate=False):
        if None == self.__calculatedFeature or recalculate:
            self.recognize(recalculate)
        
        return self.__calculatedFeature
    
    def getMatchPercentage(self, recalculate=False):
        if None == self.__matchPercentage or recalculate:
            self.recognize(recalculate)
        
        return self.__matchPercentage
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    def recognize(self, recalculate=False):
        if None == self.__img:
            return self
        
        m = cv2.moments(self.__img)
        
        centroid = np.array([m['m10'] / m['m00'], m['m01'] / m['m00'], 0])
        
        if None == self.__class:
            features = [{
                            'name': 'disperseness_from_centroid',
                            'arguments': {
                                'centroid': centroid,
                                'img': self.__img
                            }
                        },
                        {
                            'name': 'gearness',
                            'arguments': {
                                'centroid': centroid,
                                'img': self.__img
                            }
                        }]
            
            self.__featuresCalculator.setFeatures(features)
        
        self.__calculatedFeature = self.__featuresCalculator.get(recalculate)
        self.__class, self.__matchPercentage = self.__db.match(self.__calculatedFeature)
        
        return self
    
    def train(self, classesImageMap):
        self.__db.train(classesImageMap)
        
        return self
        