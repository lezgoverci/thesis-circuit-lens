package ph.edu.msuiit.circuitlens.cirsim;

// CirSim.java (c) 2010 by Paul Falstad
// For information about the theory behind this, see Electronic Circuit & System Simulation Methods by Pillage

import android.graphics.Point;
import android.util.Log;

import org.rajawali3d.Object3D;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.StringTokenizer;

import ph.edu.msuiit.circuitlens.cirsim.elements.CapacitorElm;
import ph.edu.msuiit.circuitlens.cirsim.elements.CurrentElm;
import ph.edu.msuiit.circuitlens.cirsim.elements.DcVoltageElm;
import ph.edu.msuiit.circuitlens.cirsim.elements.DiodeElm;
import ph.edu.msuiit.circuitlens.cirsim.elements.GroundElm;
import ph.edu.msuiit.circuitlens.cirsim.elements.InductorElm;
import ph.edu.msuiit.circuitlens.cirsim.elements.RailElm;
import ph.edu.msuiit.circuitlens.cirsim.elements.ResistorElm;
import ph.edu.msuiit.circuitlens.cirsim.elements.SwitchElm;
import ph.edu.msuiit.circuitlens.cirsim.elements.VoltageElm;
import ph.edu.msuiit.circuitlens.cirsim.elements.WireElm;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static ph.edu.msuiit.circuitlens.cirsim.SiUnits.getUnitText;
import static ph.edu.msuiit.circuitlens.cirsim.SiUnits.showFormat;

public class CircuitSimulator {

    /* static */
    static final int HINT_LC = 1;
    static final int HINT_RC = 2;
    static final int HINT_3DB_C = 3;
    static final int HINT_TWINT = 4;
    static final int HINT_3DB_L = 5;

    public static String ohmString = "\u2126";

    boolean analyzeFlag;
    boolean dumpMatrix;
    double t;
    int hintType = -1, hintItem1, hintItem2;
    String stopMessage;
    double timeStep;
    public ArrayList<CircuitElm> elmList;
    CircuitElm touchElm, stopElm;
    int mousePost = -1;
    SwitchElm heldSwitchElm;
    double circuitMatrix[][], circuitRightSide[], origRightSide[], origMatrix[][];
    RowInfo circuitRowInfo[];
    int circuitPermute[];
    boolean circuitNonLinear;
    int voltageSourceCount;
    int circuitMatrixSize, circuitMatrixFullSize;
    boolean circuitNeedsMap;
    double voltageRange = 5;
    double currentMult, powerMult;

    Class dumpTypes[];
    int circuitBottom;
    ArrayList<String> undoStack, redoStack;

    private boolean whiteBackground;
    private boolean stopped = false;
    private double currentBarValue;
    private boolean showPowerDissipation;
    private double powerBarValue;
    private int speedBarValue;
    private boolean showCurrent;
    private boolean smallGrid;
    private boolean showVoltage;
    private boolean showValues;
    private boolean euroResistor;
    private boolean conventionalCurrent;
    public Circuit3D cv;
    ArrayList<CircuitNode> nodeList;
    CircuitElm voltageSources[];

    private boolean unstable = false;
    private boolean disabled = false;

    public CircuitSimulator() {
        dumpTypes = new Class[300];

        // scopes are unsupported
        // these characters are reserved
        //dumpTypes[(int) 'o'] = Scope.class;
        //dumpTypes[(int) 'h'] = Scope.class;
        //dumpTypes[(int) '$'] = Scope.class;
        //dumpTypes[(int) '%'] = Scope.class;
        //dumpTypes[(int) '?'] = Scope.class;
        //dumpTypes[(int) 'B'] = Scope.class;
        cv = new Circuit3D(this);
        euroResistor = false;
        conventionalCurrent = true;
    }

    public boolean isEuroResistor() {
        return euroResistor;
    }

    public void setEuroResistor(boolean euroResistor) {
        this.euroResistor = euroResistor;
    }

    public boolean isConventionalCurrent() {
        return conventionalCurrent;
    }

    public void setConventionalCurrent(boolean conventionalCurrent) {
        this.conventionalCurrent = conventionalCurrent;
    }

    public boolean whiteBackground() {
        return whiteBackground;
    }

    public void init() {
        register(CapacitorElm.class);
        register(CurrentElm.class);
        register(DcVoltageElm.class);
        register(DiodeElm.class);
        register(GroundElm.class);
        register(InductorElm.class);
        //register(OutputElm.class);
        //register(ProbeElm.class);
        register(RailElm.class);
        register(ResistorElm.class);
        register(SwitchElm.class);
        register(WireElm.class);

        currentMult = 1.7 * 10;

        elmList = new ArrayList<>();
    }

    public void onTap(int x, int y) {
        CircuitElm elm = getElmAtPosition(x, y);
        if (elm != null) {
            if (elm instanceof SwitchElm) {
                doSwitch((SwitchElm) elm);
            }
            touchElm = elm;
        } else {
            touchElm = null;
        }
    }

    public double getT() {
        return t;
    }

    public double getTimeStep() {
        return timeStep;
    }

    public void register(Class c) {
        register(c, constructElement(c, 0, 0));
    }

    public void register(Class c, CircuitElm elm) {
        int t = elm.getDumpType();
        if (t == 0) {
            System.out.println("no dump type: " + c);
            return;
        }

        Class dclass = elm.getDumpClass();

        if (dumpTypes[t] != null && dumpTypes[t] != dclass) {
            System.out.println("dump type conflict: " + c + " "
                    + dumpTypes[t]);
            return;
        }
        dumpTypes[t] = dclass;
    }

    long lastTime = 0, lastFrameTime, lastIterTime, secTime = 0;
    int frames = 0;
    int steps = 0;
    int frameRate = 0, stepRate = 0;

    public boolean updateCircuit() {
        if (analyzeFlag) {
            analyzeCircuit();
            analyzeFlag = false;
        }

        if (!stopped) {
            try {
                runCircuit();
            } catch (Exception e) {
                e.printStackTrace();
                analyzeFlag = true;
                updateCanvas();
                return false;
            }
        }
        if (!stopped) {
            long sysTime = System.currentTimeMillis();
            if (lastTime != 0) {
                int inc = (int) (sysTime - lastTime);
                double c = currentBarValue;
                //Log.d(getClass().getSimpleName(), "CurrentBarValue: " + currentBarValue);
                c = java.lang.Math.exp(c / 3.5 - 14.2);
                currentMult = 1.7 * inc * c;
            }
            if (sysTime - secTime >= 1000) {
                frameRate = frames;
                stepRate = steps;
                frames = 0;
                steps = 0;
                secTime = sysTime;
            }
            lastTime = sysTime;
        } else
            lastTime = 0;

        // Limit to 50 fps (thanks to Jurgen Klotzer for this)
        long delay = 1000 / 50 - (System.currentTimeMillis() - lastFrameTime);
        if (delay > 0) {
            return false;
        }

        int i;
        /*else if (conductanceCheckItem.getState())
          g.setColor(Color.white);*/
        if (stopMessage != null) {
            Log.e(getClass().getSimpleName(), stopMessage);
            //    g.drawString(stopMessage, 10, circuitArea.height());
        } else {
            if (circuitBottom == 0)
                calcCircuitBottom();
        }
        frames++;

        if (!stopped && circuitMatrix != null) {
            updateCanvas();
        }
        lastFrameTime = lastTime;
        return true;
    }

    public String getTimeUnitText(){
        return SiUnits.getUnitText(getT(),"s", SiUnits.showFormat);
    }

