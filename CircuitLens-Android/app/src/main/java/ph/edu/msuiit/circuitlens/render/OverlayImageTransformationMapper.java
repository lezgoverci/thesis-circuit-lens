package ph.edu.msuiit.circuitlens.render;

import android.util.Log;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vercillius on 4/30/2016.
 */
public class OverlayImageTransformationMapper {


    /** REFERENCE IMAGE VARIABLES **/
    private MatOfPoint2f        mTrackingImageApproxHull2D = new MatOfPoint2f();            // approximated Convex hull points of the tracking image
    private Mat                 mTrackingImageBoxCorners = new Mat(4,1,CvType.CV_32FC2);    // box corners of the tracking image in pixels
    private MatOfPoint2f        mTrackingImageBoxPoints2D = new MatOfPoint2f();             // box points enclosing the tracking image in pixels
    private final MatOfPoint3f  mTrackingImageBoxCorners3D = new MatOfPoint3f();            // The reference image's corner coordinates, in 3D, in real units

    /** CURRENT IMAGE VARIABLES **/
    private Mat                 mCurrentFrameGray = new Mat();                              // A grayscale version of the current frame
    private MatOfPoint2f        mCurrentFrameApproxHull2D = new MatOfPoint2f();             // approximated Convex hull points of the current image
    private Mat                 mCurrentFrameBoxCorners = new Mat(4,1,CvType.CV_32FC2);     // box corners of the current image in pixels
    private MatOfPoint2f        mCurrentFrameBoxPoints2D = new MatOfPoint2f();              // box points enclosing the current image in pixels
    private List<MatOfPoint>    mCurrentFrameContoursList = new ArrayList<MatOfPoint>();    // a list of the current frame's contours
    private Mat                 mCurrentFrameContoursHierarchy = new Mat();                 // contours hierarchy of the current image
    private MatOfPoint          mCurrentFrameHull = new MatOfPoint();                       // Hull points in pixels of the contour of the current image
    private MatOfPoint          mCurrentFrameLargestContour = new MatOfPoint();             // largest contour of the current image
    private Mat                 mCurrentFrameHomography = new Mat();


    /** FLAGS **/
    private boolean             isSetTracking = false;


