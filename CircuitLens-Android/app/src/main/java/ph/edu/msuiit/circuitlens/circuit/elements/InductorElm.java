package ph.edu.msuiit.circuitlens.circuit.elements;

import org.rajawali3d.Object3D;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Line3D;

import java.util.Stack;
import java.util.StringTokenizer;

import ph.edu.msuiit.circuitlens.circuit.CircuitElm;

public class InductorElm extends CircuitElm {

    Inductor ind;
    double inductance;

    public InductorElm(int xx, int yy) {
        super(xx, yy);
        ind = new Inductor(sim);
        inductance = 1;
        ind.setup(inductance, current, flags);
    }

    public InductorElm(int xa, int ya, int xb, int yb, int f,
            StringTokenizer st) {
        super(xa, ya, xb, yb, f);
        ind = new Inductor(sim);
        inductance = new Double(st.nextToken()).doubleValue();
        current = new Double(st.nextToken()).doubleValue();
        ind.setup(inductance, current, flags);
    }

    public int getDumpType() {
        return 'l';
    }

    public String dump() {
        return super.dump() + " " + inductance + " " + current;
    }

    public void setPoints() {
        super.setPoints();
        calcLeads(32);
    }

//    public void draw(Graphics g) {
//        double v1 = volts[0];
//        double v2 = volts[1];
//        int i;
//        int hs = 8;
//        setBbox(point1, point2, hs);
//        draw2Leads(g);
//        setPowerColor(g, false);
//        drawCoil(g, 8, lead1, lead2, v1, v2);
//        if (sim.isShowingValues()) {
//            String s = getShortUnitText(inductance, "H");
//            drawValues(g, s, hs);
//        }
//        doDots(g);
//        drawPosts(g);
//    }

    public void reset() {
        current = volts[0] = volts[1] = curcount = 0;
        ind.reset();
    }

    public void stamp() {
        ind.stamp(nodes[0], nodes[1]);
    }

    public void startIteration() {
        ind.startIteration(volts[0] - volts[1]);
    }

    public boolean nonLinear() {
        return ind.nonLinear();
    }

    public void calculateCurrent() {
        double voltdiff = volts[0] - volts[1];
        current = ind.calculateCurrent(voltdiff);
    }

    public void doStep() {
        double voltdiff = volts[0] - volts[1];
        ind.doStep(voltdiff);
    }

//    public void getInfo(String arr[]) {
//        arr[0] = "inductor";
//        getBasicInfo(arr);
//        arr[3] = "L = " + getUnitText(inductance, "H");
//        arr[4] = "P = " + getUnitText(getPower(), "W");
//    }

//    public EditInfo getEditInfo(int n) {
//        if (n == 0) {
//            return new EditInfo("Inductance (H)", inductance, 0, 0);
//        }
//        if (n == 1) {
//            EditInfo ei = new EditInfo("", 0, -1, -1);
//            ei.checkbox = new Checkbox("Trapezoidal Approximation",
//                    ind.isTrapezoidal());
//            return ei;
//        }
//        return null;
//    }

    public double getInductance() {
        return inductance;
    }

    public Object3D generateObject3D() {
        Object3D object3D = new Object3D();
        double v1 = volts[0];
        double v2 = volts[1];
        int i;
        int hs = 8;
        setBbox(point1, point2, hs);
        object3D.addChild(draw2Leads());
//        setPowerColor(g, false);
        object3D.addChild(drawCoil(8, lead1, lead2, v1, v2));
//        if (sim.isShowingValues()) {
//            String s = getShortUnitText(inductance, "H");
//            drawValues(g, s, hs);
//        }
//        doDots(g);
        object3D.addChild(drawPosts());
        return object3D;
    }
}
