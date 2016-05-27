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
    
    def connect(self, connections):
        if not connections:
            return
        
        for node, connectibles in connections.iteritems():
            for portNum, circuitElement in connectibles:
                node.connect(portNum, circuitElement)
        
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
            
            ports_iterator = current_node.getIterator()
            ports_iterator.reset()
            
            while ports_iterator.valid():
                current_node = ports_iterator.getData().getNode()
                
                if current_node:
                    ce_iterator = current_node.getIterator()
                    ce_iterator.reset()
                    
                    while ce_iterator.valid():
                        _, ce = ce_iterator.getData()
                        
                        try:
                            ce.visited
                        except AttributeError:
                            ce.visited = True
                            if not isinstance(ce, nce.NullCircuitElement):
                                queue.enqueue(ce)
                        
                        ce_iterator.next()
                
                ports_iterator.next()
        
        return '\n'.join(netlist)
        
        