    public CircuitElm getElmAtPosition(int x, int y) {
        int bestDist = 100000;
        int bestArea = 100000;

        for (CircuitElm ce : elmList) {
            if (ce.boundingBox.contains(x, y)) {
                int j;
                int area = ce.boundingBox.width() * ce.boundingBox.height();
                int jn = ce.getPostCount();
                if (jn > 2) {
                    jn = 2;
                }
                for (j = 0; j != jn; j++) {
                    Point pt = ce.getPost(j);
                    int dist = distanceSq(x, y, pt.x, pt.y);

                    // if multiple elements have overlapping bounding boxes,
                    // we prefer selecting elements that have posts close
                    // to the mouse pointer and that have a small bounding
                    // box area.
                    if (dist <= bestDist && area <= bestArea) {
                        bestDist = dist;
                        bestArea = area;
                        return ce;
                    }
                }
                if (ce.getPostCount() == 0) {
                    return ce;
                }
            }
        }
        return null;
    }

    String getHint() {
        CircuitElm c1 = getElm(hintItem1);
        CircuitElm c2 = getElm(hintItem2);
        if (c1 == null || c2 == null) {
            return null;
        }
        if (hintType == HINT_LC) {
            if (!(c1 instanceof InductorElm)) {
                return null;
            }
            if (!(c2 instanceof CapacitorElm)) {
                return null;
            }
            InductorElm ie = (InductorElm) c1;
            CapacitorElm ce = (CapacitorElm) c2;
            return "res.f = " + getUnitText(1 / (2 * Math.PI * Math.sqrt(ie.getInductance()
                    * ce.getCapacitance())), "Hz", showFormat);
        }
        if (hintType == HINT_RC) {
            if (!(c1 instanceof ResistorElm)) {
                return null;
            }
            if (!(c2 instanceof CapacitorElm)) {
                return null;
            }
            ResistorElm re = (ResistorElm) c1;
            CapacitorElm ce = (CapacitorElm) c2;
            return "RC = " + getUnitText(re.getResistance() * ce.getCapacitance(),
                    "s", showFormat);
        }
        if (hintType == HINT_3DB_C) {
            if (!(c1 instanceof ResistorElm)) {
                return null;
            }
            if (!(c2 instanceof CapacitorElm)) {
                return null;
            }
            ResistorElm re = (ResistorElm) c1;
            CapacitorElm ce = (CapacitorElm) c2;
            return "f.3db = "
                    + getUnitText(1 / (2 * Math.PI * re.getResistance() * ce.getCapacitance()), "Hz", showFormat);
        }
        if (hintType == HINT_3DB_L) {
            if (!(c1 instanceof ResistorElm)) {
                return null;
            }
            if (!(c2 instanceof InductorElm)) {
                return null;
            }
            ResistorElm re = (ResistorElm) c1;
            InductorElm ie = (InductorElm) c2;
            return "f.3db = "
                    + getUnitText(re.getResistance() / (2 * Math.PI * ie.getInductance()), "Hz", showFormat);
        }
        if (hintType == HINT_TWINT) {
            if (!(c1 instanceof ResistorElm)) {
                return null;
            }
            if (!(c2 instanceof CapacitorElm)) {
                return null;
            }
            ResistorElm re = (ResistorElm) c1;
            CapacitorElm ce = (CapacitorElm) c2;
            return "fc = "
                    + getUnitText(1 / (2 * Math.PI * re.getResistance() * ce.getCapacitance()), "Hz", showFormat);
        }
        return null;
    }

    public void updateCanvas() {
        Log.d(getClass().getSimpleName(), "updateCanvas()");
        for (CircuitElm elm : getElmList()) {
            elm.updateObject3D();
        }
    }

    public void generateCanvas() {
        Log.d(getClass().getSimpleName(), "generateCanvas()");
        for (CircuitElm elm : getElmList()) {
            elm.updateObject3D();
            Object3D circuitElm3D = elm.circuitElm3D;
            if (circuitElm3D != null) {
                cv.addChild(circuitElm3D);
            }
        }
    }

    public void centerCircuit() {
        int minx = 10000, maxx = -10000, miny = 10000, maxy = -10000;
        for (int i = 0; i < elmList.size(); i++) {
            CircuitElm ce = getElm(i);
            miny = min(ce.y, min(ce.y2, miny));
            maxy = max(ce.y, max(ce.y2, maxy));
            minx = min(ce.x, min(ce.x2, minx));
            maxx = max(ce.x, max(ce.x2, maxx));
        }

        int centerX = Math.round((minx + maxx) / 2.0f);
        int centerY = Math.round((miny + maxy) / 2.0f);

        Log.d(getClass().getSimpleName(), "centerX: " + centerX);
        Log.d(getClass().getSimpleName(), "centerY: " + centerY);

        for (int i = 0; i < elmList.size(); i++) {
            CircuitElm ce = getElm(i);
            ce.x -= centerX;
            ce.y -= centerY;
            ce.x2 -= centerX;
            ce.y2 -= centerY;
            ce.setPoints();
        }
    }

    public void needAnalyze() {
        unstable = true;
        analyzeFlag = true;
        updateCanvas();
    }

    public CircuitNode getCircuitNode(int n) {
        if (nodeList == null || n >= nodeList.size()) {
            return null;
        }
        return nodeList.get(n);
    }

    public CircuitElm getElm(int n) {
        if (n >= elmList.size()) {
            return null;
        }
        return elmList.get(n);
    }

