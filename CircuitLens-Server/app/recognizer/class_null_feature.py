import class_feature as f

class NullFeature(f.Feature):
    #-----------------------------------------
    # Setters
    #-----------------------------------------

    def setArguments(self, args):
        pass

    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    def getCalculatedFeature(self, recalculate=False):
        return []
    
    def getNeededFeatureDataExtractors(self):
        return []
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    def calculate(self):
        return self

    def argumentsMet(self):
        return True