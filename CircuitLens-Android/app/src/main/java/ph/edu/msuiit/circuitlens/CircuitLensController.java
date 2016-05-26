package ph.edu.msuiit.circuitlens;

import android.util.Log;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Mat;

import ph.edu.msuiit.circuitlens.render.CameraProjectionAdapter;
import ph.edu.msuiit.circuitlens.render.OverlayImageTransformationMapper;
import ph.edu.msuiit.circuitlens.ui.CircuitLensView;

public class CircuitLensController{
    private static final String TAG = "CircuitLens::CLC";
    RemoteNetlistGenerator mNetlistGenerator;
    CircuitLensView mView;
    OverlayImageTransformationMapper mMapper;

    /** OTHER VARIABLES **/
    private CameraProjectionAdapter mCameraAdapter;   // Adapter that contains all information about the camera

    public CircuitLensController(CircuitLensView view, String serverUri){
        mView = view;
        mNetlistGenerator = new RemoteNetlistGenerator(serverUri);
    }

    public void onCreate(){
        mNetlistGenerator.connect();
        mCameraAdapter= new CameraProjectionAdapter();
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


    public void onResume(){
        mMapper = new OverlayImageTransformationMapper();
        mMapper.setProjection(mCameraAdapter.getProjectionCV());
    }


    private boolean isHomographyFound(){
        return mMapper.isHomographyFound();
    }

    public void map(Mat src, boolean isTakePhoto){

        mMapper.map(src,isTakePhoto);

        //TODO check if transformation is OK
        if(isHomographyFound()== true){
            // update camera pose using the new transformation from current homography
            mView.updateRendererCameraPose(mMapper.getRVec(),mMapper.getTVec(),mMapper.getmGLPose());
        }
    }
}
