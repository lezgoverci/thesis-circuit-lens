package ph.edu.msuiit.circuitlens.circuit.elements;

import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

import org.opencv.core.Mat;
import org.rajawali3d.Object3D;
import org.rajawali3d.materials.Material;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Line3D;

import java.util.Stack;
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
//        int segments = 16;
//        int i;
//        int ox = 0;
//        int hs = sim.euroResistor() ? 6 : 8;
//        double v1 = volts[0];
//        double v2 = volts[1];
//        setBbox(point1, point2, hs);
//        draw2Leads(g);
//        setPowerColor(g, true);
//        double segf = 1. / segments;
//        if (!sim.euroResistor()) {
//            // draw zigzag
//            for (i = 0; i != segments; i++) {
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
//                double v = v1 + (v2 - v1) * i / segments;
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
//            for (i = 0; i != segments; i++) {
//                double v = v1 + (v2 - v1) * i / segments;
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

    public Object3D generateObject3D() {
        Material resistorMaterial = new Material();
        resistorMaterial.setColor(Color.WHITE);
        Object3D resistor3d = new Object3D();
        resistor3d.setMaterial(resistorMaterial);
        int segments = 16;
        int i;
        int ox = 0;
        int hs = sim.euroResistor() ? 6 : 8;
        double v1 = volts[0];
        double v2 = volts[1];
        setBbox(point1, point2, hs);
        draw2Leads(resistor3d);

        //setPowerColor(g, true);
        double segf = 1. / segments;
        if (!sim.euroResistor()) {
            // draw zigzag
            for (i = 0; i != segments; i++) {
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
                double v = v1 + (v2 - v1) * i / segments;
//                setVoltageColor(g, v);
                interpPoint(lead1, lead2, ps1, i * segf, hs * ox);
                interpPoint(lead1, lead2, ps2, (i + 1) * segf, hs * nx);
                drawThickLine(resistor3d, ps1, ps2);
                ox = nx;
            }
        } else {
//            // draw rectangle
//            setVoltageColor(g, v1);
//            interpPoint2(lead1, lead2, ps1, ps2, 0, hs);
//            drawThickLine(g, ps1, ps2);
//            for (i = 0; i != segments; i++) {
//                double v = v1 + (v2 - v1) * i / segments;
//                setVoltageColor(g, v);
//                interpPoint2(lead1, lead2, ps1, ps2, i * segf, hs);
//                interpPoint2(lead1, lead2, ps3, ps4, (i + 1) * segf, hs);
//                drawThickLine(g, ps1, ps3);
//                drawThickLine(g, ps2, ps4);
//            }
//            interpPoint2(lead1, lead2, ps1, ps2, 1, hs);
//            drawThickLine(g, ps1, ps2);
       }
//        if (sim.isShowingValues()) {
//            String s = getShortUnitText(resistance, "");
//            drawValues(g, s, hs);
//        }
//        doDots(g);
        drawPosts(resistor3d);

        return resistor3d;
    }

    /*
    public Object3D generateObject3D() {
        Stack<Vector3> points = new Stack<>();
        points.add(new Vector3(point1.x,point1.y,0));
        points.add(new Vector3(point2.x,point2.y,0));
        Line3D line = new Line3D(points,10);
        return line;
    }*/
}
