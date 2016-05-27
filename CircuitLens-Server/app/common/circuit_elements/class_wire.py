import class_circuit_element as ce

class Wire(ce.CircuitElement):
    def __init__(self, value=0):
        super(Wire, self).__init__(value, 2)
        self._frequency = 0
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------
    
    def setMainProperty(self, value):
        self._resistance = value
        return self
    
    def _setAdditionalDumpables(self):
        pass
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    def getMainProperty(self):
        return self._resistance
    
    def getDumpType(self):
        return "w"
    