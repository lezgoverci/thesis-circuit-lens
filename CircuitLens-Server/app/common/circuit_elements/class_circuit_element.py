from abc import ABCMeta, abstractmethod
from class_ports import Ports
from app.common.class_iterator import Iterator

class CircuitElement(object):
    __metaclass__ = ABCMeta
    
    def __init__(self, value, numPorts):
        self._resistance = 0
        self._capacitance = 0
        self._inductance = 0
        self._voltage = 0
        self._current: 0
        self._frequency = 0
        
        self._fromPoint = (0, 0)
        self._ToPoint = (0, 0)
        
        self._ports = Ports(numPorts)
        self.setMainProperty(value)
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------

    def setVoltage(self, voltage):
        self._voltage = voltage
        return self
    
    def setCurrent(self, current):
        self._current = current
        return self
    
    def setFromPoint(self, fromPoint):
        self._fromPoint = fromPoint
        return self
    
    def setToPoint(self, toPoint):
        self._toPoint = toPoint
        return self
    
    @abstractmethod
    def setMainProperty(self, value):
        pass
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    def getCircuitElementInPort(self, portNum):
        return self._ports(portNum)
    
    def getVoltage(self):
        return self._voltage
    
    def getCurrent(self):
        return self._current
    
    def getIterator(self):
        return Iterator(self._ports)
    
    def getFrequency(self):
        return self._frequency
    
    def getFromPoint(self):
        return self._fromPoint
    
    def getToPoint(self):
        return self._toPoint
    
    def getAdditionalDump(self):
        return ""
    
    @abstractmethod
    def getMainProperty(self):
        pass
    
    @abstractmethod
    def getDumpType(self):
        pass
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------
    
    def connectToElement(self, portNum, circuitElement):
        self._ports.connect(portNum, circuitElement)
        return self
    
    def dump(self):
        dumpables = [
                        self.getDumpType(),
                        str(self._fromPoint[0]), str(self._fromPoint[1]),
                        str(self._toPoint[0]), str(self._toPoint[1]),
                        str(self._frequency),
                        str(self.getMainProperty())
                    ]
        
        return " ".join(dumpables) + self.__getAdditionalDump()