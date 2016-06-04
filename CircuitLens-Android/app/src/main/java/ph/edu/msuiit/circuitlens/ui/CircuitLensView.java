package ph.edu.msuiit.circuitlens.ui;

import org.opencv.core.MatOfDouble;

public interface CircuitLensView {
    void showMessage(String message);

    // Set orientation
    void setOrientation(double roll, double yaw, double pitch);
    void setOrientationRoll(double roll);
    void setOrientationYaw(double yaw);
    void setOrientationPitch(double pitch);

    // Set translation
    void setTranslation(double x, double y, double z);
    void setTranslationX(double x);
    void setTranslationY(double y);
    void setTranslationZ(double z);

    // Set scaling
    void setScaleTransformation(double x, double y, double z);
    void setScaleTransformationX(double x);
    void setScaleTransformationY(double y);
    void setScaleTransformationZ(double z);

    // Matrices
    void setCameraMatrix(double[] cameraMatrix);
    void setProjectionMatrix(double[] projectionMatrix);


}
