from abc import ABCMeta, abstractmethod
import common.class_ports as p

class CircuitElement(object):
    __metaclass__ = ABCMeta
    
    def __init__(self, value, numPorts):
        self._resistance = 0
        self._capacitance = 0
        self._inductance = 0
        self._voltage = 0
        self._current = 0
        self._frequency = 0
        
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

    def getFrequency(self):
        return self._frequency
    
    def getPort(self, portNum):
        return self._ports.getData(portNum)
    
    def getIterator(self):
        return self._ports.getIterator()
    
    @abstractmethod
    def getMainProperty(self):
        pass
    
    @abstractmethod
    def getDumpType(self):
        pass
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    def dump(self):
        self._dumpables = [self.getDumpType()]
        
        ports_iterator = self._ports.getIterator()
        ports_iterator.reset()
        
        while ports_iterator.valid():
            location = ports_iterator.getData().getNode().getLocation()
            
            if location:
                self._dumpables.append(str(location[0]))
                self._dumpables.append(str(location[1]))
            
            ports_iterator.next()
        
        self._dumpables.append(str(self._frequency))
        
        self._setAdditionalDumpables()
        
        return ' '.join(self._dumpables)