    public void analyzeCircuit() {
        Log.d(getClass().getSimpleName(), "analyzeCircuit()");
        calcCircuitBottom();
        if (elmList.isEmpty()) {
            return;
        }
        stopMessage = null;
        stopElm = null;
        int i, j;
        int vscount = 0;
        nodeList = new ArrayList<>();
        boolean gotGround = false;
        boolean gotRail = false;
        CircuitElm volt = null;

        //System.out.println("ac1");
        // look for voltage or ground element
        for (i = 0; i != elmList.size(); i++) {
            CircuitElm ce = getElm(i);
            if (ce instanceof GroundElm) {
                gotGround = true;
                break;
            }
            if (ce instanceof RailElm) {
                gotRail = true;
            }
            if (volt == null && ce instanceof VoltageElm) {
                volt = ce;
            }
        }

        // if no ground, and no rails, then the voltage elm's first terminal
        // is ground
        if (!gotGround && volt != null && !gotRail) {
            CircuitNode cn = new CircuitNode();
            Point pt = volt.getPost(0);
            cn.x = pt.x;
            cn.y = pt.y;
            nodeList.add(cn);
        } else {
            // otherwise allocate extra node for ground
            CircuitNode cn = new CircuitNode();
            cn.x = cn.y = -1;
            nodeList.add(cn);
        }
        //System.out.println("ac2");

        // allocate nodes and voltage sources
        for (i = 0; i != elmList.size(); i++) {
            CircuitElm ce = getElm(i);
            int inodes = ce.getInternalNodeCount();
            int ivs = ce.getVoltageSourceCount();
            int posts = ce.getPostCount();

            // allocate a node for each post and match posts to nodes
            for (j = 0; j != posts; j++) {
                Point pt = ce.getPost(j);
                int k;
                for (k = 0; k != nodeList.size(); k++) {
                    CircuitNode cn = getCircuitNode(k);
                    if (pt.x == cn.x && pt.y == cn.y) {
                        break;
                    }
                }
                if (k == nodeList.size()) {
                    CircuitNode cn = new CircuitNode();
                    cn.x = pt.x;
                    cn.y = pt.y;
                    CircuitNodeLink cnl = new CircuitNodeLink();
                    cnl.num = j;
                    cnl.elm = ce;
                    cn.links.addElement(cnl);
                    ce.setNode(j, nodeList.size());
                    nodeList.add(cn);
                } else {
                    CircuitNodeLink cnl = new CircuitNodeLink();
                    cnl.num = j;
                    cnl.elm = ce;
                    getCircuitNode(k).links.addElement(cnl);
                    ce.setNode(j, k);
                    // if it's the ground node, make sure the node voltage is 0,
                    // cause it may not get set later
                    if (k == 0) {
                        ce.setNodeVoltage(j, 0);
                    }
                }
            }
            for (j = 0; j != inodes; j++) {
                CircuitNode cn = new CircuitNode();
                cn.x = cn.y = -1;
                cn.internal = true;
                CircuitNodeLink cnl = new CircuitNodeLink();
                cnl.num = j + posts;
                cnl.elm = ce;
                cn.links.addElement(cnl);
                ce.setNode(cnl.num, nodeList.size());
                nodeList.add(cn);
            }
            vscount += ivs;
        }
        voltageSources = new CircuitElm[vscount];
        vscount = 0;
        circuitNonLinear = false;
        //System.out.println("ac3");

        // determine if circuit is nonlinear
        for (i = 0; i != elmList.size(); i++) {
            CircuitElm ce = getElm(i);
            if (ce.nonLinear()) {
                circuitNonLinear = true;
            }
            int ivs = ce.getVoltageSourceCount();
            for (j = 0; j != ivs; j++) {
                voltageSources[vscount] = ce;
                ce.setVoltageSource(j, vscount++);
            }
        }
        voltageSourceCount = vscount;

        int matrixSize = nodeList.size() - 1 + vscount;
        circuitMatrix = new double[matrixSize][matrixSize];
        circuitRightSide = new double[matrixSize];
        origMatrix = new double[matrixSize][matrixSize];
        origRightSide = new double[matrixSize];
        circuitMatrixSize = circuitMatrixFullSize = matrixSize;
        circuitRowInfo = new RowInfo[matrixSize];
        circuitPermute = new int[matrixSize];
        int vs = 0;
        for (i = 0; i != matrixSize; i++) {
            circuitRowInfo[i] = new RowInfo();
        }
        circuitNeedsMap = false;

        // stamp linear circuit elements
        for (i = 0; i != elmList.size(); i++) {
            CircuitElm ce = getElm(i);
            ce.stamp();
        }
        //System.out.println("ac4");

        // determine nodes that are unconnected
        boolean closure[] = new boolean[nodeList.size()];
        boolean tempclosure[] = new boolean[nodeList.size()];
        boolean changed = true;
        closure[0] = true;
        while (changed) {
            changed = false;
            for (i = 0; i != elmList.size(); i++) {
                CircuitElm ce = getElm(i);
                // loop through all ce's nodes to see if they are connected
                // to other nodes not in closure
                for (j = 0; j < ce.getPostCount(); j++) {
                    if (!closure[ce.getNode(j)]) {
                        if (ce.hasGroundConnection(j)) {
                            closure[ce.getNode(j)] = changed = true;
                        }
                        continue;
                    }
                    int k;
                    for (k = 0; k != ce.getPostCount(); k++) {
                        if (j == k) {
                            continue;
                        }
                        int kn = ce.getNode(k);
                        if (ce.getConnection(j, k) && !closure[kn]) {
                            closure[kn] = true;
                            changed = true;
                        }
                    }
                }
            }
            if (changed) {
                continue;
            }

            // connect unconnected nodes
            for (i = 0; i != nodeList.size(); i++) {
                if (!closure[i] && !getCircuitNode(i).internal) {
                    System.out.println("node " + i + " unconnected");
                    stampResistor(0, i, 1e8);
                    closure[i] = true;
                    changed = true;
                    break;
                }
            }
        }
        //System.out.println("ac5");

        for (i = 0; i != elmList.size(); i++) {
            CircuitElm ce = getElm(i);
            // look for inductors with no current path
            if (ce instanceof InductorElm) {
                FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce,
                        ce.getNode(1));
                // first try findPath with maximum depth of 5, to avoid slowdowns
                if (!fpi.findPath(ce.getNode(0), 5)
                        && !fpi.findPath(ce.getNode(0))) {
                    System.out.println(ce + " no path");
                    ce.reset();
                }
            }
            // look for current sources with no current path
            if (ce instanceof CurrentElm) {
                FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce,
                        ce.getNode(1));
                if (!fpi.findPath(ce.getNode(0))) {
                    stop("No path for current source!", ce);
                    return;
                }
            }
            // look for voltage source loops
            if ((ce instanceof VoltageElm && ce.getPostCount() == 2)
                    || ce instanceof WireElm) {
                FindPathInfo fpi = new FindPathInfo(FindPathInfo.VOLTAGE, ce,
                        ce.getNode(1));
                if (fpi.findPath(ce.getNode(0))) {
                    stop("Voltage source/wire loop with no resistance!", ce);
                    return;
                }
            }
            // look for shorted caps, or caps w/ voltage but no R
            if (ce instanceof CapacitorElm) {
                FindPathInfo fpi = new FindPathInfo(FindPathInfo.SHORT, ce,
                        ce.getNode(1));
                if (fpi.findPath(ce.getNode(0))) {
                    System.out.println(ce + " shorted");
                    ce.reset();
                } else {
                    fpi = new FindPathInfo(FindPathInfo.CAP_V, ce, ce.getNode(1));
                    if (fpi.findPath(ce.getNode(0))) {
                        stop("Capacitor loop with no resistance!", ce);
                        return;
                    }
                }
            }
        }
        //System.out.println("ac6");

        // simplify the matrix; this speeds things up quite a bit
        for (i = 0; i != matrixSize; i++) {
            int qm = -1, qp = -1;
            double qv = 0;
            RowInfo re = circuitRowInfo[i];
            /*System.out.println("row " + i + " " + re.lsChanges + " " + re.rsChanges + " " +
             re.dropRow);*/
            if (re.lsChanges || re.dropRow || re.rsChanges) {
                continue;
            }
            double rsadd = 0;

            // look for rows that can be removed
            for (j = 0; j != matrixSize; j++) {
                double q = circuitMatrix[i][j];
                if (circuitRowInfo[j].type == RowInfo.ROW_CONST) {
                    // keep a running total of const values that have been
                    // removed already
                    rsadd -= circuitRowInfo[j].value * q;
                    continue;
                }
                if (q == 0) {
                    continue;
                }
                if (qp == -1) {
                    qp = j;
                    qv = q;
                    continue;
                }
                if (qm == -1 && q == -qv) {
                    qm = j;
                    continue;
                }
                break;
            }
            //System.out.println("line " + i + " " + qp + " " + qm + " " + j);
            /*if (qp != -1 && circuitRowInfo[qp].lsChanges) {
             System.out.println("lschanges");
             continue;
             }
             if (qm != -1 && circuitRowInfo[qm].lsChanges) {
             System.out.println("lschanges");
             continue;
             }*/
            if (j == matrixSize) {
                if (qp == -1) {
                    stop("Matrix error", null);
                    return;
                }
                RowInfo elt = circuitRowInfo[qp];
                if (qm == -1) {
                    // we found a row with only one nonzero entry; that value
                    // is a constant
                    int k;
                    for (k = 0; elt.type == RowInfo.ROW_EQUAL && k < 100; k++) {
                        // follow the chain
                        /*System.out.println("following equal chain from " +
                         i + " " + qp + " to " + elt.nodeEq);*/
                        qp = elt.nodeEq;
                        elt = circuitRowInfo[qp];
                    }
                    if (elt.type == RowInfo.ROW_EQUAL) {
                        // break equal chains
                        //System.out.println("Break equal chain");
                        elt.type = RowInfo.ROW_NORMAL;
                        continue;
                    }
                    if (elt.type != RowInfo.ROW_NORMAL) {
                        System.out.println("type already " + elt.type + " for " + qp + "!");
                        continue;
                    }
                    elt.type = RowInfo.ROW_CONST;
                    elt.value = (circuitRightSide[i] + rsadd) / qv;
                    circuitRowInfo[i].dropRow = true;
                    //System.out.println(qp + " * " + qv + " = const " + elt.value);
                    i = -1; // start over from scratch
                } else if (circuitRightSide[i] + rsadd == 0) {
                    // we found a row with only two nonzero entries, and one
                    // is the negative of the other; the values are equal
                    if (elt.type != RowInfo.ROW_NORMAL) {
                        //System.out.println("swapping");
                        int qq = qm;
                        qm = qp;
                        qp = qq;
                        elt = circuitRowInfo[qp];
                        if (elt.type != RowInfo.ROW_NORMAL) {
                            // we should follow the chain here, but this
                            // hardly ever happens so it's not worth worrying
                            // about
                            System.out.println("swap failed");
                            continue;
                        }
                    }
                    elt.type = RowInfo.ROW_EQUAL;
                    elt.nodeEq = qm;
                    circuitRowInfo[i].dropRow = true;
                    //System.out.println(qp + " = " + qm);
                }
            }
        }
        //System.out.println("ac7");

        // find size of new matrix
        int nn = 0;
        for (i = 0; i != matrixSize; i++) {
            RowInfo elt = circuitRowInfo[i];
            if (elt.type == RowInfo.ROW_NORMAL) {
                elt.mapCol = nn++;
                //System.out.println("col " + i + " maps to " + elt.mapCol);
                continue;
            }
            if (elt.type == RowInfo.ROW_EQUAL) {
                RowInfo e2 = null;
                // resolve chains of equality; 100 max steps to avoid loops
                for (j = 0; j != 100; j++) {
                    e2 = circuitRowInfo[elt.nodeEq];
                    if (e2.type != RowInfo.ROW_EQUAL) {
                        break;
                    }
                    if (i == e2.nodeEq) {
                        break;
                    }
                    elt.nodeEq = e2.nodeEq;
                }
            }
            if (elt.type == RowInfo.ROW_CONST) {
                elt.mapCol = -1;
            }
        }
        for (i = 0; i != matrixSize; i++) {
            RowInfo elt = circuitRowInfo[i];
            if (elt.type == RowInfo.ROW_EQUAL) {
                RowInfo e2 = circuitRowInfo[elt.nodeEq];
                if (e2.type == RowInfo.ROW_CONST) {
                    // if something is equal to a const, it's a const
                    elt.type = e2.type;
                    elt.value = e2.value;
                    elt.mapCol = -1;
                    //System.out.println(i + " = [late]const " + elt.value);
                } else {
                    elt.mapCol = e2.mapCol;
                    //System.out.println(i + " maps to: " + e2.mapCol);
                }
            }
        }
        //System.out.println("ac8");

        /*System.out.println("matrixSize = " + matrixSize);

         for (j = 0; j != circuitMatrixSize; j++) {
         System.out.println(j + ": ");
         for (i = 0; i != circuitMatrixSize; i++)
         System.out.print(circuitMatrix[j][i] + " ");
         System.out.print("  " + circuitRightSide[j] + "\n");
         }
         System.out.print("\n");*/
        // make the new, simplified matrix
        int newsize = nn;
        double newmatx[][] = new double[newsize][newsize];
        double newrs[] = new double[newsize];
        int ii = 0;
        for (i = 0; i != matrixSize; i++) {
            RowInfo rri = circuitRowInfo[i];
            if (rri.dropRow) {
                rri.mapRow = -1;
                continue;
            }
            newrs[ii] = circuitRightSide[i];
            rri.mapRow = ii;
            //System.out.println("Row " + i + " maps to " + ii);
            for (j = 0; j != matrixSize; j++) {
                RowInfo ri = circuitRowInfo[j];
                if (ri.type == RowInfo.ROW_CONST) {
                    newrs[ii] -= ri.value * circuitMatrix[i][j];
                } else {
                    newmatx[ii][ri.mapCol] += circuitMatrix[i][j];
                }
            }
            ii++;
        }

        circuitMatrix = newmatx;
        circuitRightSide = newrs;
        matrixSize = circuitMatrixSize = newsize;
        for (i = 0; i != matrixSize; i++) {
            origRightSide[i] = circuitRightSide[i];
        }
        for (i = 0; i != matrixSize; i++) {
            for (j = 0; j != matrixSize; j++) {
                origMatrix[i][j] = circuitMatrix[i][j];
            }
        }
        circuitNeedsMap = true;


        System.out.println("matrixSize = " + matrixSize + " " + circuitNonLinear);
        for (j = 0; j != circuitMatrixSize; j++) {
            for (i = 0; i != circuitMatrixSize; i++)
                System.out.print(circuitMatrix[j][i] + " ");
            System.out.print("  " + circuitRightSide[j] + "\n");
        }
        System.out.print("\n");
        // if a matrix is linear, we can do the luFactor here instead of
        // needing to do it every frame
        if (!circuitNonLinear) {
            if (!luFactor(circuitMatrix, circuitMatrixSize, circuitPermute)) {
                stop("Singular matrix!", null);
                return;
            }
        }
    }

    void calcCircuitBottom() {
        circuitBottom = 0;
        for (int i = 0; i != elmList.size(); i++) {
            Rectangle rect = getElm(i).boundingBox;
            int bottom = rect.height() + rect.top;
            if (bottom > circuitBottom) {
                circuitBottom = bottom;
            }
        }
    }

    public int elmListSize() {
        if (elmList == null) {
            return 0;
        }
        return elmList.size();
    }

    public void setConverged(boolean converged) {
        this.converged = converged;
    }

    public void setUnstable(boolean unstable) {
        this.unstable = unstable;
    }

    public boolean isUnstable() {
        return unstable;
    }

    public boolean isConverged() {
        return converged;
    }

    public Circuit3D getCircuitCanvas() {
        return cv;
    }

    public boolean isStopped() {
        return stopped;
    }

    public boolean isShowingCurrent() {
        return showCurrent;
    }

    public boolean isShowingVoltage() {
        return showVoltage;
    }

    public void setShowPowerDissipation(boolean showPowerDissipation) {
        this.showPowerDissipation = showPowerDissipation;
    }

    public void setShowCurrent(boolean showCurrent) {
        this.showCurrent = showCurrent;
    }

    public void setShowVoltage(boolean showVoltage) {
        this.showVoltage = showVoltage;
    }

    public void setShowValues(boolean showValues) {
        this.showValues = showValues;
    }

    public boolean isShowingValues() {
        return showValues;
    }

    public boolean isShowingPowerDissipation() {
        return showPowerDissipation;
    }

    public boolean euroResistor() {
        return euroResistor;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public double getVoltageRange() {
        return voltageRange;
    }


    public double getPowerMult() {
        return powerMult;
    }

    public double getCurrentMult() {
        return currentMult;
    }

    boolean isReady() {
        return elmList != null && nodeList != null;
    }

    public boolean setDisabled() {
        return disabled = true;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public ArrayList<CircuitElm> getElmList() {
        return elmList;
    }

    public String getHintText() {
        int i = 0;
        int badnodes = 0;
        // find bad connections, nodes not connected to other elements which
        // intersect other elements' bounding boxes
        // debugged by hausen: nullPointerException
        if (nodeList != null)
            for (i = 0; i != nodeList.size(); i++) {
                CircuitNode cn = getCircuitNode(i);
                if (!cn.internal && cn.links.size() == 1) {
                    int bb = 0, j;
                    CircuitNodeLink cnl = cn.links.elementAt(0);
                    for (j = 0; j != elmList.size(); j++) { // TODO: (hausen) see if this change does not break stuff
                        CircuitElm ce = getElm(j);
                        if (ce instanceof GraphicElm)
                            continue;
                        if (cnl.elm != ce &&
                                getElm(j).boundingBox.contains(cn.x, cn.y))
                            bb++;
                    }
                    if (bb > 0) {
                        badnodes++;
                    }
                }
            }

        String info[] = new String[10];
        if (touchElm != null) {
            if (mousePost == -1)
                touchElm.getInfo(info);
            else
                info[0] = "V = " +
                        getUnitText(touchElm.getPostVoltage(mousePost), "V", SiUnits.showFormat);
        }
        if (hintType != -1) {
            for (i = 0; info[i] != null; i++)
                ;
            String s = getHint();
            if (s == null)
                hintType = -1;
            else
                info[i] = s;
        }

        if (badnodes > 0)
            info[i++] = badnodes + ((badnodes == 1) ?
                    " bad connection" : " bad connections");

        StringBuilder builder = new StringBuilder();

        // count lines of data
        for (i = 0; info[i] != null; i++) {
            builder.append(info[i]);
            if (info[i + 1] != null)
                builder.append("\n");
        }
        return builder.toString();
    }

    public class FindPathInfo {

        static final int INDUCT = 1;
        static final int VOLTAGE = 2;
        static final int SHORT = 3;
        static final int CAP_V = 4;
        boolean used[];
        int dest;
        CircuitElm firstElm;
        int type;

        FindPathInfo(int t, CircuitElm e, int d) {
            dest = d;
            type = t;
            firstElm = e;
            used = new boolean[nodeList.size()];
        }

        boolean findPath(int n1) {
            return findPath(n1, -1);
        }

        boolean findPath(int n1, int depth) {
            if (n1 == dest) {
                return true;
            }
            if (depth-- == 0) {
                return false;
            }
            if (used[n1]) {
                //System.out.println("used " + n1);
                return false;
            }
            used[n1] = true;
            int i;
            for (i = 0; i != elmList.size(); i++) {
                CircuitElm ce = getElm(i);
                if (ce == firstElm) {
                    continue;
                }
                if (type == INDUCT) {
                    if (ce instanceof CurrentElm) {
                        continue;
                    }
                }
                if (type == VOLTAGE) {
                    if (!(ce.isWire() || ce instanceof VoltageElm)) {
                        continue;
                    }
                }
                if (type == SHORT && !ce.isWire()) {
                    continue;
                }
                if (type == CAP_V) {
                    if (!(ce.isWire() || ce instanceof CapacitorElm
                            || ce instanceof VoltageElm)) {
                        continue;
                    }
                }
                if (n1 == 0) {
                    // look for posts which have a ground connection;
                    // our path can go through ground
                    int j;
                    for (j = 0; j != ce.getPostCount(); j++) {
                        if (ce.hasGroundConnection(j)
                                && findPath(ce.getNode(j), depth)) {
                            used[n1] = false;
                            return true;
                        }
                    }
                }
                int j;
                for (j = 0; j != ce.getPostCount(); j++) {
                    //System.out.println(ce + " " + ce.getNode(j));
                    if (ce.getNode(j) == n1) {
                        break;
                    }
                }
                if (j == ce.getPostCount()) {
                    continue;
                }
                if (ce.hasGroundConnection(j) && findPath(0, depth)) {
                    //System.out.println(ce + " has ground");
                    used[n1] = false;
                    return true;
                }
                if (type == INDUCT && ce instanceof InductorElm) {
                    double c = ce.getCurrent();
                    if (j == 0) {
                        c = -c;
                    }
                    //System.out.println("matching " + c + " to " + firstElm.getCurrent());
                    //System.out.println(ce + " " + firstElm);
                    if (Math.abs(c - firstElm.getCurrent()) > 1e-10) {
                        continue;
                    }
                }
                int k;
                for (k = 0; k != ce.getPostCount(); k++) {
                    if (j == k) {
                        continue;
                    }
                    //System.out.println(ce + " " + ce.getNode(j) + "-" + ce.getNode(k));
                    if (ce.getConnection(j, k) && findPath(ce.getNode(k), depth)) {
                        //System.out.println("got findpath " + n1);
                        used[n1] = false;
                        return true;
                    }
                    //System.out.println("back on findpath " + n1);
                }
            }
            used[n1] = false;
            //System.out.println(n1 + " failed");
            return false;
        }
    }

    public void stop(String s, CircuitElm ce) {
        stopMessage = s;
        circuitMatrix = null;
        stopElm = ce;
        stopped = true;
        analyzeFlag = false;
        updateCanvas();
    }

    // control voltage source vs with voltage from n1 to n2 (must
    // also call stampVoltageSource())
    public void stampVCVS(int n1, int n2, double coef, int vs) {
        int vn = nodeList.size() + vs;
        stampMatrix(vn, n1, coef);
        stampMatrix(vn, n2, -coef);
    }

    // stamp independent voltage source #vs, from n1 to n2, amount v
    public void stampVoltageSource(int n1, int n2, int vs, double v) {
        int vn = nodeList.size() + vs;
        stampMatrix(vn, n1, -1);
        stampMatrix(vn, n2, 1);
        stampRightSide(vn, v);
        stampMatrix(n1, vn, 1);
        stampMatrix(n2, vn, -1);
    }

    // use this if the amount of voltage is going to be updated in doStep()
    public void stampVoltageSource(int n1, int n2, int vs) {
        int vn = nodeList.size() + vs;
        stampMatrix(vn, n1, -1);
        stampMatrix(vn, n2, 1);
        stampRightSide(vn);
        stampMatrix(n1, vn, 1);
        stampMatrix(n2, vn, -1);
    }

    public void updateVoltageSource(int n1, int n2, int vs, double v) {
        int vn = nodeList.size() + vs;
        stampRightSide(vn, v);
    }

    public void stampResistor(int n1, int n2, double r) {
        double r0 = 1 / r;
        if (Double.isNaN(r0) || Double.isInfinite(r0)) {
            System.out.print("bad resistance " + r + " " + r0 + "\n");
            int a = 0;
            a /= a;
        }
        stampMatrix(n1, n1, r0);
        stampMatrix(n2, n2, r0);
        stampMatrix(n1, n2, -r0);
        stampMatrix(n2, n1, -r0);
    }

    public void stampConductance(int n1, int n2, double r0) {
        stampMatrix(n1, n1, r0);
        stampMatrix(n2, n2, r0);
        stampMatrix(n1, n2, -r0);
        stampMatrix(n2, n1, -r0);
    }

    // current from cn1 to void reset()cn2 is equal to voltage from vn1 to 2, divided by g
    public void stampVCCurrentSource(int cn1, int cn2, int vn1, int vn2, double g) {
        stampMatrix(cn1, vn1, g);
        stampMatrix(cn2, vn2, g);
        stampMatrix(cn1, vn2, -g);
        stampMatrix(cn2, vn1, -g);
    }

    public void stampCurrentSource(int n1, int n2, double i) {
        stampRightSide(n1, -i);
        stampRightSide(n2, i);
    }

    // stamp a current source from n1 to n2 depending on current through vs
    public void stampCCCS(int n1, int n2, int vs, double gain) {
        int vn = nodeList.size() + vs;
        stampMatrix(n1, vn, gain);
        stampMatrix(n2, vn, -gain);
    }

    // stamp value x in row i, column j, meaning that a voltage change
    // of dv in node j will increase the current into node i by x dv.
    // (Unless i or j is a voltage source node.)
    public void stampMatrix(int i, int j, double x) {
        if (i > 0 && j > 0) {
            if (circuitNeedsMap) {
                i = circuitRowInfo[i - 1].mapRow;
                RowInfo ri = circuitRowInfo[j - 1];
                if (ri.type == RowInfo.ROW_CONST) {
                    //System.out.println("Stamping constant " + i + " " + j + " " + x);
                    circuitRightSide[i] -= x * ri.value;
                    return;
                }
                j = ri.mapCol;
                //System.out.println("stamping " + i + " " + j + " " + x);
            } else {
                i--;
                j--;
            }
            circuitMatrix[i][j] += x;
        }
    }

    // stamp value x on the right side of row i, representing an
    // independent current source flowing into node i
    public void stampRightSide(int i, double x) {
        if (i > 0) {
            if (circuitNeedsMap) {
                i = circuitRowInfo[i - 1].mapRow;
                //System.out.println("stamping " + i + " " + x);
            } else {
                i--;
            }
            circuitRightSide[i] += x;
        }
    }

    // indicate that the value on the right side of row i changes in doStep()
    public void stampRightSide(int i) {
        //System.out.println("rschanges true " + (i-1));
        if (i > 0) {
            circuitRowInfo[i - 1].rsChanges = true;
        }
    }

    // indicate that the values on the left side of row i change in doStep()
    public void stampNonLinear(int i) {
        if (i > 0) {
            circuitRowInfo[i - 1].lsChanges = true;
        }
    }

    public double getIterCount() {
        if (speedBarValue == 0) {
            return 0;
        }
        //return (Math.exp((speedBar.getValue()-1)/24.) + .5);
        return .1 * Math.exp((speedBarValue - 61) / 24.);
    }

    boolean converged;
    int subIterations;

    public void runCircuit() {
        if (circuitMatrix == null || elmList.isEmpty()) {
            circuitMatrix = null;
            return;
        }
        int iter;
        //int maxIter = getIterCount();
        boolean debugprint = dumpMatrix;
        dumpMatrix = false;
        //Log.d(getClass().getSimpleName(), "getIterCount() = " + getIterCount());
        long steprate = (long) (160 * getIterCount());
        long tm = System.currentTimeMillis();
        long lit = lastIterTime;
        //Log.d(getClass().getSimpleName(), "lastIterTime: " + lit);
        if (1000 >= steprate * (tm - lastIterTime)) {
            return;
        }
        for (iter = 1; ; iter++) {
            int i, j, k, subiter;
            for (i = 0; i != elmList.size(); i++) {
                CircuitElm ce = getElm(i);
                ce.startIteration();
            }
            steps++;
            final int subiterCount = 5000;
            for (subiter = 0; subiter != subiterCount; subiter++) {
                converged = true;
                subIterations = subiter;
                for (i = 0; i != circuitMatrixSize; i++) {
                    circuitRightSide[i] = origRightSide[i];
                }
                if (circuitNonLinear) {
                    for (i = 0; i != circuitMatrixSize; i++) {
                        for (j = 0; j != circuitMatrixSize; j++) {
                            circuitMatrix[i][j] = origMatrix[i][j];
                        }
                    }
                }
                for (i = 0; i != elmList.size(); i++) {
                    CircuitElm ce = getElm(i);
                    ce.doStep();
                }
                if (stopMessage != null) {
                    return;
                }
                boolean printit = debugprint;
                debugprint = false;
                for (j = 0; j != circuitMatrixSize; j++) {
                    for (i = 0; i != circuitMatrixSize; i++) {
                        double x = circuitMatrix[i][j];
                        if (Double.isNaN(x) || Double.isInfinite(x)) {
                            stop("nan/infinite matrix!", null);
                            return;
                        }
                    }
                }
                if (printit) {
                    for (j = 0; j != circuitMatrixSize; j++) {
                        for (i = 0; i != circuitMatrixSize; i++) {
                            System.out.print(circuitMatrix[j][i] + ",");
                        }
                        System.out.print("  " + circuitRightSide[j] + "\n");
                    }
                    System.out.print("\n");
                }
                if (circuitNonLinear) {
                    if (converged && subiter > 0) {
                        break;
                    }
                    if (!luFactor(circuitMatrix, circuitMatrixSize,
                            circuitPermute)) {
                        stop("Singular matrix!", null);
                        return;
                    }
                }
                luSolve(circuitMatrix, circuitMatrixSize, circuitPermute,
                        circuitRightSide);

                for (j = 0; j != circuitMatrixFullSize; j++) {
                    RowInfo ri = circuitRowInfo[j];
                    double res = 0;
                    if (ri.type == RowInfo.ROW_CONST) {
                        res = ri.value;
                    } else {
                        res = circuitRightSide[ri.mapCol];
                    }
                    /*System.out.println(j + " " + res + " " +
                     ri.type + " " + ri.mapCol);*/
                    if (Double.isNaN(res)) {
                        converged = false;
                        //debugprint = true;
                        break;
                    }
                    if (j < nodeList.size() - 1) {
                        CircuitNode cn = getCircuitNode(j + 1);
                        for (k = 0; k != cn.links.size(); k++) {
                            CircuitNodeLink cnl = (CircuitNodeLink) cn.links.elementAt(k);
                            cnl.elm.setNodeVoltage(cnl.num, res);
                        }
                    } else {
                        int ji = j - (nodeList.size() - 1);
                        //System.out.println("setting vsrc " + ji + " to " + res);
                        voltageSources[ji].setCurrent(ji, res);
                    }
                }
                if (!circuitNonLinear) {
                    break;
                }
            }
            if (subiter > 1) {
                System.out.print("converged after " + subiter + " iterations\n");
                unstable = false;
            }
            if (subiter == subiterCount) {
                stop("Convergence failed!", null);
                break;
            }
            t += timeStep;
            tm = System.currentTimeMillis();
            lit = tm;
            if (iter * 1000 >= steprate * (tm - lastIterTime)
                    || (tm - lastFrameTime > 500)) {
                break;
            }
        }
        lastIterTime = lit;
        //System.out.println((System.currentTimeMillis()-lastFrameTime)/(double) iter);
    }

    public String dumpCircuit() {
        int i;
        int f = (showCurrent) ? 1 : 0;
        f |= (smallGrid) ? 2 : 0;
        f |= (showVoltage) ? 0 : 4;
        f |= (showPowerDissipation) ? 8 : 0;
        f |= (showValues) ? 0 : 16;
        // 32 = linear scale in afilter
        String dump = "$ " + f + " "
                + timeStep + " " + getIterCount() + " "
                + currentBarValue + " " + voltageRange + " "
                + powerBarValue + "\n";
        for (i = 0; i != elmList.size(); i++) {
            dump += getElm(i).dump() + "\n";
        }
        /* Scopes are unsupported
        for (i = 0; i != scopeCount; i++) {
            String d = scopes[i].dump();
            if (d != null) {
                dump += d + "\n";
            }
        }*/
        if (hintType != -1) {
            dump += "h " + hintType + " " + hintItem1 + " "
                    + hintItem2 + "\n";
        }
        return dump;
    }

    public void readSetup(String text) {
        readSetup(text, false);
    }

    public void readSetup(String text, boolean retain) {
        readSetup(text.getBytes(), text.length(), retain);
    }

    void readSetup(byte[] b, int len, boolean retain) {
        int i;
        if (!retain) {
            for (i = 0; i != elmList.size(); i++) {
                CircuitElm ce = getElm(i);
                ce.delete();
            }
            elmList.clear();
            hintType = -1;
            timeStep = 5e-6;
            showCurrent = true;
            smallGrid = false;
            showPowerDissipation = false;
            showVoltage = true;
            showValues = true;
            speedBarValue = 200; // 57
            currentBarValue = 60;
            powerBarValue = 50;
            voltageRange = 5;
        }
        int p;
        for (p = 0; p < len; ) {
            int l;
            int linelen = 0;
            for (l = 0; l != len - p; l++) {
                if (b[l + p] == '\n' || b[l + p] == '\r') {
                    linelen = l++;
                    if (l + p < b.length && b[l + p] == '\n') {
                        l++;
                    }
                    break;
                }
            }
            String line = new String(b, p, linelen);
            StringTokenizer st = new StringTokenizer(line);
            while (st.hasMoreTokens()) {
                String type = st.nextToken();
                int tint = type.charAt(0);
                try {
                    if (tint == 'o') {

                        break;
                    }
                    if (tint == 'h') {
                        readHint(st);
                        break;
                    }
                    if (tint == '$') {
                        readOptions(st);
                        break;
                    }
                    if (tint == '%' || tint == '?' || tint == 'B') {
                        // ignore afilter-specific stuff
                        break;
                    }
                    if (tint >= '0' && tint <= '9') {
                        tint = new Integer(type).intValue();
                    }
                    int x1 = new Integer(st.nextToken()).intValue();
                    int y1 = new Integer(st.nextToken()).intValue();
                    int x2 = new Integer(st.nextToken()).intValue();
                    int y2 = new Integer(st.nextToken()).intValue();
                    int f = new Integer(st.nextToken()).intValue();
                    CircuitElm ce = null;
                    Class cls = dumpTypes[tint];
                    if (cls == null) {
                        System.out.println("unrecognized dump type: " + type);
                        break;
                    }
                    // find element class
                    Class carr[] = new Class[6];
                    //carr[0] = getClass();
                    carr[0] = carr[1] = carr[2] = carr[3] = carr[4]
                            = int.class;
                    carr[5] = StringTokenizer.class;
                    Constructor cstr = null;
                    cstr = cls.getConstructor(carr);

                    // invoke constructor with starting coordinates
                    Object oarr[] = new Object[6];
                    //oarr[0] = this;
                    oarr[0] = new Integer(x1);
                    oarr[1] = new Integer(y1);
                    oarr[2] = new Integer(x2);
                    oarr[3] = new Integer(y2);
                    oarr[4] = new Integer(f);
                    oarr[5] = st;
                    ce = (CircuitElm) cstr.newInstance(oarr);
                    ce.setSim(this);
                    ce.setPoints();
                    elmList.add(ce);
                } catch (java.lang.reflect.InvocationTargetException ee) {
                    ee.getTargetException().printStackTrace();
                    break;
                } catch (Exception ee) {
                    ee.printStackTrace();
                    break;
                }
                break;
            }
            p += l;
        }
    }

    public void prepareCircuit() {
        centerCircuit();
        analyzeCircuit();
        generateCanvas();
        handleResize();
    }

    public static CircuitElm createElm(String line, CircuitSimulator cs) {
        StringTokenizer st = new StringTokenizer(line);
        while (st.hasMoreTokens()) {
            String type = st.nextToken();
            int tint = type.charAt(0);
            try {
                int x1 = new Integer(st.nextToken()).intValue();
                int y1 = new Integer(st.nextToken()).intValue();
                int x2 = new Integer(st.nextToken()).intValue();
                int y2 = new Integer(st.nextToken()).intValue();
                int f = new Integer(st.nextToken()).intValue();
                CircuitElm ce = null;
                Class cls = cs.dumpTypes[tint];
                if (cls == null) {
                    System.out.println("unrecognized dump type: " + type);
                    break;
                }
                // find element class
                Class carr[] = new Class[6];
                //carr[0] = getClass();
                carr[0] = carr[1] = carr[2] = carr[3] = carr[4]
                        = int.class;
                carr[5] = StringTokenizer.class;
                Constructor cstr = null;
                cstr = cls.getConstructor(carr);

                // invoke constructor with starting coordinates
                Object oarr[] = new Object[6];
                //oarr[0] = this;
                oarr[0] = new Integer(x1);
                oarr[1] = new Integer(y1);
                oarr[2] = new Integer(x2);
                oarr[3] = new Integer(y2);
                oarr[4] = new Integer(f);
                oarr[5] = st;
                ce = (CircuitElm) cstr.newInstance(oarr);
                ce.setSim(cs);
                ce.setPoints();
                return ce;
            } catch (java.lang.reflect.InvocationTargetException ee) {
                ee.getTargetException().printStackTrace();
                break;
            } catch (Exception ee) {
                ee.printStackTrace();
                break;
            }
        }
        return null;
    }

    void readHint(StringTokenizer st) {
        hintType = new Integer(st.nextToken()).intValue();
        hintItem1 = new Integer(st.nextToken()).intValue();
        hintItem2 = new Integer(st.nextToken()).intValue();
    }

    void readOptions(StringTokenizer st) {
        int flags = new Integer(st.nextToken()).intValue();
        showCurrent = ((flags & 1) != 0);
        smallGrid = ((flags & 2) != 0);
        showVoltage = ((flags & 4) == 0);
        showPowerDissipation = ((flags & 8) == 8);
        showValues = ((flags & 16) == 0);
        timeStep = new Double(st.nextToken()).doubleValue();
        double sp = new Double(st.nextToken()).doubleValue();
        int sp2 = (int) (Math.log(10 * sp) * 24 + 61.5);
        //int sp2 = (int) (Math.log(sp)*24+1.5);
        speedBarValue = (sp2);
        currentBarValue = (new Double(st.nextToken()).doubleValue());
        voltageRange = new Double(st.nextToken()).doubleValue();
        try {
            powerBarValue = (new Double(st.nextToken()).doubleValue());
        } catch (Exception e) {
        }
    }

    public void doSwitch(SwitchElm se) {
        se.toggle();
        if (se.isMomentary()) {
            heldSwitchElm = se;
        }
        needAnalyze();
    }

    public int locateElm(CircuitElm elm) {
        int i;
        for (i = 0; i != elmList.size(); i++) {
            if (elm == elmList.get(i)) {
                return i;
            }
        }
        return -1;
    }

    void removeZeroLengthElements() {
        int i;
        boolean changed = false;
        for (i = elmList.size() - 1; i >= 0; i--) {
            CircuitElm ce = getElm(i);
            if (ce.x == ce.x2 && ce.y == ce.y2) {
                elmList.remove(i);
                ce.delete();
                changed = true;
            }
        }
        needAnalyze();
    }

    int distanceSq(int x1, int y1, int x2, int y2) {
        x2 -= x1;
        y2 -= y1;
        return x2 * x2 + y2 * y2;
    }

    public CircuitElm constructElement(Class c, int x0, int y0) {
        // find element class
        Class carr[] = new Class[2];
        //carr[0] = getClass();
        carr[0] = carr[1] = int.class;
        Constructor cstr = null;
        try {
            cstr = c.getConstructor(carr);
        } catch (NoSuchMethodException ee) {
            System.out.println("caught NoSuchMethodException " + c);
            return null;
        } catch (Exception ee) {
            ee.printStackTrace();
            return null;
        }

        // invoke constructor with starting coordinates
        Object oarr[] = new Object[2];
        oarr[0] = new Integer(x0);
        oarr[1] = new Integer(y0);
        try {
            CircuitElm elm = (CircuitElm) cstr.newInstance(oarr);
            elm.setSim(this);
            elm.getSim();
            return elm;
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return null;
    }

    void pushUndo() {
        redoStack.clear();
        String s = dumpCircuit();
        if (undoStack.size() > 0) {
            if (s.equals(undoStack.get(undoStack.size() - 1))) {
                return;
            }
        }
        undoStack.add(s);
        enableUndoRedo();
    }

    void doUndo() {
        if (undoStack.size() == 0) {
            return;
        }
        redoStack.add(dumpCircuit());
        String s = undoStack.remove(undoStack.size() - 1);
        readSetup(s);
        enableUndoRedo();
    }

    void doRedo() {
        if (redoStack.size() == 0) {
            return;
        }
        undoStack.add(dumpCircuit());
        String s = redoStack.remove(redoStack.size() - 1);
        readSetup(s);
        enableUndoRedo();
    }

    void enableUndoRedo() {
//        redoItem.setEnabled(redoStack.size() > 0); //TODO
//        undoItem.setEnabled(undoStack.size() > 0);
    }

    void doDelete() {
        int i;
        pushUndo();
        boolean hasDeleted = false;

        for (i = elmList.size() - 1; i >= 0; i--) {
            CircuitElm ce = getElm(i);
            if (ce.isSelected()) {
                ce.delete();
                elmList.remove(i);
                hasDeleted = true;
            }
        }

        if (!hasDeleted) {
            for (i = elmList.size() - 1; i >= 0; i--) {
                CircuitElm ce = getElm(i);
                if (ce == touchElm) {
                    ce.delete();
                    elmList.remove(i);
                    hasDeleted = true;
                    touchElm = null;
                    break;
                }
            }
        }

        if (hasDeleted) {
            needAnalyze();
        }
    }

    void clearSelection() {
        for (int i = 0; i < elmList.size(); i++) {
            CircuitElm ce = getElm(i);
            ce.setSelected(false);
        }
    }

    void doSelectAll() {
        for (int i = 0; i < elmList.size(); i++) {
            CircuitElm ce = getElm(i);
            ce.setSelected(true);
        }
    }

    public void handleResize() {
        int minx = 10000, maxx = -10000, miny = 10000, maxy = -10000;
        for (int i = 0; i != elmList.size(); i++) {
            CircuitElm ce = getElm(i);
            miny = min(ce.y, min(ce.y2, miny));
            maxy = max(ce.y, max(ce.y2, maxy));
            minx = min(ce.x, min(ce.x2, minx));
            maxx = max(ce.x, max(ce.x2, maxx));
        }

        Log.d(getClass().getSimpleName(), "minx: " + minx);
        Log.d(getClass().getSimpleName(), "maxx: " + maxx);
        Log.d(getClass().getSimpleName(), "miny: " + miny);
        Log.d(getClass().getSimpleName(), "maxy: " + maxy);

        cv.setCircuitBounds(minx, miny, maxx, maxy);

        Log.d(getClass().getSimpleName(), "circuitCenterX: " + cv.getCircuitCenterX());
        Log.d(getClass().getSimpleName(), "circuitCenterY: " + cv.getCircuitCenterY());

        needAnalyze();
        circuitBottom = 0;
    }

    // factors a matrix into upper and lower triangular matrices by
    // gaussian elimination.  On entry, a[0..n-1][0..n-1] is the
    // matrix to be factored.  ipvt[] returns an integer vector of pivot
    // indices, used in the luSolve() routine.
    public static boolean luFactor(double a[][], int n, int ipvt[]) {
        double scaleFactors[];
        int i, j, k;

        scaleFactors = new double[n];

        // divide each row by its largest element, keeping track of the
        // scaling factors
        for (i = 0; i != n; i++) {
            double largest = 0;
            for (j = 0; j != n; j++) {
                double x = Math.abs(a[i][j]);
                if (x > largest) {
                    largest = x;
                }
            }
            // if all zeros, it's a singular matrix
            if (largest == 0) {
                return false;
            }
            scaleFactors[i] = 1.0 / largest;
        }

        // use Crout's method; loop through the columns
        for (j = 0; j != n; j++) {

            // calculate upper triangular elements for this column
            for (i = 0; i != j; i++) {
                double q = a[i][j];
                for (k = 0; k != i; k++) {
                    q -= a[i][k] * a[k][j];
                }
                a[i][j] = q;
            }

            // calculate lower triangular elements for this column
            double largest = 0;
            int largestRow = -1;
            for (i = j; i != n; i++) {
                double q = a[i][j];
                for (k = 0; k != j; k++) {
                    q -= a[i][k] * a[k][j];
                }
                a[i][j] = q;
                double x = Math.abs(q);
                if (x >= largest) {
                    largest = x;
                    largestRow = i;
                }
            }

            // pivoting
            if (j != largestRow) {
                double x;
                for (k = 0; k != n; k++) {
                    x = a[largestRow][k];
                    a[largestRow][k] = a[j][k];
                    a[j][k] = x;
                }
                scaleFactors[largestRow] = scaleFactors[j];
            }

            // keep track of row interchanges
            ipvt[j] = largestRow;

            // avoid zeros
            if (a[j][j] == 0.0) {
                System.out.println("avoided zero");
                a[j][j] = 1e-18;
            }

            if (j != n - 1) {
                double mult = 1.0 / a[j][j];
                for (i = j + 1; i != n; i++) {
                    a[i][j] *= mult;
                }
            }
        }
        return true;
    }

    // Solves the set of n linear equations using a LU factorization
    // previously performed by luFactor.  On input, b[0..n-1] is the right
    // hand side of the equations, and on output, contains the solution.
    public static void luSolve(double a[][], int n, int ipvt[], double b[]) {
        int i;

        // find first nonzero b element
        for (i = 0; i != n; i++) {
            int row = ipvt[i];

            double swap = b[row];
            b[row] = b[i];
            b[i] = swap;
            if (swap != 0) {
                break;
            }
        }

        int bi = i++;
        for (; i < n; i++) {
            int row = ipvt[i];
            int j;
            double tot = b[row];

            b[row] = b[i];
            // forward substitution using the lower triangular matrix
            for (j = bi; j < i; j++) {
                tot -= a[i][j] * b[j];
            }
            b[i] = tot;
        }
        for (i = n - 1; i >= 0; i--) {
            double tot = b[i];

            // back-substitution using the upper triangular matrix
            int j;
            for (j = i + 1; j != n; j++) {
                tot -= a[i][j] * b[j];
            }
            b[i] = tot / a[i][i];
        }
    }

}
