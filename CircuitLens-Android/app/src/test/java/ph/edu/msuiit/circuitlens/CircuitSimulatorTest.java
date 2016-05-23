package ph.edu.msuiit.circuitlens;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import ph.edu.msuiit.circuitlens.circuit.CircuitSimulator;
import ph.edu.msuiit.circuitlens.circuit.Scope;
import ph.edu.msuiit.circuitlens.circuit.elements.CapacitorElm;
import ph.edu.msuiit.circuitlens.circuit.elements.DCVoltageElm;
import ph.edu.msuiit.circuitlens.circuit.elements.InductorElm;
import ph.edu.msuiit.circuitlens.circuit.elements.OutputElm;
import ph.edu.msuiit.circuitlens.circuit.elements.ProbeElm;
import ph.edu.msuiit.circuitlens.circuit.elements.ResistorElm;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;
import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.Request;
import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.WampClientBuilder;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.connection.IWampConnectorProvider;
import ws.wamp.jawampa.internal.SingleThreadedComputationScheduler;
import ws.wamp.jawampa.transport.netty.NettyWampClientConnectorProvider;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class CircuitSimulatorTest {
    @Test
    public void testInit() throws Exception {
        CircuitSimulator cirsim = new CircuitSimulator();
        cirsim.init();
    }

    @Test
    public void testDumpCircuit() throws Exception {
        CircuitSimulator cirsim = new CircuitSimulator();
        cirsim.init();
        cirsim.register(ResistorElm.class);
        cirsim.register(InductorElm.class);
        cirsim.register(CapacitorElm.class);
        cirsim.register(DCVoltageElm.class);
        final String expectedNetlist = "$ 1 5.0E-6 14.841315910257661 48.0 5.0 50.0\n" +
                "v 112 240 112 160 0 1 35.0 5.0 0.0 0.0 0.5\n" +
                "r 112 160 464 160 0 10.0\n" +
                "c 464 160 464 240 0 1.5E-5 17.99235566721663\n" +
                "l 112 240 464 240 0 1.0 0.0082376017397377\n" +
                "o 2 64 0 35 40.0 0.2 0 -1\n";
        cirsim.readSetup(expectedNetlist);
        assertThat(cirsim.dumpCircuit(),is(expectedNetlist));
    }

    @Test
    public void testRunCircuit() throws Exception {
        CircuitSimulator cirsim = new CircuitSimulator();
        cirsim.init();
        cirsim.register(ResistorElm.class);
        cirsim.register(InductorElm.class);
        cirsim.register(CapacitorElm.class);
        cirsim.register(DCVoltageElm.class);
        cirsim.register(OutputElm.class);
        cirsim.register(ProbeElm.class);
        final String expectedNetlist = "$ 1 5.0E-6 14.841315910257661 48.0 5.0 50.0\n" +
                "v 112 240 112 160 0 1 35.0 5.0 0.0 0.0 0.5\n" +
                "r 112 160 464 160 0 10.0\n" +
                "c 464 160 464 240 0 1.5E-5 17.99235566721663\n" +
                "l 112 240 464 240 0 1.0 0.0082376017397377\n" +
                "o 2 64 0 35 40.0 0.2 0 -1\n";
        cirsim.readSetup(expectedNetlist);
        cirsim.analyzeCircuit();
        cirsim.runCircuit();
    }

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