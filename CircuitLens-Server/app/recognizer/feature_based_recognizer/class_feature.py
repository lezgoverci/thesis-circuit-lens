from abc import ABCMeta, abstracmethod

class Feature:
    #-----------------------------------------
    # Setters
    #-----------------------------------------
    
    @abstracmethod
    def setArguments(self, args):
        pass
    
    @abstracmethod
    def setFeatureDataExtractors(self, featureDataExtractors):
        pass
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    @abstracmethod
    def getCalculatedFeature(self, recalculate=False):
        pass
    
    @abstracmethod
    def getNeededFeatureDataExtractors(self):
        pass
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------
    
    @abstracmethod
    def calculate(self):
        pass
    
    @abstracmethod
    def argumentsMet(self):
        pass