import class_circuit_element as ce

class Capacitor(ce.CircuitElement):
    def __init__(self, value=0):
        super(Capacitor, self).__init__(value, 2)
        self._frequency = 0
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------
    
    def setMainProperty(self, value):
        self._capacitance = value
        return self
    
    def _setAdditionalDumpables(self):
        self._dumpables += [str(self.getMainProperty()), '0']
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    def getMainProperty(self):
        return self._capacitance
    
    def getDumpType(self):
        return "c"
    