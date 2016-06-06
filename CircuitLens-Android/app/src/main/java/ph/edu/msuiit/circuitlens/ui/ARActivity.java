package ph.edu.msuiit.circuitlens.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.rajawali3d.surface.RajawaliSurfaceView;

import ph.edu.msuiit.circuitlens.CircuitLensController;
import ph.edu.msuiit.circuitlens.R;
import ph.edu.msuiit.circuitlens.ui.gl.RendererTransformations;


public class ARActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2,CircuitLensView {
    private static final String TAG = "CircuitLens::ARActivity";
    private long startTime;
    protected CircuitLensController mController;
    protected CameraBridgeViewBase mOpenCvCameraView;
    protected RendererTransformations mRenderer;
    protected RajawaliSurfaceView mSurface;

    protected View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
            {
                // this needs to be defined on the renderer:
                //Log.d(this.getClass().getSimpleName(),": " + event.getX()+ "," + event.getY());
                //mRenderer.onTouchEvent(event);
                mTakePhoto = true;
                Log.d("touch", "touched in Rajawali");
            }
            return true;
        }
    };

    // V: camera shutter
    private boolean mTakePhoto = false;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.d(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    Log.d("checkOpenCvCameraView"," is enabled");
                    mController.onResume();

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // If the Android version is lower than Jellybean, use this call to hide
        // the status bar.
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else{
            View decorView = getWindow().getDecorView();
            // Hide the status bar in Jellybean up
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }

        setContentView(R.layout.activity_ar);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String serverUri = preferences.getString("server_uri","ws://127.0.0.1:8080/ws");
        boolean rotate = preferences.getBoolean("rotate",false);



        mController = new CircuitLensController(this,serverUri);
        mController.onCreate();
        Log.d("checkController"," is bound to activity");

        initializeViews();
        initializeRenderer();


    }

    public void initializeViews(){
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.surfaceView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        Log.d("checkCameraView"," is bound to surface view");
    }

    private void initializeRenderer(){
        mRenderer = new RendererTransformations(this);
        mSurface = (RajawaliSurfaceView) findViewById(R.id.rajawali_surface);
        mSurface.setOnTouchListener(mTouchListener);
        mSurface.setTransparent(true);
        mSurface.setSurfaceRenderer(mRenderer);
        mSurface.setZOrderMediaOverlay(true);
        Log.d("checkRenderer"," is bound to surface view");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);

        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        Log.d("checkOnResume", "done");
    }

    public void onDestroy() {
        super.onDestroy();
        mController.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

    }

    public void onCameraViewStarted(int width, int height) {
        startTime = System.currentTimeMillis();
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Log.d("checkOnCameraFrame","entering");
        Mat frame = inputFrame.rgba();
        long currentTime = System.currentTimeMillis();
        if((currentTime - startTime) >= 3000){
            Log.d(TAG,"diff: "+String.valueOf(currentTime-startTime));
            startTime = currentTime;

            // TODO: check if image is blurry

            mController.onFocus(inputFrame);
        }


        // Update renderer using the current frame
        mController.map(frame,mTakePhoto); //TODO use AsyncTask?
        mTakePhoto = false;

        return frame;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);
        Log.d("touch", "touched in Opencv");
        return ret;
    }


    @Override
    public void showMessage(String message) {

    }

    @Override
    public void setOrientation(double roll, double yaw, double pitch) {
        mRenderer.setOrientation(roll,yaw,pitch);
    }

    @Override
    public void setOrientationRoll(double roll) {
        mRenderer.setOrientationRoll(roll);
    }

    @Override
    public void setOrientationYaw(double yaw) {
        mRenderer.setOrientationYaw(yaw);
    }

    @Override
    public void setOrientationPitch(double pitch) {
        mRenderer.setOrientationPitch(pitch);
    }

    @Override
    public void setTranslation(double x, double y, double z) {
        mRenderer.setTranslation(x,y,z);
    }

    @Override
    public void setTranslationX(double x) {
        mRenderer.setTranslationX(x);
    }

    @Override
    public void setTranslationY(double y) {
        mRenderer.setTranslationY(y);
    }

    @Override
    public void setTranslationZ(double z) {
        mRenderer.setTranslationZ(z);
    }

    @Override
    public void setScaleTransformation(double x, double y, double z) {
        mRenderer.setScaleTransformation(x,y,z);
    }

    @Override
    public void setScaleTransformationX(double x) {
        mRenderer.setScaleTransformationX(x);
    }

    @Override
    public void setScaleTransformationY(double y) {
        mRenderer.setScaleTransformationY(y);
    }

    @Override
    public void setScaleTransformationZ(double z) {
        mRenderer.setScaleTransformationZ(z);
    }

    @Override
    public void setCameraMatrix(double[] cameraMatrix) {
        int width = (int)cameraMatrix[0];
        int height = (int)cameraMatrix[1];
        double vertical = cameraMatrix[2];
        double horizontal = cameraMatrix[3];
        double aspectRatio = cameraMatrix[4];

        if(mRenderer != null){
            mRenderer.setCameraValues(width,height,vertical,horizontal,aspectRatio);
        }
    }

    @Override
    public void setProjectionMatrix(double[] projectionMatrix) {

    }

    @Override
    public void setTrigger(boolean isTakePhoto) {
        mRenderer.setTrigger(isTakePhoto);
    }

}