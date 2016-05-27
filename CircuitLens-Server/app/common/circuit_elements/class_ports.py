import common.class_iterable as i
import class_circuit_element_factory as cef

class Ports(i.Iterable):
    def __init__(self, size):
        self.__size = size
        self.__circuitElements = {}
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    def getData(self, portNum):
        if not self.accessible(portNum):
            raise LookupError
        
        return self.__circuitElements.get(portNum, cef.CircuitElementFactory.create('null'))
    
    def size(self):
        return self.__size
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------
    
    def connect(self, portNum, circuitElement):
        if not self.accessible(portNum):
            raise LookupError
        
        self.__circuitElements[portNum] = circuitElement
        return self
    
    def accessible(self, portNum):
        return portNum >= 0 and portNum < self.__size