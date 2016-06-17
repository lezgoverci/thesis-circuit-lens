package ph.edu.msuiit.circuitlens.cirsim.elements;

class AcRailElm extends RailElm {
    public AcRailElm(int xx, int yy) { super(xx, yy, WF_AC); }
	public Class getDumpClass() { return RailElm.class; }
    }
