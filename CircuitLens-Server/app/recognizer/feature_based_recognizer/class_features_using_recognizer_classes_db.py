import class_classes_database as cdb
import class_feature_using_recognizer as fur
import class_fur_absolute_magnitude_difference_solver as famd

class FeaturesUsingRecognizerClassesDB(cdb.ClassesDatabase):
    def __init__(self):
        self.__recognizer = fur.FeatureUsingRecognizer()
        self.__featuresDistanceSolver = famd.FURAbsoluteMagnitudeDifferenceSolver()
        
    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    def match(self, calculatedFeature):
        if not self._classes_values_map:
            return None
        
        minClass = None
        minMatchPercentage = float('inf')
        
        for className, storedFeature in self._classes_values_map.iteritems():
            d = self.__featuresDistanceSolver.solve(storedFeature, calculatedFeature)
            
            if minMatchPercentage > d:
                minMatchPercentage = d
                minClass = className
        
        return minClass, minMatchPercentage
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------
    
    def train(self, classes_images_map):
        if not classes_images_map:
            return self

        for classStr, img in classes_images_map.iteritems():
            self._classes_values_map[classStr] = self.__recognizer.setImage(img).getCalculatedFeature()
        
        return self