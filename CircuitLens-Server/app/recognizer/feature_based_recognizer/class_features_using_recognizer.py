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
        self.__img = img
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------

    def setImage(self, img):
        self.__img = img
        
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
        if self.__img is None:
            return self

        m = cv2.moments(self.__img)
        
        centroid = np.array([m['m10'] / m['m00'], m['m01'] / m['m00'], 0])
        
        features = [{
                        'name': 'disperseness_from_centroid',
                        'arguments': {
                            'centroid': centroid,
                            'img': self.__img,
                            'area': m['m00']
                        }
                    },
                    {
                        'name': 'gearness',
                        'arguments': {
                            'centroid': centroid,
                            'img': self.__img
                        }
                    },
                    {
                        'name': 'corner_distance',
                        'arguments': {
                            'centroid': centroid,
                            'img': self.__img,
                            'area': m['m00']
                        }
                    }]
        
        self.__featuresCalculator.setFeatures(features)
        
        self.__calculatedFeature = self.__featuresCalculator.get(recalculate)
        
        return self
    
    def train(self, classesImageMap):
        db.instance.train(classesImageMap)
        
        return self
        
    def __getOtherArguments(self):
        if self.__calculatedFeature is None:
            self.recognize(True)
        
        self.__class, self.__matchPercentage = db.instance.match(self.__calculatedFeature)
        