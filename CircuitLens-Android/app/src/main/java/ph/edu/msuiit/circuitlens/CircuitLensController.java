package ph.edu.msuiit.circuitlens;

import android.content.Context;
import android.util.Log;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.rajawali3d.surface.RajawaliSurfaceView;

import ph.edu.msuiit.circuitlens.render.CameraProjectionAdapter;
import ph.edu.msuiit.circuitlens.render.OverlayImageTransformationMapper;
import ph.edu.msuiit.circuitlens.ui.ARView;
import ph.edu.msuiit.circuitlens.ui.CircuitLensView;
import ph.edu.msuiit.circuitlens.ui.gl.OpenGLRenderer;

public class CircuitLensController{
    private static final String TAG = "CircuitLens::CLC";
    RemoteNetlistGenerator mNetlistGenerator;
    CircuitLensView mView;
    OverlayImageTransformationMapper mMapper;

    /** OTHER VARIABLES **/
    private CameraProjectionAdapter mCameraAdapter;   // Adapter that contains all information about the camera
    private OpenGLRenderer mRenderer;
    private RajawaliSurfaceView mSurface;

    public CircuitLensController(String serverUri){
        mNetlistGenerator = new RemoteNetlistGenerator(serverUri);
    }

    public void onCreate(){
        mNetlistGenerator.connect();
        mCameraAdapter= new CameraProjectionAdapter();
        mView = new ARView();
        initRenderer();
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


        double aspectRatio = mCameraAdapter.getAspectRatio();
        int width = mCameraAdapter.getCameraWidth();
        int height = mCameraAdapter.getCameraHeight();

        // Camera Matrix from Android camera
        MatOfDouble projection = mCameraAdapter.getProjectionCV();

        mMapper.setProjection(projection,aspectRatio,width,height);
        mRenderer.setCameraValues(width,height);
    }


    private boolean isHomographyFound(){
        return mMapper.isHomographyFound();
    }

    public void map(Mat src, boolean isTakePhoto){

        mMapper.map(src,isTakePhoto);

        //TODO check if transformation is OK
        if(isHomographyFound()== true){
            // update camera pose using the new transformation from current homography
            mView.updateRendererCameraPose(mMapper.getRVec(),mMapper.getTVec(),mMapper.getDimens());
        }
    }


    private void initRenderer() {

        mRenderer = new OpenGLRenderer((Context) mView);
        mSurface.setTransparent(true);
        mSurface.setSurfaceRenderer(mRenderer);
        mSurface.setZOrderMediaOverlay(true);

    }

//    private void updateRendererCameraPose() {
//        //compute rotation and translation values
//        //mMapper.setTransformationMatrixValues();
//        // set the computed values to renderer
//        mRenderer.setProjectionValues(mMapper.getRVec(),mMapper.getTVec(),mMapper.getDimens());
//    }

//    public void draw(Mat src, Mat dst){
//        mMapper.draw(src,dst);
//    }

}
