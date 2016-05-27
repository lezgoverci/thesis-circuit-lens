class Port:
    def __init__(self, location=None):
        self.__location = location
        self.__node = None
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------
    
    def setLocation(self, location):
        self.__location = location
        return self
        
    def connect(self, node):
        self.__node = node
        return self
    
    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    def getLocation(self):
        return self.__location
    
    def getNode(self):
        return self.__node