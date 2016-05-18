package ph.edu.msuiit.circuitlens;

import android.util.Log;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vercillius on 4/30/2016.
 */
public class OverlayImageTransformationMapper {

    // The tracking image.
    // Also the circuit diagram image from camera frame
    private Mat mTrackingImage = new Mat();

    private boolean isSetTracking = false;

    private Size blurKernel = new Size(5,5);

    // Grayscale version of the tracking image
    private Mat mGrayTrackingImage = new Mat();

    // The good detected corner coordinates, in pixels, as integers
    private final MatOfPoint mIntCurrentFrameCorners = new MatOfPoint();

    // A grayscale version of the current frame
    private Mat mGrayCurrentFrame = new Mat();

    // contours hierarchy
    Mat hierarchy = new Mat();

    // a list of the current frame's contours
    private List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

    private final Scalar mLineColor = new Scalar(255);
    private final Scalar mLineColor2 = new Scalar(0.0,0.0,255.0);

    private Mat dilateKernel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,new Size(5.0,5.0));

    private Mat mCurrentFrameContour = new Mat();

    private MatOfPoint mHullMatOfPoint = new MatOfPoint();
    private MatOfPoint mLargestContour = new MatOfPoint();
    private MatOfPoint2f mTrackingImageApproxHullMatOfPoint2f = new MatOfPoint2f();
    private MatOfPoint2f mCurrentFrameApproxHullMatOfPoint2f = new MatOfPoint2f();
    Mat mBackup = new Mat();


    public OverlayImageTransformationMapper(){

    }


    public Mat map(Mat currentFrame,boolean isTakePhoto) {

        // Enhance features
        Imgproc.cvtColor(currentFrame,mGrayCurrentFrame,Imgproc.COLOR_RGB2GRAY);
        Imgproc.blur(mGrayCurrentFrame,mGrayCurrentFrame,blurKernel);
        Imgproc.Canny(mGrayCurrentFrame,mGrayCurrentFrame,150,250);
        Imgproc.dilate(mGrayCurrentFrame,mGrayCurrentFrame,dilateKernel);

        // Find Contours
        Imgproc.findContours(mGrayCurrentFrame.clone(),contours,hierarchy,Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);

        // Find the largest contour
        int indexLargest = findLargestContour(contours);

        // find convex hull of largest contour
        MatOfInt hull = new MatOfInt();
        Imgproc.convexHull(mLargestContour,hull,true);

        // Convert hull to MatOfPoint from MatOfInt
       // mHullMatOfPoint = convertHullToMatOfPoint(sortHullPoints(hull));
        mHullMatOfPoint = convertHullToMatOfPoint(hull);

        // Approximate convex hull polygon
        mCurrentFrameApproxHullMatOfPoint2f = approxHull(mHullMatOfPoint);

        // Draw contour and convex hull
        mCurrentFrameContour = Mat.zeros(currentFrame.size(),currentFrame.type());

        // Temporary list for drawing convex hull points.
        // drawContours requires List<MatOfPoint> as input parameter
        List<MatOfPoint> hullList = new ArrayList<>();
        hullList.add(mHullMatOfPoint);

        // Draw contour
        Imgproc.drawContours(currentFrame,contours,indexLargest,mLineColor2,5);

        // Draw convex hull
        if(hullList.size() > 0){
            Imgproc.drawContours(currentFrame,hullList,-1,mLineColor,5);
            // draw labels
            drawLabels(currentFrame,mCurrentFrameApproxHullMatOfPoint2f);
        }

        contours.clear();
        hullList.clear();


        if(isTakePhoto){
            //setTrackingImage(mCurrentFrameContour);
            mTrackingImageApproxHullMatOfPoint2f = mCurrentFrameApproxHullMatOfPoint2f;
            isSetTracking = true;
        }

        if(isSetTracking ){
            //find homography matrix
            Mat homography = findHomographyTransformation();
            //Mat box = currentFrame.submat(new Rect(0,0,currentFrame.width() / 2,currentFrame.height() /2));
            //Mat box = mCurrentFrameContour.clone();
            Mat box = currentFrame;
            drawImage(box,homography);
        }

        return  mCurrentFrameContour;

    }

    private void drawImage(Mat img, Mat homography) {
//        Point topLeft = new Point(0,0);
//        Point topRight = new Point(0,img.width() - 1);
//        Point bottomLeft = new Point(img.height() - 1, 0);
//        Point bottomRight = new Point(img.height() - 1, img.width() - 1);
       Imgproc.warpPerspective(img,mCurrentFrameContour,homography,new Size(mCurrentFrameContour.width(),mCurrentFrameContour.height()));
        //Core.perspectiveTransform(img,img,homography);
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

        Log.d("approxHull",res.dump() + "size: " + res.toArray().length + "");

        return res;
    }

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

        List<Point> largestContourPointsList = mLargestContour.toList();

        for(int i = 0; i < hullIntList.length; i++){
            hullPointsList.add(largestContourPointsList.get(hullIntList[i]));
        }
        Log.d("hullInt",hull.dump());

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
            mLargestContour = contours.get(largestIndex);
        }
        return largestIndex;
    }


    public void setTrackingImage(Mat trackingImg) {
        mGrayTrackingImage = trackingImg;

        //convert tracking image to grayscale
        //Imgproc.cvtColor(trackingImg,mGrayTrackingImage,Imgproc.COLOR_RGB2GRAY);
        //Imgproc.Canny(mGrayTrackingImage,mGrayTrackingImage,100,255);

        Log.d("mapper","Done Set tracking image");
    }


    private Mat findHomographyTransformation() {
        // Else there are enough good points to find homography



        // Convert the matched points to MatOfPoint2f format, as
        // required by the Calib3d.findHomography function
        List<Point> trackingImageHullPoints = new ArrayList<>();
        trackingImageHullPoints = mTrackingImageApproxHullMatOfPoint2f.toList();

        List<Point> currentFrameHullPoints = new ArrayList<>();
        currentFrameHullPoints = mCurrentFrameApproxHullMatOfPoint2f.toList();

        // find the homography
       if(trackingImageHullPoints.size() == currentFrameHullPoints.size()){
           mBackup = Calib3d.findHomography(mTrackingImageApproxHullMatOfPoint2f,mCurrentFrameApproxHullMatOfPoint2f,Calib3d.RANSAC,1.0);
       }

        Log.d("homo",currentFrameHullPoints.size() +"");
        return mBackup;


    }
}
