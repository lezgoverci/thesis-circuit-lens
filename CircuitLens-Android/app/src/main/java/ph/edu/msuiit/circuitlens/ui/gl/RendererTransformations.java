package ph.edu.msuiit.circuitlens.ui.gl;

import android.content.Context;
import android.util.Log;

import org.opencv.core.MatOfDouble;
import org.rajawali3d.math.MathUtil;
import org.rajawali3d.math.Quaternion;

/**
 * Created by vercillius on 6/3/2016.
 */
public class RendererTransformations extends OpenGLRenderer {

    private double mPosX;
    private double mPosY;
    private double mPosZ;

    private double mInitPosX;
    private double mInitPosY;
    private double mInitPosZ;

    private double mScaleX;
    private double mScaleY;
    private double mScaleZ;

    private double mYaw;
    private double mPitch;
    private double mRoll;

    private double mInitYaw;
    private double mInitPitch;
    private double mInitRoll;

    private Quaternion mQuaternionOrientation;
    private Quaternion mInitQuaternionOrientation;

    private int mCameraWidth;
    private int mCameraHeight;

    private double mVerticalFOV;
    private double mHorizontalFOV;
    private double mAspectRatio;
    private boolean mCameraValuesDirty = true;


    public RendererTransformations(Context context) {
        super(context);
    }

    @Override
    public void initScene() {
        super.initScene();

        /**
         * Reminder: Order of transformations
         *  Rotate -> Translate -> Scale
         **/

        setInitCircuitProjectionValues();       // set default projection values
        setInitViewingTransformation();         // Set viewing transformation
        setInitModellingTransformation();       // Set modelling transformation
        setInitProjectionTransformation();      // Set Projection transformation
        setInitViewportTransformation();        // Set Viewport transformation

        circuit3D.setZ(500);

    }

    private void setInitCircuitProjectionValues() {
        mPosX = 0.0;
        mPosY = 0.0;
        mPosZ = 0.0;
        mScaleX = 1.0;
        mScaleY = 1.0;
        mScaleZ = 1.0;
        mQuaternionOrientation = Quaternion.getIdentity();
    }

    private void setInitViewingTransformation() {
        // Default camera (right hand rule).
        // Anchor point is in the center

        // Before setting camera position to center of circuit
        // x = 0.0
        // y = 0.0
        // z = 4.0
        // looking towards the negative z

        // set camera center to the center of the circuit
        double circuitCenterX = circuit3D.getCircuitCenterX();
        double circuitCenterY = circuit3D.getCircuitCenterY();

        getCurrentCamera().setX(circuitCenterX);
        getCurrentCamera().setY(circuitCenterY);

        // After setting camera position
        // x = x coordinate of the center point of the circuit
        // y = y coordinate of the center point of the circuit
        // z = 4.0

        Log.d(getClass().getSimpleName(), "width: " + getViewportWidth());
        Log.d(getClass().getSimpleName(), "height: " + getViewportHeight());
    }

    private void setInitModellingTransformation() {
        // default scene and circuit.
        // Assuming no flipping along the axes.
        // Anchor point is at the bottom-left corner

        // x = 0.0
        // y = 0.0
        // z = 0.0
        // looking towards the positive z

        // Record init values for later reference

        mInitQuaternionOrientation = circuit3D.getOrientation();

        mInitYaw = mInitQuaternionOrientation.getYaw();
        mInitPitch = mInitQuaternionOrientation.getPitch();
        mInitRoll = mInitQuaternionOrientation.getRoll();

        mInitPosX = circuit3D.getX();
        mInitPosY = circuit3D.getY();
        mInitPosZ = circuit3D.getZ();

        Log.d("InitOrient", "Yaw: " + mInitQuaternionOrientation.getYaw() + "Pitch: " + mInitQuaternionOrientation.getPitch() + "Roll: " + mInitQuaternionOrientation.getRoll() + "");
        Log.d("InitPos", "X: " + mInitPosX + "Y: " + mInitPosY + "Z: " + mInitPosZ + "");
    }

    private void setInitProjectionTransformation() {
        // default near plane z = 1.0
        // default far plane z = 120.0
        // default FOV = 45.0

        getCurrentCamera().updatePerspective(mHorizontalFOV, mVerticalFOV);
        getCurrentCamera().setProjectionMatrix(mCameraWidth, mCameraHeight);
    }

    private void setInitViewportTransformation() {
        //TODO initial scaling here
    }

    @Override
    public void onRender(long elapsedTime, double deltaTime) {
        super.onRender(elapsedTime, deltaTime);
        renderImage();
    }

    private void renderImage() {
        // if camera matrix is not yet set, do not render
        if (!mCameraValuesDirty) {
            // proceed to rendering
            renderImageOrientation();
            renderImageTranslation();
            renderImageScaling();
        }
    }

