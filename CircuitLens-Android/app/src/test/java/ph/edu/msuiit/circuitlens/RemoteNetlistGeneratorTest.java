package ph.edu.msuiit.circuitlens;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import rx.Observer;
import rx.functions.Action1;
import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.WampClientBuilder;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.connection.IWampConnectorProvider;
import ws.wamp.jawampa.transport.netty.NettyWampClientConnectorProvider;

public class RemoteNetlistGeneratorTest {
    @Test
    public void testWamp() throws Exception {
        IWampConnectorProvider connectorProvider = new NettyWampClientConnectorProvider();
        final WampClient client;
        try {
            // Create a builder and configure the client
            WampClientBuilder builder = new WampClientBuilder();
            builder.withConnectorProvider(connectorProvider)
                    .withUri("ws://aa7e360f.ngrok.io/ws")
                    .withRealm("realm1")
                    .withInfiniteReconnects()
                    .withReconnectInterval(5, TimeUnit.SECONDS);
            // Create a client through the builder. This will not immediately start
            // a connection attempt
            client = builder.build();
        } catch (WampError e) {
            // Catch exceptions that will be thrown in case of invalid configuration
            System.out.println(e);
            return;
        }

        client.statusChanged()
                .subscribe(new Action1<WampClient.State>() {
                    @Override
                    public void call(WampClient.State t1) {
                        System.out.println("Session status changed to " + t1);

                        if (t1 instanceof WampClient.ConnectedState) {
                            client.call("ph.edu.msuiit.circuitlens.recognize", String.class, "PLACE_SERIALIZED_IMAGE_HERE").subscribe(new Observer<String>() {
                                @Override
                                public void onCompleted() {
                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(String netlist) {
                                    System.out.println(netlist);
                                }
                            });
                        }
                    }
                });
        client.open();
        try {
            // Wait until the other client could register the procedure
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        client.close().toBlocking().last();
    }
}
