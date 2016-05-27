from class_queue import Queue

class Circuit:
    def __init__(self, root=None):
        self.__root = root
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------
    
    def setRoot(self, root):
        self.__root = root
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------
    
    def connect(self, circuitElementOne, portOne, circuitElementTwo, portTwo):
        #use the same coordinates here
        
        circuitElementOne.connectToElement(portOne, circuitElementTwo)
        circuitElementTwo.connectToElement(portTwo, circuitElementOne)
    
    def generateNetlist(self):
        import circuit_elements.class_circuit_element as ce
        import circuit_elements.class_null_circuit_element as nce
        
        if not isinstance(self.__root, ce.CircuitElement):
            return ''
        
        queue = Queue()
        queue.enqueue(self.__root)
        
        netlist = []
        
        while not queue.isEmpty():
            current_node = queue.dequeue()
            current_node.visited = True
            netlist.append(current_node.dump())
            
            iterator = current_node.getIterator()
            iterator.reset()
            
            while iterator.valid():
                neighbour = iterator.getData()
                
                try:
                    neighbour.visited
                except AttributeError:
                    neighbour.visited = True
                    if not isinstance(neighbour, nce.NullCircuitElement):
                        queue.enqueue(neighbour)
                
                iterator.next()
        
        return '\n'.join(netlist)
        
        