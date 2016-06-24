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

        features = self.__NDTo2DFeatures(np.array(self.__extractFeatures(self.__img), dtype=np.float32))
        self.__class = self.__responsesClassesMap[self.__machine.predict(features)]

        return self
    
    def train(self, classesImageMap):
        counter = 1
        featuresSet = []

        for className, imgs in classesImageMap.iteritems():
            if self.__responsesClassesMap.get(className, None) is None:
                self.__responsesClassesMap[className] = counter
                self.__responsesClassesMap[counter] = className
                counter += 1

            extractedFeatures = [self.__NDTo2DFeatures(np.array(self.__extractFeatures(img), dtype=np.float32),
                                                      self.__responsesClassesMap[className]) for img in imgs]

            minVal = min(extractedFeatures, key = lambda x: x[1])
            maxVal = max(extractedFeatures, key = lambda x: x[1])

            featuresSet.append({
                'ave': (minVal + maxVal) / 2,
                'min': minVal,
                'max': maxVal
            })

        twoDFeatureSet = sorted(featuresSet, key = lambda x: x['ave'][1])

        responses = []
        for i in range(len(twoDFeatureSet)):
            responses.append(twoDFeatureSet[i]['ave'][0])
            twoDFeatureSet[i]['ave'][0] = 1
            twoDFeatureSet[i]['min'][0] = 1
            twoDFeatureSet[i]['max'][0] = 1

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
            curMax = (fs[0]['max'] + fs[1]['min']) / 2
            curMax = curMax if curMax[1] >= fs[0]['ave'][1] else fs[0]['max']

            newFeatures = [fs[0]['min'], (fs[0]['min'] + curMax) / 2, curMax]
            r = [0, rs[i], 0]
        elif i == len(fs) - 1:
            curMin = (fs[i-1]['max'] + fs[i]['min']) / 2
            curMin = curMin if curMin[1] <= fs[i]['ave'][1] else fs[i]['min']

            newFeatures = [curMin, (fs[i]['max'] + curMin) / 2, fs[i]['max']]
            r = [0, rs[i], 0]
        else:
            curMin = (fs[i-1]['max'] + fs[i]['min']) / 2
            curMin = curMin if curMin[1] <= fs[i]['ave'][1] else fs[i]['min']

            curMax = (fs[i+1]['min'] + fs[i]['max']) / 2
            curMax = curMax if curMax[1] >= fs[i]['ave'][1] else fs[i]['max']

            newFeatures = [curMin, (curMin + curMax) / 2, curMax]
            
            r = [0, rs[i], 0]

        return np.array(newFeatures, dtype=np.float32), np.array(r, dtype=np.float32)
    
    def __NDTo2DFeatures(self, features, firstDimVal=1):
        numD = features.shape[0]
        if numD == 1:
            return np.array([firstDimVal, features[0]], dtype=np.float32)
        
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

        dst = (np.dot(projectionLineUnitVector, features) / divisor) * projectionLineUnitVector
        return np.array([firstDimVal, np.linalg.norm(dst)], dtype=np.float32)
