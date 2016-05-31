package ph.edu.msuiit.circuitlens.ui;

/**
 * Created by vercillius on 5/31/2016.
 */
public class AndroidCameraAdapter implements CameraAdapter {
    @Override
    public int getCameraWidth(int index) {
        return 0;
    }

    @Override
    public int getCameraHeight(int index) {
        return 0;
    }

    @Override
    public float getVerticalFOV(int index) {
        return 0;
    }

    @Override
    public float getHorizontalFOV(int index) {
        return 0;
    }

    @Override
    public boolean isFrontCamera(int index) {
        return false;
    }

    @Override
    public boolean isBackCamera(int index) {
        return false;
    }

    @Override
    public boolean isMultipleCameras() {
        return false;
    }

    @Override
    public int getNumberOfCameras() {
        return 0;
    }

    @Override
    public int getIndexOfCurrentCamera() {
        return 0;
    }

    @Override
    public void useCamera(int index) {

    }

    @Override
    public boolean isCurrentCameraFront() {
        return false;
    }

    @Override
    public boolean isCurrentCameraBack() {
        return false;
    }
}
