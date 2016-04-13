package ph.edu.msuiit.circuitlens;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;

import de.tavendo.autobahn.WebSocketException;
import ph.edu.msuiit.circuitlens.ui.CircuitLensClientView;

public class CircuitLensClientController implements CircuitSimulatorClientListener{
    CircuitSimulatorClient mClient;
    CircuitLensClientView mView;

    public CircuitLensClientController(CircuitLensClientView view, CircuitSimulatorClient client){
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
