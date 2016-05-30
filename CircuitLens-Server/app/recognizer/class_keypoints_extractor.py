import class_feature_processable_data_extractor as fpde
import common.class_basic_functions as bf

class KeypointsExtractor(fpde.FeatureProcessableDataExtractor):

    def __init__(self):
        self.__arguments = None
        self.__needed_arguments = ['centroid', 'img']
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------

    def setArguments(self, args):
        self.__arguments = args
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    def extract(self):
        if not self.__arguments or not self.argumentsMet():
            return None
        
        centroid = self.__arguments['centroid']
        img = self.__arguments['img']

        img = np.float32(img)
        dst = cv2.cornerHarris(img,2,3,0.04)
        
        img = np.uint8(img)
        h, w = img.shape[:2]
        
        corners = []
        
        newImg = np.zeros(img.shape, np.uint8)
        y = 0
        while y < h:
            x = 0
            while x < w:
                if 0 != img[y][x] and dst[y][x] > 0.01 * dst.max():
                    corners.append(np.array([float(x), float(y), 0.0]))
                    cv2.line(newImg, (centroid[0], centroid[1]), (x, y), 255, 2)
                x += 1
            y += 1
        
        return corners, newImg
    
    def argumentsMet(self):
        for needed_arg in self.__needed_arguments:
            if None == self.__arguments.get(needed_arg, None):
                return False
        return True