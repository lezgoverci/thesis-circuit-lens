from class_circuit_element import CircuitElement

class NullCircuitElement(CircuitElement):
    def __init__(self, value=0):
        super(NullCircuitElement, self).__init__(value, 2)
        self._frequency = 0
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------
    
    def setMainProperty(self, value):
        return self
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    def getMainProperty(self):
        return 0
    
    def getDumpType(self):
        return ""
    
    def dump(self):
        return ""
    