import sys, unittest
from os import path

sys.path.append( path.dirname( path.dirname( path.abspath(__file__) ) ) )

import unittest
from common.class_circuit import Circuit
from common.circuit_elements.class_circuit_element_factory import CircuitElementFactory
import common.class_node as n

class NetlistGenerationTest(unittest.TestCase):
    def setUp(self):
        self.expectedNetlist = [
            'v 50 50 50 90 0 0 0 5 0 0 0',
            's 50 50 120 50 0 1 false',
            'w 120 50 140 50 0',
            'r 120 50 120 90 0 10',
            'c 140 50 140 90 0 1.5e-05 0',
            'w 120 90 140 90 0',
            'w 50 90 120 90 0'
        ]
        
        # Setup elements
        r = CircuitElementFactory.create('resistor', 10)
        r.getPort(0).setLocation((120, 50))
        r.getPort(1).setLocation((120, 90))

        s = CircuitElementFactory.create('toggle_switch', 1)
        s.getPort(0).setLocation((50, 50))
        s.getPort(1).setLocation((120, 50))
        
        v = CircuitElementFactory.create('voltage_source', 5)
        v.getPort(0).setLocation((50, 50))
        v.getPort(1).setLocation((50, 90))
        
        c = CircuitElementFactory.create('capacitor', 0.000015)
        c.getPort(0).setLocation((140, 50))
        c.getPort(1).setLocation((140, 90))
        
        w1 = CircuitElementFactory.create('wire', 0)
        w1.getPort(0).setLocation((120, 50))
        w1.getPort(1).setLocation((140, 50))
        
        w2 = CircuitElementFactory.create('wire', 0)
        w2.getPort(0).setLocation((50, 90))
        w2.getPort(1).setLocation((120, 90))
        
        w3 = CircuitElementFactory.create('wire', 0)
        w3.getPort(0).setLocation((120, 90))
        w3.getPort(1).setLocation((140, 90))
        
        # Connect Elements
        connections = {
            n.Node(): [
                (0, v),
                (0, s)  
            ],
            n.Node(): [
                (1, s),
                (0, r),
                (0, w1)
            ],
            n.Node(): [
                (1, w1),
                (0, c)
            ],
            n.Node(): [
                (1, v),
                (0, w2)
            ],
            n.Node(): [
                (1, w2),
                (1, r),
                (0, w3)
            ],
            n.Node(): [
                (1, w3),
                (1, c)
            ]
        }
        
        self.circuit = Circuit(v)
        self.circuit.connect(connections)
    
    def test_netlist_correctness(self):
        n = self.circuit.generateNetlist().split('\n')
        
        equal = True
        
        if len(self.expectedNetlist) != len(n):
            equal = False
        
        if equal:
            for e in n:
                if e not in self.expectedNetlist:
                    equal = False
                    break
        
        self.assertTrue(equal)

if __name__ == '__main__':
    unittest.main()