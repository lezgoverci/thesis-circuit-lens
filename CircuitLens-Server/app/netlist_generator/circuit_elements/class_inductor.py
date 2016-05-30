import class_circuit_element as ce

class Inductor(ce.CircuitElement):
    def __init__(self, value=0):
        super(Inductor, self).__init__(value, 2)
        self._frequency = 0
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------
    
    def setMainProperty(self, value):
        self._inductance = value
        return self
    
    def _setAdditionalDumpables(self):
        self._dumpables += [str(self.getMainProperty()), '0']
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    def getMainProperty(self):
        return self._inductance
    
    def getDumpType(self):
        return "l"
    