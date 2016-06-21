import class_recognizer as r
import class_integrated_features_calculator as ifc
import numpy as np
import cv2
import ml.class_machine_factory as mf

class MLQuadraticRecognizer(r.Recognizer):
    
    def __init__(self, img=None):
        self.__class = None
        self.__matchPercentage = None
        self.__featuresCalculator = ifc.IntegratedFeaturesCalculator()
        self.__responsesClassesMap = {}

        args = {
            'type': ('multivariate_classification', None),
            'mathematical_model': ('quadratic', None),
            'minimizer': ('normal_equation', None)
        }

        self.__machine = mf.MachineFactory.create('sl_machine', args)

        self.__img = img
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------

    def setImage(self, img):
        if img is None:
            return self
        
        self.__img = img
        return self
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------

    def getClass(self, recalculate=False):
        if self.__class is None or recalculate:
            self.recognize(recalculate)
        
        return self.__class
    
    def getCalculatedFeature(self, recalculate=False):
        return None
    
    def getMatchPercentage(self, recalculate=False):
        return None
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    def recognize(self, recalculate=False):
        if self.__img is None:
            return self

        features = [1]
        features.extend(self.__extractFeatures(self.__img))
        self.__class = self.__responsesClassesMap[self.__machine.predict(features)]

        return self
    
    def train(self, classesImageMap):
        counter = 1
        responses = []
        featuresSet = []

        for className, img in classesImageMap.iteritems():
            if self.__responsesClassesMap.get(className, None) is None:
                self.__responsesClassesMap[className] = counter
                self.__responsesClassesMap[counter] = className
                counter += 1
            
            responses.append(self.__responsesClassesMap[className])
            features = [1]
            features.extend(self.__extractFeatures(img))
            featuresSet.append(features)
        
        self.__machine.train(np.array(featuresSet, dtype=np.float32), \
                             np.array(responses, dtype=np.float32))
        
        return self

    def __extractFeatures(self, img, recalculate=True):
        m = cv2.moments(img)
        centroid = np.array([m['m10'] / m['m00'], m['m01'] / m['m00'], 0])

        features = [{
                        'name': 'hull',
                        'arguments': {
                            'img': img,
                            'centroid': centroid
                        }
                    }]
        
        self.__featuresCalculator.setFeatures(features)
        return self.__featuresCalculator.get(recalculate)