import class_recognizer as r
import class_integrated_features_calculator as ifc
import numpy as np
import cv2
import ml.class_machine_factory as mf
import common.class_basic_functions as bf

class MLQuadraticRecognizer(r.Recognizer):
    
    def __init__(self, img=None):
        self.__class = None
        self.__matchPercentage = None
        self.__featuresCalculator = ifc.IntegratedFeaturesCalculator()
        self.__responsesClassesMap = {}

        temp = {'fs_and_rs_selector': lambda fs, rs, i: self.__selectFeaturesAndResponses(fs, rs, i)}
        args = {
            'type': ('multivariate_classification', temp),
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
        features = self.__NDTo2DFeatures(np.array([features], dtype=np.float32))
        self.__class = self.__responsesClassesMap[self.__machine.predict(np.array(features[0], dtype=np.float32))]

        return self
    
    def train(self, classesImageMap):
        counter = 1
        featuresSet = []

        for className, img in classesImageMap.iteritems():
            if self.__responsesClassesMap.get(className, None) is None:
                self.__responsesClassesMap[className] = counter
                self.__responsesClassesMap[counter] = className
                counter += 1

            features = [self.__responsesClassesMap[className]]
            features.extend(self.__extractFeatures(img))
            featuresSet.append(features)

        twoDFeatureSet = self.__NDTo2DFeatures(np.array(featuresSet, dtype=np.float32))
        twoDFeatureSet = sorted(twoDFeatureSet, key = lambda f: f[1])

        responses = []
        for i in range(len(twoDFeatureSet)):
            responses.append(twoDFeatureSet[i][0])
            twoDFeatureSet[i][0] = 1

        self.__machine.train(twoDFeatureSet, np.array(responses, dtype=np.float32))
        
        return self

    def __extractFeatures(self, img, recalculate=True):
        m = cv2.moments(img)
        centroid = np.array([m['m10'] / m['m00'], m['m01'] / m['m00'], 0.0])

        features = [{
                    #     'name': 'num_contours',
                    #     'arguments': {
                    #         'img': img.copy()
                    #     }
                    # },
                    # {
                        'name': 'hull',
                        'arguments': {
                            'img': img,
                            'centroid': centroid
                        }
                    }]
        
        self.__featuresCalculator.setFeatures(features)
        return self.__featuresCalculator.get(recalculate)
    
    def __selectFeaturesAndResponses(self, fs, rs, i):
        targetF = fs[i]
        
        if 0 == i:
            newFeatures = [fs[-1], targetF, fs[1]]
        elif i == len(fs) - 1:
            newFeatures = [fs[i - 1], targetF, fs[0]]
        else:
            newFeatures = [fs[i - 1], targetF, fs[i + 1]]

        return np.array(newFeatures, dtype=np.float32), np.array([0, rs[i], 0], dtype=np.float32)
    
    def __NDTo2DFeatures(self, features):
        numD = features.shape[1] - 1
        if numD == 1:
            return features
        
        projectionLineUnitVector = np.zeros(numD, dtype=np.float32)
        multiplierUnitVector = np.zeros(numD, dtype=np.float32)

        projectionLineUnitVector[1] = 1

        for i in range(numD - 1):
            multiplierUnitVector *= 0
            multiplierUnitVector[i + 1] = 1

            projectionLineUnitVector = projectionLineUnitVector + multiplierUnitVector
            projectionLineUnitVector /= np.linalg.norm(projectionLineUnitVector)
        
        projectedFeatures = []
        divisor = np.dot(projectionLineUnitVector, projectionLineUnitVector)
        i = 0
        for feature in features:
            dst = (np.dot(projectionLineUnitVector, feature[1:]) / divisor) * projectionLineUnitVector
            projectedFeatures.append([features[i][0], np.linalg.norm(dst)])
            i += 1
        
        return np.array(projectedFeatures, dtype=np.float32)
