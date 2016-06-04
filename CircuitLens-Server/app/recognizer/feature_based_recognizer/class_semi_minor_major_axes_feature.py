import class_feature as f
import numpy as np
import class_feature_processable_data_extractor as fpde
import math

class SemiMinorMajorAxesFeature(f.Feature):
    def __init__(self):
        self.__arguments = None
        self.__neededArguments = ['moments']
        self.__neededFeatureDataExtractors = []
        self.__calculatedFeature = None
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------

    def setArguments(self, args):
        self.__arguments = args
        
        return self

    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    def getCalculatedFeature(self, recalculate=False):
        if self.__calculatedFeature is None or recalculate:
            self.calculate(True)
        
        return self.__calculatedFeature
    
    def getNeededFeatureDataExtractors(self):
        return self.__neededFeatureDataExtractors
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    def calculate(self, recalculate=False):
        if not self.argumentsMet():
            return None
        
        m = self.__arguments['moments']
        nu_sum = m['nu20'] + m['nu02']
        nu_diff = m['nu20'] - m['nu02']
        
        a = math.sqrt(0.5 * nu_sum + (math.sqrt((4 * math.pow(m['nu11'], 2)) - math.pow(nu_diff, 2))))
        b = math.sqrt(0.5 * nu_sum - (math.sqrt((4 * math.pow(m['nu11'], 2)) - math.pow(nu_diff, 2))))
        
        self.__calculatedFeature = np.array([a, b, 0.0])
        
        return self

    def argumentsMet(self):
        import class_feature_processable_data_extractor as fpde
        
        return len(self.__arguments) > 0 and \
               all(neededArg in self.__arguments for neededArg in self.__neededArguments) and \
               all(isinstance(self.__arguments['feature_data_extractors'][featureDataExtractor], fpde.FeatureProcessableDataExtractor) \
                    for featureDataExtractor in self.__neededFeatureDataExtractors)