package ph.edu.msuiit.circuitlens.ui.gl;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import org.rajawali3d.math.MathUtil;
import org.rajawali3d.math.Quaternion;

/**
 * Created by vercillius on 6/3/2016.
 */
public class RendererTransformations extends ArRajawaliRenderer {

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
    private boolean mfirstStep = true;
    private boolean mIsTakePhoto;
    private boolean isSetInitialValues = false;


    public RendererTransformations(Context context, TextView timeTextView, TextView infoTextView) {
        super(context, null);
    }

    @Override
    public void initScene() {
        super.initScene();

        /**
         * Reminder: Order of transformations
         *  Rotate -> Translate -> Scale
         **/

        Log.d("renderInit1", "entering init scene");
        getCurrentCamera().setPosition(0, 0, 500);
        getCurrentCamera().setFarPlane(5000);

        /*
        //setInitCircuitProjectionValues();       // set default projection values
        setInitViewingTransformation();         // Set viewing transformation
        setInitModellingTransformation();       // Set modelling transformation
        setInitProjectionTransformation();      // Set Projection transformation
        setInitViewportTransformation();        // Set Viewport transformation

        getCurrentCamera().setZ(500);

        Log.d("renderInit2","done init scene");*/
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

        getCurrentCamera().setX(0);
        getCurrentCamera().setY(0);

        // After setting camera position
        // x = x coordinate of the center point of the circuit
        // y = y coordinate of the center point of the circuit
        // z = 4.0
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

//        mInitPosX = circuit3D.getX();
//        mInitPosY = circuit3D.getY();
//        mInitPosZ = circuit3D.getZ();

        //Log.d("InitOrient", "Yaw: " + mInitQuaternionOrientation.getYaw() + "Pitch: " + mInitQuaternionOrientation.getPitch() + "Roll: " + mInitQuaternionOrientation.getRoll() + "");
        //Log.d("InitPos", "X: " + mInitPosX + "Y: " + mInitPosY + "Z: " + mInitPosZ + "");
    }

    private void setInitProjectionTransformation() {
        // default near plane z = 1.0
        // default far plane z = 120.0
        // default FOV = 45.0
        Log.d("checkCameraParameters", " going to use parameters values in renderer");
        getCurrentCamera().updatePerspective(mHorizontalFOV, mVerticalFOV);
        getCurrentCamera().setProjectionMatrix(mCameraWidth, mCameraHeight);
        //Log.d("rendererValues","Horizontal FOV: " + getCurrentCamera().get);
        //TODO use camera values set in camera parameter values
        Log.d("checkCameraParameters", " parameters values are used in renderer");
    }

    private void setInitViewportTransformation() {
        //TODO initial scaling here
        circuit3D.setScaleZ(-1);
    }

    @Override
    public void onRender(long elapsedTime, double deltaTime) {
        super.onRender(elapsedTime, deltaTime);

        // if camera matrix is not yet set, do not render
        //if (!mCameraValuesDirty) {
        renderImage();
        //}
        Log.d("checkDimensions", "Viewport: " + getViewportWidth() + " x " + getViewportHeight());
    }

    private void renderImage() {

        if (mIsTakePhoto) {
            mInitQuaternionOrientation = circuit3D.getOrientation();
            mInitYaw = mInitQuaternionOrientation.getYaw();
            mInitPitch = mInitQuaternionOrientation.getPitch();
            mInitRoll = mInitQuaternionOrientation.getRoll();

            mInitPosX = mPosX;
            mInitPosY = mPosY;
            mInitPosZ = mPosZ;
            isSetInitialValues = true;
        }

        if (isSetInitialValues) {
            // proceed to rendering
            renderImageOrientation();
            renderImageTranslation();
            //renderImageScaling();
        }

    }

    private void renderImageOrientation() {

        if (true) {       // turn this to false to use raw orientation data

            double rawRoll = mRoll;
            double rawYaw = mYaw;
            double rawPitch = mPitch;

            double diffRoll = rawRoll - mInitRoll;
            double diffYaw = rawYaw - mInitYaw;
            double diffPitch = rawPitch - mInitPitch;

            double roll = MathUtil.radiansToDegrees(/*circuit3D.getOrientation().getRoll() + */diffRoll);
            double yaw = MathUtil.radiansToDegrees(/*circuit3D.getOrientation().getYaw() + */diffYaw);
            double pitch = MathUtil.radiansToDegrees(/*circuit3D.getOrientation().getPitch() +*/ diffPitch);

            Quaternion orient = new Quaternion();
            orient.fromEuler(-yaw, pitch, roll);
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

            double diffPosX = rawPosX - mInitPosX;
            double diffPosY = rawPosY - mInitPosY;
            double diffPosZ = rawPosZ - mInitPosZ;

            double posX = /*circuit3D.getX() +*/ diffPosX;
            double posY = /*circuit3D.getY() +*/ diffPosY;
            double posZ = /*circuit3D.getZ() +*/ diffPosZ;

            circuit3D.setX((int) posX);
            circuit3D.setY((int) posY * -1);
            //circuit3D.setZ((int)posZ * 1);

            //circuit3D.setZ(posZ);
            Log.d("rendererValues", "X: " + circuit3D.getX() + " Y: " + circuit3D.getY() + " Z: " + circuit3D.getZ() + "");

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
        //TODO check if this comes first before rendering
        Log.d("checkCameraParameters", " going to save parameters values in renderer");
        mCameraHeight = height;
        mCameraWidth = width;
        mVerticalFOV = verticalFOV;
        mHorizontalFOV = horizontalFOV;
        mAspectRatio = aspectRatio;
        mCameraValuesDirty = false;
        setViewPort(mCameraWidth, mCameraHeight);
        Log.d("checkViewport", " view renderer: h" + getViewportHeight() + " w: " + getViewportWidth());
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

    public void setTrigger(boolean isTakePhoto) {
        mIsTakePhoto = isTakePhoto;
    }
}
