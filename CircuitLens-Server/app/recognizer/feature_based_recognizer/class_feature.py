from abc import ABCMeta, abstractmethod

class Feature:
    #-----------------------------------------
    # Setters
    #-----------------------------------------
    
    @abstractmethod
    def setArguments(self, args):
        pass
    
    @abstractmethod
    def setFeatureDataExtractors(self, featureDataExtractors):
        pass
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    @abstractmethod
    def getCalculatedFeature(self, recalculate=False):
        pass
    
    @abstractmethod
    def getNeededFeatureDataExtractors(self):
        pass
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------
    
    @abstractmethod
    def calculate(self):
        pass
    
    @abstractmethod
    def argumentsMet(self):
        pass