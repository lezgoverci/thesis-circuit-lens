package ph.edu.msuiit.circuitlens;

import android.content.Context;
import android.util.Log;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.rajawali3d.surface.RajawaliSurfaceView;

import ph.edu.msuiit.circuitlens.render.CameraProjectionAdapter;
import ph.edu.msuiit.circuitlens.render.OverlayImageTransformationMapper;
import ph.edu.msuiit.circuitlens.ui.CircuitLensView;
import ph.edu.msuiit.circuitlens.ui.gl.OpenGLRenderer;

public class CircuitLensController{
    private static final String TAG = "CircuitLens::CLC";
    RemoteNetlistGenerator mNetlistGenerator;
    CircuitLensView mView;
    OverlayImageTransformationMapper mMapper;


    private final RajawaliSurfaceView mSurface;
    private OpenGLRenderer mRenderer;

    /** OTHER VARIABLES **/
    private CameraProjectionAdapter mCameraAdapter;   // Adapter that contains all information about the camera


    public CircuitLensController(CircuitLensView view, String serverUri, RajawaliSurfaceView surface){
        mView = view;
        mNetlistGenerator = new RemoteNetlistGenerator(serverUri);
        mSurface = surface;
        initRenderer();

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


    public void onResume(){
        mCameraAdapter= new CameraProjectionAdapter();
        mMapper = new OverlayImageTransformationMapper();
    }


    public void map(Mat src,boolean isTakePhoto){

        mMapper.map(src,isTakePhoto);

        //TODO check if transformation is OK
        if(isHomographyFound()== true){
            // update camera pose using the new transformation from current homography
            updateRendererCameraPose();
        }
    }

    private boolean isHomographyFound(){
        return mMapper.isHomographyFound();
    }

    private void initRenderer() {

        mRenderer = new OpenGLRenderer((Context) mView);
        mSurface.setTransparent(true);
        mSurface.setSurfaceRenderer(mRenderer);
        mSurface.setZOrderMediaOverlay(true);
    }

    private void updateRendererCameraPose() {
        //TODO change homography matrix to rotation and translation matrices
        final MatOfDouble projection = mCameraAdapter.getProjectionCV();
        mMapper.setTransformationMatrixValues(projection);
        mRenderer.setProjectionValues(mMapper.getRVec(),mMapper.getTVec(),mMapper.getmGLPose());
    }

//    public void draw(Mat src, Mat dst){
//        mMapper.draw(src,dst);
//    }
}
