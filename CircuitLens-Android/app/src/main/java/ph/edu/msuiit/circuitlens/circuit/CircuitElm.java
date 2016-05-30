package ph.edu.msuiit.circuitlens.circuit;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import org.rajawali3d.Object3D;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Plane;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import ph.edu.msuiit.circuitlens.circuit.elements.RailElm;
import ph.edu.msuiit.circuitlens.circuit.elements.VoltageElm;
import ph.edu.msuiit.circuitlens.ui.gl.Circle3D;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static ph.edu.msuiit.circuitlens.circuit.Graphics.draw3DText;

public abstract class CircuitElm {

    protected Object3D circuitElm3D;
    private static final int colorScaleCount = 32;
    private static final int colorScale[] = new int[colorScaleCount];
    public static int whiteColor;

    protected int x, y, x2, y2, flags, nodes[], voltSource;
    protected int dx, dy, dsign;
    protected double dn, dpx1, dpy1;
    protected Point point1, point2, lead1, lead2;
    protected double volts[];
    protected double current, curcount;
    protected Rect boundingBox;
    public boolean selected;
    protected CircuitSimulator sim;

    protected Point ps1 = new Point();
    protected Point ps2 = new Point();

    public static final NumberFormat showFormat, shortFormat, noCommaFormat;

    static {
        int i;
        for (i = 0; i != colorScaleCount; i++) {
            double v = i * 2. / colorScaleCount - 1;
            if (v < 0) {
                int n1 = (int) (128 * -v) + 100;
                int n2 = (int) (100 * (1 + v));
                int n3 = (int) (100 * (1 + v));
                colorScale[i] = Color.rgb(n1, n2, n3);
            } else {
                int n1 = (int) (128 * v) + 100;
                int n2 = (int) (100 * (1 - v));
                int n3 = (int) (100 * (1 - v));
                colorScale[i] = Color.rgb(n2, n1, n3);
            }
        }

        showFormat = DecimalFormat.getInstance();
        showFormat.setMaximumFractionDigits(2);
        shortFormat = DecimalFormat.getInstance();
        shortFormat.setMaximumFractionDigits(1);
        noCommaFormat = DecimalFormat.getInstance();
        noCommaFormat.setMaximumFractionDigits(10);
        noCommaFormat.setGroupingUsed(false);
    }

    public CircuitSimulator getCS() {
        return sim;
    }

    public int getDumpType() {
        return 0;
    }

    public Class getDumpClass() {
        return getClass();
    }

    public int getDefaultFlags() {
        return 0;
    }

    public CircuitElm(int xx, int yy) {
        x = x2 = xx;
        y = y2 = yy;
        flags = getDefaultFlags();
        allocNodes();
        initBoundingBox();
    }

    public CircuitElm(int xa, int ya, int xb, int yb, int f) {
        x = xa;
        y = ya;
        x2 = xb;
        y2 = yb;
        flags = f;
        allocNodes();
        initBoundingBox();
    }

    public void initBoundingBox() {
        boundingBox = new Rect();
        boundingBox.set(min(x, x2), min(y, y2),
                abs(x2 - x) + 1, abs(y2 - y) + 1);
    }

    public void allocNodes() {
        nodes = new int[getPostCount() + getInternalNodeCount()];
        volts = new double[getPostCount() + getInternalNodeCount()];
    }

    // determine if moving this element by (dx,dy) will put it on top of another element
    public boolean allowMove(int dx, int dy) {
        int nx = x + dx;
        int ny = y + dy;
        int nx2 = x2 + dx;
        int ny2 = y2 + dy;
        int i;
        for (i = 0; i != sim.elmListSize(); i++) {
            CircuitElm ce = sim.getElm(i);
            if (ce.x == nx && ce.y == ny && ce.x2 == nx2 && ce.y2 == ny2) {
                return false;
            }
            if (ce.x == nx2 && ce.y == ny2 && ce.x2 == nx && ce.y2 == ny) {
                return false;
            }
        }
        return true;
    }

    public String dump() {
        int t = getDumpType();
        return (t < 127 ? ((char) t) + " " : t + " ") + x + " " + y + " "
                + x2 + " " + y2 + " " + flags;
    }

