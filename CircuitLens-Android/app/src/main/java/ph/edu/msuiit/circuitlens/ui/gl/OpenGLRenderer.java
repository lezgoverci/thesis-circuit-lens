package ph.edu.msuiit.circuitlens.ui.gl;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import org.opencv.core.MatOfDouble;
import org.rajawali3d.ATransformable3D;
import org.rajawali3d.Object3D;
import org.rajawali3d.math.MathUtil;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.RajawaliRenderer;
import org.rajawali3d.scene.RajawaliScene;

import ph.edu.msuiit.circuitlens.circuit.CircuitCanvas3D;
import ph.edu.msuiit.circuitlens.circuit.CircuitSimulator;

public class OpenGLRenderer  extends RajawaliRenderer {

    public Context context;

    private CircuitCanvas3D circuit3D;
    private double mRotX;
    private double mRotY;
    private double mRotZ;
    private double mPosX;
    private double mPosY;
    private double mPosZ;
    private float[] mGLpose;
    private boolean isSetProjectionValues = false;
    private boolean isSetInitCameraValues = false;
    private double mInitPosX;
    private double mInitPosY;
    private double mInitPosZ;
    private double mInitRotX;
    private double mInitRotY;
    private double mInitRotZ;
    private double mInitGlPosX;
    private double mInitGlPosY;
    private double mInitGlPosZ;

    private double testValue = 0.0;
    private boolean isSetInitialValues = false;
    private int mTargetCircuitWidth = 0;
    private int mTargetCircuitHeight = 0;
    private int mTargetCircuitX;
    private int mTargetCircuitY;
    private int mInitZ = 10;
    private double mSize;
    private int mCameraWidth;
    private int mCameraHeight;
    private double mScaleInit;


    public OpenGLRenderer(Context context) {
        super(context);
        this.context = context;
        setFrameRate(60);
    }

    public void initScene(){
        CircuitSimulator cirsim = new CircuitSimulator();
        cirsim.init();
        final String expectedNetlist =
                "$ 1 0.000005 10.20027730826997 50 5 50\n" +
                        "v 160 368 160 48 0 0 40 5 0 0 0.5\n" +
                        "w 160 48 256 48 1\n" +
                        "w 256 48 352 48 0\n" +
                        "w 352 48 448 48 0\n" +
                        "s 256 48 256 128 0 0 false\n" +
                        "s 352 48 352 128 0 1 false\n" +
                        "s 448 48 448 128 0 0 false\n" +
                        "r 256 128 256 192 0 100\n" +
                        "r 352 128 352 192 0 400\n" +
                        "r 448 128 448 192 0 800\n" +
                        "w 256 192 352 192 0\n" +
                        "w 352 192 448 192 0\n" +
                        "w 352 224 352 192 0\n" +
                        "w 352 224 448 224 0\n" +
                        "w 352 224 256 224 0\n" +
                        "s 352 224 352 304 0 0 false\n" +
                        "s 256 224 256 304 0 1 false\n" +
                        "r 256 304 256 368 0 600\n" +
                        "r 352 304 352 368 0 200\n" +
                        "s 448 224 448 368 0 1 false\n" +
                        "w 160 368 256 368 0\n" +
                        "w 256 368 352 368 0\n" +
                        "w 352 368 448 368 0\n";

        cirsim.readSetup(expectedNetlist);
        cirsim.analyzeCircuit();
        cirsim.runCircuit();
        cirsim.setStopped(false);
        cirsim.draw();
        circuit3D = cirsim.getCircuitCanvas();
        //circuit3D.drawBounds(circuit3D);
        getCurrentScene().addChild(circuit3D);



        // Set viewing transformation
        setInitViewingTransformation();

        // Set modelling transformation
        setInitModellingTransformation();

        // Set Projection transformation
        setInitProjectionTransformation();

        // Set Viewport transformation
        setInitViewportTransformation();




    }

    private void setInitViewportTransformation() {

        //double scale = mCameraHeight / (((circuit3D.getCircuitHeight() / 2) + circuit3D.getCircuitTopLeftY()) * 2);
        //double scale = mTargetCircuitHeight / circuit3D.getCircuitHeight();

        mScaleInit = 1.0;

        circuit3D.setScale(mScaleInit,-mScaleInit,mScaleInit);
        //TODO fix Z-axis scaling
        //circuit3D.setZ(-70.0);

    }

