package ph.edu.msuiit.circuitlens.circuit;

import org.rajawali3d.Object3D;

public class GraphicElm extends CircuitElm {

    public GraphicElm(int xx, int yy) {
        super(xx, yy);
    }

    public GraphicElm(int xa, int ya, int xb, int yb, int flags) {
        super(xa, ya, xb, yb, flags);
    }

    @Override
    public int getPostCount() {
        return 0;
    }

    @Override
    public void updateObject3D() {
        circuitElm3D = generateObject3D();
    }

    @Override
    public Object3D generateObject3D() {
        return new Object3D();
    }
}
