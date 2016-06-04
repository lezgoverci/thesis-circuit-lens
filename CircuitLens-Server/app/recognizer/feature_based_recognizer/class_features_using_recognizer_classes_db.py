import class_classes_database as cdb
import class_features_using_recognizer as fur
import class_fur_absolute_magnitude_difference_solver as famd
import class_fur_feature_difference_solver as ffds
import numpy as np
import os
import json

class FeaturesUsingRecognizerClassesDB(cdb.ClassesDatabase):
    def __init__(self):
        self.__file = os.path.dirname(os.path.realpath(__file__)) + '/classes_db.txt'
        super(FeaturesUsingRecognizerClassesDB, self).__init__()
        self.__featuresDistanceSolver = famd.FURAbsoluteMagnitudeDifferenceSolver()
        # self.__featuresDistanceSolver = ffds.FURFeatureDifferenceSolver()
        
        self.load()
        
    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    def match(self, calculatedFeature):
        if not self._classesValuesMap:
            return None

        maxClass = None
        maxMatchPercentage = 0
        
        for className, storedFeature in self._classesValuesMap.iteritems():
            d = self.__featuresDistanceSolver.solve(storedFeature, calculatedFeature)
            
            if maxMatchPercentage < d:
                maxMatchPercentage = d
                maxClass = className
        
        if 'voltage_source' in maxClass:
            maxClass = 'voltage_source'
        
        return maxClass, maxMatchPercentage
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------
    
    def train(self, classesImagesMap):
        recognizer = fur.FeaturesUsingRecognizer()
        
        for classStr, img in classesImagesMap.iteritems():
            self._classesValuesMap[classStr] = recognizer.setImage(img).getCalculatedFeature(True)
        
        try:
            self.__writeToFile()
        except IOError as e:
            print e
        
        return self
    
    def __print(self, label, feature):
        print 'label: ' + label
        print 'raw value: \n' + str(feature)
        print 'magnitude: ' + str(np.linalg.norm(feature))
        print '---------------------------------------'
    
    def load(self):
        try:
            self.__loadFromFile()
        except IOError:
            self._classesValuesMap = {}
    
    def __writeToFile(self):
        if self._classesValuesMap is None:
            return
        
        fileHolder = open(self.__file, 'w')
        
        for className, calculatedFeatures in self._classesValuesMap.iteritems():
            fileHolder.write(className + '=')
            
            strBuffer = []

            for featureX, featureY, featureZ in calculatedFeatures:
                strBuffer.append("%s,%s,%s" % (str(featureX), str(featureY), str(featureZ)))
            
            fileHolder.write('|'.join(strBuffer) + "\n")
        
        fileHolder.close()
    
    def __loadFromFile(self):
        if not os.path.isfile(self.__file):
            raise IOError
        
        fileHolder = open(self.__file, 'r')
        
        self._classesValuesMap = {}
        
        for line in fileHolder:
            line = line.rstrip("\n")
            className, strCalculatedFeatures = line.split('=')
            
            calculatedFeatures = np.array([np.array([np.float(x) for x in features.split(',')]) for features in strCalculatedFeatures.split('|')])

            self._classesValuesMap[className] = calculatedFeatures
        
        fileHolder.close()

instance = FeaturesUsingRecognizerClassesDB()