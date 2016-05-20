package ph.edu.msuiit.circuitlens.ui.gl;


import android.graphics.Color;

import org.rajawali3d.Object3D;
import org.rajawali3d.math.vector.Vector3;

import java.util.Stack;

import android.opengl.GLES20;

public class Triangle3D extends Object3D {
    protected Stack<Vector3> mPoints;
    protected float mLineThickness;
    protected int[] mColors;

    public Triangle3D() {}

    /**
     * Creates a triangle primitive.
     *
     * @param points
     * @param thickness
     */
    public Triangle3D(Stack<Vector3> points, float thickness)
    {
        this(points, thickness, null);
    }

    /**
     * Creates a triangle primitive with a single color.
     *
     * @param points
     * @param thickness
     * @param color
     */
    public Triangle3D(Stack<Vector3> points, float thickness, int color)
    {
        this(points, thickness, null);
        setColor(color);
    }

    /**
     * Creates a triangle primitive with a specified color for each point.
     *
     * @param points
     * @param thickness
     * @param colors
     */
    public Triangle3D(Stack<Vector3> points, float thickness, int[] colors) {
        this(points, thickness, colors, true);
    }

    /**
     * Creates a triangle primitive with a specified color for each point.
     *
     * @param points
     * @param thickness
     * @param colors
     * @param createVBOs
     */
    public Triangle3D(Stack<Vector3> points, float thickness, int[] colors, boolean createVBOs) {
        super();
        mPoints = points;
        mLineThickness = thickness;
        mColors = colors;
        if (colors != null && colors.length != points.size())
            throw new RuntimeException("The number of line points and colors is not the same.");
        init(createVBOs);
    }

    public Vector3 getPoint(int point) {
        return mPoints.get(point);
    }

    protected void init(boolean createVBOs) {
        setDoubleSided(true);
        setDrawingMode(GLES20.GL_TRIANGLES);

        int numVertices = mPoints.size();

        float[] vertices = new float[numVertices * 3];
        int[] indices = new int[numVertices];
        float[] colors = null;

        if(mColors != null)
            colors = new float[mColors.length * 4];

        for(int i=0; i<numVertices; i++) {
            Vector3 point = mPoints.get(i);
            int index = i * 3;
            vertices[index] = (float) point.x;
            vertices[index+1] = (float) point.y;
            vertices[index+2] = (float) point.z;
            indices[i] = (short)i;

            if(mColors != null)
            {
                int color = mColors[i];
                int colorIndex = i * 4;
                colors[colorIndex] = Color.red(color) / 255.f;
                colors[colorIndex + 1] = Color.green(color) / 255.f;
                colors[colorIndex + 2] = Color.blue(color) / 255.f;
                colors[colorIndex + 3] = Color.alpha(color) / 255.f;
            }
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
