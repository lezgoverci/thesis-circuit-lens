package ph.edu.msuiit.circuitlens.cirsim.elements;

import org.rajawali3d.Object3D;
import org.rajawali3d.materials.Material;

import java.util.StringTokenizer;

import ph.edu.msuiit.circuitlens.cirsim.CircuitElm;
import ph.edu.msuiit.circuitlens.cirsim.SiUnits;

import static ph.edu.msuiit.circuitlens.cirsim.Graphics.drawThickLine;
import static ph.edu.msuiit.circuitlens.cirsim.SiUnits.getCurrentDText;
import static ph.edu.msuiit.circuitlens.cirsim.SiUnits.getVoltageText;

public class WireElm extends CircuitElm {

    public WireElm(int xx, int yy) {
        super(xx, yy);
    }

    public WireElm(int xa, int ya, int xb, int yb, int f,
            StringTokenizer st) {
        super(xa, ya, xb, yb, f);
    }
    static final int FLAG_SHOWCURRENT = 1;
    static final int FLAG_SHOWVOLTAGE = 2;

    public void stamp() {
        sim.stampVoltageSource(nodes[0], nodes[1], voltSource, 0);
    }

    boolean mustShowCurrent() {
        return (flags & FLAG_SHOWCURRENT) != 0;
    }

    boolean mustShowVoltage() {
        return (flags & FLAG_SHOWVOLTAGE) != 0;
    }

    public int getVoltageSourceCount() {
        return 1;
    }

    public void getInfo(String arr[]) {
        arr[0] = "wire";
        arr[1] = "I = " + getCurrentDText(getCurrent(), SiUnits.showFormat);
        arr[2] = "V = " + getVoltageText(volts[0], SiUnits.showFormat);
    }

    public int getDumpType() {
        return 'w';
    }

    public double getPower() {
        return 0;
    }

    public double getVoltageDiff() {
        return volts[0];
    }

    public boolean isWire() {
        return true;
    }

//    public EditInfo getEditInfo(int n) {
//        if (n == 0) {
//            EditInfo ei = new EditInfo("", 0, -1, -1);
//            ei.checkbox = new Checkbox("Show Current", mustShowCurrent());
//            return ei;
//        }
//        if (n == 1) {
//            EditInfo ei = new EditInfo("", 0, -1, -1);
//            ei.checkbox = new Checkbox("Show Voltage", mustShowVoltage());
//            return ei;
//        }
//        return null;
//    }
//
//    public void setEditValue(int n, EditInfo ei) {
//        if (n == 0) {
//            if (ei.checkbox.getState()) {
//                flags = FLAG_SHOWCURRENT;
//            } else {
//                flags &= ~FLAG_SHOWCURRENT;
//            }
//        }
//        if (n == 1) {
//            if (ei.checkbox.getState()) {
//                flags = FLAG_SHOWVOLTAGE;
//            } else {
//                flags &= ~FLAG_SHOWVOLTAGE;
//            }
//        }
//    }

    Material wireMaterial;

    @Override
    public void updateObject3D() {
        if(circuitElm3D == null){
            circuitElm3D = generateObject3D();
        }
        int color = getVoltageColor(volts[0]);
        wireMaterial.setColor(color);
        doDots(circuitElm3D);
    }

    public Object3D generateObject3D() {
        Object3D wire3D = new Object3D();
        wireMaterial = new Material();
        drawThickLine(wire3D, point1, point2, wireMaterial);
        setBbox(point1, point2, 3);
//        if (mustShowCurrent()) {\
//            String s = getShortUnitText(Math.abs(getCurrent()), "A");
//            drawValues(g, s, 4);
//        } else if (mustShowVoltage()) {
//            String s = getShortUnitText(volts[0], "V");
//            drawValues(g, s, 4);
//        }
        drawPosts(wire3D);
        return wire3D;
    }
}
