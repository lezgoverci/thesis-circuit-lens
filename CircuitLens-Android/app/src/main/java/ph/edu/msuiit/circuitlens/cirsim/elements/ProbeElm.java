package ph.edu.msuiit.circuitlens.cirsim.elements;

import android.graphics.Point;

import org.rajawali3d.Object3D;

import java.util.StringTokenizer;

import ph.edu.msuiit.circuitlens.cirsim.CircuitElm;

import static ph.edu.msuiit.circuitlens.cirsim.Graphics.interpPoint;

public class ProbeElm extends CircuitElm {

    static final int FLAG_SHOWVOLTAGE = 1;

    public ProbeElm(int xx, int yy) {
        super(xx, yy);
    }

    public ProbeElm(int xa, int ya, int xb, int yb, int f,
            StringTokenizer st) {
        super(xa, ya, xb, yb, f);
    }

    public int getDumpType() {
        return 'p';
    }

    Point center;

    public void setPoints() {
        super.setPoints();
        // swap points so that we subtract higher from lower
        if (point2.y < point1.y) {
            Point x = point1;
            point1 = point2;
            point2 = x;
        }
        center = interpPoint(point1, point2, .5);
    }

    /*
    public void draw(Graphics g) {
        int hs = 8;
        setBbox(point1, point2, hs);
        boolean selected = (needsHighlight() || sim.getPlotYElm() == this);
        double len = (selected || sim.getDragElm() == this) ? 16 : dn - 32;
        calcLeads((int) len);
        setVoltageColor(g, volts[0]);
        if (selected) {
            g.setColor(selectColor);
        }
        drawThickLine(g, point1, lead1);
        setVoltageColor(g, volts[1]);
        if (selected) {
            g.setColor(selectColor);
        }
        drawThickLine(g, lead2, point2);
        Font f = new Font("SansSerif", Font.BOLD, 14);
        g.setFont(f);
        if (this == sim.getPlotXElm()) {
            drawCenteredText(g, "X", center.x, center.y, true);
        }
        if (this == sim.getPlotYElm()) {
            drawCenteredText(g, "Y", center.x, center.y, true);
        }
        if (mustShowVoltage()) {
            String s = getShortUnitText(volts[0], "V");
            drawValues(g, s, 4);
        }
        drawPosts(g);
    }*/

    boolean mustShowVoltage() {
        return (flags & FLAG_SHOWVOLTAGE) != 0;
    }

    public boolean getConnection(int n1, int n2) {
        return false;
    }

    @Override
    public void updateObject3D() {
        if(circuitElm3D == null) {
            circuitElm3D = generateObject3D();
        }
    }

    @Override
    public Object3D generateObject3D() {
        return new Object3D();
    }
}
