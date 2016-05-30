from abc import ABCMeta, abstracmethod

class FeatureProcessableDataExtractor:
    #-----------------------------------------
    # Setters
    #-----------------------------------------
    
    @abstracmethod
    def setArguments(self, args):
        pass
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    @abstracmethod
    def getExtractedData(self, reextract=False):
        pass
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------
    
    @abstracmethod
    def extract(self):
        pass
    
    @abstracmethod
    def argumentsMet(self):
        pass