import sys
import os

root_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

if root_dir not in sys.path:
    sys.path.append(root_dir)

import unittest

import recognizer.ml.class_machine_factory as mf
import numpy as np

class MachineLearningTest(unittest.TestCase):
    def setUp(self):
        onedFeatures = [245, 312, 279, 308, 199, 219, 405, 324, 319, 255]
        self.__features = np.array([[1, x] for x in onedFeatures], dtype=np.float32)
        self.__responses = np.array([1400, 1600, 1700, 1875, 1100, 1550, 2350, 2450, 1425, 1700], dtype=np.float32)
        
        # onedFeatures = [1, 2]
        # self.__features = np.array([[1, x] for x in onedFeatures], dtype=np.float32)
        # self.__responses = np.array([1, 2], dtype=np.float32)
    
    def test_linear_regression_using_batch_gradient_descent(self):
        args = {
            'type': ('regression', None),
            'mathematical_model': ('linear', None),
            'minimizer': ('batch_gradient_descent', {
                'learning_rate': 0.00001,
                'iterations': 10
            })
        }

        machine = mf.MachineFactory.create('sl_machine', args)
        machine.train(self.__features, self.__responses)

        print machine.getThetas()

        assert(True)

    def test_linear_regression_using_normal_equation(self):
        args = {
            'type': ('regression', None),
            'mathematical_model': ('linear', None),
            'minimizer': ('normal_equation', None)
        }

        machine = mf.MachineFactory.create('sl_machine', args)
        machine.train(self.__features, self.__responses)

        print machine.getThetas()

        assert(True)

def runtests():
    unittest.main()

runtests()