    public void reset() {
        int i;
        for (i = 0; i != getPostCount() + getInternalNodeCount(); i++) {
            volts[i] = 0;
        }
        curcount = 0;
    }

    public void setCurrent(int x, double c) {
        current = c;
    }

    public double getCurrent() {
        return current;
    }

    public double getWhut() {
        return curcount;
    }

    public void doStep() {
    }

    public void delete() {
    }

    public void startIteration() {
    }

    public double getPostVoltage(int x) {
        return volts[x];
    }

    public void setNodeVoltage(int n, double c) {
        volts[n] = c;
        calculateCurrent();
    }

    public void calculateCurrent() {
    }

    public void setPoints() {
        dx = x2 - x;
        dy = y2 - y;
        dn = Math.sqrt(dx * dx + dy * dy);
        dpx1 = dy / dn;
        dpy1 = -dx / dn;
        dsign = (dy == 0) ? sign(dx) : sign(dy);
        point1 = new Point(x, y);
        point2 = new Point(x2, y2);
    }

    public void calcLeads(int len) {
        if (dn < len || len == 0) {
            lead1 = point1;
            lead2 = point2;
            return;
        }
        lead1 = Graphics.interpPoint(point1, point2, (dn - len) / (2 * dn));
        lead2 = Graphics.interpPoint(point1, point2, (dn + len) / (2 * dn));
    }

    Material lead1Material, lead2Material;

    public void draw2Leads(Object3D object3D) {
        // draw first lead
        lead1Material = new Material();
        Graphics.drawThickLine(object3D, point1, lead1, lead1Material);

        // draw second lead
        lead2Material = new Material();
        Graphics.drawThickLine(object3D, lead2, point2, lead2Material);
    }

    public void update2Leads() {
        // update first lead
        int color1 = getVoltageColor(volts[0]);
        lead1Material.setColor(color1);

        // update second lead
        int color2 = getVoltageColor(volts[1]);
        lead2Material.setColor(color2);
    }

    public Point[] newPointArray(int n) {
        Point a[] = new Point[n];
        while (n > 0) {
            a[--n] = new Point();
        }
        return a;
    }

    public void movePoint(int n, int dx, int dy) {
        if (n == 0) {
            x += dx;
            y += dy;
        } else {
            x2 += dx;
            y2 += dy;
        }
        setPoints();
    }

    public void drawPosts(Object3D object3D) {
        int i;
        for (i = 0; i != getPostCount(); i++) {
            Point p = getPost(i);
            drawPost(object3D, p.x, p.y, nodes[i]);
        }
    }

    public void stamp() {
    }

    public int getVoltageSourceCount() {
        return 0;
    }

    public int getInternalNodeCount() {
        return 0;
    }

    public void setNode(int p, int n) {
        nodes[p] = n;
    }

    public void setVoltageSource(int n, int v) {
        voltSource = v;
    }

    public int getVoltageSource() {
        return voltSource;
    }

    public double getVoltageDiff() {
        return volts[0] - volts[1];
    }

    public boolean nonLinear() {
        return false;
    }

    public int getPostCount() {
        return 2;
    }

    public int getNode(int n) {
        return nodes[n];
    }

    public Point getPost(int n) {
        return (n == 0) ? point1 : (n == 1) ? point2 : null;
    }

    public void drawPost(Object3D object3D, int x0, int y0, int n) {
        if (sim.getCircuitNode(n) != null && sim.getCircuitNode(n).links.size() == 2) {
            return;
        }
        Material material = new Material();
        material.setColor(Color.WHITE);
        Circle3D circle = new Circle3D(new Vector3(x0, y0, 1), 3, 1, true);
        circle.setMaterial(material);
        object3D.addChild(circle);
    }

