package ph.edu.msuiit.circuitlens.render;

import java.util.Vector;

/**
 * Created by vercillius on 5/31/2016.
 */
public class OpenCvMapper implements Mapper {
    @Override
    public Vector getRotation() {
        return null;
    }

    @Override
    public double getAxisRotationX() {
        return 0;
    }

    @Override
    public double getAxisRotationY() {
        return 0;
    }

    @Override
    public double getAxisRotationZ() {
        return 0;
    }

    @Override
    public Vector getTranslation() {
        return null;
    }

    @Override
    public double getTranslationX() {
        return 0;
    }

    @Override
    public double getTranslationY() {
        return 0;
    }

    @Override
    public double getTranslationZ() {
        return 0;
    }

    @Override
    public Vector getScaleTransformation() {
        return null;
    }

    @Override
    public double getScaleTransformationX() {
        return 0;
    }

    @Override
    public double getScaleTransformationY() {
        return 0;
    }

    @Override
    public double getScaleTransformationZ() {
        return 0;
    }

    @Override
    public void setPixelDensity(int pixelDensity) {

    }

    @Override
    public int getPixelDensity() {
        return 0;
    }

    @Override
    public double[] getCameraMatrix() {
        return new double[0];
    }

    @Override
    public double[] getProjectionMatrix() {
        return new double[0];
    }
}
