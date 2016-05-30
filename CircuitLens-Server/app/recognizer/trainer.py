from __future__ import division
import cv2
import numpy as np
import argparse
from memory import Memory

ap = argparse.ArgumentParser()
ap.add_argument("-i", "--image", required = True, help = "Path to the image")
ap.add_argument("-c", "--class", required = True, help = "Class of the training datum")

args = vars(ap.parse_args())

image = cv2.imread(args["image"])
training_class = args["class"]

gray_image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
inverse_gray_image = cv2.bitwise_not(gray_image)

training_datum = inverse_gray_image / 2550

memory_size = training_datum.shape[:1][0] * training_datum.shape[1:2][0]

training_datum =  training_datum.reshape((1, memory_size))

m = Memory(memory_size, 'mem_matrix.txt', 'classes.txt')
m.add(training_class, training_datum)
m.save('mem_matrix.txt', 'classes.txt')

