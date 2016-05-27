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
import org.rajawali3d.util.OnObjectPickedListener;
import org.rajawali3d.util.RayPicker;

import ph.edu.msuiit.circuitlens.circuit.CircuitCanvas3D;
import ph.edu.msuiit.circuitlens.circuit.CircuitSimulator;

public class OpenGLRenderer extends RajawaliRenderer implements OnObjectPickedListener {

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
    private RayPicker mPicker;
    private CircuitSimulator cirsim;

    public OpenGLRenderer(Context context) {
        super(context);
        this.context = context;
        setFrameRate(60);
    }

    public void initScene() {
        mPicker = new RayPicker(this);
        mPicker.setOnObjectPickedListener(this);

        cirsim = new CircuitSimulator();
        cirsim.init();

        // String dump = "$ " + f + " "
        //        + timeStep + " " + getIterCount() + " "
        //        + currentBarValue + " " + voltageRange + " "
        //        + powerBarValue + "\n";

        final String expectedNetlist = "$ 1 0.000005 14.841315910257661 48 5 50\n" +
                "v 128 128 128 48 0 1 35 5 0 0 0.5\n" +
                "v 128 240 128 160 0 1 41.09 5 0 0 0.5\n" +
                "v 128 352 128 272 0 1 45 5 0 0 0.5\n" +
                "r 128 48 480 48 0 10\n" +
                "c 480 48 480 128 0 0.000015 -13.442730220640706\n" +
                "l 128 128 480 128 0 1 -0.07723247812140081\n" +
                "r 128 160 480 160 0 10\n" +
                "c 480 160 480 240 0 0.000015 19.43400535188842\n" +
                "l 128 240 480 240 0 1 -0.08168000341131327\n" +
                "r 128 272 480 272 0 10\n" +
                "c 480 272 480 352 0 0.000015 25.178314901249074\n" +
                "l 128 352 480 352 0 1 0.0007998750679867475\n" +
                "o 4 64 0 35 40 0.1 0 -1\n" +
                "o 7 64 0 35 40 0.2 1 -1\n" +
                "o 10 64 0 35 40 0.1 2 -1\n" +
                "h 1 5 4\n";

        cirsim.readSetup(expectedNetlist);
        //cirsim.analyzeCircuit();
        //cirsim.runCircuit();
        //cirsim.generateCanvas();
        cirsim.updateCircuit();

        circuit3D = cirsim.getCircuitCanvas();
        circuit3D.setScale(1, -1, 1);

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

        Log.d(getClass().getSimpleName(),"width: "+ getViewportWidth());
        Log.d(getClass().getSimpleName(),"height: "+ getViewportHeight());
    }

    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);
        cirsim.updateCircuit();
        /*
        // Use transformation values
        if (isSetProjectionValues) {
            useTransformationValues();
        } else {*/
            //circuit3D.rotate(Vector3.Axis.X, 5);
            //circuit3D.rotate(Vector3.Axis.Y, 1);
        //}
    }

    private void useTransformationValues() {
        // Orientation
        Quaternion orient = new Quaternion();
        // Yaw, Pitch, Roll
        double yaw = mRotX;
        double pitch = mRotY;
        double roll = mRotZ;
        orient.fromEuler(yaw, pitch, roll);
        getCurrentCamera().setOrientation(orient);

        // Translation
        getCurrentCamera().setX(mPosX);
        getCurrentCamera().setY(mPosY);
        getCurrentCamera().setZ(mPosZ);
    }


    public void onOffsetsChanged(float x, float y, float z, float w, int i, int j) {

    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        Log.d(getClass().getSimpleName(),event.toString());
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

        }
    }

    public void setProjectionValues(MatOfDouble rVec, MatOfDouble tVec, float[] pose) {
        // Rotation values in radians
        mRotX = MathUtil.radiansToDegrees(rVec.toArray()[0]);
        mRotY = MathUtil.radiansToDegrees(rVec.toArray()[1]);
        mRotZ = MathUtil.radiansToDegrees(rVec.toArray()[2]);

        // Position values
        mPosX = tVec.toArray()[0];
        mPosY = tVec.toArray()[1];
        mPosZ = tVec.toArray()[2];

        // OpenGL pose
        mGLpose = pose;

        Log.d("setValRot", mRotX + " " + mRotY + " " + mRotZ + " ");
        Log.d("setValTrans", mPosX + " " + mPosY + " " + mPosX + " ");
        Log.d("setValPose", pose.toString());

        // Projection values are set
        isSetProjectionValues = true;
    }

    @Override
    public void onObjectPicked(Object3D object) {

    }
}

