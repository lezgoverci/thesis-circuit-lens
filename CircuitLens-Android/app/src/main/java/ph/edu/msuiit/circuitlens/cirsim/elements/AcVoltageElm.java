package ph.edu.msuiit.circuitlens.cirsim.elements;

class AcVoltageElm extends VoltageElm {
    public AcVoltageElm(int xx, int yy) { super(xx, yy, WF_AC); }
	public Class getDumpClass() { return VoltageElm.class; }
    }
