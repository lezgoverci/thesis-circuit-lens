package ph.edu.msuiit.circuitlens.ui.gl;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

import org.opencv.core.Mat;
import org.rajawali3d.Object3D;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.RajawaliRenderer;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;

import ph.edu.msuiit.circuitlens.circuit.CircuitElm;
import ph.edu.msuiit.circuitlens.circuit.CircuitSimulator;
import ph.edu.msuiit.circuitlens.circuit.elements.CapacitorElm;
import ph.edu.msuiit.circuitlens.circuit.elements.DCVoltageElm;
import ph.edu.msuiit.circuitlens.circuit.elements.GroundElm;
import ph.edu.msuiit.circuitlens.circuit.elements.InductorElm;
import ph.edu.msuiit.circuitlens.circuit.elements.ResistorElm;
import ph.edu.msuiit.circuitlens.circuit.elements.SwitchElm;
import ph.edu.msuiit.circuitlens.circuit.elements.WireElm;

public class OpenGLRenderer  extends RajawaliRenderer {

    public Context context;

    private Object3D circuitDiagram;

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
        final String expectedNetlist =
                "r 176 64 384 64 0 10\n" +
                "s 384 64 448 64 0 1 false\n" +
                "w 176 64 176 336 0\n" +
                "c 384 336 176 336 0 0.000014999999999999998 -0.0000020730346837365553\n" +
                "l 384 64 384 336 0 1 -2.4257677603955542e-8\n" +
                "v 448 336 448 64 0 0 40 5 0 0 0.5\n" +
                "r 384 336 448 336 0 100\n";

        circuitDiagram = new Object3D();
        cirsim.readSetup(expectedNetlist);
        for(CircuitElm elm : cirsim.elmList){
            Object3D circuitElm3d = elm.generateObject3D();
            circuitElm3d.setMaterial(material);
            circuitDiagram.addChild(circuitElm3d);
            Log.d(this.getClass().getSimpleName(),"CircuitElm: " + circuitElm3d.getX() + "," + circuitElm3d.getY());
        }
        // invert y-axis
        circuitDiagram.setScaleY(-1);

        getCurrentScene().addChild(circuitDiagram);
        getCurrentCamera().setZ(100);
        getCurrentCamera().setX(300);
        getCurrentCamera().setFarPlane(500);
        getCurrentCamera().setY(-200);
        getCurrentCamera().setFieldOfView(130);
    }

    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);
        circuitDiagram.rotate(Vector3.Axis.X, 3);
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

    public void setProjectionValues(Mat f) {
//        Calib3d.decomposeHomographyMat(f,);
    }
}

