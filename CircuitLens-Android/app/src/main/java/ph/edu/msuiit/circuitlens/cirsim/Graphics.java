package ph.edu.msuiit.circuitlens.cirsim;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;

import org.rajawali3d.Object3D;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Line3D;
import org.rajawali3d.primitives.Plane;

import java.util.Stack;

import ph.edu.msuiit.circuitlens.ui.gl.Circle3D;
import ph.edu.msuiit.circuitlens.ui.gl.Triangle3D;

public class Graphics {

    public static Point interpPoint(Point a, Point b, double f) {
        Point p = new Point();
        interpPoint(a, b, p, f);
        return p;
    }

    public static void interpPoint(Point a, Point b, Point c, double f) {
        double q = (a.x*(1-f)+b.x*f+.48);
        System.out.println(q + " " + (int) q);
        c.x = (int) Math.floor(a.x * (1 - f) + b.x * f + .48);
        c.y = (int) Math.floor(a.y * (1 - f) + b.y * f + .48);
    }

    public static void interpPoint(Point a, Point b, Point c, double f, double g) {
        int gx = b.y - a.y;
        int gy = a.x - b.x;
        g /= Math.sqrt(gx * gx + gy * gy);
        c.x = (int) Math.floor(a.x * (1 - f) + b.x * f + g * gx + .48);
        c.y = (int) Math.floor(a.y * (1 - f) + b.y * f + g * gy + .48);
    }

    public Point interpPoint(Point a, Point b, double f, double g) {
        Point p = new Point();
        interpPoint(a, b, p, f, g);
        return p;
    }

    public static void interpPoint2(Point a, Point b, Point c, Point d, double f, double g) {
        int gx = b.y - a.y;
        int gy = a.x - b.x;
        g /= Math.sqrt(gx * gx + gy * gy);
        c.x = (int) Math.floor(a.x * (1 - f) + b.x * f + g * gx + .48);
        c.y = (int) Math.floor(a.y * (1 - f) + b.y * f + g * gy + .48);
        d.x = (int) Math.floor(a.x * (1 - f) + b.x * f - g * gx + .48);
        d.y = (int) Math.floor(a.y * (1 - f) + b.y * f - g * gy + .48);
    }

    public static void drawTriangle(Object3D object3D, Point[] points, Material material) {
        Stack<Vector3> points3D = new Stack<>();
        for(int i=0;i<3;i++)
            points3D.add(new Vector3(points[i].x,points[i].y,0));

        Triangle3D triangle = new Triangle3D(points3D,1);
        triangle.setMaterial(material);
        object3D.addChild(triangle);
    }

    public static void drawCoil(Object3D object3D, int hs, Point p1, Point p2,
                                Material[] coilMaterials) {
        int segments = 30;
        int i;
        double segf = 1. / segments;
        Point ps1 = new Point();
        Point ps2 = new Point();
        ps1.set(p1.x, p1.y);
        for (i = 0; i != segments; i++) {
            double cx = (((i + 1) * 6. * segf) % 2) - 1;
            double hsx = Math.sqrt(1 - cx * cx);
            if (hsx < 0)
                hsx = -hsx;
            interpPoint(p1, p2, ps2, i * segf, hsx * hs);
            coilMaterials[i] = new Material();
            drawThickLine(object3D, ps1, ps2, coilMaterials[i]);
            ps1.set(ps2.x,ps2.y);
        }
    }

    public static double distance(Point p1, Point p2) {
        double x = p1.x - p2.x;
        double y = p1.y - p2.y;
        return Math.sqrt(x * x + y * y);
    }

    public static void drawThickLine(Object3D object3D, Point pa, Point pb, Material material) {
        drawThickLine(object3D, pa.x, pa.y, pb.x, pb.y, material);
    }

    public static void drawThickLine(Object3D object3D, int x, int y, int x2, int y2) {
        Material material = new Material();
        material.setColor(Color.WHITE);
        drawThickLine(object3D, x, y, x2, y2, material);
    }

    public static void drawThickLine(Object3D object3D, int x, int y, int x2, int y2, Material material) {
        Stack<Vector3> points = new Stack<>();
        points.add(new Vector3(x,y,0));
        points.add(new Vector3(x2,y2,0));
        Line3D thickLine = new Line3D(points,5);
        thickLine.setMaterial(material);

        object3D.addChild(thickLine);
    }

    public static void drawThickLine(Object3D object3D, Point pa, Point pb) {
        drawThickLine(object3D, pa.x, pa.y, pb.x, pb.y);
    }

    public static Material drawThickCircle(Object3D object3D, int cx, int cy, int ri) {
        Material material = new Material();
        material.setColor(Color.WHITE);
        drawThickCircle(object3D, cx, cy, ri, material);
        return material;
    }

    public static void drawThickCircle(Object3D object3D, int cx, int cy, int ri, Material material) {
        Circle3D circle = new Circle3D(new Vector3(cx,cy,1),ri, 5);
        circle.setMaterial(material);
        object3D.addChild(circle);
    }

    public static Bitmap textAsBitmap(String text, int textSize, int color) {
        Rect bounds = new Rect();
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        paint.setAntiAlias(true);
        paint.getTextBounds(text,0,text.length(),bounds);
        paint.setColor(color);
        Bitmap image = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text,0,bounds.height(),paint);
        return image;
    }

    public static void draw3DText(Object3D object3D, String text, int textSize, int color) {
        draw3DText(object3D, text, 0, 0, textSize, color);
    }

    public static void draw3DText(Object3D object3D, String text, int x, int y, int textSize, int color) {
        Bitmap rasterText = Graphics.textAsBitmap(text, textSize, color);
        Material textMaterial = new Material();
        textMaterial.setColor(Color.TRANSPARENT);
        Texture texture = new Texture("text",rasterText);
        try {
            textMaterial.addTexture(texture);
        } catch (ATexture.TextureException e) {
            e.printStackTrace();
        }
        Plane textPlane = new Plane(rasterText.getWidth(), rasterText.getHeight(), 2, 2);
        textPlane.setDoubleSided(true);
        textPlane.setScale(-1,-1,1);
        textPlane.setTransparent(true);
        textPlane.setMaterial(textMaterial);
        textPlane.setPosition(x,y,0);
        object3D.addChild(textPlane);
    }
}
