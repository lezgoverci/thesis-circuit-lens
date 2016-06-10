package ph.edu.msuiit.circuitlens.ui.gl;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.rajawali3d.Object3D;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.materials.Material;
import org.rajawali3d.math.MathUtil;
import org.rajawali3d.math.Matrix;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.Plane;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Cube;
import org.rajawali3d.primitives.Line3D;
import org.rajawali3d.renderer.RajawaliRenderer;
import org.rajawali3d.util.GLU;
import org.rajawali3d.util.OnObjectPickedListener;
import org.rajawali3d.util.RayPicker;

import java.util.Stack;

import ph.edu.msuiit.circuitlens.circuit.CircuitCanvas3D;
import ph.edu.msuiit.circuitlens.circuit.CircuitElm;
import ph.edu.msuiit.circuitlens.circuit.CircuitSimulator;
import ph.edu.msuiit.circuitlens.circuit.elements.SwitchElm;

import static ph.edu.msuiit.circuitlens.circuit.Graphics.draw3DText;
import static ph.edu.msuiit.circuitlens.circuit.OpenGLUtils.getWorldPosition;

public class OpenGLRenderer extends RajawaliRenderer{

    public Context context;


    protected CircuitCanvas3D circuit3D;
    private RayPicker mPicker;
    private CircuitSimulator cirsim;

    public OpenGLRenderer(Context context) {
        super(context);
        this.context = context;
        setFrameRate(60);
    }

    public void initScene() {
        cirsim = new CircuitSimulator();
        cirsim.init();
        // String dump = "$ " + f + " "
        //        + timeStep + " " + getIterCount() + " "
        //        + currentBarValue + " " + voltageRange + " "
        //        + powerBarValue + "\n";

        final String expectedNetlist = "$ 1 0.000005 10.20027730826997 50 5 43\n" +
                "r 176 64 384 64 0 10\n" +
                "s 384 64 448 64 0 1 false\n" +
                "w 176 64 176 336 0\n" +
                "c 384 336 176 336 0 0.000014999999999999998 -7.279418928421005\n" +
                "l 384 64 384 336 0 1 -0.0027853453186539572\n" +
                "v 448 336 448 64 0 0 40 5 0 0 0.5\n" +
                "r 384 336 448 336 0 100\n" +
                "o 4 64 0 35 20 0.05 0 -1\n" +
                "o 3 64 0 35 10 0.05 1 -1\n" +
                "o 0 64 0 35 0.625 0.05 2 -1\n" +
                "h 1 4 3\n\n";

        cirsim.readSetup(expectedNetlist);
        cirsim.updateCircuit();


        circuit3D = cirsim.getCircuitCanvas();

        //circuit3D.drawBounds(circuit3D);
        //circuit3D.getTransformedBoundingVolume().setBoundingColor(100);
        getCurrentScene().addChild(circuit3D);

        Log.d(getClass().getSimpleName(), "width: " + getViewportWidth());
        Log.d(getClass().getSimpleName(), "height: " + getViewportHeight());

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
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int[] viewPort = new int[]{0, 0, getViewportWidth(), getViewportHeight()};

            Vector3 position3D = new Vector3();
            getWorldPosition(event.getX(),event.getY(),viewPort, getCurrentCamera(), circuit3D, position3D);

            /*
            Material material = new Material();
            material.setColor(Color.BLUE);
            Cube cube = new Cube(10);
            cube.setMaterial(material);
            cube.setPosition(position3D);
            getCurrentScene().addChild(cube);
            */

            circuit3D.onTouch((int) position3D.x,- (int) position3D.y);

            //Log.d(getClass().getSimpleName(), "diff: " + arrayToString(position3D.toArray()));
        }
    }
}
