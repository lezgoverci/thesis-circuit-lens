import class_classes_database as cdb
import class_features_using_recognizer as fur
import class_fur_absolute_magnitude_difference_solver as famd
import class_fur_feature_difference_solver as ffds
import numpy as np

class FeaturesUsingRecognizerClassesDB(cdb.ClassesDatabase):
    def __init__(self):
        super(FeaturesUsingRecognizerClassesDB, self).__init__()
        # self.__featuresDistanceSolver = famd.FURAbsoluteMagnitudeDifferenceSolver()
        self.__featuresDistanceSolver = ffds.FURFeatureDifferenceSolver()
        
    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    def getScaleVectorWRTNearestRes(self, shape):
        if not self._classesValuesMap:
            return np.array([1, 1])
        
        maxRes = None
        maxMatchPercentage = 0.0

        for _, (_, res) in self._classesValuesMap.iteritems():
            d = self.__featuresDistanceSolver.solve(np.array(res), np.array(shape[:2]))
            
            if maxMatchPercentage < d:
                maxRes = res
                maxMatchPercentage = d
        
        scaleVector = np.array([1.0, 1.0])
        
        i = 0
        for dim in maxRes:
            if dim > res[i]:
                scaleVector[i] = dim / res[i]
            i += 1

        return scaleVector
        
    
    def match(self, calculatedFeature):
        if not self._classesValuesMap:
            return None

        minClass = None
        minMatchPercentage = float('inf')
        
        for className, (storedFeature, _) in self._classesValuesMap.iteritems():
            d = self.__featuresDistanceSolver.solve(storedFeature, calculatedFeature)
            
            if minMatchPercentage > d:
                minMatchPercentage = d
                minClass = className
        
        return minClass, minMatchPercentage
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------
    
    def train(self, classesImagesMap):
        recognizer = fur.FeaturesUsingRecognizer()
        
        for classStr, img in classesImagesMap.iteritems():
            self._classesValuesMap[classStr] = (recognizer.setImage(img).getCalculatedFeature(True), img.shape)
        
        return self

instance = FeaturesUsingRecognizerClassesDB()