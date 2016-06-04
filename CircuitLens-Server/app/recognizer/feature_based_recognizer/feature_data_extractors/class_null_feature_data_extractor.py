import class_feature_processable_data_extractor as fpde

class NullFeatureDataExtractor(fpde.FeatureProcessableDataExtractor):
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------

    def setArguments(self, args):
        return self
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------

    def getExtractedData(self, reextract=False):
        return []
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    def extract(self):
        return self
    
    def argumentsMet(self):
        return True