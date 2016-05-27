package ph.edu.msuiit.circuitlens.ui.gl;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import org.opencv.core.MatOfDouble;
import org.rajawali3d.Object3D;
import org.rajawali3d.math.MathUtil;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.RajawaliRenderer;

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
        circuit3D.setScale(1,-1,1);

        // show bounds of circuitcanvas for debugging
        //Object3D bounds3D = new Object3D();
        //circuit3D.drawBounds(bounds3D);
        //bounds3D.setScale(1,-1,1);
        //getCurrentScene().addChild(bounds3D);

        // point camera to the center of the circuit
        getCurrentCamera().setX(circuit3D.getCircuitCenterX());
        getCurrentCamera().setY(-circuit3D.getCircuitCenterY());


        getCurrentCamera().setZ(500);
        getCurrentCamera().setFarPlane(5000);

        getCurrentScene().addChild(circuit3D);


        //TODO rotate the object not the camera
        mInitGlPosX = getCurrentCamera().getX();
        mInitGlPosY = getCurrentCamera().getY();
        mInitGlPosZ = getCurrentCamera().getZ();
    }

    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);

        //runRotationTest();


        if(!isSetInitCameraValues && isSetProjectionValues){
            mInitPosX = mPosX;
            mInitPosY = mPosY;
            mInitPosZ = mPosZ;

            mInitRotX = mRotX;
            mInitRotY = mRotY;
            mInitRotZ = mRotZ;

            isSetInitCameraValues = true;
        }


        // Use transformation values
        if(isSetProjectionValues && isSetInitCameraValues){
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

        // Orientation
        Quaternion orient = new Quaternion();
        // Yaw, Pitch, Roll
        double yaw = 0;
        double pitch = 0;
        double roll = mRotZ * -1.0;
        orient.fromEuler(yaw,pitch,roll);
        circuit3D.setOrientation(orient);

        // Translation
        //getCurrentCamera().setX(mPosX);
       // getCurrentCamera().setY(mPosY);
        //getCurrentCamera().setZ(mPosZ);
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

    public void setProjectionValues(MatOfDouble rVec, MatOfDouble tVec, float[] pose) {
        // Rotation values in radians
        mRotX = MathUtil.radiansToDegrees(rVec.toArray()[0]);
        mRotY = MathUtil.radiansToDegrees(rVec.toArray()[1]);
        mRotZ = MathUtil.radiansToDegrees(rVec.toArray()[2]);

        // Position values
        if(!isSetInitCameraValues){
            mPosX = mInitPosX;
            mPosY = mInitPosY;
            mPosZ = mInitPosZ;
        }
        else{
            mPosX = mInitGlPosX + (tVec.toArray()[0] - mInitPosX);
            mPosY = mInitGlPosY + (tVec.toArray()[1] - mInitPosY);
            mPosZ = mInitGlPosZ + (tVec.toArray()[2] - mInitPosZ);
        }


        // OpenGL pose
        mGLpose = pose;

        Log.d("setValRot",mRotX + " " + mRotY + " " + mRotZ + " ");
        Log.d("setValTrans",mPosX + " " + mPosY + " " + mPosX + " ");
        Log.d("setValPose",pose.toString());

        // Projection values are set
        isSetProjectionValues = true;
    }
}

