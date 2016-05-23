package ph.edu.msuiit.circuitlens.ui.gl;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.rajawali3d.Object3D;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.math.MathUtil;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.RajawaliRenderer;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;

import ph.edu.msuiit.circuitlens.circuit.CircuitElm;
import ph.edu.msuiit.circuitlens.circuit.CircuitSimulator;
import ph.edu.msuiit.circuitlens.circuit.elements.CapacitorElm;
import ph.edu.msuiit.circuitlens.circuit.elements.DCVoltageElm;
import ph.edu.msuiit.circuitlens.circuit.elements.DiodeElm;
import ph.edu.msuiit.circuitlens.circuit.elements.GroundElm;
import ph.edu.msuiit.circuitlens.circuit.elements.InductorElm;
import ph.edu.msuiit.circuitlens.circuit.elements.ResistorElm;
import ph.edu.msuiit.circuitlens.circuit.elements.SwitchElm;
import ph.edu.msuiit.circuitlens.circuit.elements.WireElm;

public class OpenGLRenderer  extends RajawaliRenderer {

    public Context context;

    private Object3D circuitDiagram;
    private double mRotX;
    private double mRotY;
    private double mRotZ;
    private double mPosX;
    private double mPosY;
    private double mPosZ;
    private float[] mGLpose;
    private boolean isSetProjectionValues = false;

    public OpenGLRenderer(Context context) {
        super(context);
        this.context = context;
        setFrameRate(60);
    }

    public void initScene(){
        Material material = new Material();
        material.setColor(Color.WHITE);

        CircuitSimulator cirsim = new CircuitSimulator();
        cirsim.init();
        cirsim.register(ResistorElm.class);
        cirsim.register(InductorElm.class);
        cirsim.register(CapacitorElm.class);
        cirsim.register(DCVoltageElm.class);
        cirsim.register(WireElm.class);
        cirsim.register(SwitchElm.class);
        cirsim.register(DCVoltageElm.class);
        cirsim.register(GroundElm.class);
        cirsim.register(DiodeElm.class);
        final String expectedNetlist = "$ 1 0.000005 10.20027730826997 50 5 43\n" +
                        "r 0 0 208 0 0 10\n" +
                        "s 208 0 272 0 0 1 false\n" +
                        "c 208 272 0 272 0 0.000014999999999999998 0.3433733821905817\n" +
                        "l 208 0 208 272 0 1 -9.225810542827757e-9\n" +
                        "v 272 272 272 0 0 0 40 5 0 0 0.5\n" +
                        "r 208 272 272 272 0 100\n" +
                        "d 0 0 0 272 1 0.805904783\n" +
                        "g 272 272 272 304 0\n" +
                        "o 3 64 0 35 0.0000762939453125 0.00009765625 0 -1\n" +
                        "o 2 64 0 35 2.5 0.00009765625 1 -1\n" +
                        "o 0 64 0 35 0.0000762939453125 0.00009765625 2 -1\n\n";

        circuitDiagram = new Object3D();
        cirsim.readSetup(expectedNetlist);
        //cirsim.analyzeCircuit();
        //cirsim.runCircuit();
        //cirsim.setStopped(false);
        for(CircuitElm elm : cirsim.elmList){
            Object3D circuitElm3d = elm.generateObject3D();
            circuitElm3d.setMaterial(material);
            circuitDiagram.addChild(circuitElm3d);
            Log.d(this.getClass().getSimpleName(),"CircuitElm: " + circuitElm3d.getX() + "," + circuitElm3d.getY());
        }
        // invert y-axis
        circuitDiagram.setScale(1,-1,1);

        getCurrentCamera().setX(120);
        getCurrentCamera().setY(-140);
        getCurrentCamera().setZ(1000);
        getCurrentCamera().setFarPlane(5000);


        getCurrentScene().addChild(circuitDiagram);
    }

    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);


        // Use transformation values
        if(isSetProjectionValues){
            useTransformationValues();
        } else{
            circuitDiagram.rotate(Vector3.Axis.X, 5);
            circuitDiagram.rotate(Vector3.Axis.Y, 1);
        }

    }

    private void useTransformationValues() {

        // Orientation
        Quaternion orient = new Quaternion();
        // Yaw, Pitch, Roll
        double yaw = mRotX;
        double pitch = mRotY;
        double roll = mRotZ;
        orient.fromEuler(yaw,pitch,roll);
        getCurrentCamera().setOrientation(orient);

        // Translation
        getCurrentCamera().setX(mPosX);
        getCurrentCamera().setY(mPosY);
        getCurrentCamera().setZ(mPosZ);
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
        mPosX = tVec.toArray()[0];
        mPosY = tVec.toArray()[1];
        mPosZ = tVec.toArray()[2];

        // OpenGL pose
        mGLpose = pose;

        // Projection values are set
        isSetProjectionValues = true;
    }
}