    /** VALUES **/
    private Size                blurKernel = new Size(5,5);
    private Scalar              mLineColor = new Scalar(255);
    private Scalar              mLineColor2 = new Scalar(0.0,0.0,255.0);
    private Mat                 dilateKernel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,new Size(5.0,5.0));
    private final int           FROM_TRACKING_IMAGE = 0;
    private final int           FROM_CURRENT_FRAME = 1;
    private double              mRealSize = 1.0;


    /**  OUTPUT VARIABLES  **/
    private final MatOfDouble   mRVec = new MatOfDouble();                                  // The Euler angles of the detected target.
    private final MatOfDouble   mTVec = new MatOfDouble();                                  // The XYZ coordinates of the detected target.
    private final MatOfDouble   mRotation = new MatOfDouble();                              // The rotation matrix of the detected target.
    private final float[]       mGLPose = new float[16];                                    // The OpenGL pose matrix of the detected target.
    private final MatOfDouble   mDistCoeffs = new MatOfDouble(0.0, 0.0, 0.0, 0.0);          // Assume no distortion
    private boolean             mIsHomographyFound;
    private boolean             mIsTransformed;
    private MatOfDouble         mProjection;
    private int                 mCircuitWidth;
    private int                 mCircuitHeight;
    private int mCircuitX;
    private int mCircuitY;
    private double mCameraAspectRatio;
    private int mCameraWidth;
    private int mCameraHeight;


    /** CONSTRUCTOR **/
    public OverlayImageTransformationMapper(){

        // set initial values of essential variables


    }

    public float[] getmGLPose() {
        return mGLPose;
    }

    public MatOfDouble getRotation(){
        return mRotation;
    }




    public void map(Mat currentFrame,boolean isTakePhoto) {

        // Enhance features
        Imgproc.cvtColor(currentFrame,mCurrentFrameGray,Imgproc.COLOR_RGB2GRAY);
        Imgproc.blur(mCurrentFrameGray,mCurrentFrameGray,blurKernel);
        Imgproc.Canny(mCurrentFrameGray,mCurrentFrameGray,150,250);
        Imgproc.dilate(mCurrentFrameGray,mCurrentFrameGray,dilateKernel);

        // Find Contours
        Imgproc.findContours(mCurrentFrameGray.clone(),mCurrentFrameContoursList,mCurrentFrameContoursHierarchy,Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);

        // Find the largest contour
        int indexLargest = findLargestContour(mCurrentFrameContoursList);

        // find convex hull of largest contour
        MatOfInt hull = new MatOfInt();
        if(mCurrentFrameLargestContour.toArray().length > 3){
            Imgproc.convexHull(mCurrentFrameLargestContour,hull,true);
        }
        else{
            return;
        }


        // Convert hull to MatOfPoint from MatOfInt
       // mCurrentFrameHull = convertHullToMatOfPoint(sortHullPoints(hull));
        mCurrentFrameHull = convertHullToMatOfPoint(hull);

        // Approximate convex hull polygon
        mCurrentFrameApproxHull2D= approxHull(mCurrentFrameHull);

        // Draw contour and convex hull
        //mCurrentFrameContour = Mat.zeros(currentFrame.size(),currentFrame.type());

        // Temporary list for drawing convex hull points.
        // drawContours requires List<MatOfPoint> as input parameter
        List<MatOfPoint> hullList = new ArrayList<>();
        hullList.add(mCurrentFrameHull);

        // Draw contour
        Imgproc.drawContours(currentFrame,mCurrentFrameContoursList,indexLargest,mLineColor2,5);

        // Draw convex hull
        if(hullList.size() > 0){
            Imgproc.drawContours(currentFrame,hullList,-1,mLineColor,5);
            // draw labels
            drawLabels(currentFrame,mCurrentFrameApproxHull2D);
        }

        mCurrentFrameContoursList.clear();
        hullList.clear();


        if(isTakePhoto){
            setTrackingImageHullPoints();
            setTrackingImageBoxCorners();
            setTrackingImageBoxCorners3D(mRealSize);
            isSetTracking = true;
        }

        if(isSetTracking ){
            //finds homography matrix
            setHomographyTransformation();


            if(isHomographyFound()){
                // apply the current homography to the corners of the contour box
                applyHomographyTransformation();



                // projects the contour box points to new transformation matrix
                updateCurrentFrameBoxPoints();

                setTransformationMatrixValues();

                // draw new points
                drawPoints(mCurrentFrameBoxPoints2D, currentFrame);
            }




        }


    }

    private void drawPoints(MatOfPoint2f mCurrentFrameBoxPoints2D, Mat currentFrame) {
        List<Point> points = mCurrentFrameBoxPoints2D.toList();
        Imgproc.line(currentFrame,points.get(0),points.get(1),mLineColor);
        Imgproc.line(currentFrame,points.get(1),points.get(2),mLineColor);
        Imgproc.line(currentFrame,points.get(2),points.get(3),mLineColor);
        Imgproc.line(currentFrame,points.get(3),points.get(0),mLineColor);


    }

    public boolean isHomographyFound(){
        if(mIsHomographyFound){
            return true;
        }else{
            return false;
        }
    }

    private void setTransformationMatrixValues() {

        Log.d("projection",mProjection.dump());
        Log.d("projection3D",mTrackingImageBoxCorners3D.dump());
        Log.d("projection2D",mCurrentFrameBoxPoints2D.dump());
        mIsTransformed = Calib3d.solvePnP(mTrackingImageBoxCorners3D,mCurrentFrameBoxPoints2D,mProjection,mDistCoeffs,mRVec,mTVec);

//        final double[] rVecArray = mRVec.toArray();
//        rVecArray[0] *= -1.0; // negate x angle
//        mRVec.fromArray(rVecArray);
//
//        // Convert the Euler angles to a 3x3 rotation matrix.
//        Calib3d.Rodrigues(mRVec, mRotation);
//
//        Log.d("setValTrue",mIsTransformed +"");
//
//
//        final double[] tVecArray = mTVec.toArray();
//
//        // OpenCV's matrix format is transposed, relative to
//        // OpenGL's matrix format.
//        mGLPose[0]  =  (float)mRotation.get(0, 0)[0];
//        mGLPose[1]  =  (float)mRotation.get(0, 1)[0];
//        mGLPose[2]  =  (float)mRotation.get(0, 2)[0];
//        mGLPose[3]  =  0f;
//        mGLPose[4]  =  (float)mRotation.get(1, 0)[0];
//        mGLPose[5]  =  (float)mRotation.get(1, 1)[0];
//        mGLPose[6]  =  (float)mRotation.get(1, 2)[0];
//        mGLPose[7]  =  0f;
//        mGLPose[8]  =  (float)mRotation.get(2, 0)[0];
//        mGLPose[9]  =  (float)mRotation.get(2, 1)[0];
//        mGLPose[10] =  (float)mRotation.get(2, 2)[0];
//        mGLPose[11] =  0f;
//        mGLPose[12] =  (float)tVecArray[0];
//        mGLPose[13] = -(float)tVecArray[1]; // negate y position
//        mGLPose[14] = -(float)tVecArray[2]; // negate z position
//        mGLPose[15] =  1f;
    }

    public MatOfDouble getRVec(){
        return mRVec;
    }

    public MatOfDouble getTVec(){
        return mTVec;
    }

    private void setTrackingImageBoxCorners3D(double size) {
        double aspectRatio = (double) mCurrentFrameGray.cols() / (double) mCurrentFrameGray.rows();
        Log.d("projectionAspect",aspectRatio+"");
        double halfRealWidth;
        double halfRealHeight;

        if(mCurrentFrameGray.cols() > mCurrentFrameGray.rows()){
            halfRealHeight = 0.5f * size;
            halfRealWidth = halfRealHeight * aspectRatio;
        } else{
            halfRealWidth = 0.5f * size;
            halfRealHeight = halfRealWidth / aspectRatio;
        }

        mTrackingImageBoxCorners3D.fromArray(
                new Point3(-halfRealWidth,-halfRealHeight, 0.0),
                new Point3(halfRealWidth,-halfRealHeight, 0.0),
                new Point3(halfRealWidth,halfRealHeight, 0.0),
                new Point3(-halfRealWidth,halfRealHeight, 0.0)
        );
    }

    private void updateCurrentFrameBoxPoints() {
            mCurrentFrameBoxPoints2D = getBoxPoints(mCurrentFrameBoxCorners);



    }

    private void applyHomographyTransformation() {
        // Use current homography to project tracking image
        // corner coordinates into current frame corner coordinates
        Log.d("boxCornersT",mTrackingImageBoxCorners.dump());
        Log.d("boxCornersC",mCurrentFrameBoxCorners.dump());
       // Log.d("boxCornersH",mCurrentFrameHomography.dump());

        if(mIsHomographyFound){
            Core.perspectiveTransform(mTrackingImageBoxCorners,mCurrentFrameBoxCorners,mCurrentFrameHomography);
        }

        Log.d("boxCornersC2",mCurrentFrameBoxCorners.dump());

    }

    private void setTrackingImageBoxCorners() {
        Rect box = Imgproc.boundingRect(mCurrentFrameHull);
        mCircuitX = box.x;
        mCircuitY = box.y;
        mCircuitWidth = box.width;
        mCircuitHeight = box.height;
        mTrackingImageBoxCorners.put(0,0,new double[]{mCircuitX,mCircuitY});
        mTrackingImageBoxCorners.put(1,0,new double[]{mCircuitX + mCircuitWidth ,mCircuitY});
        mTrackingImageBoxCorners.put(2,0,new double[]{mCircuitX + mCircuitWidth ,mCircuitY + mCircuitHeight});
        mTrackingImageBoxCorners.put(3,0,new double[]{mCircuitX ,mCircuitY + mCircuitHeight});
    }

    private MatOfPoint2f getBoxPoints(Mat boxCorners) {

        MatOfPoint2f boxPoints2D = new MatOfPoint2f();

        final double[] trackingImageBoxCorner0 = boxCorners.get(0,0);
        final double[] trackingImageBoxCorner1 = boxCorners.get(1,0);
        final double[] trackingImageBoxCorner2 = boxCorners.get(2,0);
        final double[] trackingImageBoxCorner3 = boxCorners.get(3,0);
        boxPoints2D.fromArray(
                new Point(trackingImageBoxCorner0[0],trackingImageBoxCorner0[1]),
                new Point(trackingImageBoxCorner1[0],trackingImageBoxCorner1[1]),
                new Point(trackingImageBoxCorner2[0],trackingImageBoxCorner2[1]),
                new Point(trackingImageBoxCorner3[0],trackingImageBoxCorner3[1])
        );

        return boxPoints2D;
    }

    private void setTrackingImageHullPoints() {
        mTrackingImageApproxHull2D = mCurrentFrameApproxHull2D;
    }


    private void drawLabels(Mat currentFrame, MatOfPoint2f approxHull) {
        List<Point> list = approxHull.toList();

        for(int i =0; i < list.size(); i++){
            Imgproc.putText(currentFrame,i+1 + "",list.get(i),1,10.,new Scalar(0,255,0));
        }


    }

    private MatOfPoint2f approxHull(MatOfPoint hull) {
        MatOfPoint2f curve = new MatOfPoint2f(hull.toArray());
        MatOfPoint2f res = new MatOfPoint2f();

        double arcLength = Imgproc.arcLength(curve,true);
        double epsilon = 0.01* arcLength;

        Imgproc.approxPolyDP(curve,res,epsilon,true);

       // Log.d("approxHull",res.dump() + "size: " + res.toArray().length + "");

        return res;
    }

    //TODO sort hull points for convex hull matching
    private MatOfInt sortHullPoints(MatOfInt hullInt){
        List hullPointsList = new ArrayList();
        hullPointsList = hullInt.toList();
        MatOfInt res = new MatOfInt();
        List resList = new ArrayList();

        int startIndex;
        int middleIndex1;
        int endIndex;

        startIndex = hullPointsList.indexOf(0);
        if(startIndex < 0){
            startIndex = 0;
        }
        middleIndex1 = hullPointsList.size() - 1;
        endIndex = startIndex - 1;


        if(hullPointsList.size() > 0){
            if(startIndex != 0){
                resList.addAll(hullPointsList.subList(startIndex,middleIndex1));
                resList.addAll(hullPointsList.subList(0,endIndex));
            }
            else{
                resList.addAll(hullPointsList.subList(startIndex,middleIndex1));
            }

        }



        res.fromList(resList);

        // clear lists
        hullPointsList.clear();
        resList.clear();

        return res;
    }

    private MatOfPoint convertHullToMatOfPoint(MatOfInt hull) {
        List<Point> hullPointsList = new ArrayList<>();

        int[] hullIntList = hull.toArray();
        MatOfPoint res = new MatOfPoint();

        List<Point> largestContourPointsList = mCurrentFrameLargestContour.toList();

        for(int i = 0; i < hullIntList.length; i++){
            hullPointsList.add(largestContourPointsList.get(hullIntList[i]));
        }
       // Log.d("hullInt",hull.dump());

        res.fromList(hullPointsList);

        // clear lists
        hullPointsList.clear();

        return res;
    }



    private int findLargestContour(List<MatOfPoint> contours) {
        double largestArea = 0;
        int largestIndex = -1;

        if(contours.size() > 0){
            for(int i = 0; i < contours.size();i++){
                Mat cnt = contours.get(i);
                double cntArea = Imgproc.contourArea(cnt);
                if(cntArea > largestArea){
                    largestArea = cntArea;
                    largestIndex = i;
                }
            }
            mCurrentFrameLargestContour = contours.get(largestIndex);
        }
        contours.clear();
        return largestIndex;
    }



    private void setHomographyTransformation() {
        // Else there are enough good points to find homography

        mIsHomographyFound = false;

        // Convert the matched points to MatOfPoint2f format, as
        // required by the Calib3d.findHomography function
        List<Point> trackingImageHullPoints = new ArrayList<>();
        trackingImageHullPoints = mTrackingImageApproxHull2D.toList();

        List<Point> currentFrameHullPoints = new ArrayList<>();
        currentFrameHullPoints = mCurrentFrameApproxHull2D.toList();

        // find the homography
        //TODO check correspondence
       if(trackingImageHullPoints.size() == currentFrameHullPoints.size()){
           mCurrentFrameHomography = Calib3d.findHomography(mTrackingImageApproxHull2D,mCurrentFrameApproxHull2D,Calib3d.RANSAC,50.0);
           mIsHomographyFound = true;
       }

       // Log.d("homo",currentFrameHullPoints.size() +"");
       // return mCurrentFrameHomography;


    }

    public Mat getHomography() {
        return mCurrentFrameHomography;
    }

    public void setProjection(MatOfDouble projection, double aspectRatio, int width, int height) {

        mProjection = projection;
        mCameraAspectRatio = aspectRatio;
        mCameraWidth = width;
        mCameraHeight = height;
    }

    public int[] getDimens() {
        return new int[]{mCircuitX,mCircuitY,mCircuitWidth,mCircuitHeight};
    }

    public double getAspectRatio(){
        return mCameraAspectRatio;
    }
}
