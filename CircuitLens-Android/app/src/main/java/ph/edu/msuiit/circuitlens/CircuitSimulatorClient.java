package ph.edu.msuiit.circuitlens;

import android.util.Log;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class CircuitSimulatorClient extends WebSocketHandler {
    private static final String WSURI = "ws://localhost:9000";
    private static final String TAG = "CircuitLens::CSSC";
    private WebSocketConnection mConnection = new WebSocketConnection();
    private CircuitLensClientController netlistReceivedListener;

    public CircuitSimulatorClient(){

    }
git init
        
    public void connect() throws WebSocketException {
        mConnection.connect(WSURI, this);
    }

    public void disconnect(){
        mConnection.disconnect();
    }

    public void requestNetlist(CvCameraViewFrame frame) {
        Log.d("onFocus","FrameSize: " + frame.gray().size());
    }

    public void setNetlistReceivedListener(CircuitLensClientController netlistReceivedListener) {
        this.netlistReceivedListener = netlistReceivedListener;
    }

    @Override
    public void onOpen() {
        Log.d(TAG, "Status: Connected to " + WSURI);
        mConnection.sendTextMessage("Hello, world!");
    }

    @Override
    public void onTextMessage(String payload) {
        Log.d(TAG, "Got echo: " + payload);
    }

    @Override
    public void onClose(int code, String reason) {
        Log.d(TAG, "Connection lost.");
    }
}