    private void renderImageOrientation() {

        if (true) {       // turn this to false to use raw orientation data

            double rawRoll = mRoll;
            double rawYaw = mYaw;
            double rawPitch = mPitch;

            double diffRoll = mInitRoll - rawRoll;
            double diffYaw = mInitYaw - rawYaw;
            double diffPitch = mInitPitch - rawPitch;

            double roll = MathUtil.radiansToDegrees(circuit3D.getOrientation().getRoll() + diffRoll);
            double yaw = MathUtil.radiansToDegrees(circuit3D.getOrientation().getYaw() + diffYaw);
            double pitch = MathUtil.radiansToDegrees(circuit3D.getOrientation().getPitch() + diffPitch);

            Quaternion orient = new Quaternion();
            orient.fromEuler(yaw, pitch, roll);
            circuit3D.setOrientation(orient);

        } else {

            Quaternion orient = new Quaternion();
            orient.fromEuler(mYaw, mPitch, mRoll);
            circuit3D.setOrientation(orient);
        }
    }

    private void renderImageTranslation() {
        if (true) {       // turn this to false to use raw position data

            double rawPosX = mPosX;
            double rawPosY = mPosY;
            double rawPosZ = mPosZ;

            double diffPosX = mInitPosX - rawPosX;
            double diffPosY = mInitPosY - rawPosY;
            double diffPosZ = mInitPosZ - rawPosZ;

            double posX = circuit3D.getX() + diffPosX;
            double posY = circuit3D.getY() + diffPosY;
            double posZ = circuit3D.getZ() + diffPosZ;

            circuit3D.setX(posX);
            circuit3D.setY(posY);
            circuit3D.setZ(posZ);

        } else {
            circuit3D.setX(mPosX);
            circuit3D.setY(mPosY);
            circuit3D.setZ(mPosZ);
        }
    }

    private void renderImageScaling() {
        circuit3D.setScaleX(mScaleX);
        circuit3D.setScaleY(mScaleY);
        circuit3D.setScaleZ(mScaleZ);
    }

    public void setCameraValues(int width, int height, double verticalFOV, double horizontalFOV, double aspectRatio) {
        mCameraHeight = height;
        mCameraWidth = width;
        mVerticalFOV = verticalFOV;
        mHorizontalFOV = horizontalFOV;
        mAspectRatio = aspectRatio;
        mCameraValuesDirty = false;
    }


    public void setOrientation(double roll, double yaw, double pitch) {
        mRoll = roll;
        mYaw = yaw;
        mPitch = pitch;
    }

    public void setTranslation(double x, double y, double z) {
        mPosX = x;
        mPosY = y;
        mPosZ = z;
    }

    public void setScaleTransformation(double x, double y, double z) {
        mScaleX = x;
        mScaleY = y;
        mScaleZ = z;
    }

    public void setOrientationRoll(double roll) {
        mRoll = roll;
    }

    public void setOrientationYaw(double yaw) {
        mYaw = yaw;
    }

    public void setOrientationPitch(double pitch) {
        mPitch = pitch;
    }

    public void setTranslationX(double x) {
        mPosX = x;
    }

    public void setTranslationY(double y) {
        mPosY = y;
    }

    public void setTranslationZ(double z) {
        mPosZ = z;
    }

    public void setScaleTransformationX(double x) {
        mScaleX = x;
    }

    public void setScaleTransformationY(double y) {
        mScaleY = y;
    }

    public void setScaleTransformationZ(double z) {
        mScaleZ = z;
    }

    private void runRotationTest() {

        double inputW = 0;
        double inputX = 0;
        double inputY = 0;
        double inputZ = 45;

        Quaternion quat = new Quaternion();
        quat.fromEuler(inputY, inputX, inputZ);

        double x00 = circuit3D.getRotX();
        double y00 = circuit3D.getRotY();
        double z00 = circuit3D.getRotZ();

        circuit3D.setOrientation(quat);

        double x0 = circuit3D.getRotX();
        double y0 = circuit3D.getRotY();
        double z0 = circuit3D.getRotZ();

        double x = MathUtil.radiansToDegrees(circuit3D.getRotX());
        double y = MathUtil.radiansToDegrees(circuit3D.getRotY());
        double z = MathUtil.radiansToDegrees(circuit3D.getRotZ());

        circuit3D.setOrientation(quat);

        double x1 = MathUtil.radiansToDegrees(circuit3D.getRotX());
        double y1 = MathUtil.radiansToDegrees(circuit3D.getRotY());
        double z1 = MathUtil.radiansToDegrees(circuit3D.getRotZ());
    }
}
