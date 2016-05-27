from app.comon.circuit_elements.class_circuit_element import CircuitElement
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
    
    def generateNetlist(self):
        if CircuitElement != type(self.__root):
            return ''
        
        queue = Queue()
        queue.enqueue(self.__root)
        
        netlist = ''
        
        while not queue.isEmpty():
            current_node = queue.dequeue()
            current_node.visited = True
            netlist += current_node.dump()
            
            iterator = current_node.getIterator()
            iterator.reset()
            
            while iterator.valid():
                neighbour = iterator.getData()
                
                try:
                    neighbour.visited
                except AttributeError:
                    queue.enqueue(neighbour)
                
                iterator.next()
        
        return netlist
        
        