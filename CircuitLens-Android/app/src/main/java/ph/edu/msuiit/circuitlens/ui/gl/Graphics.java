package ph.edu.msuiit.circuitlens.ui.gl;

import android.graphics.Point;

import org.rajawali3d.Object3D;
import org.rajawali3d.materials.Material;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Line3D;

import java.util.Stack;

public class Graphics {
    public void drawTriangle(Object3D object3D, Point[] points, Material material) {
        Stack<Vector3> points3D = new Stack<>();
        for(int i=0;i<3;i++)
            points3D.add(new Vector3(points[i].x,points[i].y,0));

        Triangle3D triangle = new Triangle3D(points3D,1);
        triangle.setMaterial(material);
        object3D.addChild(triangle);
    }

    public static void drawThickLine(Object3D object3D, int x, int y, int x2, int y2, Material material) {
        Stack<Vector3> points = new Stack<>();
        points.add(new Vector3(x,y,0));
        points.add(new Vector3(x2,y2,0));
        Line3D thickLine = new Line3D(points,6);
        thickLine.setMaterial(material);

        object3D.addChild(thickLine);
    }

    public static void drawThickCircle(Object3D object3D, int cx, int cy, int ri, Material material) {
        Circle3D circle = new Circle3D(new Vector3(cx,cy,1),ri, 5);
        circle.setMaterial(material);
        object3D.addChild(circle);
    }
}
