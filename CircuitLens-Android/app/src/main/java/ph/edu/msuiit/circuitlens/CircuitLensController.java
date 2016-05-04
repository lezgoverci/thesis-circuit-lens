package ph.edu.msuiit.circuitlens;

import android.util.Log;
import android.util.StringBuilderPrinter;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;

import ph.edu.msuiit.circuitlens.ui.CircuitLensView;

public class CircuitLensController{
    private static final String TAG = "CircuitLens::CLC";
    RemoteNetlistGenerator mNetlistGenerator;
    CircuitLensView mView;

    public CircuitLensController(CircuitLensView view){
        mView = view;
        mNetlistGenerator = new RemoteNetlistGenerator();
    }

    public void onCreate(){
        mNetlistGenerator.connect();
    }

    public void onFocus(CvCameraViewFrame frame){
        Log.d(TAG,"requestNetlist");
        mNetlistGenerator.requestNetlist(frame,mCallback);
    }

    public void onDestroy() {
        mNetlistGenerator.disconnect();
    }

    RPCallback<String> mCallback = new RPCallback<String>() {
        @Override
        public void onResult(String netlist) {
            // TODO: display netlist
            Log.d(TAG,"Received");
            System.out.print(netlist);
        }

        @Override
        public void onError(String error) {
            Log.d(TAG,error);
            mView.showMessage(error);
        }
    };
}
