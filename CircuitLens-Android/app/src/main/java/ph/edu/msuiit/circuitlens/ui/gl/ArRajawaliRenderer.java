package ph.edu.msuiit.circuitlens.ui.gl;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import org.rajawali3d.renderer.RajawaliRenderer;

import ph.edu.msuiit.circuitlens.cirsim.Circuit3D;
import ph.edu.msuiit.circuitlens.cirsim.CircuitSimulator;

public class ArRajawaliRenderer extends RajawaliRenderer implements ArRenderer{
    protected Circuit3D circuit3D;
    private ArRendererListener mListener;
    private boolean initialized = false;

    public ArRajawaliRenderer(Context context, ArRendererListener listener) {
        super(context);
        mListener = listener;
        setFrameRate(60);
    }

    public void initScene() {
        getCurrentCamera().setPosition(0, 0, 500);
        getCurrentCamera().setFarPlane(5000);

        if(mListener != null) {
            mListener.onInitScene();
        }
        if(circuit3D != null)
            getCurrentScene().addChild(circuit3D);
        initialized = true;
    }

    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);
        if(mListener != null)
            mListener.onRender(elapsedTime, deltaTime);
    }

    public void onOffsetsChanged(float x, float y, float z, float w, int i, int j) {

    }

    @Override
    public void onTouchEvent(MotionEvent event) {

    }

    @Override
    public void showCircuit(Circuit3D circuit3D) {
        if(initialized) {
            getCurrentScene().addChild(circuit3D);
        }
        circuit3D.setScaleY(-1);
        this.circuit3D = circuit3D;
    }

    @Override
    public void hideCircuit() {
        if(circuit3D != null){
            getCurrentScene().removeChild(circuit3D);
        }
    }
}
