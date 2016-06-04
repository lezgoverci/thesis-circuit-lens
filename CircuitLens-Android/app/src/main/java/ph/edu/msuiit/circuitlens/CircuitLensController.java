package ph.edu.msuiit.circuitlens;

import android.util.Log;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Mat;
import ph.edu.msuiit.circuitlens.render.OpenCvMapper;
import ph.edu.msuiit.circuitlens.ui.ARView;
import ph.edu.msuiit.circuitlens.ui.AndroidCameraAdapter;

public class CircuitLensController{
    private static final String TAG = "CircuitLens::CLC";
    RemoteNetlistGenerator mNetlistGenerator;

    /** NEW STRUCTURE **/
    OpenCvMapper mMapper;
    AndroidCameraAdapter mCameraAdapter;
    ARView mArView;

    public CircuitLensController(String serverUri){
        mNetlistGenerator = new RemoteNetlistGenerator(serverUri);

        /** New constructors **/
        mMapper = new OpenCvMapper();
        mCameraAdapter = new AndroidCameraAdapter();
        mArView = new ARView();
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
            //mView.showMessage(error);
        }
    };


    public void onResume(){
        // get height, width, camera matrix  from camera adapter
        //mCameraAdapter = new AndroidCameraAdapter();       // Only use this if you want to use new and updated camera parameters
        mCameraAdapter.setCameraParameters();       // This will update camera parameters based on the recent camera parameters

        // set height, width, camera matrix to ARView
        mArView.setCameraMatrix(new double[]{
                mCameraAdapter.getCameraWidth(),
                mCameraAdapter.getCameraHeight(),
                mCameraAdapter.getVerticalFOV(),
                mCameraAdapter.getHorizontalFOV(),
                mCameraAdapter.getAspectRatio()
        });

        // Reset mapper values
        mMapper = new OpenCvMapper();
        mMapper.setCamera(mCameraAdapter.getCameraProjectionMatrix(),mCameraAdapter.getDistortion()); //TODO check if projection matrix is right parameter
        //mMapper.reset(); //TODO create a reset function?

    }

    public void map(Mat src, boolean isTakePhoto){

        mMapper.setImg(src);
        mMapper.map(isTakePhoto);
        mMapper.drawDebug(); // turn on debug drawing

        mArView.setOrientationRoll(mMapper.getOrientationRoll());       // Roll
        mArView.setOrientationYaw(mMapper.getOrientationYaw());       // Yaw
        mArView.setOrientationPitch(mMapper.getOrientationPitch());       // Pitch

        mArView.setTranslationX(mMapper.getTranslationX());
        mArView.setTranslationY(mMapper.getTranslationY());
        mArView.setTranslationZ(mMapper.getTranslationZ());

    }

}
