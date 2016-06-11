package ph.edu.msuiit.circuitlens.circuit.elements;

class AcRailElm extends RailElm {
    public AcRailElm(int xx, int yy) { super(xx, yy, WF_AC); }
	public Class getDumpClass() { return RailElm.class; }
    }