    private void setInitProjectionTransformation() {
        // default near plane z = 1.0
        // default far plane z = 120.0
        // default FOV = 45.0
        getCurrentCamera().setFarPlane(5000);
    }

    private void setInitModellingTransformation() {
        // default scene and circuit z = 0.0;
//        //double initX = circuit3D.getX() - circuit3D.getCircuitCenterX();
//        double initX = (circuit3D.getCircuitWidth() / 2.0);
//        //double initY = circuit3D.getY() + circuit3D.getCircuitCenterY();
//        double initY = (circuit3D.getCircuitHeight() / 2.0);
//        circuit3D.moveRight(initX - 500);
//        circuit3D.moveUp(initY + 500);

        int initX = (int) ((circuit3D.getCircuitWidth() / 2) + circuit3D.getCircuitTopLeftX());
        int initY = (int) ((circuit3D.getCircuitHeight()/ 2) + circuit3D.getCircuitTopLeftY());

        circuit3D.setX(initX * -1);
        circuit3D.setY(initY);



    }

    private void setInitViewingTransformation() {
        // Default camera z = 4.0
    }

    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);

        //runRotationTest();

        // Use transformation values
        if(isSetProjectionValues && isSetInitialValues){
            useTransformationValues();
        } else{
            //circuitDiagram.rotate(Vector3.Axis.X, 5);
            //circuitDiagram.rotate(Vector3.Axis.Y, 1);
        }

    }

    private void runRotationTest() {

        double inputW = 0;
        double inputX = 0;
        double inputY = 0;
        double inputZ = 45;

        Quaternion quat = new Quaternion();
        quat.fromEuler(inputY,inputX,inputZ);

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

    private void useTransformationValues() {


        circuit3D.setX(mPosX * 100.0);
        circuit3D.setY(mPosY * -100.0);
        circuit3D.setZ(mPosZ * -100.0);

        // Orientation
        Quaternion orient = new Quaternion();

        // Yaw, Pitch, Roll
        double yaw = (mRotY - mInitRotY);
        double pitch = ((mRotX* -1.0) - mInitRotX);
        double roll = ((mRotZ * -1.0) - mInitRotZ);
        orient.fromEuler(yaw,pitch,roll);
        circuit3D.setOrientation(orient);
    }


    public void onOffsetsChanged(float x, float y, float z, float w, int i, int j){

    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            Log.d(this.getClass().getSimpleName(),": " + event.getX()+ "," + event.getY());
        }
    }


    // After homography is found
    public void setProjectionValues(MatOfDouble rVec, MatOfDouble tVec, int[] dimens) {
        // Raw rotation values in degrees
        double rotXraw = MathUtil.radiansToDegrees(rVec.toArray()[0]);
        double rotYraw = MathUtil.radiansToDegrees(rVec.toArray()[1]);
        double rotZraw = MathUtil.radiansToDegrees(rVec.toArray()[2]);
        
        // Raw translation values
        double posXraw = tVec.toArray()[0];
        double posYraw = tVec.toArray()[1];
        double posZraw = tVec.toArray()[2];

        // If target circuit dimension is not set
        if(!isSetInitialValues){
            // initial position
            mTargetCircuitX = dimens[0];
            mTargetCircuitY = dimens[1];

            // target dimension
            mTargetCircuitWidth = dimens[2];
            mTargetCircuitHeight = dimens[3];

            //mInitPosX = mTargetCircuitX - (mTargetCircuitWidth / 2.0);
           // mInitPosY = mTargetCircuitY + (mTargetCircuitHeight / 2.0);
            mInitPosX = (circuit3D.getCircuitWidth() / 2.0) * -1.0;
            mInitPosY = (circuit3D.getCircuitHeight() / 2.0);
            mInitPosZ = 0;

            // Camera height and width
           // mCameraWidth = dimens[4];
           // mCameraHeight = dimens[5];

            // initial rotation
            mInitRotX = rotXraw;
            mInitRotY = rotYraw;
            mInitRotZ = rotZraw;

            mPosX = mInitPosX;
            mPosY = mInitPosY;
            mPosZ = 0;

            isSetInitialValues = true;
        }
        else{
            mPosX = posXraw;
            mPosY = posYraw;
            mPosZ = posZraw;
        }

        mRotX = rotXraw;
        mRotY = rotYraw;
        mRotZ = rotZraw;

        // Projection values are set
        isSetProjectionValues = true;
    }

    public void setCameraValues(int width, int height) {
        mCameraHeight = height;
        mCameraWidth = width;
    }
}

