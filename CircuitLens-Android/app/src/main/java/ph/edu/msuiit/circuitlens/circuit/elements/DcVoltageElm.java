package ph.edu.msuiit.circuitlens.circuit.elements;

public class DcVoltageElm extends VoltageElm {

    public DcVoltageElm(int xx, int yy) {
        super(xx, yy, WF_DC);
    }

    public Class getDumpClass() {
        return VoltageElm.class;
    }
}
