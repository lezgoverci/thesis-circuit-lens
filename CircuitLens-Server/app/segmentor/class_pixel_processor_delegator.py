from threading import Thread

class PixelProcessorDelegator(Thread):
    def __init__(self, center, pixelProcessor):
        Thread.__init__(self)
        self.__pixelProcessor = pixelProcessor
        self.__center = center

    def run(self):
        self.__pixelProcessor.process(self.__center)
    