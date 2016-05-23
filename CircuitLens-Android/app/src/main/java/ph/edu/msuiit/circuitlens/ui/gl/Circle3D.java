package ph.edu.msuiit.circuitlens.ui.gl;

import android.opengl.GLES20;

import org.rajawali3d.Object3D;
import org.rajawali3d.math.vector.Vector3;

import java.util.Stack;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Circle3D extends Object3D {
    protected Stack<Vector3> mPoints;
    protected Vector3 mCenter;
    protected float mLineThickness;
    protected double mRadius;

    /**
     * Creates a circle primitive.
     *
     * @param center
     * @param radius
     * @param thickness
     */
    public Circle3D(Vector3 center, double radius, float thickness)
    {
        this(center, radius, thickness, false);
    }

    /**
     * Creates a circle primitive.
     *
     * @param center
     * @param radius
     * @param thickness
     */
    public Circle3D(Vector3 center, double radius, float thickness, boolean filled)
    {
        this(center, radius, thickness, filled, true);
    }

    /**
     * Creates a circle primitive with specified thickness and color.
     *
     * @param center
     * @param radius
     * @param thickness
     * @param color
     */
    public Circle3D(Vector3 center, double radius, float thickness, boolean filled, int color)
    {
        this(center, radius, thickness, filled, true);
        setColor(color);
    }

    /**
     * Creates a circle primitive with a specified thickness
     *
     * @param center
     * @param radius
     * @param thickness
     * @param createVBOs
     */
    public Circle3D(Vector3 center, double radius, float thickness,boolean filled, boolean createVBOs) {
        super();
        mCenter = center;
        mRadius = radius;
        mPoints = new Stack<>();
        mLineThickness = thickness;
        init(filled,createVBOs);
    }

    public Vector3 getPoint(int point) {
        return mPoints.get(point);
    }

    protected void init(boolean filled, boolean createVBOs) {
        setDoubleSided(true);

        if(filled) {
            setDrawingMode(GLES20.GL_TRIANGLE_FAN);
            int i;
            int triangleAmount = 40; //# of triangles used to draw circle

            //GLfloat radius = 0.8f; //radius
            double twicePi = 2 * Math.PI;

            mPoints.add(mCenter); // center of circle
            for(i = 0; i <= triangleAmount;i++) {
                Vector3 vertex = new Vector3(
                        mRadius * cos(i *  twicePi / triangleAmount),
                        mRadius * sin(i * twicePi / triangleAmount),
                        0
                );
                mPoints.add(vertex.add(mCenter));
            }
        }
        else {
            setDrawingMode(GLES20.GL_LINE_LOOP);
            final int num_segments = 100;
            double theta = 2 * Math.PI / num_segments;
            double c = cos(theta);//precalculate the sine and cosine
            double s = sin(theta);
            double t;

            double x = mRadius;//we start at angle = 0
            double y = 0;
            for(int ii = 0; ii < num_segments; ii++)
            {
                Vector3 vertex = new Vector3(x,y,0);
                mPoints.add(vertex.add(mCenter));//output vertex

                //apply the rotation matrix
                t = x;
                x = c * x - s * y;
                y = s * t + c * y;
            }
        }

        int numVertices = mPoints.size();

        float[] vertices = new float[numVertices * 3];
        int[] indices = new int[numVertices];
        float[] colors = null;

        for(int i=0; i<numVertices; i++) {
            Vector3 point = mPoints.get(i);
            int index = i * 3;
            vertices[index] = (float) point.x;
            vertices[index+1] = (float) point.y;
            vertices[index+2] = (float) point.z;
            indices[i] = (short)i;
        }

        setData(vertices, null, null, colors, indices, createVBOs);

        vertices = null;
        colors = null;
        indices = null;
    }

    public void preRender() {
        super.preRender();
        GLES20.glLineWidth(mLineThickness);
    }

    public void setLineThickness(final float lineThickness) {
        mLineThickness = lineThickness;
    }

    public float getLineThickness() {
        return mLineThickness;
    }
}
