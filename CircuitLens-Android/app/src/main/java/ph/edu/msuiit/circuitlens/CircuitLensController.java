package ph.edu.msuiit.circuitlens;

import android.util.Log;
import android.util.StringBuilderPrinter;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Mat;

import ph.edu.msuiit.circuitlens.ui.CircuitLensView;

public class CircuitLensController{
    private static final String TAG = "CircuitLens::CLC";
    RemoteNetlistGenerator mNetlistGenerator;
    CircuitLensView mView;
    OverlayImageTransformationMapper mMapper;

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

    public void setTrackingImage(Mat img, Mat dst){
        mMapper.setTrackingImage(img,dst);
    }

    public String onResume(){
        mMapper = new OverlayImageTransformationMapper();
        return "yehey";
    }

    public void map(Mat src,Mat dst){
        mMapper.map(src,dst);
    }

//    public void draw(Mat src, Mat dst){
//        mMapper.draw(src,dst);
//    }
}
