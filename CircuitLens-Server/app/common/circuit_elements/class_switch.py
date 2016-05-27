from class_circuit_element import CircuitElement

class Switch(CircuitElement):
    def __init__(self, value=False):
        self._state = False
        super(Resistor, self).__init__(value, 2)
        self._frequency = 0
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------
    
    def setMainProperty(self, value):
        self._state = value
        return self
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    def getMainProperty(self):
        return self._state
    
    def getDumpType(self):
        return "s"
    
    