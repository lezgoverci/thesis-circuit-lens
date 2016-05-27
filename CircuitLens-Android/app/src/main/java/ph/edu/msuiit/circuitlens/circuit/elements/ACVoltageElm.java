package ph.edu.msuiit.circuitlens.circuit.elements;

class ACVoltageElm extends VoltageElm {
    public ACVoltageElm(int xx, int yy) { super(xx, yy, WF_AC); }
	public Class getDumpClass() { return VoltageElm.class; }
    }
