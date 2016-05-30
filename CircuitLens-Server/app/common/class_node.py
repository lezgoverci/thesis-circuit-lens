import class_iterable as i
import class_list_iterable_iterator as lii

class Node(i.Iterable):
    def __init__(self):
        self.__location = None
        self.__circuitElements = []
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    def getData(self, n):
        if not self.accessible(n):
            raise LookupError
        
        return self.__circuitElements[n]
    
    def getLocation(self, recalculate=False):
        if None == self.__location or recalculate:
            self.__calculateAverageLocation()
        
        return self.__location
    
    def size(self):
        return len(self.__circuitElements)
    
    def getIterator(self):
        return lii.ListIterableIterator(self)
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------
    
    def connect(self, portNum, circuitElement):
        self.__circuitElements.append((portNum, circuitElement))
        circuitElement.getPort(portNum).connect(self)
        return self
    
    def __calculateAverageLocation(self):
        if 0 == len(self.__circuitElements):
            return
        
        acc_x, acc_y, counter = 0, 0, 0
        
        for portNum, ce in self.__circuitElements:
            x, y = ce.getPort(portNum).getLocation()
            
            acc_x += x
            acc_y += y
            
            counter += 1
        
        self.__location = (int(acc_x/counter), int(acc_y/counter))
    
    def accessible(self, n):
        return n < len(self.__circuitElements) and n >= 0