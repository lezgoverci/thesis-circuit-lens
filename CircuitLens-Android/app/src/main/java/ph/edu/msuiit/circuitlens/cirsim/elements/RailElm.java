package ph.edu.msuiit.circuitlens.cirsim.elements;

//import java.awt.Font;
//import java.awt.Graphics;

import org.rajawali3d.Object3D;
import org.rajawali3d.materials.Material;

import java.util.StringTokenizer;

import static ph.edu.msuiit.circuitlens.cirsim.Graphics.drawThickLine;
import static ph.edu.msuiit.circuitlens.cirsim.Graphics.interpPoint;

public class RailElm extends VoltageElm {

    public RailElm(int xx, int yy) {
        super(xx, yy, WF_DC);
    }

    public RailElm(int xx, int yy, int wf) {
        super(xx, yy, wf);
    }

    public RailElm(int xa, int ya, int xb, int yb, int f,
                   StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
    }

    final int FLAG_CLOCK = 1;

    @Override
    public int getDumpType() {
        return 'R';
    }

    @Override
    public int getPostCount() {
        return 1;
    }

    @Override
    public void setPoints() {
        super.setPoints();
        lead1 = interpPoint(point1, point2, 1 - circleSize / dn);
    }

    @Override
    public void updateObject3D() {
        if (circuitElm3D == null) {
            circuitElm3D = generateObject3D();
        }
        int color = getVoltageColor(volts[0]);
        wireMaterial.setColor(color);
    }

    Material wireMaterial;

    @Override
    public Object3D generateObject3D() {
        Object3D rail3D = new Object3D();
        setBbox(point1, point2, circleSize);

        wireMaterial = new Material();
        drawThickLine(rail3D, point1, lead1, wireMaterial);
        boolean clock = waveform == WF_SQUARE && (flags & FLAG_CLOCK) != 0;
        if (waveform == WF_DC || waveform == WF_VAR || clock) {
            //Font f = new Font("SansSerif", 0, 12);
            //g.setFont(f);
            //g.setColor(needsHighlight() ? selectColor : whiteColor);
            //setPowerColor(g, false);
            double v = getVoltage();
            //String s = getShortUnitText(v, "V");
            //if (Math.abs(v) < 1) {
            //    s = showFormat.format(v) + "V";
            //}
            //if (getVoltage() > 0) {
            //    s = "+" + s;
            //}
            //if (this instanceof AntennaElm) {
            //    s = "Ant";
            //}
            //if (clock) {
            //    s = "CLK";
            //}
            //drawCenteredText(g, s, x2, y2, true);
        } else {
            drawWaveform(rail3D, point2);
        }
        drawPosts(rail3D);
        curcount = updateDotCount(-current, curcount);

        //drawDots(rail3D, point1, lead1, curcount);
        return rail3D;
    }

    @Override
    public double getVoltageDiff() {
        return volts[0];
    }

    @Override
    public void stamp() {
        if (waveform == WF_DC) {
            sim.stampVoltageSource(0, nodes[0], voltSource, getVoltage());
        } else {
            sim.stampVoltageSource(0, nodes[0], voltSource);
        }
    }

    @Override
    public void doStep() {
        if (waveform != WF_DC) {
            sim.updateVoltageSource(0, nodes[0], voltSource, getVoltage());
        }
    }

    @Override
    public boolean hasGroundConnection(int n1) {
        return true;
    }
}
