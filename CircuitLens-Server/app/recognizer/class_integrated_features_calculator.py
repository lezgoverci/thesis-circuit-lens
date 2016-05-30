import class_feature_factory as ff
import class_feature_data_extractor_factory as fdef

class IntegratedFeaturesCalculator:
    def __init__(self, features=[]):
        self.__features = features
        self.__featureDataExtractors = {}
        self.__calculatedFeature = None
    
    def setFeatures(self, features):
        self.__features = features
        
        return self
    
    def addFeature(self, feature):
        self.__features.append(feature)
        
        return self
    
    def get(self, recalculate=False):
        if None != self.__calculatedFeature and not recalculate:
            return self.__calculatedFeature
            
        self.__calculatedFeature = []
        
        for featureStr in self.__features:
            feature = ff.FeatureFactory.create(featureStr['name'])
            featureDataExtractors = feature.getNeededFeatureDataExtractors()
            
            featureArgs = featureStr['arguments']
            
            if featureDataExtractors:
                featureArgs['feature_data_extractors'] = {}
            
            for featureDataExtractorStr in featureDataExtractors:
                if None == self.__featureDataExtractors.get(featureDataExtractor, None):
                    featureDataExtractor = fdef.FeatureDataExtractorFactory.create(featureDataExtractorStr)
                    
                    self.__featureDataExtractors[featureDataExtractorStr] = featureDataExtractor
                else:
                    featureDataExtractor = self.__featureDataExtractors[featureDataExtractorStr]
                
                featureArgs['feature_data_extractors'][featureDataExtractorStr] = featureDataExtractor
            
            self.__calculatedFeature.append(feature.setArguments(featureArgs).getCalculatedFeature())
        
        return self.__calculatedFeature