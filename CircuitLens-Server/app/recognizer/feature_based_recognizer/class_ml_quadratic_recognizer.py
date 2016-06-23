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

        args = {
            'type': ('multivariate_classification', {
                            'fs_and_rs_selector': lambda fs, rs, i: self.__selectFeaturesAndResponses(fs, rs, i)
            }),
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

        #TEMPORARY
        print features

        features = self.__NDTo2DFeatures(np.array([features], dtype=np.float32))
        self.__class = self.__responsesClassesMap[self.__machine.predict(np.array(features[0], dtype=np.float32))]

        return self
    
    def train(self, classesImageMap):
        counter = 1
        featuresSet = []

        for className, imgs in classesImageMap.iteritems():
            if self.__responsesClassesMap.get(className, None) is None:
                self.__responsesClassesMap[className] = counter
                self.__responsesClassesMap[counter] = className
                counter += 1

            features = [self.__responsesClassesMap[className]]

            featureAccumulator = None
            accCounter = 0
            for img in imgs:
                currentFeatures = np.array(self.__extractFeatures(img), dtype=np.float32)
                if featureAccumulator is None:
                    featureAccumulator = np.zeros(currentFeatures.shape[0], dtype=np.float32)
                
                featureAccumulator += currentFeatures
                accCounter += 1
            
            featureAccumulator /= counter
            features.extend(list(featureAccumulator))
            featuresSet.append(features)

            #TEMPORARY
            print "===================================="
            print className + " details:"
            print "(%lf, %lf)" % (features[0], features[1])

        twoDFeatureSet = self.__NDTo2DFeatures(np.array(featuresSet, dtype=np.float32))
        twoDFeatureSet = sorted(twoDFeatureSet, key = lambda f: f[1])

        responses = []
        for i in range(len(twoDFeatureSet)):
            responses.append(twoDFeatureSet[i][0])
            twoDFeatureSet[i][0] = 1
        
        #TEMPORARY
        print self.__responsesClassesMap

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
                            'm': m
                        }
                    }]
        
        self.__featuresCalculator.setFeatures(features)
        return self.__featuresCalculator.get(recalculate)
    
    def __selectFeaturesAndResponses(self, fs, rs, i):
        if 0 == i:
            diff = (fs[1] - fs[0]) / 2
            newFeatures = [fs[0] - diff, fs[0], fs[0] + diff]
            r = [0, rs[i], 0]
        elif i == len(fs) - 1:
            diff = (fs[i] - fs[i-1]) / 2
            newFeatures = [fs[i] - diff, fs[i], fs[i] + diff]
            r = [0, rs[i], 0]
        else:
            newFeatures = [(fs[i] + fs[i - 1]) / 2, fs[i], (fs[i] + fs[i + 1]) / 2]
            r = [0, rs[i], 0]

        return np.array(newFeatures, dtype=np.float32), np.array(r, dtype=np.float32)
    
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
