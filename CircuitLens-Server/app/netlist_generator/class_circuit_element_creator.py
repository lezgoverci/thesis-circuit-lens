import circuit_elements.class_circuit_element_factory as cef

class CircuitElementCreator:
    def __init__(self, image=None):
        self.__image = image
        self.__contour = None
    
    def setImage(self, image):
        self.__image = image
        return self
    
    def setContour(self, contour):
        self.__contour = contour
        return self
    
    def create(self, recognized_class):
        if not self.__contour or not self.__image:
            return cef.CircuitElementFactory.create('null')