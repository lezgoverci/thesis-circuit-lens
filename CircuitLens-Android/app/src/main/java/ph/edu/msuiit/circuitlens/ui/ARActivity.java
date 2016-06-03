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
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.rajawali3d.surface.RajawaliSurfaceView;

import ph.edu.msuiit.circuitlens.CircuitLensController;
import ph.edu.msuiit.circuitlens.R;
import ph.edu.msuiit.circuitlens.ui.gl.OpenGLRenderer;


public class ARActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "CircuitLens::ARActivity";
    private long startTime;
    private CircuitLensController mController;
    protected CameraBridgeViewBase mOpenCvCameraView;

    protected View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
            {
                // this needs to be defined on the renderer:
                Log.d(this.getClass().getSimpleName(),": " + event.getX()+ "," + event.getY());
                //mRenderer.onTouchEvent(event);
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

//        mSurface = (RajawaliSurfaceView) findViewById(R.id.rajawali_surface);
//        mSurface.setOnTouchListener(touchListener);
        mController = new CircuitLensController(serverUri);
        mController.onCreate();
        initializeViews();
//        initializeRenderer();
    }

//    private void initializeRenderer() {
//        mRenderer = new OpenGLRenderer(this);
//        mSurface.setTransparent(true);
//        mSurface.setSurfaceRenderer(mRenderer);
//        mSurface.setZOrderMediaOverlay(true);
//    }

    private void initializeViews(){
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.surfaceView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
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
        mTakePhoto = true;
        return ret;
    }



}