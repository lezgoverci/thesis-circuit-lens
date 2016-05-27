import class_iterator as i

class CircuitElementIterator(i.Iterator):
    def __init__(self, iterable=None):
        self.__iterable = iterable
        self.__currentIndex = 0
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------
    
    def setIterable(self, iterable):
        self.__iterable = iterable
    
    #-----------------------------------------
    # Getters
    #----------------------------------------- 
    
    def getData(self):
        return self.__iterable.getData(self.__currentIndex)
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------
    
    def next(self):
        self.__currentIndex += 1
    
    def prev(self):
        self.__currentIndex -= 1
    
    def reset(self):
        self.__currentIndex = 0
    
    def end(self):
        self.__currentIndex = self.__iterable.size() - 1
    
    def valid(self):
        return self.__iterable.accessible(self.__currentIndex)