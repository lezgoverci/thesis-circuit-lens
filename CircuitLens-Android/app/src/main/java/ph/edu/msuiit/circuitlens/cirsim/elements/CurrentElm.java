package ph.edu.msuiit.circuitlens.cirsim.elements;

import android.graphics.Color;
import android.graphics.Point;

import org.rajawali3d.Object3D;
import org.rajawali3d.materials.Material;

import java.util.StringTokenizer;

import ph.edu.msuiit.circuitlens.cirsim.CircuitElm;

import static ph.edu.msuiit.circuitlens.cirsim.Graphics.drawThickCircle;
import static ph.edu.msuiit.circuitlens.cirsim.Graphics.drawThickLine;
import static ph.edu.msuiit.circuitlens.cirsim.Graphics.drawTriangle;
import static ph.edu.msuiit.circuitlens.cirsim.Graphics.interpPoint;
import static ph.edu.msuiit.circuitlens.cirsim.SiUnits.getShortUnitText;

public class CurrentElm extends CircuitElm {

    double currentValue;

    public CurrentElm(int xx, int yy) {
        super(xx, yy);
        currentValue = .01;
    }

    public CurrentElm(int xa, int ya, int xb, int yb, int f,
                      StringTokenizer st) {
        super(xa, ya, xb, yb, f);
        try {
            currentValue = new Double(st.nextToken()).doubleValue();
        } catch (Exception e) {
            currentValue = .01;
        }
    }

    public String dump() {
        return super.dump() + " " + currentValue;
    }

    public int getDumpType() {
        return 'i';
    }

    Point[] arrowPoints;
    Point ashaft1, ashaft2, center;

    public void setPoints() {
        super.setPoints();
        calcLeads(26);
        ashaft1 = interpPoint(lead1, lead2, .25);
        ashaft2 = interpPoint(lead1, lead2, .6);
        center = interpPoint(lead1, lead2, .5);
        Point p2 = interpPoint(lead1, lead2, .75);
        arrowPoints = new Point[]{p2, new Point(center.x - 4, center.y), new Point(center.x + 4, center.y)};
    }

    @Override
    public void updateObject3D() {
        if (circuitElm3D == null) {
            circuitElm3D = generateObject3D();
        }
        update2Leads();
        int color = getVoltageColor((volts[0] + volts[1]) / 2);
        colorMaterial.setColor(color);

        doDots(circuitElm3D);
    }

    Material colorMaterial;

    public Object3D generateObject3D() {
        Object3D currentSource3D = new Object3D();
        int cr = 12;
        draw2Leads(currentSource3D);

        //setPowerColor(g, false);
        colorMaterial = new Material();
        drawThickCircle(currentSource3D, center.x, center.y, cr, colorMaterial);
        Material whiteMaterial = new Material();
        whiteMaterial.setColor(Color.WHITE);
        drawThickLine(currentSource3D, ashaft1, ashaft2, whiteMaterial);
        drawTriangle(currentSource3D, arrowPoints, whiteMaterial);
        setBbox(point1, point2, cr);

        if (sim.isShowingValues()) {
            String s = getShortUnitText(currentValue, "A");
            if (dx == 0 || dy == 0) {
                drawValues(currentSource3D, s, cr);
            }
        }
        drawPosts(currentSource3D);
        return currentSource3D;
    }

    public void stamp() {
        current = currentValue;
        sim.stampCurrentSource(nodes[0], nodes[1], current);
    }

    public void getInfo(String arr[]) {
        arr[0] = "current source";
        getBasicInfo(arr);
    }

    public double getVoltageDiff() {
        return volts[1] - volts[0];
    }

}
