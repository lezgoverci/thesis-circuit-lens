import sys
import os

root_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

if root_dir not in sys.path:
    sys.path.append(root_dir)

import recognizer.class_recognizer_factory as rf
import cv2
import common.class_basic_functions as bf
import unittest
import numpy as np

class RecognizerTest(unittest.TestCase):
    @classmethod
    def setUpClass(self):
        self.__imagesDir = 'D:\\Thesis\\Recognizer\\training_set\\'
        self.__recognizer = rf.RecognizerFactory.create('ml_quadratic')
        
        # For training only
        classesImagesMap = {
            'inductor': self.__imagesDir + 'symbol_inductor.png',
            'resistor': self.__imagesDir + 'symbol_resistor.png',
            'diode': self.__imagesDir + 'symbol_diode.png',
            'capacitor': self.__imagesDir + 'symbol_capacitor.png',
            'voltage_source': self.__imagesDir + 'symbol_voltage_source.png',
            'voltage_source_2': self.__imagesDir + 'symbol_voltage_source2.png',
            'ground': self.__imagesDir + 'ground.png'
        }
        
        for className, imgName in classesImagesMap.iteritems():
            gray = bf.BasicFunctions.loadImage(imgName)
            classesImagesMap[className] = gray

        self.__recognizer.train(classesImagesMap)
    
    # def test_always_passes(self):
    #     self.assertTrue(True)

    def test_curved_capacitor_recognized(self):
        expectedClass = 'capacitor'
        queryImgName = 'capacitor'
        queryImg = bf.BasicFunctions.loadImage(self.__imagesDir + queryImgName + '.png')

        calculatedClass = self.__recognizer.setImage(queryImg).getClass(True)

        self.assertEqual(expectedClass, calculatedClass)
    
    def test_straight_capacitor_recognized(self):
        expectedClass = 'capacitor'
        queryImgName = 'capacitor2'
        queryImg = bf.BasicFunctions.loadImage(self.__imagesDir + queryImgName + '.png')

        calculatedClass = self.__recognizer.setImage(queryImg).getClass(True)

        self.assertEqual(expectedClass, calculatedClass)
    
    def test_small_capacitor_recognized(self):
        expectedClass = 'capacitor'
        queryImgName = 'symbol_capacitor_small'
        queryImg = bf.BasicFunctions.loadImage(self.__imagesDir + queryImgName + '.png')

        calculatedClass = self.__recognizer.setImage(queryImg).getClass(True)

        self.assertEqual(expectedClass, calculatedClass)
    
    def test_tilted_thin_diode_recognized(self):
        expectedClass = 'diode'
        queryImgName = 'diode2'
        queryImg = bf.BasicFunctions.loadImage(self.__imagesDir + queryImgName + '.jpg')

        calculatedClass = self.__recognizer.setImage(queryImg).getClass(True)

        self.assertEqual(expectedClass, calculatedClass)
    
    def test_tilted_thick_diode_recognized(self):
        expectedClass = 'diode'
        queryImgName = 'symbol_diode2'
        queryImg = bf.BasicFunctions.loadImage(self.__imagesDir + queryImgName + '.png')

        calculatedClass = self.__recognizer.setImage(queryImg).getClass(True)

        self.assertEqual(expectedClass, calculatedClass)
        
    def test_upside_down_inductor_recognized(self):
        expectedClass = 'inductor'
        queryImgName = 'inductor'
        queryImg = bf.BasicFunctions.loadImage(self.__imagesDir + queryImgName + '.png')

        calculatedClass = self.__recognizer.setImage(queryImg).getClass(True)

        self.assertEqual(expectedClass, calculatedClass)
   
    def test_tilted_inductor_recognized(self):
        expectedClass = 'inductor'
        queryImgName = 'inductor2'
        queryImg = bf.BasicFunctions.loadImage(self.__imagesDir + queryImgName + '.png')

        calculatedClass = self.__recognizer.setImage(queryImg).getClass(True)

        self.assertEqual(expectedClass, calculatedClass)
    
    def test_thin_small_resistor_recognized(self):
        expectedClass = 'resistor'
        queryImgName = 'resistor'
        queryImg = bf.BasicFunctions.loadImage(self.__imagesDir + queryImgName + '.jpg')

        calculatedClass = self.__recognizer.setImage(queryImg).getClass(True)

        self.assertEqual(expectedClass, calculatedClass)
    
    def test_thin_large_resistor_recognized(self):
        expectedClass = 'resistor'
        queryImgName = 'resistor'
        queryImg = bf.BasicFunctions.loadImage(self.__imagesDir + queryImgName + '.png')

        calculatedClass = self.__recognizer.setImage(queryImg).getClass(True)

        self.assertEqual(expectedClass, calculatedClass)
        
    def test_thick_medium_tilted_resistor_recognized(self):
        expectedClass = 'resistor'
        queryImgName = 'resistor2'
        queryImg = bf.BasicFunctions.loadImage(self.__imagesDir + queryImgName + '.png')

        calculatedClass = self.__recognizer.setImage(queryImg).getClass(True)

        self.assertEqual(expectedClass, calculatedClass)
    
    def test_capacitor_like_voltage_source_recognized(self):
        expectedClass = 'voltage_source'
        queryImgName = 'symbol_voltage_source3'
        queryImg = bf.BasicFunctions.loadImage(self.__imagesDir + queryImgName + '.png')

        calculatedClass = self.__recognizer.setImage(queryImg).getClass(True)

        self.assertEqual(expectedClass, calculatedClass)

def runtests():
    unittest.main()

runtests()