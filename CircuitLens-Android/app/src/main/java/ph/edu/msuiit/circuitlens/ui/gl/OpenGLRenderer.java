package ph.edu.msuiit.circuitlens.ui.gl;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import org.opencv.core.MatOfDouble;
import org.rajawali3d.math.MathUtil;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.renderer.RajawaliRenderer;

import ph.edu.msuiit.circuitlens.circuit.CircuitCanvas3D;
import ph.edu.msuiit.circuitlens.circuit.CircuitSimulator;

public class OpenGLRenderer  extends RajawaliRenderer {

    public Context context;

    private CircuitCanvas3D circuit3D;
    private double mOrientX;
    private double mOrientY;
    private double mOrientZ;
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
    private double mInitOrientZ;
    private double mInitOrientY;
    private double mInitOrientX;
    private double mRatio;
    private double mScale = 1.0;


    public OpenGLRenderer(Context context) {
        super(context);
        this.context = context;
        setFrameRate(60);
    }

    public void initScene(){
        CircuitSimulator cirsim = new CircuitSimulator();
        cirsim.init();
        final String expectedNetlist =
                "$ 1 0.000005 10.200277308269968 50 5 50\n" +
                        "w 160 64 256 64 1\n" +
                        "w 256 64 352 64 0\n" +
                        "w 352 64 448 64 0\n" +
                        "s 448 64 448 144 0 0 false\n" +
                        "r 304 64 304 384 0 100\n" +
                        "r 448 144 448 208 0 800\n" +
                        "s 448 208 448 384 0 0 false\n" +
                        "w 160 384 256 384 0\n" +
                        "w 256 384 352 384 0\n" +
                        "w 352 384 448 384 0\n" +
                        "w 160 400 160 64 0\n";


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

        //double scale = mCameraHeight / (((circuit3D.getCircuitHeight() / 2) + circuit3D.getCircuitBottomLeftY()) * 2);
        //double scale = mTargetCircuitHeight / circuit3D.getCircuitHeight();

        mScaleInit = 1.0;

        //circuit3D.setScale(mScaleInit,mScaleInit,mScaleInit);
        //TODO fix Z-axis scaling
        circuit3D.setZ(-500.0);


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


        double initX =  (circuit3D.getCircuitBottomLeftX() + (circuit3D.getCircuitWidth() / 2)) * mScale;
        double initY = (circuit3D.getCircuitBottomLeftY() + (circuit3D.getCircuitHeight()/ 2)) * mScale;

        circuit3D.setX(-initX);
        circuit3D.setY(-initY);

        
        mInitOrientZ = circuit3D.getOrientation().z;
        mInitOrientY = circuit3D.getOrientation().y;
        mInitOrientX = circuit3D.getOrientation().x;

        Log.d("orientInit","X: " + mInitOrientX + "Y: " + mInitOrientY + "Z: " + mInitOrientZ + "");



    }

    private void setInitViewingTransformation() {
        // Default camera z = 4.0
    }

    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);
        //circuit3D.setScaleY(circuit3D.getScaleY() - 0.005);
        //circuit3D.setZ(circuit3D.getZ() - 1.0);
        Log.d("animScale","X: " + circuit3D.getX() + " Y: " + circuit3D.getY() + " Z: " + circuit3D.getZ());


        //runRotationTest();

        // Use transformation values
        if(isSetProjectionValues){
            useTransformationValues();
        } else{
            //circuitDiagram.rotate(Vector3.Axis.X, 5);
            //circuitDiagram.rotate(Vector3.Axis.Y, 1);
        }

        Log.d("final",
                "isSetInitial:" + isSetInitialValues +
                " x:" + circuit3D.getX() +
                        " y:" + circuit3D.getY() +
                        " z:" + circuit3D.getZ() );

       circuit3D.setScale(mScale,mScale,mScale);

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

        if(isSetInitialValues){

            circuit3D.setX(mPosX);
            circuit3D.setY(mPosY);
            circuit3D.setZ(mPosZ);
            //circuit3D.setZ(circuit3D.getZ() - 1.0);

            // Orientation
            Quaternion orient = new Quaternion();

            // Yaw, Pitch, Roll
            double yaw = MathUtil.radiansToDegrees(mOrientY);
            double pitch = MathUtil.radiansToDegrees(mOrientX);
            double roll = MathUtil.radiansToDegrees(mOrientZ);
            orient.fromEuler(yaw,pitch,roll);
            circuit3D.setOrientation(orient);
        }
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
        double orientXraw = MathUtil.radiansToDegrees(rVec.toArray()[0]);
        double orientYraw = MathUtil.radiansToDegrees(rVec.toArray()[1]);
        double orientZraw = MathUtil.radiansToDegrees(rVec.toArray()[2]);
        
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




//            Log.d("scaleInit",mScaleInit + "");
//            Log.d("scaleRatio1",mRatio + "");
//            Log.d("scaleX1",circuit3D.getScaleX() + "");
//            Log.d("scaleY1",circuit3D.getScaleY() + "");
//            Log.d("scaleZ1",circuit3D.getScaleZ() + "");
            mRatio = (double) circuit3D.getCircuitHeight() / (double) mTargetCircuitHeight;
            mScale = mScaleInit * mRatio;
//            circuit3D.setScale(mScale,-mScale,mScale);
//            Log.d("scaleX2",circuit3D.getScaleX() + "");
//            Log.d("scaleRatio2",mRatio + "");
//            Log.d("scaleX1",circuit3D.getScaleX() + "");
//            Log.d("scaleY1",circuit3D.getScaleY() + "");
//            Log.d("scaleZ1",circuit3D.getScaleZ() + "");

            double initX =  (circuit3D.getCircuitBottomLeftX() + (circuit3D.getCircuitWidth() / 2)) * mScale;
            double initY = (circuit3D.getCircuitBottomLeftY() + (circuit3D.getCircuitHeight()/ 2)) * mScale;


            mPosX = initX * -1.0;
            mPosY = initY * -1.0;
            mPosZ = 0.0;

            mInitPosX = mPosX;
            mInitPosY = mPosY;
            mInitPosZ = mPosZ;

            circuit3D.setX(mInitPosX);
            circuit3D.setY(mInitPosY);
            circuit3D.setZ(mInitPosZ);



            isSetInitialValues = true;



        }
        else{
            mPosX = /*circuit3D.getX() + */(mInitPosX - posXraw) ;
            mPosY = /*circuit3D.getY() + */(mInitPosY - posYraw) ;
            mPosZ = /*circuit3D.getZ() + */(mInitPosZ - posZraw) ;
        }

        mOrientX = /*circuit3D.getOrientation().getPitch() +*/ (mInitOrientX - orientXraw);
        mOrientY = /*circuit3D.getOrientation().getYaw() + */( mInitOrientY - orientYraw);
        mOrientZ = /*circuit3D.getOrientation().getRoll() +*/ (mInitOrientZ - orientZraw);

        // Projection values are set
        isSetProjectionValues = true;



        Log.d("scalePosX",mPosX +"");
        Log.d("scalePosY",mPosY +"");
        Log.d("scalePosZ",mPosZ +"");
        Log.d("scaleInitPosX",mInitPosX +"");
        Log.d("scaleInitPosY",mInitPosY +"");
        Log.d("scaleInitPosZ",mInitPosZ +"");
    }

    public void setCameraValues(int width, int height) {
        mCameraHeight = height;
        mCameraWidth = width;
    }
}

