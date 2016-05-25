package ph.edu.msuiit.circuitlens.circuit;

import android.graphics.Color;
import android.util.Log;

import org.rajawali3d.Object3D;
import org.rajawali3d.materials.Material;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Line3D;

import java.util.Stack;

public class CircuitCanvas3D extends Object3D{
    private int x1;
    private int x2;
    private int y1;
    private int y2;

    public CircuitCanvas3D(CircuitSimulator circuitSimulator) {

    }

    public void setCircuitBounds(int x1, int y1, int x2, int y2){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        Log.d(getClass().getSimpleName(),"x1: " + x1);
        Log.d(getClass().getSimpleName(),"y1: " + y1);
        Log.d(getClass().getSimpleName(),"x2: " + x2);
        Log.d(getClass().getSimpleName(),"y2: " + y2);
    }

    public int getCircuitWidth() {
        return x2-x1;
    }

    public int getCircuitHeight() {
        return y2-y1;
    }

    public int getCircuitCenterX(){
        return x1+getCircuitWidth()/2;
    }

    public int getCircuitCenterY(){
        return y1+getCircuitHeight()/2;
    }

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }

    public void drawBounds(Object3D object3D){
        drawThickLine(object3D,x1,y1,x2,y1); // top
        drawThickLine(object3D,x1,y1,x1,y2); // left
        drawThickLine(object3D,x2,y1,x2,y2); // right
        drawThickLine(object3D,x1,y2,x2,y2); // bottom
        drawThickLine(object3D,x1,y1,x2,y1); // top

        int x0 = getCircuitCenterX();
        int y0 = getCircuitCenterY();
        drawThickLine(object3D,x0-15,y0-15,x0+15,y0+15);
        drawThickLine(object3D,x0+15,y0-15,x0-15,y0+15);
    }

    public static void drawThickLine(Object3D object3D, int x, int y, int x2, int y2) {
        Material material = new Material();
        material.setColor(Color.BLUE);

        Stack<Vector3> points = new Stack<>();
        points.add(new Vector3(x,y,5));
        points.add(new Vector3(x2,y2,5));
        Line3D thickLine = new Line3D(points,10);
        thickLine.setMaterial(material);

        object3D.addChild(thickLine);
    }
}
