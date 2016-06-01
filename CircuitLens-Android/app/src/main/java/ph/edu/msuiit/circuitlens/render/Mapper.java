package ph.edu.msuiit.circuitlens.render;

import org.opencv.core.MatOfDouble;

import java.util.Vector;

/**
 * Created by vercillius on 5/31/2016.
 */
public interface Mapper {
    // Rotation
    Vector getRotation();
    double getAxisRotationX();
    double getAxisRotationY();
    double getAxisRotationZ();

    // Translation
    Vector getTranslation();
    double getTranslationX();
    double getTranslationY();
    double getTranslationZ();

    // Scaling
    Vector getScaleTransformation();
    double getScaleTransformationX();
    double getScaleTransformationY();
    double getScaleTransformationZ();

    // Pixel density
    void setPixelDensity(int pixelDensity);
    int getPixelDensity();

    // Matrices
    double[] getTransformationMatrix();
    MatOfDouble getCameraProjectionMatrix();
}
