package ph.edu.msuiit.circuitlens.ui.gl;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

import org.rajawali3d.Object3D;
import org.rajawali3d.materials.Material;
import org.rajawali3d.math.MathUtil;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Cube;
import org.rajawali3d.renderer.RajawaliRenderer;

import ph.edu.msuiit.circuitlens.circuit.CircuitCanvas3D;
import ph.edu.msuiit.circuitlens.circuit.CircuitSimulator;

import static ph.edu.msuiit.circuitlens.circuit.OpenGlUtils.getWorldPosition;

public class OpenGlRenderer extends RajawaliRenderer{

    public Context context;
    protected CircuitCanvas3D circuit3D;
    private CircuitSimulator cirsim;

    public OpenGlRenderer(Context context) {
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
        circuit3D.setScaleY(-1);
        //circuit3D.drawBounds(circuit3D);
        getCurrentScene().addChild(circuit3D);

        Log.d(getClass().getSimpleName(), "width: " + getViewportWidth());
        Log.d(getClass().getSimpleName(), "height: " + getViewportHeight());
    }

    private static final double PI_OVER_2 = Math.PI/2.0;
    private static boolean prevFlipX, prevFlipY;

    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);
        cirsim.updateCircuit();
        circuit3D.setRotation(Vector3.Axis.X,50);
        //circuit3D.rotate(Vector3.Axis.Y,1);
        //circuit3D.rotate(Vector3.Axis.Z,1);
    }

    public void onOffsetsChanged(float x, float y, float z, float w, int i, int j) {

    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int[] viewPort = new int[]{0, 0, getViewportWidth(), getViewportHeight()};

            Vector3 position3D = new Vector3();
            getWorldPosition(event.getX(),event.getY(),viewPort, getCurrentCamera(), circuit3D, position3D);

            Material material = new Material();
            material.setColor(Color.BLUE);
            Cube cube = new Cube(10);
            cube.setMaterial(material);
            cube.setPosition(position3D);
            circuit3D.addChild(cube);

            circuit3D.onTouch((int) position3D.x,(int) position3D.y);

            //Log.d(getClass().getSimpleName(), "diff: " + arrayToString(position3D.toArray()));
        }
    }
}
