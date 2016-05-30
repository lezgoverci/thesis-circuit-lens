import class_classes_database as cdb
import class_feature_using_recognizer as fur

class FeaturesUsingRecognizerClassesDB(cdb.ClassesDatabase):
    def train(self, classes_images_map):
        if not classes_images_map:
            return self
        
        recognizer = fur.FeatureUsingRecognizer()
        
        for classStr, img in classes_images_map.iteritems():
            self._classes_values_map[classStr] = recognizer.setImage(img).getCalculatedFeature()
        
        return self