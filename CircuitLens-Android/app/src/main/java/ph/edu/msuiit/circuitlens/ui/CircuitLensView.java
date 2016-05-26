package ph.edu.msuiit.circuitlens.ui;

import org.opencv.core.MatOfDouble;

public interface CircuitLensView {
    void showMessage(String message);
    void updateRendererCameraPose(MatOfDouble rVec, MatOfDouble tVec, float[] floats);
}
