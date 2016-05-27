from abc import ABCMeta, abstractmethod
import class_ports as p
import common.class_circuit_element_iterator as cei

class CircuitElement(object):
    __metaclass__ = ABCMeta
    
    def __init__(self, value, numPorts):
        self._resistance = 0
        self._capacitance = 0
        self._inductance = 0
        self._voltage = 0
        self._current = 0
        self._frequency = 0
        
        self._fromPoint = (0, 0)
        self._ToPoint = (0, 0)
        
        self._ports = p.Ports(numPorts)
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
    
    def setBoundaries(self, fromPoint, toPoint):
        self.setFromPoint(fromPoint)
        self.setToPoint(toPoint)
        return self
    
    @abstractmethod
    def setMainProperty(self, value):
        pass
    
    def _setAdditionalDumpables(self):
        self._dumpables.append(str(self.getMainProperty()))
    
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
        return cei.CircuitElementIterator(self._ports)
    
    def getFrequency(self):
        return self._frequency
    
    def getFromPoint(self):
        return self._fromPoint
    
    def getToPoint(self):
        return self._toPoint
    
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
        self._dumpables = [
                        self.getDumpType(),
                        str(self._fromPoint[0]), str(self._fromPoint[1]),
                        str(self._toPoint[0]), str(self._toPoint[1]),
                        str(self._frequency)
                    ]
        
        self._setAdditionalDumpables()
        
        return ' '.join(self._dumpables)