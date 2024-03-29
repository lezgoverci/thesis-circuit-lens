package ph.edu.msuiit.circuitlens.cirsim.elements;

import android.graphics.Point;

import org.rajawali3d.Object3D;
import org.rajawali3d.materials.Material;

import java.util.StringTokenizer;

import ph.edu.msuiit.circuitlens.cirsim.CircuitElm;

import static ph.edu.msuiit.circuitlens.cirsim.Graphics.drawThickLine;
import static ph.edu.msuiit.circuitlens.cirsim.Graphics.interpPoint;
import static ph.edu.msuiit.circuitlens.cirsim.Graphics.interpPoint2;

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

    @Override
    public void updateObject3D() {
        if(circuitElm3D == null) {
            circuitElm3D = generateObject3D();
        }
        int color = getVoltageColor(0);
        wireMaterial.setColor(color);
    }

    Material wireMaterial;

    public Object3D generateObject3D() {
        Object3D ground3d = new Object3D();

        Point ps1 = new Point();
        Point ps2 = new Point();

        wireMaterial = new Material();
        drawThickLine(ground3d, point1, point2, wireMaterial);
        int i;
        for (i = 0; i != 3; i++) {
            int a = 10 - i * 4;
            int b = i * 5; // -10;
            interpPoint2(point1, point2, ps1, ps2, 1 + b / dn, a);
            drawThickLine(ground3d, ps1, ps2, wireMaterial);
        }
        doDots(ground3d);
        interpPoint(point1, point2, ps2, 1 + 11. / dn);
        setBbox(point1, ps2, 11);
        drawPost(ground3d, x, y, nodes[0]);

        return ground3d;
    }
}
