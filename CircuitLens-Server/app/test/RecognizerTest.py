import sys
import os

root_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

if root_dir not in sys.path:
    sys.path.append(root_dir)

import recognizer.class_recognizer_factory as rf
import cv2
import common.class_basic_functions as bf
import unittest

class RecognizerTest(unittest.TestCase):

    def test_feature_using_recognizer_recognition(self):
        imagesDir = 'D:\\Thesis\\Recognizer\\training_set\\'
        
        classesImagesMap = {
            'inductor': imagesDir + 'symbol_inductor.png',
            'resistor': imagesDir + 'symbol_resistor.png',
            'diode': imagesDir + 'symbol_diode.png',
            'capacitor': imagesDir + 'symbol_capacitor.png',
            'voltage_source': imagesDir + 'symbol_voltage_source.png',
        }
        
        for className, imgName in classesImagesMap.iteritems():
            gray = bf.BasicFunctions.loadImage(imgName)
            classesImagesMap[className] = gray
        
        recognizer = rf.RecognizerFactory.create('features_using')
        recognizer.train(classesImagesMap)
        
        queryImgName = 'resistor.png'
        queryImg = bf.BasicFunctions.loadImage(imagesDir + queryImgName)
        
        calculatedClass = recognizer.recognize('resistor').getClass()
        
        print "%s matched with %s! Match percentage is %lf." % (queryImgName, calculatedClass, recognizer.getMatchPercentage())
        
        self.assertEqual(calculatedClass, 'resistor')
        
def runtests():
    unittest.main()

runtests()