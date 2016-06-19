package ph.edu.msuiit.circuitlens.ui;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import ph.edu.msuiit.circuitlens.R;
import ph.edu.msuiit.circuitlens.main.controller.CircuitLensController;
import ph.edu.msuiit.circuitlens.main.model.CircuitLensModel;
import ph.edu.msuiit.circuitlens.main.view.CircuitLensView;


public class ArActivity extends AppCompatActivity {
    private static final String TAG = "CircuitLens::ArActivity";
    private static final int PERMISSIONS_REQUEST_ACCESS_CAMERA = 1;
    CircuitLensModel mModel;
    CircuitLensView mView;
    CircuitLensController mController;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.d(TAG, "OpenCV loaded successfully");
                    mController.onResume();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        fetchSettings();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                // TODO: show reason for permission
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSIONS_REQUEST_ACCESS_CAMERA);
            }
        } else{
            loadOpenCv();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_ar);

        initialize();
    }

    public void initialize(){
        mModel = new CircuitLensModel();
        mView = new CircuitLensView(this);
        mController = new CircuitLensController(mModel, mView);
    }

    private void fetchSettings(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String serverUri = preferences.getString("server_uri", "ws://127.0.0.1:8080/ws");
        boolean rotate = preferences.getBoolean("rotate", false);
        boolean useTestCircuit = preferences.getBoolean("test_circuit", true);
        boolean fpsMeterEnabled = preferences.getBoolean("fps_enabled", false);
        boolean conventionalCurrent = preferences.getBoolean("conventional_current", true);
        boolean showVoltageColor = preferences.getBoolean("show_voltage_color", true);
        boolean showCurrent = preferences.getBoolean("show_current", true);
        boolean showPowerDissipation = preferences.getBoolean("show_power_dissipation", false);
        boolean europeanResistor = preferences.getBoolean("european_resistor", false);

        mModel.setServerUri(serverUri);
        mModel.setRotate(rotate);
        mModel.setUseTestCircuit(useTestCircuit);
        mModel.setFpsMeterEnabled(fpsMeterEnabled);
        mModel.setConventionalCurrent(conventionalCurrent);
        mModel.setShowVoltageColor(showVoltageColor);
        mModel.setShowCurrent(showCurrent);
        mModel.setShowPowerDissipation(showPowerDissipation);
        mModel.setEuropeanResistor(europeanResistor);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadOpenCv();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mController != null)
            mController.onPause();
    }

    public void loadOpenCv(){
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
        if(mController != null)
            mController.onClose();
    }
}