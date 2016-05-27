from class_circuit_element import CircuitElement

class Resistor(CircuitElement):
    def __init__(self, value=0):
        super(Resistor, self).__init__(value, 2)
        self._frequency = 0
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------
    
    def setMainProperty(self, value):
        self._resistance = value
        return self
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    def getMainProperty(self):
        return self._resistance
    
    def getDumpType(self):
        return "r"
    
    