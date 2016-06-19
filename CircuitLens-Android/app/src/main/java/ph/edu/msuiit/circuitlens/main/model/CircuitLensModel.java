package ph.edu.msuiit.circuitlens.main.model;

import org.opencv.core.Mat;

import ph.edu.msuiit.circuitlens.cirsim.Circuit3D;
import ph.edu.msuiit.circuitlens.cirsim.CircuitSimulator;
import ph.edu.msuiit.circuitlens.netlist.RemoteNetlistGenerator;
import ph.edu.msuiit.circuitlens.netlist.RemoteNetlistGenerator.RpCallback;

/* MODEL'S RESPONSIBILITY:
 *
 * Access database using DAOs and use business logic only. It communicates only with
 * with the controller and other models.
 *
 * IMPORTANT: IT DOESN'T CARE ABOUT THE VIEW AND IT DOESN'T KNOW ANYTHING ABOUT THE VIEW
 */
public class CircuitLensModel {
    String mServerUri;
    RemoteNetlistGenerator mNetlistGenerator;
    CircuitSimulator mSimulator;
    boolean mStopped;
    private boolean rotate;
    private boolean useTestCircuit;

    private boolean mFpsMeterEnabled;

    public CircuitLensModel() {
        mSimulator = new CircuitSimulator();
        mSimulator.init();
    }

    public String getServerUri() {
        return mServerUri;
    }

    public void setServerUri(String serverUri) {
        mServerUri = serverUri;
        mNetlistGenerator = new RemoteNetlistGenerator(mServerUri);
    }

    public void requestNetlist(Mat frame, final RpCallback<String> callback) {
        mNetlistGenerator.requestNetlist(frame, callback);
    }

    public void connectToServer() {
        mNetlistGenerator.connect();
    }

    public void disconnectFromServer() {
        mNetlistGenerator.disconnect();
    }

    public void simulateCircuit(String netlist) {
        mSimulator.readSetup(netlist);
        mSimulator.prepareCircuit();
    }

    public Circuit3D getCircuitCanvas() {
        return mSimulator.getCircuitCanvas();
    }

    public void simulateTestCircuit() {
        final String testNetlist = "$ 1 0.000005 10.20027730826997 63 10 62\n" +
                "v 112 368 112 48 0 0 40 10 0 0 0.5\n" +
                "r 240 48 240 208 0 10000\n" +
                "r 240 208 240 368 0 10000\n" +
                "w 112 368 240 368 0\n" +
                "w 240 48 432 48 0\n" +
                "w 240 368 432 368 0\n" +
                "r 432 48 432 128 0 10000\n" +
                "r 432 128 432 208 0 10000\n" +
                "r 432 208 432 288 0 10000\n" +
                "r 432 288 432 368 0 10000\n" +
                "s 112 48 240 48 0 0 false\n";
        mSimulator.readSetup(testNetlist);
        mSimulator.prepareCircuit();
    }

    public boolean updateCircuitCanvas3D() {
        if(mSimulator.cv != null) {
            return mSimulator.updateCircuit();
        }
        return false;
    }

    public String getTimeText() {
        return mSimulator.getTimeUnitText();
    }

    public String getHintText() {
        return mSimulator.getHintText();
    }

    public void onTapCircuit(int x, int y) {
        mSimulator.onTap(x,y);
    }

    public void setStoppedSimulator(boolean stopped) {
        mStopped = stopped;
        mSimulator.setStopped(stopped);
    }

    public boolean getStoppedSimulator(){
        return mStopped;
    }


    public void setRotate(boolean rotate) {
        this.rotate = rotate;
    }

    public boolean rotateCircuit() {
        return rotate;
    }

    public void setUseTestCircuit(boolean useTestCircuit) {
        this.useTestCircuit = useTestCircuit;
    }

    public boolean useTestCircuit() {
        return useTestCircuit;
    }

    public boolean fpsMeterEnabled() {
        return mFpsMeterEnabled;
    }

    public void setFpsMeterEnabled(boolean fpsMeterEnabled) {
        mFpsMeterEnabled = fpsMeterEnabled;
    }
}
