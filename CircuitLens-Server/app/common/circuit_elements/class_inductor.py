from class_circuit_element import CircuitElement

class Inductor(CircuitElement):
    def __init__(self, value=0):
        super(Inductor, self).__init__(value, 2)
        self._frequency = 0
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------
    
    def setMainProperty(self, value):
        self._inductance = value
        return self
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    def getMainProperty(self):
        return self._inductance
    
    def getDumpType(self):
        return "l"
    