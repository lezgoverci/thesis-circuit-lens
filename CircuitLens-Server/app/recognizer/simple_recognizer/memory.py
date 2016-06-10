import numpy as np
import json

class Memory:
    __image_memory = None
    __class_memory = None
    __memory_size = 0

    def __init__(self, memory_size, image_txt_file=None, class_txt_file=None):
        self.__memory_size = memory_size
        self.load(image_txt_file, class_txt_file)
    
    def load(self, image_txt_file, class_txt_file):
        """
        image_txt_file(string)
                The file to store the image memory to
        class_txt_file(string)
                The file to store the classes to
        """
        if image_txt_file is None or class_txt_file is None:
            return

        try:
            self.__image_memory = np.loadtxt(image_txt_file, np.float32)
            self.__class_memory = json.load(open(class_txt_file))

        except IOError:
            self.__image_memory = np.zeros((1, self.__memory_size))
            self.__class_memory = {}

    def add(self, class_name, training_datum):
        """
        class_name(char) 
                The class the training_datum is associated to
        training_datum(numpy array)
                The flatted numpy array representation of the image
        """
        try:
            index = self.__class_memory[class_name]
            self.__image_memory[index:index+1][0] = self.__image_memory[index:index+1][0] + training_datum
        except KeyError:
            self.__image_memory = np.append(self.__image_memory, training_datum, 0)

            last_index = self.__image_memory.shape[:1][0] - 1
            self.__class_memory[class_name] = last_index
            self.__class_memory[last_index] = class_name

    def retrieve(self, training_datum):
        """
        training_datum(numpy array)
                The flattened image to be compared
        """
        search_result = 0
        target_location = 0
        index_counter = 0

        for stored_memory in self.__image_memory:
            image_distance = sum(stored_memory * training_datum[0])

            if search_result < image_distance:
                search_result = image_distance
                target_location = index_counter

            index_counter = index_counter + 1

        try:
            return self.__class_memory[str(target_location)]
        except KeyError:
            return None

    def save(self, image_txt_file, class_txt_file):
        """
        image_txt_file(string)
                The file to store the image memory to
        class_txt_file(string)
                The file to store the classes to
        """
        np.savetxt(image_txt_file, self.__image_memory)
        json.dump(self.__class_memory, open(class_txt_file, 'w'))