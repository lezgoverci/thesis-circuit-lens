package ph.edu.msuiit.circuitlens;

import android.util.Log;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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

    // Features of the tracking image
    private final MatOfKeyPoint mTrackingImageKeypoints = new MatOfKeyPoint();

    // Descriptors of the tracking image's features
    private final Mat mTrackingImageDescriptors = new Mat();

    // The corner coordinates of the tracking image, in pixels
    private final Mat mTrackingImageCorners = new Mat(4,1, CvType.CV_32FC2);

    // Features of the current frame
    private final MatOfKeyPoint mCurrentFrameKeypoints = new MatOfKeyPoint();

    // Descriptors of the current frame's features
    private final Mat mCurrentFrameDescriptors = new Mat();

    // Tentative corner coordinates detected in the current frame, in pixels
    private final Mat mCandidateCurrentFrameCorners = new Mat(4,1,CvType.CV_32FC2);

    // Good corner coordinates detected in the current frame, in pixels
    private final Mat mCurrentFrameCorners = new Mat(4,1,CvType.CV_32FC2);

    // The good detected corner coordinates, in pixels, as integers
    private final MatOfPoint mIntCurrentFrameCorners = new MatOfPoint();

    // A grayscale version of the current frame
    private Mat mGrayCurrentFrame = new Mat();

    // contours hierarchy
    Mat hierarchy = new Mat();

    // a list of the current frame's contours
    private List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

    // Tentative matches of current frame features and tracking image features
    private final MatOfDMatch mMatches = new MatOfDMatch();

    // A feature detector, which finds features in images
    private final FeatureDetector mFeatureDetector = FeatureDetector.create(FeatureDetector.ORB);

    // A descriptor extractor, which creates descriptors of features
    private final DescriptorExtractor mDescriptorExtrator = DescriptorExtractor.create(DescriptorExtractor.ORB);

    // A descriptor matcher, which matches features based on their descriptors
    private final DescriptorMatcher mDescriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMINGLUT);

    private final Scalar mLineColor = new Scalar(255);
    private final Scalar mLineColor2 = new Scalar(0.0,0.0,255.0);

    private Mat dilateKernel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,new Size(5.0,5.0));

    private Mat mCurrentFrameContour = new Mat();
    private Mat triangle = new Mat();

    private Mat mTrianglePoints = new Mat(3,1, CvType.CV_32FC2);
    private MatOfPoint mhullMatOfPoint = new MatOfPoint();
    private MatOfPoint mLargestContour = new MatOfPoint();

    public OverlayImageTransformationMapper(){

    }


    public Mat map(Mat currentFrame,boolean isTakePhoto) {

        mTrackingImage = currentFrame;

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
       // mhullMatOfPoint = convertHullToMatOfPoint(sortHullPoints(hull));
        mhullMatOfPoint = convertHullToMatOfPoint(hull);

        // Approximate convex hull polygon
        MatOfPoint2f approxHull = approxHull(mhullMatOfPoint);

        // Draw contour and convex hull
        mCurrentFrameContour = Mat.zeros(currentFrame.size(),currentFrame.type());
        List<MatOfPoint> hullList = new ArrayList<>();
        hullList.add(mhullMatOfPoint);
        Imgproc.drawContours(mCurrentFrameContour,contours,indexLargest,mLineColor2,5);
        if(hullList.size() > 0){
            Imgproc.drawContours(mCurrentFrameContour,hullList,-1,mLineColor,5);
            // draw labels
            drawLabels(approxHull);

        }


//        if(contours.size() > 0){
//            Imgproc.minEnclosingTriangle(contours.get(indexLargest),triangle);
//            contours.clear();
//            return drawTriangle(sortTrianglePoints(triangle));
//
//        }


        contours.clear();
        hullList.clear();


        if(isTakePhoto){
            setTrackingImage(mCurrentFrameContour);
            isSetTracking = true;
        }

        //Features2d.drawKeypoints(mGrayCurrentFrame,mTrackingImageKeypoints,mGrayCurrentFrame);


        //find features of the current frame
       // mFeatureDetector.detect(mCurrentFrameContour,mCurrentFrameKeypoints);

        //find descriptors of the current frame
       // mDescriptorExtrator.compute(mCurrentFrameContour,mCurrentFrameKeypoints,mCurrentFrameDescriptors);

        if(isSetTracking ){
            //match tracking image descriptors with current frame descriptors
            mDescriptorMatcher.match(mCurrentFrameDescriptors,mTrackingImageDescriptors,mMatches);
            //Attempt to find the tracking image's corners in the current frame
            findCurrentFrameCorners();
        }


        // see debug frame
       // draw(currentFrame,mGrayCurrentFrame);
        // see production frame

        return  mCurrentFrameContour;

    }

    private void drawLabels(MatOfPoint2f approxHull) {
        List<Point> list = approxHull.toList();

        for(int i =0; i < list.size(); i++){
            Imgproc.putText(mCurrentFrameContour,i+1 + "",list.get(i),1,10.,new Scalar(0,255,0));
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


        //find features of the tracking image
        mFeatureDetector.detect(mGrayTrackingImage,mTrackingImageKeypoints);


        //find descriptors of the tracking image
       mDescriptorExtrator.compute(mGrayTrackingImage,mTrackingImageKeypoints,mTrackingImageDescriptors);


        //Store the tracking image's corner coordinates, in pixels
        mTrackingImageCorners.put(0,0,new double[]{0.0,0.0});
        mTrackingImageCorners.put(1,0,new double[]{mGrayTrackingImage.cols(),0.0});
        mTrackingImageCorners.put(2,0,new double[]{mGrayTrackingImage.cols(),mGrayTrackingImage.rows()});
        mTrackingImageCorners.put(3,0,new double[]{0.0,mGrayTrackingImage.rows()});
        Log.d("mapper","Done Set tracking image");
    }


    private void findCurrentFrameCorners() {
        List<DMatch> matchesList = mMatches.toList();

        if(matchesList.size() < 8){
            //There are too few matches to find the homography
            return;
        }

        List<KeyPoint> trackingImageKeypointsList = mTrackingImageKeypoints.toList();
        List<KeyPoint> currentFrameKeypointsList = mCurrentFrameKeypoints.toList();

        //Calculate the max and min distance between keypoints
        double maxDist = 0.0;
        double minDist = Double.MAX_VALUE;
        for(DMatch match : matchesList){
            double dist = match.distance;
            if(dist < minDist){
                minDist = dist;
            }
            if(dist > maxDist){
                maxDist = dist;
            }
        }
        Log.d("MinDist: ",minDist + "");
        if(minDist > 20.0){
            // The target is completely lost
            // Discard any previously found corners
            mCurrentFrameCorners.create(0,0,mCurrentFrameCorners.type());
            return;
        } else if(minDist > 10.0){
            // The target is lost but maybe it is still close
            // keep any previously found corners
            return;
        }

        // Identify good keypoints based on match distancezzz
        ArrayList<Point> goodTrackingImagePointsList = new ArrayList<Point>();
        ArrayList<Point> goodCurrentFramePointsList = new ArrayList<Point>();

        double maxGoodMatchDist = 1.75 * minDist;
        for(DMatch match : matchesList){
            if(match.distance < maxGoodMatchDist){
                goodTrackingImagePointsList.add(trackingImageKeypointsList.get(match.trainIdx).pt);
                goodCurrentFramePointsList.add(currentFrameKeypointsList.get(match.queryIdx).pt);

            }
        }
        Log.d("goodTrack: ", goodTrackingImagePointsList.size() + "");
        Log.d("goodCurr: ", goodCurrentFramePointsList.size() + "");
        if(goodTrackingImagePointsList.size() < 4 || goodCurrentFramePointsList.size() < 4){
            // There are too few good points to find homography
            return;
        }

        // Else there are enough good points to find homography

        // Convert the matched points to MatOfPoint2f format, as
        // required by the Calib3d.findHomography function
        MatOfPoint2f goodTrackingImagePoints = new MatOfPoint2f();
        goodTrackingImagePoints.fromList(goodTrackingImagePointsList);

        MatOfPoint2f goodCurrentFramePoints = new MatOfPoint2f();
        goodCurrentFramePoints.fromList(goodCurrentFramePointsList);

        // find the homography
        Mat homography = Calib3d.findHomography(goodTrackingImagePoints,goodCurrentFramePoints);

        // Use the homography to project the tracking image corner
        // coordinates into the current frame coordinates
        Core.perspectiveTransform(mTrackingImageCorners,mCandidateCurrentFrameCorners,homography);

        // Convert the current frame corners to integer format, as required
        //  by the Imgproc.isContourConvex function
        mCandidateCurrentFrameCorners.convertTo(mIntCurrentFrameCorners,CvType.CV_32S);

        // Check whether the corners form a convex polygon
        if(Imgproc.isContourConvex(mIntCurrentFrameCorners)){
            // the corners form a convex polygon, so record them
            // as valid scene corners
            mCandidateCurrentFrameCorners.copyTo(mCurrentFrameCorners);
        }


    }



}
