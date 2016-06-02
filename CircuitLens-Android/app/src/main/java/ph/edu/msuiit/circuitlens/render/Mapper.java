package ph.edu.msuiit.circuitlens.render;


/**
 * Created by vercillius on 5/31/2016.
 */
public interface Mapper {


    // Rotation
    double[] getRotation();
    double getAxisRotationX();
    double getAxisRotationY();
    double getAxisRotationZ();

    // Translation
    double[] getTranslation();
    double getTranslationX();
    double getTranslationY();
    double getTranslationZ();

    // Scaling
    double[] getScaleTransformation();
    double getScaleTransformationX();
    double getScaleTransformationY();
    double getScaleTransformationZ();

    // Pixel density
    void setPixelDensity(int pixelDensity);
    int getPixelDensity();

    // Matrices
    double[] getTransformationMatrix();

}
