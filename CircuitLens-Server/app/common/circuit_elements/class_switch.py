import class_circuit_element as ce

class Switch(ce.CircuitElement):
    def __init__(self, value=0):
        self._state = 0
        super(Switch, self).__init__(value, 2)
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
    
    