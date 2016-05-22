package ph.edu.msuiit.circuitlens.circuit;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;

import org.rajawali3d.Object3D;
import org.rajawali3d.materials.Material;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Line3D;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Stack;

import ph.edu.msuiit.circuitlens.ui.gl.Circle3D;

public abstract class CircuitElm {

    private static final int colorScaleCount = 32;
    private static final int colorScale[] = new int[colorScaleCount];
    public static int whiteColor, selectColor, lightGrayColor;
    //protected static final Font unitsFont;

    protected Point ps1 = new Point();
    protected Point ps2 = new Point();

    protected CircuitSimulator sim = null;

    public void setSim(CircuitSimulator sim){
        this.sim = sim;
    }

    public static final NumberFormat showFormat, shortFormat, noCommaFormat;
    public static final double pi = 3.14159265358979323846;

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

        //unitsFont = new Font("SansSerif", 0, 10);

        showFormat = DecimalFormat.getInstance();
        showFormat.setMaximumFractionDigits(2);
        shortFormat = DecimalFormat.getInstance();
        shortFormat.setMaximumFractionDigits(1);
        noCommaFormat = DecimalFormat.getInstance();
        noCommaFormat.setMaximumFractionDigits(10);
        noCommaFormat.setGroupingUsed(false);
    }

    protected int x, y, x2, y2, flags, nodes[], voltSource;
    protected int dx, dy, dsign;
    protected double dn, dpx1, dpy1;
    protected Point point1, point2, lead1, lead2;
    protected double volts[];
    protected double current, curcount;
    protected Rect boundingBox;
    protected boolean noDiagonal;
    public boolean selected;

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
        lead1 = interpPoint(point1, point2, (dn - len) / (2 * dn));
        lead2 = interpPoint(point1, point2, (dn + len) / (2 * dn));
    }


    public Point interpPoint(Point a, Point b, double f) {
        Point p = new Point();
        interpPoint(a, b, p, f);
        return p;
    }

    public void interpPoint(Point a, Point b, Point c, double f) {
        int xpd = b.x - a.x;
        int ypd = b.y - a.y;
        double q = (a.x*(1-f)+b.x*f+.48);
         System.out.println(q + " " + (int) q);
        c.x = (int) Math.floor(a.x * (1 - f) + b.x * f + .48);
        c.y = (int) Math.floor(a.y * (1 - f) + b.y * f + .48);
    }

    public void interpPoint(Point a, Point b, Point c, double f, double g) {
        int xpd = b.x - a.x;
        int ypd = b.y - a.y;
        int gx = b.y - a.y;
        int gy = a.x - b.x;
        g /= Math.sqrt(gx * gx + gy * gy);
        c.x = (int) Math.floor(a.x * (1 - f) + b.x * f + g * gx + .48);
        c.y = (int) Math.floor(a.y * (1 - f) + b.y * f + g * gy + .48);
    }

    public Point interpPoint(Point a, Point b, double f, double g) {
        Point p = new Point();
        interpPoint(a, b, p, f, g);
        return p;
    }

    public void interpPoint2(Point a, Point b, Point c, Point d, double f, double g) {
        int xpd = b.x - a.x;
        int ypd = b.y - a.y;
        int gx = b.y - a.y;
        int gy = a.x - b.x;
        g /= Math.sqrt(gx * gx + gy * gy);
        c.x = (int) Math.floor(a.x * (1 - f) + b.x * f + g * gx + .48);
        c.y = (int) Math.floor(a.y * (1 - f) + b.y * f + g * gy + .48);
        d.x = (int) Math.floor(a.x * (1 - f) + b.x * f - g * gx + .48);
        d.y = (int) Math.floor(a.y * (1 - f) + b.y * f - g * gy + .48);
    }

    public Object3D draw2Leads() {
        Material material = new Material();
        material.setColor(Color.WHITE);
        Object3D leads = new Object3D();
        // draw first lead
        //setVoltageColor(g, volts[0]);
        Stack<Vector3> ptsLead1 = new Stack<>();
        ptsLead1.add(new Vector3(point1.x,point1.y,0));
        ptsLead1.add(new Vector3(lead1.x,lead1.y,0));
        Line3D line1 = new Line3D(ptsLead1,10);
        line1.setMaterial(material);
        leads.addChild(line1);

        // draw second lead
        // setVoltageColor(g, volts[1]);
        Stack<Vector3> ptsLead2 = new Stack<>();
        ptsLead2.add(new Vector3(point2.x,point2.y,0));
        ptsLead2.add(new Vector3(lead2.x,lead2.y,0));
        Line3D line2 = new Line3D(ptsLead2,10);
        line2.setMaterial(material);
        leads.addChild(line2);
        return leads;
    }


    public Point[] newPointArray(int n) {
        Point a[] = new Point[n];
        while (n > 0) {
            a[--n] = new Point();
        }
        return a;
    }

    /*
    public void drawDots(Graphics g, Point pa, Point pb, double pos) {
        if (sim.isStopped() || pos == 0 || !sim.isShowingCurrent()) {
            return;
        }
        int dx = pb.x - pa.x;
        int dy = pb.y - pa.y;
        double dn = Math.sqrt(dx * dx + dy * dy);
        int ds = 16;
        pos %= ds;
        if (pos < 0) {
            pos += ds;
        }
        double di = 0;
        for (di = pos; di < dn; di += ds) {
            int x0 = (int) (pa.x + di * dx / dn);
            int y0 = (int) (pa.y + di * dy / dn);
            g.setColor(Color.yellow);
            g.fillOval(x0 - 1, y0 - 1, 4, 4);
        }
    }*/

    /*
    public Polygon calcArrow(Point a, Point b, double al, double aw) {
        Polygon poly = new Polygon();
        Point p1 = new Point();
        Point p2 = new Point();
        int adx = b.x - a.x;
        int ady = b.y - a.y;
        double l = Math.sqrt(adx * adx + ady * ady);
        poly.addPoint(b.x, b.y);
        interpPoint2(a, b, p1, p2, 1 - al / l, aw);
        poly.addPoint(p1.x, p1.y);
        poly.addPoint(p2.x, p2.y);
        return poly;
    }

    public Polygon createPolygon(Point a, Point b, Point c) {
        Polygon p = new Polygon();
        p.addPoint(a.x, a.y);
        p.addPoint(b.x, b.y);
        p.addPoint(c.x, c.y);
        return p;
    }

    public Polygon createPolygon(Point a, Point b, Point c, Point d) {
        Polygon p = new Polygon();
        p.addPoint(a.x, a.y);
        p.addPoint(b.x, b.y);
        p.addPoint(c.x, c.y);
        p.addPoint(d.x, d.y);
        return p;
    }

    public Polygon createPolygon(Point a[]) {
        Polygon p = new Polygon();
        int i;
        for (i = 0; i != a.length; i++) {
            p.addPoint(a[i].x, a[i].y);
        }
        return p;
    }*/

    public void drag(int xx, int yy) {
        xx = sim.snapGrid(xx);
        yy = sim.snapGrid(yy);
        if (noDiagonal) {
            if (Math.abs(x - xx) < Math.abs(y - yy)) {
                xx = x;
            } else {
                yy = y;
            }
        }
        x2 = xx;
        y2 = yy;
        setPoints();
    }

    /*
    public void move(int dx, int dy) {
        x += dx;
        y += dy;
        x2 += dx;
        y2 += dy;
        boundingBox.move(dx, dy);
        setPoints();
    }*/

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


    public Object3D drawPosts() {
        Object3D posts = new Object3D();
        int i;
        for (i = 0; i != getPostCount(); i++) {
            Point p = getPost(i);
            posts.addChild(drawPost(p.x, p.y, nodes[i]));
        }
        return posts;
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

    public Object3D drawPost(int x0, int y0, int n) {
        /*
        if (sim.getDragElm() == null && !needsHighlight()
                && sim.getCircuitNode(n) != null && sim.getCircuitNode(n).links.size() == 2) {
            return;
        }
        if (sim.mouseMode == CircuitController.MODE_DRAG_ROW
                || sim.mouseMode == CircuitController.MODE_DRAG_COLUMN) {
            return;
        }*/
        return drawPost(x0, y0);
    }

    public Object3D drawPost(int x0, int y0) {
        Material material = new Material();
        material.setColor(Color.WHITE);
        Circle3D circle = new Circle3D(new Vector3(x0,y0,0),3, 1, true);
        circle.setMaterial(material);
        return circle;
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
        boundingBox.set(x1,y1,x2-x1+1,y2-y1+1);
    }

    public void setBbox(Point p1, Point p2, double w) {
        setBbox(p1.x, p1.y, p2.x, p2.y);
        int gx = p2.y - p1.y;
        int gy = p1.x - p2.x;
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
    }/*

    public boolean isCenteredText() {
        return false;
    }

    public void drawCenteredText(Graphics g, String s, int x, int y, boolean cx) {
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(s);
        if (cx) {
            x -= w / 2;
        }
        g.drawString(s, x, y + fm.getAscent() / 2);
        adjustBbox(x, y - fm.getAscent() / 2,
                x + w, y + fm.getAscent() / 2 + fm.getDescent());
    }

    public void drawValues(Graphics g, String s, double hs) {
        if (s == null) {
            return;
        }
        g.setFont(unitsFont);
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(s);
        g.setColor(whiteColor);
        int ya = fm.getAscent() / 2;
        int xc, yc;
        if (this instanceof RailElm || this instanceof SweepElm) {
            xc = x2;
            yc = y2;
        } else {
            xc = (x2 + x) / 2;
            yc = (y2 + y) / 2;
        }
        int dpx = (int) (dpx1 * hs);
        int dpy = (int) (dpy1 * hs);
        if (dpx == 0) {
            g.drawString(s, xc - w / 2, yc - abs(dpy) - 2);
        } else {
            int xx = xc + abs(dpx) + 2;
            if (this instanceof VoltageElm || (x < x2 && y > y2)) {
                xx = xc - (w + abs(dpx) + 2);
            }
            g.drawString(s, xx, yc + dpy + ya);
        }
    }*/

    public Object3D drawCoil(int hs, Point p1, Point p2,
            double v1, double v2) {
        Object3D object3D = new Object3D();
        double len = distance(p1, p2);
        int segments = 30; // 10*(int) (len/10);
        int i;
        double segf = 1. / segments;

        ps1.set(p1.x,p1.y);
        for (i = 0; i != segments; i++) {
            double cx = (((i + 1) * 6. * segf) % 2) - 1;
            double hsx = Math.sqrt(1 - cx * cx);
            if (hsx < 0) {
                hsx = -hsx;
            }
            interpPoint(p1, p2, ps2, i * segf, hsx * hs);
            double v = v1 + (v2 - v1) * i / segments;
//            setVoltageColor(g, v);
            object3D.addChild(drawThickLine(ps1, ps2));
            ps1.set(ps2.x,ps2.y);
        }
        return object3D;
    }

    public static Object3D drawThickLine(int x, int y, int x2, int y2) {
        Material material = new Material();
        material.setColor(Color.WHITE);

        Stack<Vector3> points = new Stack<>();
        points.add(new Vector3(x,y,0));
        points.add(new Vector3(x2,y2,0));
        Line3D line = new Line3D(points,10);
        line.setMaterial(material);

        return line;
    }

    public static Object3D drawThickLine(Point pa, Point pb) {
        Material material = new Material();
        material.setColor(Color.WHITE);

        Stack<Vector3> points = new Stack<>();
        points.add(new Vector3(pa.x,pa.y,0));
        points.add(new Vector3(pb.x,pb.y,0));
        Line3D line = new Line3D(points,10);
        line.setMaterial(material);

        return line;
    }

    /*
    public static void drawThickPolygon(Graphics g, int xs[], int ys[], int c) {
        int i;
        for (i = 0; i != c - 1; i++) {
            drawThickLine(g, xs[i], ys[i], xs[i + 1], ys[i + 1]);
        }
        drawThickLine(g, xs[i], ys[i], xs[0], ys[0]);
    }

    public static void drawThickPolygon(Graphics g, Polygon p) {
        drawThickPolygon(g, p.xpoints, p.ypoints, p.npoints);
    }

    public static void drawThickCircle(Graphics g, int cx, int cy, int ri) {
        int a;
        double m = pi / 180;
        double r = ri * .98;
        for (a = 0; a != 360; a += 20) {
            double ax = Math.cos(a * m) * r + cx;
            double ay = Math.sin(a * m) * r + cy;
            double bx = Math.cos((a + 20) * m) * r + cx;
            double by = Math.sin((a + 20) * m) * r + cy;
            drawThickLine(g, (int) ax, (int) ay, (int) bx, (int) by);
        }
    }

    public static String getVoltageDText(double v) {
        return getUnitText(Math.abs(v), "V");
    }

    public static String getVoltageText(double v) {
        return getUnitText(v, "V");
    }

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

    public static String getShortUnitText(double v, String u) {
        double va = Math.abs(v);
        if (va < 1e-13) {
            return null;
        }
        if (va < 1e-9) {
            return shortFormat.format(v * 1e12) + "p" + u;
        }
        if (va < 1e-6) {
            return shortFormat.format(v * 1e9) + "n" + u;
        }
        if (va < 1e-3) {
            return shortFormat.format(v * 1e6) + CircuitSimulator.muString + u;
        }
        if (va < 1) {
            return shortFormat.format(v * 1e3) + "m" + u;
        }
        if (va < 1e3) {
            return shortFormat.format(v) + u;
        }
        if (va < 1e6) {
            return shortFormat.format(v * 1e-3) + "k" + u;
        }
        if (va < 1e9) {
            return shortFormat.format(v * 1e-6) + "M" + u;
        }
        return shortFormat.format(v * 1e-9) + "G" + u;
    }

    public static String getCurrentText(double i) {
        return getUnitText(i, "A");
    }

    public static String getCurrentDText(double i) {
        return getUnitText(Math.abs(i), "A");
    }

    public void updateDotCount() {
        curcount = updateDotCount(current, curcount);
    }

    public double updateDotCount(double cur, double cc) {
        if (sim.isStopped()) {
            return cc;
        }
        double cadd = cur * sim.getCurrentMult();*/
        /*if (cur != 0 && cadd <= .05 && cadd >= -.05)
         cadd = (cadd < 0) ? -.05 : .05;*/
        //cadd %= 8;
        /*if (cadd > 8)
         cadd = 8;
         if (cadd < -8)
         cadd = -8;*/
        //return cc + cadd;
    //}

/*    public void doDots(Graphics g) {
        updateDotCount();
        if (sim.getDragElm() != this) {
            drawDots(g, point1, point2, curcount);
        }
    }*/

    public void doAdjust() {
    }

    public void setupAdjust() {
    }

    public void getInfo(String arr[]) {
    }

    public int getBasicInfo(String arr[]) {
        //arr[1] = "I = " + getCurrentDText(getCurrent());
        //arr[2] = "Vd = " + getVoltageDText(getVoltageDiff());
        return 3;
    }

    public int getVoltageColor(double volts) {
        if (needsHighlight()) {
            return selectColor;
        }
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
            //g.setColor(Color.decode("#3E80BD"));//blue
            //g.setColor(Color.white);//blue
        } else {
            return colorScale[c];
            //g.setColor(colorScale[c]);
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

//    public EditInfo getEditInfo(int n) {
//        return null;
//    }

//    public void setEditValue(int n, EditInfo ei) {
//    }

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

    public boolean needsHighlight() {
        return sim.mouseElm == this || selected;
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

    public static int min(int a, int b) {
        return (a < b) ? a : b;
    }

    public static int max(int a, int b) {
        return (a > b) ? a : b;
    }

    public static double distance(Point p1, Point p2) {
        double x = p1.x - p2.x;
        double y = p1.y - p2.y;
        return Math.sqrt(x * x + y * y);
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

    public abstract Object3D generateObject3D();
}
