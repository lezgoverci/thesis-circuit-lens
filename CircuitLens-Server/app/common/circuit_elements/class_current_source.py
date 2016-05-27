from class_circuit_element import CircuitElement

class CurrentSource(CircuitElement):
    def __init__(self, value=0, frequency=0):
        super(CurrentSource, self).__init__(value, 2)
        self._frequency = frequency
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------
    
    def setMainProperty(self, value):
        self._current = value
        return self
    
    def setFrequency(self, frequency):
        self._frequency = frequency
        return self
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    def getMainProperty(self):
        return self._current
    
    def getDumpType(self):
        return "i"
    
    