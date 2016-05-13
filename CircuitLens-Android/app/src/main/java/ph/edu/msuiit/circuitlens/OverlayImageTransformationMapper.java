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

    public OverlayImageTransformationMapper(){

    }


    public Mat map(Mat currentFrame,boolean isTakePhoto) {

//        Mat small = new Mat();
//        Imgproc.pyrDown(currentFrame,small);

       // mGrayCurrentFrame = currentFrame;
        mTrackingImage = currentFrame;

        //convert current frame image to grayscale
        Imgproc.cvtColor(currentFrame,mGrayCurrentFrame,Imgproc.COLOR_RGB2GRAY);
        //Imgproc.GaussianBlur(mGrayCurrentFrame,mGrayCurrentFrame,blurKernel,5.0);
        Imgproc.blur(mGrayCurrentFrame,mGrayCurrentFrame,blurKernel);
        Imgproc.Canny(mGrayCurrentFrame,mGrayCurrentFrame,150,250);
        Imgproc.dilate(mGrayCurrentFrame,mGrayCurrentFrame,dilateKernel);

        ///tteeesssttttthhasdjbsd
        Imgproc.findContours(mGrayCurrentFrame.clone(),contours,hierarchy,Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_NONE);

        int indexLargest = findLargestContour(contours);

        //MatOfInt hull = new MatOfInt();

        //Imgproc.convexHull(contours.get(indexLargest),hull);

        mCurrentFrameContour = Mat.zeros(currentFrame.size(),currentFrame.type());
        Imgproc.drawContours(mCurrentFrameContour,contours,indexLargest,mLineColor2,5);
        if(contours.size() > 0){
            Imgproc.minEnclosingTriangle(contours.get(indexLargest),triangle);
            contours.clear();
            return drawTriangle(sortTrianglePoints(triangle));

        }


        contours.clear();


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

    private Mat sortTrianglePoints(Mat triangle){

        Log.d("typeOfTri",triangle.toString());



        List<Point> pts = new ArrayList<Point>();
        Mat ptsMat = new Mat();



        double dst[] = new double[3];
        dst[0] = getDistanceFromOrigin(triangle.get(0,0));
        dst[1] = getDistanceFromOrigin(triangle.get(1,0));
        dst[2]= getDistanceFromOrigin(triangle.get(2,0));

        double[] tmp = dst.clone();
        Arrays.sort(tmp);
        Log.d("dst1",dst[0] +"");
        Log.d("dst2",dst[1] +"");
        Log.d("dst3",dst[2] +"");
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                if(tmp[i] == dst[j]){
                    if(triangle.get(j,0) != null){
                        pts.add(new Point(triangle.get(j,0)));
                    }


                    Log.d("add",pts.toString());
                }
            }
        }

//        Log.d("devdst0 ", dst[0] + "" );
//        Log.d("devtmp0 ", tmp[0] + "" );
//        Log.d("devdst1 ", dst[1] + "" );
//        Log.d("devtmp1 ", tmp[1] + "" );
//        Log.d("devdst2 ", dst[2] + "" );
//        Log.d("devtmp2 ", tmp[2] + "" );


        ptsMat = Converters.vector_Point_to_Mat(pts);
        Log.d("ptsMat",ptsMat.dump());

        return ptsMat;


    }

    private double getDistanceFromOrigin(double[] pt) {
        double tmp = 0;
        if(pt != null){
            if(pt.length == 2){
                tmp = Math.sqrt((pt[0] * pt[0]) + (pt[1] * pt[1]));
            }
        }
        else{
            tmp = 0.0;
        }
        return tmp;
    }

    private Mat drawTriangle(Mat triangle) {
        if((triangle.get(0,0) != null) && (triangle.get(1,0) != null) && (triangle.get(2,0) != null)){
            Imgproc.line(mCurrentFrameContour,new Point(triangle.get(0,0)[0],triangle.get(0,0)[1]),new Point(triangle.get(1,0)[0],triangle.get(1,0)[1]),new Scalar(255,0,0));
            Imgproc.line(mCurrentFrameContour,new Point(triangle.get(1,0)[0],triangle.get(1,0)[1]),new Point(triangle.get(2,0)[0],triangle.get(2,0)[1]),new Scalar(0,255,0));
            Imgproc.line(mCurrentFrameContour,new Point(triangle.get(2,0)[0],triangle.get(2,0)[1]),new Point(triangle.get(0,0)[0],triangle.get(0,0)[1]),new Scalar(0,0,255));
        }



        return mCurrentFrameContour;
    }

    private int findLargestContour(List<MatOfPoint> contours) {
        double largestArea = 0;
        int largestIndex = 0;

        for(int i = 0; i < contours.size();i++){
            Mat cnt = contours.get(i);
            double cntArea = Imgproc.contourArea(cnt);
            if(cntArea > largestArea){
                largestArea = cntArea;
                largestIndex = i;
            }
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

    public Mat getCurrentFrameCorners(){
        return  mCurrentFrameCorners;
    }

    public Mat draw(Mat src){

        Imgproc.line(src,new Point(mCurrentFrameCorners.get(0,0)), new Point(mCurrentFrameCorners.get(1,0)), mLineColor, 4);
        Imgproc.line(src,new Point(mCurrentFrameCorners.get(1,0)), new Point(mCurrentFrameCorners.get(2,0)), mLineColor, 4);
        Imgproc.line(src,new Point(mCurrentFrameCorners.get(2,0)), new Point(mCurrentFrameCorners.get(3,0)), mLineColor, 4);
        Imgproc.line(src,new Point(mCurrentFrameCorners.get(3,0)), new Point(mCurrentFrameCorners.get(0,0)), mLineColor, 4);

        return src;
    }

}
