package ph.edu.msuiit.circuitlens.ui;

import org.opencv.core.MatOfDouble;

public interface CircuitLensView {
    void showMessage(String message);
    void updateRendererCameraPose(MatOfDouble rVec, MatOfDouble tVec, int[] floats);

    // Set rotation
    void setRotation(double xAxisRotation, double yAxisRotation, double zAxisRotation);
    void setRotationX(double xAxisRotation);
    void setRotationY(double yAxisRotation);
    void setRotationZ(double zAxisRotation);

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
