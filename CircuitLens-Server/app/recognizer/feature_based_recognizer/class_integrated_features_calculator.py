import features.class_feature_factory as ff
import feature_data_extractors.class_feature_data_extractor_factory as fdef
import numpy as np

class IntegratedFeaturesCalculator:
    def __init__(self, features=[]):
        self.__features = features
        self.__featureDataExtractors = {}
        self.__calculatedFeature = None
        self.__storedFeatures = {}
    
    def setFeatures(self, features):
        self.__features = features
        
        return self
    
    def addFeature(self, feature):
        self.__features.append(feature)
        
        return self
    
    def get(self, recalculate=False, flatten=True):
        if self.__calculatedFeature is not None and not recalculate:
            return self.__calculatedFeature
            
        calculatedFeature = []
        
        for featureStr in self.__features:
            if None == self.__storedFeatures.get(featureStr['name'], None):
                feature = ff.FeatureFactory.create(featureStr['name'])
                self.__storedFeatures[featureStr['name']] = feature
            else:
                feature = self.__storedFeatures[featureStr['name']]
            
            featureDataExtractors = feature.getNeededFeatureDataExtractors()
            
            featureArgs = featureStr['arguments']
            
            if featureDataExtractors:
                featureArgs['feature_data_extractors'] = {}
            
            for featureDataExtractorStr in featureDataExtractors:
                if None == self.__featureDataExtractors.get(featureDataExtractorStr, None):
                    featureDataExtractor = fdef.FeatureDataExtractorFactory.create(featureDataExtractorStr)
                    
                    self.__featureDataExtractors[featureDataExtractorStr] = featureDataExtractor
                else:
                    featureDataExtractor = self.__featureDataExtractors[featureDataExtractorStr]
                
                featureArgs['feature_data_extractors'][featureDataExtractorStr] = featureDataExtractor

            calculatedFeature.append(feature.setArguments(featureArgs).getCalculatedFeature(recalculate))
        
        self.__calculatedFeature = np.array(calculatedFeature.flatten() if flatten else calculatedFeature)
        
        return self.__calculatedFeature