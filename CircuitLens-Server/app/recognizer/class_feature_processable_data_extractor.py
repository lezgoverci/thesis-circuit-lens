from abc import ABCMeta, abstracmethod

class FeatureProcessableDataExtractor:
    #-----------------------------------------
    # Setters
    #-----------------------------------------
    
    @abstracmethod
    def setArguments(self, args):
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