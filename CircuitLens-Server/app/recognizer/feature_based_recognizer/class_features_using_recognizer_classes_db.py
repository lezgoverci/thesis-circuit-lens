import class_classes_database as cdb
import class_features_using_recognizer as fur
import class_fur_absolute_magnitude_difference_solver as famd

class FeaturesUsingRecognizerClassesDB(cdb.ClassesDatabase):
    def __init__(self):
        super(FeaturesUsingRecognizerClassesDB, self).__init__()
        self.__featuresDistanceSolver = famd.FURAbsoluteMagnitudeDifferenceSolver()
        
    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    def match(self, calculatedFeature):
        if not self._classesValuesMap:
            return None
        
        minClass = None
        minMatchPercentage = float('inf')
        
        for className, storedFeature in self._classesValuesMap.iteritems():
            d = self.__featuresDistanceSolver.solve(storedFeature, calculatedFeature)
            
            if minMatchPercentage > d:
                minMatchPercentage = d
                minClass = className
        
        return minClass, minMatchPercentage
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------
    
    def train(self, classesImagesMap):
        if not self._classesValuesMap:
            return self
        
        recognizer = fur.FeaturesUsingRecognizer()
        
        for classStr, img in self._classesValuesMap.iteritems():
            self._classesValuesMap[classStr] = recognizer.setImage(img).getCalculatedFeature(True)
        
        return self