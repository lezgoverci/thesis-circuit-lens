import common.class_iterable as i
import class_port as p
import common.class_list_iterable_iterator as lii

class Ports(i.Iterable):
    def __init__(self, size):
        self.__size = size
        self.__ports = []
        
        for i in range(size):
            self.__ports.append(p.Port())
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    def getData(self, portNum):
        if not self.accessible(portNum):
            raise LookupError
        
        return self.__ports[portNum]
    
    def getIterator(self):
        return lii.ListIterableIterator(self)
    
    def size(self):
        return self.__size
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------
    
    def accessible(self, portNum):
        return portNum >= 0 and portNum < self.__size