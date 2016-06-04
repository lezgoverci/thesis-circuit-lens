package ph.edu.msuiit.circuitlens.ui.gl;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import org.opencv.core.MatOfDouble;
import org.rajawali3d.Object3D;
import org.rajawali3d.materials.Material;
import org.rajawali3d.math.MathUtil;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Line3D;
import org.rajawali3d.renderer.RajawaliRenderer;
import org.rajawali3d.util.OnObjectPickedListener;
import org.rajawali3d.util.RayPicker;

import java.util.Stack;

import ph.edu.msuiit.circuitlens.circuit.CircuitCanvas3D;
import ph.edu.msuiit.circuitlens.circuit.CircuitSimulator;

public class OpenGLRenderer extends RajawaliRenderer implements OnObjectPickedListener {

    public Context context;

    protected CircuitCanvas3D circuit3D;
    private RayPicker mPicker;
    private CircuitSimulator cirsim;
    private float mTime;
    private Material mMaterial;

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

//        final String expectedNetlist =
//                "$ 1 0.000005 10.200277308269968 50 5 50\n" +
//                        "w 160 64 256 64 1\n" +
//                        "w 256 64 352 64 0\n" +
//                        "w 352 64 448 64 0\n" +
//                        "s 448 64 448 144 0 0 false\n" +
//                        "r 304 64 304 384 0 100\n" +
//                        "r 448 144 448 208 0 800\n" +
//                        "s 448 208 448 384 0 0 false\n" +
//                        "w 160 384 256 384 0\n" +
//                        "w 256 384 352 384 0\n" +
//                        "w 352 384 448 384 0\n" +
//                        "w 160 400 160 64 0\n";
        final String expectedNetlist =
                "$ 3 0.000005 10.200277308269968 50 5 43\n" +
                        "r 0 0 336 0 0 10\n" +
                        "s 336 0 624 0 0 0 false\n" +
                        "w 0 0 0 416 0\n" +
                        "c 336 416 0 416 0 0.000014999999999999999 -1.0809131367750524e-12\n" +
                        "l 336 0 336 416 0 1 0.04999999999998969\n" +
                        "v 624 416 624 0 0 0 40 5 0 0 0.5\n" +
                        "r 336 416 624 416 0 100\n" +
                        "o 4 64 0 35 0.0000762939453125 0.05 0 -1\n" +
                        "o 3 64 0 35 0.0000762939453125 0.00009765625 1 -1\n" +
                        "o 0 64 0 35 0.0000762939453125 0.00009765625 2 -1\n" +
                        "h 1 4 3\n";


        cirsim.readSetup(expectedNetlist);
        cirsim.updateCircuit();

        circuit3D = cirsim.getCircuitCanvas();

        //circuit3D.drawBounds(circuit3D);
        getCurrentScene().addChild(circuit3D);
    }


    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);
        cirsim.updateCircuit();
    }

    public void onOffsetsChanged(float x, float y, float z, float w, int i, int j) {

    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        Log.d(getClass().getSimpleName(),event.toString());
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

        }
    }

    @Override
    public void onObjectPicked(Object3D object) {

    }
}
