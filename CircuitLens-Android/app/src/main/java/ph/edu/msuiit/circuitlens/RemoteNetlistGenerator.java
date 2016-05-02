package ph.edu.msuiit.circuitlens;

import android.util.Log;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;

import java.util.concurrent.TimeUnit;

import rx.Observer;
import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.WampClientBuilder;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.connection.IWampConnectorProvider;
import ws.wamp.jawampa.transport.netty.NettyWampClientConnectorProvider;

public class RemoteNetlistGenerator {
    private static final String URI = "ws://aa7e360f.ngrok.io/ws";
    private static final String REALM = "realm1";
    IWampConnectorProvider connectorProvider = new NettyWampClientConnectorProvider();
    WampClient client;

    public void connect() {
        try {
            // Create a builder and configure the client
            WampClientBuilder builder = new WampClientBuilder();
            builder.withConnectorProvider(connectorProvider)
                    .withUri(URI)
                    .withRealm(REALM)
                    .withInfiniteReconnects()
                    .withReconnectInterval(5, TimeUnit.SECONDS);
            // Create a client through the builder. This will not immediately start
            // a connection attempt
            client = builder.build();
        } catch (WampError e) {
            // Catch exceptions that will be thrown in case of invalid configuration
            System.out.println(e);
            return;
        } catch (Exception e) {

        }
        client.open();
    }

    public void disconnect() {
        client.close();
    }

    public void requestNetlist(final CvCameraViewFrame frame, final RPCallback<String> callback) {
        Log.d("onFocus", "FrameSize: " + frame.gray().size());
        final String serializedImage = "";
        // TODO: serialize frame

        client.call("ph.edu.msuiit.circuitlens.recognize", String.class, serializedImage).subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onNext(String netlist) {
                callback.onResult(netlist);
            }
        });
    }
}