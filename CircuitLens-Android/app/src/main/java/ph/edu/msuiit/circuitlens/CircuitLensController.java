package ph.edu.msuiit.circuitlens;

import android.util.Log;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Mat;

import ph.edu.msuiit.circuitlens.render.OpenCvMapper;
import ph.edu.msuiit.circuitlens.ui.AndroidCameraAdapter;
import ph.edu.msuiit.circuitlens.ui.CircuitLensView;

public class CircuitLensController{
    private static final String TAG = "CircuitLens::CLC";
    RemoteNetlistGenerator mNetlistGenerator;

    /** NEW STRUCTURE **/
    OpenCvMapper mMapper;
    AndroidCameraAdapter mCameraAdapter;
    CircuitLensView mViewRenderer;
    private boolean isSetupDone = false;

    public CircuitLensController(CircuitLensView activityView, String serverUri){
        mNetlistGenerator = new RemoteNetlistGenerator(serverUri);

        //mMapper = new OpenCvMapper();
        mViewRenderer = activityView;
        Log.d("checkViewRenderer"," is bound");
    }

    public void onCreate(){
        mNetlistGenerator.connect();
        mCameraAdapter = new AndroidCameraAdapter();
        // TODO transfer to oncreate?
        // get height, width, camera matrix  from camera adapter
        mCameraAdapter.setCameraParameters();       // This will update camera parameters based on the recent camera parameters

        // set height, width, camera matrix to ARView
        mViewRenderer.setCameraMatrix(new double[]{
                mCameraAdapter.getCameraWidth(),
                mCameraAdapter.getCameraHeight(),
                mCameraAdapter.getVerticalFOV(),
                mCameraAdapter.getHorizontalFOV(),
                mCameraAdapter.getAspectRatio()
        });
    }

    public void onFocus(CvCameraViewFrame frame){
        Log.d(TAG,"requestNetlist");
        mNetlistGenerator.requestNetlist(frame,mCallback);
    }

    public void onDestroy() {
        mNetlistGenerator.disconnect();
    }

    RpCallback<String> mCallback = new RpCallback<String>() {
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

        if(!isSetupDone){
            // Reset mapper values

            mMapper = new OpenCvMapper();
            mMapper.onResume();
            mMapper.setCamera(mCameraAdapter.getCameraProjectionMatrix(),mCameraAdapter.getDistortion()); //TODO check if projection matrix is right parameter
            //mMapper.reset(); //TODO create a reset function?
            Log.d("checkMapper"," is bound");
            isSetupDone = true;
        }
    }

    public void map(Mat src, boolean isTakePhoto){
        Log.d("checkMapInController","entering");
        if(isSetupDone && !src.empty()){
            mMapper.setImg(src);
            Log.d("checkMapSetImg","is done");
            mMapper.map(isTakePhoto);
            Log.d("checkMapMapping","is done");
            mMapper.drawDebug(); // turn on debug drawing
            //Log.d("rendererValues",mMapper.isHomographyFound() + "");
            if(mMapper.isHomographyFound()){
                mViewRenderer.setTrigger(isTakePhoto);
                mViewRenderer.setOrientationRoll(mMapper.getOrientationRoll());       // Roll
                mViewRenderer.setOrientationYaw(mMapper.getOrientationYaw());       // Yaw
                mViewRenderer.setOrientationPitch(mMapper.getOrientationPitch());       // Pitch

                mViewRenderer.setTranslationX(mMapper.getTranslationX());
                mViewRenderer.setTranslationY(mMapper.getTranslationY());
                mViewRenderer.setTranslationZ(mMapper.getTranslationZ());


            }




        }

    }


}
