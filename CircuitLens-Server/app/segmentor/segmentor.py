import cv2
import numpy as np

img = cv2.imread('test_images/test_003.jpg')
g = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)

clean_img = cv2.adaptiveThreshold(g,255,cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY_INV, 3,3)

dst = cv2.cornerHarris(edges,2,3,0.04)
new = np.zeros(g.shape, np.uint8)
new[dst>0.01*dst.max()]=255