    public void setBbox(int x1, int y1, int x2, int y2) {
        if (x1 > x2) {
            int q = x1;
            x1 = x2;
            x2 = q;
        }
        if (y1 > y2) {
            int q = y1;
            y1 = y2;
            y2 = q;
        }
        boundingBox.set(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
    }

    public void setBbox(Point p1, Point p2, double w) {
        setBbox(p1.x, p1.y, p2.x, p2.y);
        int dpx = (int) (dpx1 * w);
        int dpy = (int) (dpy1 * w);
        adjustBbox(p1.x + dpx, p1.y + dpy, p1.x - dpx, p1.y - dpy);
    }

    public void adjustBbox(int x1, int y1, int x2, int y2) {
        if (x1 > x2) {
            int q = x1;
            x1 = x2;
            x2 = q;
        }
        if (y1 > y2) {
            int q = y1;
            y1 = y2;
            y2 = q;
        }
        x1 = min(boundingBox.left, x1);
        y1 = min(boundingBox.top, y1);
        x2 = max(boundingBox.left + boundingBox.width() - 1, x2);
        y2 = max(boundingBox.top + boundingBox.height() - 1, y2);
        boundingBox.set(x1, y1, x2 - x1, y2 - y1);
    }

    public void adjustBbox(Point p1, Point p2) {
        adjustBbox(p1.x, p1.y, p2.x, p2.y);
    }

    public boolean isCenteredText() {
        return false;
    }

    public void drawCenteredText(Object3D object3D, String s, int x, int y, boolean cx) {
        //Graphics.draw3DText(object3D, s, x, y, cx);
    }

    public void drawValues(Object3D object3D, String s, double hs) {
        Log.d(getClass().getSimpleName(), "drawValues(object3d,"+s+","+hs+")");
        if (s == null) {
            return;
        }

        Bitmap rasterText = Graphics.textAsBitmap(s, 12, Color.WHITE);
        Material textMaterial = new Material();
        textMaterial.setColor(Color.TRANSPARENT);
        Texture texture = new Texture("text",rasterText);

        int w = rasterText.getWidth();

        try {
            textMaterial.addTexture(texture);
        } catch (ATexture.TextureException e) {
            e.printStackTrace();
        }
        Plane textPlane = new Plane(w, rasterText.getHeight(), 2, 2);
        textPlane.setDoubleSided(true);
        textPlane.setScale(-1,-1,1);
        textPlane.setTransparent(true);
        textPlane.setMaterial(textMaterial);
        object3D.addChild(textPlane);

        /*g.setFont(unitsFont);
        FontMetrics fm = g.getFontMetrics();
        g.setColor(whiteColor);
        int ya = fm.getAscent() / 2;*/

        int xc, yc;
        if (this instanceof RailElm /*|| this instanceof SweepElm*/) {
            xc = x2;
            yc = y2;
        } else {
            xc = (x2 + x) / 2;
            yc = (y2 + y) / 2;
        }
        int dpx = (int) (dpx1 * hs);
        int dpy = (int) (dpy1 * hs);
        if (dpx == 0) {
            //g.drawString(s, xc - w / 2, yc - abs(dpy) - 2);
            textPlane.setPosition(xc, yc - abs(dpy) - 12, 5);
        } else {
            int xx = xc + abs(dpx) + w/2;
            if (this instanceof VoltageElm || (x < x2 && y > y2)) {
                xx = xc - (w/2 + abs(dpx) + 2);
            }
            //g.drawString(s, xx, yc + dpy + ya);
            textPlane.setPosition(xx, yc, 5);
        }
    }

    public void updateCoil(double v1, double v2, Material[] materials) {
        int segments = 30;
        int i;
        double segf = 1. / segments;

        for (i = 0; i != segments; i++) {
            double v = v1 + (v2 - v1) * i / segments;
            int color = getVoltageColor(v);
            materials[i].setColor(color);
        }
    }


    public void updateDotCount() {
        curcount = updateDotCount(current, curcount);
    }

    public double updateDotCount(double cur, double cc) {
        if (sim.isStopped()) {
            return cc;
        }
        double cadd = cur * sim.getCurrentMult();
        cadd %= 8;
        return cc + cadd;
    }

    public void doDots(Object3D object3D) {
        updateDotCount();
        //if (sim.getDragElm() != this) {
        //drawDots(object3D, point1, point2, curcount);
        //}
    }

    public void getInfo(String arr[]) {
    }

    public int getBasicInfo(String arr[]) {
        //arr[1] = "I = " + getCurrentDText(getCurrent());
        //arr[2] = "Vd = " + getVoltageDText(getVoltageDiff());
        return 3;
    }

    public int getVoltageColor(double volts) {
        if (!sim.isShowingVoltage()) {
            if (!sim.isShowingPowerDissipation()) // && !conductanceCheckItem.getState())
            {
                return whiteColor;
            }
        }
        int c = (int) ((volts + sim.getVoltageRange()) * (colorScaleCount - 1)
                / (sim.getVoltageRange() * 2));
        if (c < 0) {
            c = 0;
        }
        if (c >= colorScaleCount) {
            c = colorScaleCount - 1;
        }
        if (sim.isStopped()) {
            return Color.parseColor("#3E80BD");
        } else {
            return colorScale[c];
        }
    }

    /*
    public void setPowerColor(Graphics g, boolean yellow) {
        if (conductanceCheckItem.getState()) {
         setConductanceColor(g, current/getVoltageDiff());
         return;
         
        if (!sim.isShowingPowerDissipation()) {
            return;
        }
        setPowerColor(g, getPower());
    }*/

    /*
    public void setPowerColor(Graphics g, double w0) {
        w0 *= sim.getPowerMult();
        //System.out.println(w);
        double w = (w0 < 0) ? -w0 : w0;
        if (w > 1) {
            w = 1;
        }
        int rg = 128 + (int) (w * 127);
        int b = (int) (128 * (1 - w));
        if (yellow)
         g.setColor(new Color(rg, rg, b));
         else
        if (w0 > 0) {
            g.setColor(new Color(rg, b, b));
        } else {
            g.setColor(new Color(b, rg, b));
        }
    }*/

/*
    public void setConductanceColor(Graphics g, double w0) {
        w0 *= sim.getPowerMult();
        sim.getPowerMult()
        //System.out.println(w);
        double w = (w0 < 0) ? -w0 : w0;
        if (w > 1) {
            w = 1;
        }
        int rg = (int) (w * 255);
        g.setColor(new Color(rg, rg, rg));
    }*/

    public double getPower() {
        return getVoltageDiff() * current;
    }

    public double getScopeValue(int x) {
        return (x == 1) ? getPower() : getVoltageDiff();
    }

    public String getScopeUnits(int x) {
        return (x == 1) ? "W" : "V";
    }

    public boolean getConnection(int n1, int n2) {
        return true;
    }

    public boolean hasGroundConnection(int n1) {
        return false;
    }

    public boolean isWire() {
        return false;
    }

/*    public boolean canViewInScope() {
        return getPostCount() <= 2;
    }*/

    public boolean comparePair(int x1, int x2, int y1, int y2) {
        return ((x1 == y1 && x2 == y2) || (x1 == y2 && x2 == y1));
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean x) {
        selected = x;
    }

    public static int abs(int x) {
        return x < 0 ? -x : x;
    }

    public static int sign(int x) {
        return (x < 0) ? -1 : (x == 0) ? 0 : 1;
    }

    public boolean needsShortcut() {
        return getShortcut() > 0;
    }

    public int getShortcut() {
        return 0;
    }

    public boolean isGraphicElmt() {
        return false;
    }

    public abstract void updateObject3D();

    public abstract Object3D generateObject3D();

    public synchronized static String getUnitText(double v, String u) {
        double va = Math.abs(v);
        if (va < 1e-14) {
            return "0 " + u;
        }
        if (va < 1e-9) {
            return showFormat.format(v * 1e12) + " p" + u;
        }
        if (va < 1e-6) {
            return showFormat.format(v * 1e9) + " n" + u;
        }
        if (va < 1e-3) {
            return showFormat.format(v * 1e6) + " " + CircuitSimulator.muString + u;
        }
        if (va < 1) {
            return showFormat.format(v * 1e3) + " m" + u;
        }
        if (va < 1e3) {
            return showFormat.format(v) + " " + u;
        }
        if (va < 1e6) {
            return showFormat.format(v * 1e-3) + " k" + u;
        }
        if (va < 1e9) {
            return showFormat.format(v * 1e-6) + " M" + u;
        }
        return showFormat.format(v * 1e-9) + " G" + u;
    }

    public void setSim(CircuitSimulator sim) {
        this.sim = sim;
    }

    public void move(int dx, int dy) {

    }

    public void selectRect(Rect r) {
        selected = r.intersect(boundingBox);
    }

    public CircuitSimulator getSim() {
        return sim;
    }
}
