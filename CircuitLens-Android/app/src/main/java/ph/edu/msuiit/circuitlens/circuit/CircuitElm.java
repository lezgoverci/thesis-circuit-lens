package ph.edu.msuiit.circuitlens.circuit;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;

import org.rajawali3d.Object3D;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Cube;
import org.rajawali3d.primitives.Line3D;
import org.rajawali3d.primitives.Plane;

import java.util.Stack;

import ph.edu.msuiit.circuitlens.circuit.elements.RailElm;
import ph.edu.msuiit.circuitlens.circuit.elements.VoltageElm;
import ph.edu.msuiit.circuitlens.ui.gl.Circle3D;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static ph.edu.msuiit.circuitlens.circuit.SiUnits.getCurrentDText;
import static ph.edu.msuiit.circuitlens.circuit.SiUnits.getVoltageDText;

public abstract class CircuitElm {

    protected Object3D circuitElm3D;
    private static final int colorScaleCount = 32;
    private static final int colorScale[] = new int[colorScaleCount];
    public static int whiteColor = Color.WHITE, selectColor = Color.CYAN;

    protected int x, y, x2, y2, flags, nodes[], voltSource;
    protected int dx, dy, dsign;
    protected double dn, dpx1, dpy1;
    protected Point point1, point2, lead1, lead2;
    protected double volts[];
    protected double current, curcount;
    protected Rect boundingBox;
    public boolean selected;
    protected CircuitSimulator sim;

    static {
        for (int i = 0; i < colorScaleCount; i++) {
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
        for (int i = 0; i < sim.elmListSize(); i++) {
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
        for (int i = 0; i < getPostCount() + getInternalNodeCount(); i++) {
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
        for (int i = 0; i < getPostCount(); i++) {
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
        material.setColor(whiteColor);
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
        setBbox(p1.x, p1.y, p2.x, p2.y, w);
    }

    public void setBbox(int x1, int y1, int x2, int y2, double w) {
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
        int iw = (int) Math.floor(w);
        boundingBox.set(x1 - iw, y1 - iw, x2 + iw, y2 + iw);
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

    public void drawBoundingBox(Object3D object3D) {
        Material material = new Material();
        material.setColor(Color.RED);
        Stack<Vector3> boxPts = new Stack<>();
        boxPts.add(new Vector3(boundingBox.left, boundingBox.top, 0));
        boxPts.add(new Vector3(boundingBox.left, boundingBox.bottom, 0));
        boxPts.add(new Vector3(boundingBox.right, boundingBox.bottom, 0));
        boxPts.add(new Vector3(boundingBox.right, boundingBox.top, 0));
        boxPts.add(new Vector3(boundingBox.left, boundingBox.top, 0));

        Line3D boundingBox = new Line3D(boxPts, 3);
        boundingBox.setMaterial(material);
        object3D.addChild(boundingBox);
    }

    public void drawValues(Object3D object3D, String s, double hs) {
        if (s == null) {
            return;
        }

        Bitmap rasterText = Graphics.textAsBitmap(s, 12, whiteColor);
        Material textMaterial = new Material();
        textMaterial.setColor(Color.TRANSPARENT);
        Texture texture = new Texture("text", rasterText);

        int w = rasterText.getWidth();

        try {
            textMaterial.addTexture(texture);
        } catch (ATexture.TextureException e) {
            e.printStackTrace();
        }
        Plane textPlane = new Plane(w, rasterText.getHeight(), 2, 2);
        textPlane.setDoubleSided(true);
        textPlane.setScale(-1, -1, 1);
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
            int xx = xc + abs(dpx) + w / 2;
            if (this instanceof VoltageElm || (x < x2 && y > y2)) {
                xx = xc - (w / 2 + abs(dpx) + 2);
            }
            //g.drawString(s, xx, yc + dpy + ya);
            textPlane.setPosition(xx, yc, 5);
        }
    }

    public void updateCoil(double v1, double v2, Material[] materials) {
        int segments = 30;
        for (int i = 0; i < segments; i++) {
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
        drawDots(object3D, point1, point2, curcount);
    }

    Object3D dots;

    public void drawDots(Object3D object3D, Point pa, Point pb, double pos) {
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
        int count = 0;
        for (double di = pos; di < dn; di += ds) {
            int x0 = (int) (pa.x + di * dx / dn);
            int y0 = (int) (pa.y + di * dy / dn);

            if (count == 0) {
                if (dots == null) {
                    Material material = new Material();
                    material.setColor(Color.YELLOW);
                    dots = new Cube(3);
                    dots.setMaterial(material);
                    dots.setRenderChildrenAsBatch(true);
                    object3D.addChild(dots);
                }
                dots.setPosition(x0, y0, 3);
            } else if (dots.getNumChildren() == 0) {
                Object3D dot = dots.clone();
                dots.addChild(dot);
                dot.setPosition(x0 - dots.getX(), y0 - dots.getY(), 0);
            } else {
                Object3D dot;
                if (count < dots.getNumChildren()) {
                    dot = dots.getChildAt(count - 1);
                } else {
                    dot = dots.getChildAt(0).clone();
                    dots.addChild(dot);
                }
                dot.setPosition(x0 - dots.getX(), y0 - dots.getY(), 0);
            }
            count++;
        }
    }

    public void getInfo(String arr[]) {
    }

    public int getBasicInfo(String arr[]) {
        arr[1] = "I = " + getCurrentDText(getCurrent(), SiUnits.showFormat);
        arr[2] = "Vd = " + getVoltageDText(getVoltageDiff(), SiUnits.showFormat);
        return 3;
    }

    public boolean needsHighlight() {
        return sim.touchElm == this || selected;
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
        } else {
            return colorScale[c];
        }
    }

    public int getPowerColor(double w0) {
        int color;
        w0 *= sim.getPowerMult();
        double w = (w0 < 0) ? -w0 : w0;
        if (w > 1) {
            w = 1;
        }
        int rg = 128 + (int) (w * 127);
        int b = (int) (128 * (1 - w));
        if (w0 > 0) {
            color = Color.rgb(rg, b, b);
        } else {
            color = Color.rgb(b, rg, b);
        }
        return color;
    }

    public int getConductanceColor(double w0) {
        w0 *= sim.getPowerMult();
        sim.getPowerMult();
        //System.out.println(w);
        double w = (w0 < 0) ? -w0 : w0;
        if (w > 1) {
            w = 1;
        }
        int rg = (int) (w * 255);
        return Color.rgb(rg, rg, rg);
    }

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

    public boolean isGraphicElmt() {
        return false;
    }

    public abstract void updateObject3D();

    public abstract Object3D generateObject3D();

    public void setSim(CircuitSimulator sim) {
        this.sim = sim;
    }

    public void move(int newX, int newY) {
        circuitElm3D.setPosition(newX, newY, 0);
    }

    public void selectRect(Rect r) {
        selected = r.intersect(boundingBox);
    }

    public CircuitSimulator getSim() {
        return sim;
    }
}
