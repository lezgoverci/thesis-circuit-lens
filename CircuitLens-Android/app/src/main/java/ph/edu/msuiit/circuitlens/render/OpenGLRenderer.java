package ph.edu.msuiit.circuitlens.render;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.rajawali3d.Object3D;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.MathUtil;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.Quaternion;
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
    private Matrix4 mPose;
    private MatOfDouble mRotation = null;

    Quaternion mOrient;


    private Object3D mNullObj;
    private Object3D mNullObjLook;
    private float[] mGLPose;


    public OpenGLRenderer(Context context) {
        super(context);
        this.context = context;
        setFrameRate(60);

    }

    public void initScene(){

//        directionalLight = new DirectionalLight(1f, .2f, 1.0f);
//        directionalLight.setColor(1.0f, 1.0f, 1.0f);
//        directionalLight.setPower(2);
//        getCurrentScene().addLight(directionalLight);

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

        mNullObj = new Object3D();
        mNullObjLook = new Object3D();

        getCurrentScene().addChild(circuitDiagram);
        getCurrentScene().addChild(mNullObj);
        getCurrentScene().addChild(mNullObjLook);
        getCurrentCamera().setFarPlane(1000.0);




    }


    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);
        //circuitDiagram.rotate(Vector3.Axis.Y, 1.0);

        if(mTransX != 0.0){

//            mOrient= new Quaternion();
//            mOrient.fromEuler(mRotY,mRotX,mRotZ);
//
//            mNullObjLook.setPosition(mTransX * -1,mTransY,mTransZ);
//            mNullObj.setPosition(mTransX * -1,mTransY,mTransZ);
//            mNullObj.setRotX(MathUtil.radiansToDegrees(mRotY));
//            mNullObj.setRotY(MathUtil.radiansToDegrees(mRotX));
//            mNullObj.setRotZ(MathUtil.radiansToDegrees(mRotZ * -1));
            //mNullObj.setOrientation(mOrient);


//            circuitDiagram.setRotation(mRotX,mRotY,mRotZ);
//            circuitDiagram.setX(mTransX);
//            circuitDiagram.setY(mTransY);
//            circuitDiagram.setZ(mTransZ);

            getCurrentCamera().setPosition(mTransX * -1,mTransY,mTransZ);
//            getCurrentCamera().setRotX(mNullObj.getRotX());
//            getCurrentCamera().setRotY(mNullObj.getRotY());
//            getCurrentCamera().setRotZ(mNullObj.getRotZ());
            //getCurrentCamera().setOrientation(mNullObj.getOrientation());
            getCurrentCamera().setCameraYaw(MathUtil.radiansToDegrees(mRotY));
            getCurrentCamera().setCameraPitch(MathUtil.radiansToDegrees(mRotX));
            getCurrentCamera().setCameraRoll(MathUtil.radiansToDegrees(mRotZ * -1));
//            getCurrentCamera().setRotX(mRotX);
//            getCurrentCamera().setRotY(mRotY);
//            getCurrentCamera().setRotZ(mRotZ);




        }



    }


    public void onTouchEvent(MotionEvent event){

    }

    public void onOffsetsChanged(float x, float y, float z, float w, int i, int j){

    }

    public void setProjectionValues(MatOfDouble rVec, MatOfDouble tVec, float[] glPose) {
       // mCameraMatrix = new Mat(3,3,CvType.CV_64FC1);
//        Mat.eye(3, 3, CvType.CV_64FC1).copyTo(mCameraMatrix);
//
//        mCameraMatrix.put(0, 0, 1.0);
//        mCameraMatrix.put(0,2,1.0);
//        mCameraMatrix.put(1,1,1.0);
//        mCameraMatrix.put(1,2,1.0);
//        mCameraMatrix.put(2,2,1.0);
//        List<Mat> rotations = new ArrayList<>();
//        List<Mat> translations = new ArrayList<>();
//        List<Mat> normals = new ArrayList<>();
//        Log.d("hommm",f.dump());
//        Calib3d.decomposeHomographyMat(f,mCameraMatrix,rotations,translations,normals);
//        Log.d("rotationList",rotations.toString());
//        Log.d("rotationMat",rotations.get(0).dump());

        mRotX = rVec.toArray()[0];
        mRotY = rVec.toArray()[1];
        mRotZ = rVec.toArray()[2];

        //mRotation = rVec;
        mTransX = tVec.toArray()[0];
        mTransY = tVec.toArray()[1];
        mTransZ = tVec.toArray()[2];

        mGLPose = glPose;


        Log.d("rendererRVec",rVec.dump());
        Log.d("rendererTVec",tVec.dump());



        // mPose = new Matrix4(pose);
    }
}

