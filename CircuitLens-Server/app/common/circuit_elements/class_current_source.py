import class_circuit_element as ce

class CurrentSource(ce.CircuitElement):
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
    
    def _setAdditionalDumpables(self):
        self._dumpables += ['0', '0', str(self.getMainProperty()), '0', '0', '0']
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    def getMainProperty(self):
        return self._current
    
    def getDumpType(self):
        return "i"
    
    