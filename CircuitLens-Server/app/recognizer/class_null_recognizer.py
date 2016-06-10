class Recognizer:

    #-----------------------------------------
    # Setters
    #-----------------------------------------

    def setImage(self, img):
        return self
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------

    def getClass(self, recalculate):
        return None
    
    def getMatchPercentage(self, recalculate=False):
        return 0
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    def recognize(self):
        return self
    
    def train(self, classesImagesMap):
        return self