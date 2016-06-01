package ph.edu.msuiit.circuitlens.ui;

/**
 * Created by vercillius on 5/31/2016.
 */
public interface CameraAdapter {

    int getCameraWidth();
    int getCameraHeight();
    int getIndexOfCurrentCamera();
    int getNumberOfCameras();
    float getVerticalFOV();
    float getHorizontalFOV();
    double getAspectRatio();
    boolean isFrontCamera();
    boolean isBackCamera();
    boolean isMultipleCameras();
    void useCamera(int index);
    void calibrate();



}
