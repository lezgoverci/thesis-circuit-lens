package ph.edu.msuiit.circuitlens.main.controller;

import android.util.Log;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.rajawali3d.Object3D;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.math.vector.Vector3;

import ph.edu.msuiit.circuitlens.R;
import ph.edu.msuiit.circuitlens.main.model.CircuitLensModel;
import ph.edu.msuiit.circuitlens.main.view.CircuitLensView;
import ph.edu.msuiit.circuitlens.netlist.RemoteNetlistGenerator.RpCallback;
import ph.edu.msuiit.circuitlens.ui.gl.ArRendererListener;

import static ph.edu.msuiit.circuitlens.cirsim.OpenGlUtils.getWorldPosition;

/* CONTROLLER'S RESPONSIBILITY:
 *
 * Mediate user's actions between model and view.
 *
 * This class contains the main app behaviors receives all events from the user.
 *
 * NOTE: ONLY METHODS RELATING TO EVENTS SHOULD BE CREATED IN THIS CLASS.
 *       NO COMPLEX LOGIC OR CALCULATIONS SHOULD BE DONE IN THIS CLASS.
 *       LIMIT CONTENTS OF EACH METHOD TO AT MOST TEN LINES.
 */
public class CircuitLensController implements CameraBridgeViewBase.CvCameraViewListener2, ArRendererListener {
    private static final String TAG = CircuitLensController.class.getSimpleName();
    CircuitLensModel mModel;
    CircuitLensView mView;

    private long startTime;

    public CircuitLensController(CircuitLensModel model, CircuitLensView view) {
        mModel = model;
        mView = view;
        bindControllerOnView();

        onCreate();
    }

    private void bindControllerOnView() {
        mView.bind(this);
    }

    /*
     *  Controller is created.
     */
    public void onCreate() {

    }

    /*
     *  App is being paused.
     */
    public void onPause() {
        mView.disableCameraView();
    }

    /*
     *  App is being closed.
     */
    public void onClose() {
        mView.disableCameraView();
        mModel.disconnectFromServer();
    }

    /*
     *  Screen is tapped.
     */
    public void onTap(float x, float y) {
        int[] viewPort = new int[]{0, 0, mView.getViewportWidth(), mView.getViewportHeight()};
        Camera camera = mView.getCamera();
        Vector3 position3D = new Vector3();
        getWorldPosition(x, y,viewPort, camera, mModel.getCircuitCanvas(), position3D);
        mModel.onTapCircuit((int) position3D.x,(int) position3D.y);
    }

    public void onResume() {
        if(mModel.fpsMeterEnabled()) {
            mView.enableFpsMeter(true);
        }
        mView.enableCameraView();
        mModel.connectToServer();
    }

    /*
     *  Receives results of remote procedure call by the RemoteNetlistGenerator class
     */
    RpCallback<String> mCallback = new RpCallback<String>() {
        @Override
        public void onResult(String netlist) {
            // TODO: display netlist
            Log.d(TAG, "Received");
            System.out.print(netlist);

            // simulate circuit
            mModel.simulateCircuit(netlist);
            mView.showCircuit(mModel.getCircuitCanvas());
        }

        @Override
        public void onError(String error) {
            Log.d(TAG, error);
            mView.showMessage(error);
        }
    };

    @Override
    public void onCameraViewStarted(int width, int height) {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat frame = inputFrame.rgba();
        long currentTime = System.currentTimeMillis();
        if ((currentTime - startTime) >= 3000) {
            startTime = currentTime;

            // TODO: check if image is blurry before requesting netlist
            mModel.requestNetlist(inputFrame.gray(), mCallback);
        }
        return frame;
    }

    @Override
    public void onRender(long elapsedTime, double deltaTime) {
        if(mModel.updateCircuitCanvas3D()){
            mView.setUpperLeftText("t=" + mModel.getTimeText());
            mView.setBottomLeftText(mModel.getHintText());
        }
        if(mModel.rotateCircuit()){
            Object3D circuit3D = mModel.getCircuitCanvas();
            if(circuit3D != null) {
                circuit3D.rotate(Vector3.Axis.X, 1);
                circuit3D.rotate(Vector3.Axis.Y, 0.5);
                circuit3D.rotate(Vector3.Axis.Z, 0.25);
            }
        }
    }

    @Override
    public void onInitScene() {
        if(mModel.useTestCircuit()) {
            mModel.simulateTestCircuit();
            onChangeSimulatorState();
            mView.showCircuit(mModel.getCircuitCanvas());
        }
    }

    public void onSettingsButtonClick() {
        mView.showPopUpMenu();
    }

    public void onFocusButtonClick() {
        long currentTime = System.currentTimeMillis();
        startTime = currentTime + 3000;
    }

    public void onPlayButtonClick() {
        boolean stopped = mModel.getStoppedSimulator();
        mModel.setStoppedSimulator(!stopped);
        onChangeSimulatorState();
    }

    public void onChangeSimulatorState(){
        if(mModel.getStoppedSimulator()){
            mView.setPlayButtonIcon(R.drawable.ic_action_play);
        } else{
            mView.setPlayButtonIcon(R.drawable.ic_action_stop);
        }
    }
}
