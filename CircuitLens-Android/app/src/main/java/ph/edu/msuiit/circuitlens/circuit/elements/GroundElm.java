package ph.edu.msuiit.circuitlens.circuit.elements;

import android.graphics.Color;

import org.rajawali3d.Object3D;
import org.rajawali3d.materials.Material;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Line3D;

import java.util.Stack;
import java.util.StringTokenizer;

import ph.edu.msuiit.circuitlens.circuit.CircuitElm;

public class GroundElm extends CircuitElm {

    public GroundElm(int xx, int yy) {
        super(xx, yy);
    }

    public GroundElm(int xa, int ya, int xb, int yb, int f,
            StringTokenizer st) {
        super(xa, ya, xb, yb, f);
    }

    public int getDumpType() {
        return 'g';
    }

    public int getPostCount() {
        return 1;
    }

//    public void draw(Graphics g) {
//        setVoltageColor(g, 0);
//        drawThickLine(g, point1, point2);
//        int i;
//        for (i = 0; i != 3; i++) {
//            int a = 10 - i * 4;
//            int b = i * 5; // -10;
//            interpPoint2(point1, point2, ps1, ps2, 1 + b / dn, a);
//            drawThickLine(g, ps1, ps2);
//        }
//        doDots(g);
//        interpPoint(point1, point2, ps2, 1 + 11. / dn);
//        setBbox(point1, ps2, 11);
//        drawPost(g, x, y, nodes[0]);
//    }

    public void setCurrent(int x, double c) {
        current = -c;
    }

    public void stamp() {
        sim.stampVoltageSource(0, nodes[0], voltSource, 0);
    }

    public double getVoltageDiff() {
        return 0;
    }

    public int getVoltageSourceCount() {
        return 1;
    }

    public void getInfo(String arr[]) {
        arr[0] = "ground";
//        arr[1] = "I = " + getCurrentText(getCurrent());
    }

    public boolean hasGroundConnection(int n1) {
        return true;
    }

    public int getShortcut() {
        return 'g';
    }

    public Object3D generateObject3D() {
        Object3D object3D = new Object3D();

//        setVoltageColor(g, 0);
        object3D.addChild(drawThickLine(point1, point2));
        int i;
        for (i = 0; i != 3; i++) {
            int a = 10 - i * 4;
            int b = i * 5; // -10;
            interpPoint2(point1, point2, ps1, ps2, 1 + b / dn, a);
            object3D.addChild(drawThickLine(ps1, ps2));
        }
//        doDots(g);
        interpPoint(point1, point2, ps2, 1 + 11. / dn);
        setBbox(point1, ps2, 11);
        object3D.addChild(drawPost(x, y, nodes[0]));
        return object3D;
    }
}
