import sys, unittest
from os import path

sys.path.append( path.dirname( path.dirname( path.abspath(__file__) ) ) )

import unittest
from common.class_circuit import Circuit
from common.circuit_elements.class_circuit_element_factory import CircuitElementFactory

class NetlistGenerationTest(unittest.TestCase):
    def setUp(self):
        netlist = [
            'v 50 50 50 90 0 0 0 5 0 0 0',
            's 50 50 120 50 0 1 false',
            'w 120 50 140 50 0',
            'r 120 50 120 90 0 10',
            'c 140 50 140 90 0.000015 0',
            'w 120 90 140 90 0',
            'w 50 90 120 90 0'
        ]
        
        self.expectedNetlist = '\n'.join(netlist)
        
        # Setup elements
        
        r = CircuitElementFactory.create('resistor', 10)
        r.setBoundaries((120, 50), (120, 90))
        
        s = CircuitElementFactory.create('toggle_switch', 1)
        s.setBoundaries((70, 50), (100, 50))
        
        v = CircuitElementFactory.create('voltage_source', 5)
        v.setBoundaries((50, 50), (50, 90))
        
        c = CircuitElementFactory.create('capacitor', 0.000015)
        c.setBoundaries((140, 50), (140, 90))
        
        w1 = CircuitElementFactory.create('wire', 0)
        w1.setBoundaries((120, 50), (140, 50))
        
        w2 = CircuitElementFactory.create('wire', 0)
        w2.setBoundaries((50, 90), (120, 90))
        
        w3 = CircuitElementFactory.create('wire', 0)
        w3.setBoundaries((120, 90), (140, 90))
        
        # Connect Elements
        
        self.circuit = Circuit(v)
        
        self.circuit.connect(v, 0, s, 0)
        self.circuit.connect(v, 1, w2, 0)
        
        self.circuit.connect(s, 1, w1, 0)
        self.circuit.connect(s, 1, r, 0)
        
        self.circuit.connect(w1, 1, c, 0)
        self.circuit.connect(r, 1, w2, 1)

        self.circuit.connect(c, 1, w3, 1)
        self.circuit.connect(r, 1, w3, 0)
    
    def test_netlist_correctness(self):
        n = self.circuit.generateNetlist()
        
        self.assertEqual(self.expectedNetlist, n)
        
        print "netlist: " + n

if __name__ == '__main__':
    unittest.main()