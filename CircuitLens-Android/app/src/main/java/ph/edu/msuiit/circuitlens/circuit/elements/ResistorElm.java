package ph.edu.msuiit.circuitlens.circuit.elements;

import android.graphics.Point;

import org.rajawali3d.Object3D;
import org.rajawali3d.materials.Material;

import java.util.StringTokenizer;

import ph.edu.msuiit.circuitlens.circuit.CircuitElm;

public class ResistorElm extends CircuitElm {

    double resistance;

    public ResistorElm(int xx, int yy) {
        super(xx, yy);
        resistance = 100;
    }

    public ResistorElm(int xa, int ya, int xb, int yb, int f,
                       StringTokenizer st) {
        super(xa, ya, xb, yb, f);
        resistance = new Double(st.nextToken()).doubleValue();
    }

    public double getResistance() {
        return resistance;
    }

    public int getDumpType() {
        return 'r';
    }

    @Override
    public String dump() {
        return super.dump() + " " + resistance;
    }

    Point ps3, ps4;

    @Override
    public void setPoints() {
        super.setPoints();
        calcLeads(32);
        ps3 = new Point();
        ps4 = new Point();
    }

//    @Override
//    public void draw(Graphics g) {
//        int SEGMENTS = 16;
//        int i;
//        int ox = 0;
//        int hs = sim.euroResistor() ? 6 : 8;
//        double v1 = volts[0];
//        double v2 = volts[1];
//        setBbox(point1, point2, hs);
//        draw2Leads(g);
//        setPowerColor(g, true);
//        double segf = 1. / SEGMENTS;
//        if (!sim.euroResistor()) {
//            // draw zigzag
//            for (i = 0; i != SEGMENTS; i++) {
//                int nx = 0;
//                switch (i & 3) {
//                    case 0:
//                        nx = 1;
//                        break;
//                    case 2:
//                        nx = -1;
//                        break;
//                    default:
//                        nx = 0;
//                        break;
//                }
//                double v = v1 + (v2 - v1) * i / SEGMENTS;
//                setVoltageColor(g, v);
//                interpPoint(lead1, lead2, ps1, i * segf, hs * ox);
//                interpPoint(lead1, lead2, ps2, (i + 1) * segf, hs * nx);
//                drawThickLine(g, ps1, ps2);
//                ox = nx;
//            }
//        } else {
//            // draw rectangle
//            setVoltageColor(g, v1);
//            interpPoint2(lead1, lead2, ps1, ps2, 0, hs);
//            drawThickLine(g, ps1, ps2);
//            for (i = 0; i != SEGMENTS; i++) {
//                double v = v1 + (v2 - v1) * i / SEGMENTS;
//                setVoltageColor(g, v);
//                interpPoint2(lead1, lead2, ps1, ps2, i * segf, hs);
//                interpPoint2(lead1, lead2, ps3, ps4, (i + 1) * segf, hs);
//                drawThickLine(g, ps1, ps3);
//                drawThickLine(g, ps2, ps4);
//            }
//            interpPoint2(lead1, lead2, ps1, ps2, 1, hs);
//            drawThickLine(g, ps1, ps2);
//        }
//        if (sim.isShowingValues()) {
//            String s = getShortUnitText(resistance, "");
//            drawValues(g, s, hs);
//        }
//        doDots(g);
//        drawPosts(g);
//    }

    @Override
    public void calculateCurrent() {
        current = (volts[0] - volts[1]) / resistance;
        //System.out.print(this + " res current set to " + current + "\n");
    }

    public void stamp() {
        sim.stampResistor(nodes[0], nodes[1], resistance);
    }

//    public void getInfo(String arr[]) {
//        arr[0] = "resistor";
//        getBasicInfo(arr);
//        arr[3] = "R = " + getUnitText(resistance, sim.ohmString);
//        arr[4] = "P = " + getUnitText(getPower(), "W");
//    }

    @Override
    public int getShortcut() {
        return 'r';
    }

    @Override
    public void updateObject3D() {
        if (circuitElm3D == null) {
            circuitElm3D = generateObject3D();
        }
        double v1 = volts[0];
        double v2 = volts[1];
        for (int i = 0; i != SEGMENTS; i++) {
            double v = v1 + (v2 - v1) * i / SEGMENTS;
            int color = getVoltageColor(v);
            colorMaterials[i].setColor(color);
        }

        update2Leads();
    }

    Material[] colorMaterials;
    final int SEGMENTS = 16;

    public Object3D generateObject3D() {
        Object3D resistor3D = new Object3D();
        colorMaterials = new Material[SEGMENTS];
        int i;
        int ox = 0;
        int hs = sim.euroResistor() ? 6 : 8;

        double v1 = volts[0];
        double v2 = volts[1];
        setBbox(point1, point2, hs);
        draw2Leads(resistor3D);

        //setPowerColor(g, true);
        double segf = 1. / SEGMENTS;
        if (!sim.euroResistor()) {
            // draw zigzag
            for (i = 0; i != SEGMENTS; i++) {
                int nx = 0;
                switch (i & 3) {
                    case 0:
                        nx = 1;
                        break;
                    case 2:
                        nx = -1;
                        break;
                    default:
                        nx = 0;
                        break;
                }
                colorMaterials[i] = new Material();
                interpPoint(lead1, lead2, ps1, i * segf, hs * ox);
                interpPoint(lead1, lead2, ps2, (i + 1) * segf, hs * nx);
                drawThickLine(resistor3D, ps1, ps2, colorMaterials[i]);
                ox = nx;
            }
        } else {
            // draw rectangle
            colorMaterials[0] = new Material();
            interpPoint2(lead1, lead2, ps1, ps2, 0, hs);
            drawThickLine(resistor3D, ps1, ps2, colorMaterials[0]);
            for (i = 0; i != SEGMENTS; i++) {
                if (colorMaterials[i] == null) {
                    colorMaterials[i] = new Material();
                }
                interpPoint2(lead1, lead2, ps1, ps2, i * segf, hs);
                interpPoint2(lead1, lead2, ps3, ps4, (i + 1) * segf, hs);
                drawThickLine(resistor3D, ps1, ps3, colorMaterials[i]);
                drawThickLine(resistor3D, ps2, ps4, colorMaterials[i]);
            }
            interpPoint2(lead1, lead2, ps1, ps2, 1, hs);
            drawThickLine(resistor3D, ps1, ps2, colorMaterials[0]);
        }
//        if (sim.isShowingValues()) {
//            String s = getShortUnitText(resistance, "");
//            drawValues(g, s, hs);
//        }
        doDots(resistor3D);
        drawPosts(resistor3D);

        return resistor3D;
    }
}
