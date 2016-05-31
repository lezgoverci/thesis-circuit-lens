package ph.edu.msuiit.circuitlens.ui;

/**
 * Created by vercillius on 5/31/2016.
 */
public interface CameraAdapter {
    int getCameraWidth(int index);
    int getCameraHeight(int index);
    float getVerticalFOV(int index);
    float getHorizontalFOV(int index);
    boolean isFrontCamera(int index);
    boolean isBackCamera(int index);
    boolean isMultipleCameras();
    int getNumberOfCameras();
    int getIndexOfCurrentCamera();
    void useCamera(int index);
    boolean isCurrentCameraFront();
    boolean isCurrentCameraBack();
}
