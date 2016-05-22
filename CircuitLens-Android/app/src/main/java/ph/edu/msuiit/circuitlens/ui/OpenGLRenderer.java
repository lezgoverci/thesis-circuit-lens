package ph.edu.msuiit.circuitlens.ui;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.RajawaliRenderer;

import java.util.ArrayList;
import java.util.List;

import ph.edu.msuiit.circuitlens.R;

public class OpenGLRenderer  extends RajawaliRenderer {

    public Context context;

    private DirectionalLight directionalLight;
    private Plane circuitDiagram;
    Mat mCameraMatrix;

    double mRotX, mRotY, mRotZ, mTransX, mTransY, mTransZ = 0.0;


    public OpenGLRenderer(Context context) {
        super(context);
        this.context = context;
        setFrameRate(60);

    }

    public void initScene(){

        directionalLight = new DirectionalLight(1f, .2f, 1.0f);
        directionalLight.setColor(1.0f, 1.0f, 1.0f);
        directionalLight.setPower(2);
        getCurrentScene().addLight(directionalLight);

        Material material = new Material();
        material.enableLighting(true);
        material.setDiffuseMethod(new DiffuseMethod.Lambert());
        material.setColor(0);

        Texture earthTexture = new Texture("Circuit", R.drawable.circuit_a);
        try{
            material.addTexture(earthTexture);
        } catch (ATexture.TextureException error){
            Log.d("DEBUG", "TEXTURE ERROR");
        }

        circuitDiagram = new Plane(3, 3, 1, 1);
        circuitDiagram.setMaterial(material);
        circuitDiagram.setTransparent(true);
        circuitDiagram.setDoubleSided(true);

        getCurrentScene().addChild(circuitDiagram);
        getCurrentCamera().setZ(4.2f);


    }


    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);
        //circuitDiagram.rotate(Vector3.Axis.Y, 1.0);
//        circuitDiagram.setRotation(mRotX,mRotY,mRotZ);
//        circuitDiagram.setX(mTransX);
//        circuitDiagram.setY(mTransY);
//        circuitDiagram.setZ(mTransZ);
    }


    public void onTouchEvent(MotionEvent event){

    }

    public void onOffsetsChanged(float x, float y, float z, float w, int i, int j){

    }

    public void setProjectionValues(Mat f) {
       // mCameraMatrix = new Mat(3,3,CvType.CV_64FC1);
        Mat.eye(3, 3, CvType.CV_64FC1).copyTo(mCameraMatrix);

        mCameraMatrix.put(0, 0, 1.0);
        mCameraMatrix.put(0,2,1.0);
        mCameraMatrix.put(1,1,1.0);
        mCameraMatrix.put(1,2,1.0);
        mCameraMatrix.put(2,2,1.0);
        List<Mat> rotations = new ArrayList<>();
        List<Mat> translations = new ArrayList<>();
        List<Mat> normals = new ArrayList<>();
        Log.d("hommm",f.dump());
        Calib3d.decomposeHomographyMat(f,mCameraMatrix,rotations,translations,normals);
        Log.d("rotationList",rotations.toString());
        Log.d("rotationMat",rotations.get(0).dump());
    }
}

