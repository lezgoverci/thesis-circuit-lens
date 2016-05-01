package ph.edu.msuiit.circuitlens;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;

import de.tavendo.autobahn.WebSocketException;
import ph.edu.msuiit.circuitlens.ui.CircuitLensView;

public class CircuitLensClientController implements CircuitSimulatorClientListener{
    RemoteNetlistGenerator mClient;
    CircuitLensView mView;

    public CircuitLensClientController(CircuitLensView view, RemoteNetlistGenerator client){
        mClient = client;
        mView = view;
    }

    public void onCreate(){
        mClient.setNetlistReceivedListener(this);
        try {
            mClient.connect();
        } catch (WebSocketException e) {
            mView.showMessage(e.getMessage());
        }
    }

    public void onFocus(CvCameraViewFrame frame){
        mClient.requestNetlist(frame);
    }

    public void onReceiveNetlist(String netlist){
        mView.showMessage(netlist);
    }

    public void onDestroy() {
        mClient.disconnect();
    }
}
