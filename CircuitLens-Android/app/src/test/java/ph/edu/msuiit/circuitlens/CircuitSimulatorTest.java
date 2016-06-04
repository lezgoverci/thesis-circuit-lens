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
        final String expectedNetlist = "$ 1 5.0E-6 14.841315910257661 48.0 5.0 50.0\n" +
                "v 112 240 112 160 0 1 35.0 5.0 0.0 0.0 0.5\n" +
                "r 112 160 464 160 0 10.0\n" +
                "c 464 160 464 240 0 1.5E-5 17.99235566721663\n" +
                "l 112 240 464 240 0 1.0 0.0082376017397377\n" +
                "o 2 64 0 35 40.0 0.2 0 -1\n";
        cirsim.init();
        cirsim.readSetup(expectedNetlist);
        cirsim.updateCircuit();
        assertThat(cirsim.dumpCircuit(),is(expectedNetlist));
    }

    @Test
    public void testRunCircuit() throws Exception {
        CircuitSimulator cirsim = new CircuitSimulator();
        final String expectedNetlist = "$ 1 5.0E-6 14.841315910257661 48.0 5.0 50.0\n" +
                "v 112 240 112 160 0 1 35.0 5.0 0.0 0.0 0.5\n" +
                "r 112 160 464 160 0 10.0\n" +
                "c 464 160 464 240 0 1.5E-5 17.99235566721663\n" +
                "l 112 240 464 240 0 1.0 0.0082376017397377\n" +
                "o 2 64 0 35 40.0 0.2 0 -1\n";
        cirsim.readSetup(expectedNetlist);
        cirsim.runCircuit();
    }
}