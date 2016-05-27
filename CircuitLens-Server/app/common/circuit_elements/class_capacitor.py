from class_circuit_element import CircuitElement

class Capacitor(CircuitElement):
    def __init__(self, value=0):
        super(Capacitor, self).__init__(value, 2)
        self._frequency = 0
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------
    
    def setMainProperty(self, value):
        self._capacitance = value
        return self
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    def getMainProperty(self):
        return self._capacitance
    
    def getDumpType(self):
        return "c"
    