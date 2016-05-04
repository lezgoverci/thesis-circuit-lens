package ph.edu.msuiit.circuitlens;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
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
    private Mat mTrackingImage;

    public Mat getmGrayTrackingImage() {
        return mGrayTrackingImage;
    }

    // Grayscale version of the tracking image
    private Mat mGrayTrackingImage;

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
    private final Mat mGrayCurrentFrame = new Mat();

    // Tentative matches of current frame features and tracking image features
    private final MatOfDMatch mMatches = new MatOfDMatch();

    // A feature detector, which finds features in images
    private final FeatureDetector mFeatureDetector = FeatureDetector.create(FeatureDetector.ORB);

    // A descriptor extractor, which creates descriptors of features
    private final DescriptorExtractor mDescriptorExtrator = DescriptorExtractor.create(DescriptorExtractor.ORB);

    // A descriptor matcher, which matches features based on their descriptors
    private final DescriptorMatcher mDescriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMINGLUT);

    private final Scalar mLineColor = new Scalar(0,255,0);


    public void map(Mat currentFrame) {


        //convert current frame image to grayscale
        Imgproc.cvtColor(currentFrame,mGrayCurrentFrame,Imgproc.COLOR_RGB2GRAY);


        //find features of the current frame
        mFeatureDetector.detect(currentFrame,mCurrentFrameKeypoints);

        //find descriptors of the current frame
        mDescriptorExtrator.compute(currentFrame,mCurrentFrameKeypoints,mCurrentFrameDescriptors);

        //match tracking image descriptors with current frame descriptors
        mDescriptorMatcher.match(mCurrentFrameDescriptors,mTrackingImageDescriptors,mMatches);

        //Attempt to find the tracking image's corners in the current frame
        findCurrentFrameCorners();
    }


    public void setTrackingImage(Mat trackingImg) {
        mTrackingImage = trackingImg;

        //convert tracking image to grayscale
        //TODO check the input color format if correct
        Imgproc.cvtColor(mTrackingImage,mGrayTrackingImage,Imgproc.COLOR_RGB2GRAY);

        //find features of the tracking image
        mFeatureDetector.detect(mGrayTrackingImage,mTrackingImageKeypoints);

        //find descriptors of the tracking image
        mDescriptorExtrator.compute(mGrayTrackingImage,mTrackingImageKeypoints,mTrackingImageDescriptors);


        //Store the tracking image's corner coordinates, in pixels
        mTrackingImageCorners.put(0,0,new double[]{0.0,0.0});
        mTrackingImageCorners.put(1,0,new double[]{mGrayTrackingImage.cols(),0.0});
        mTrackingImageCorners.put(2,0,new double[]{mGrayTrackingImage.cols(),mGrayTrackingImage.rows()});
        mTrackingImageCorners.put(3,0,new double[]{0.0,mGrayTrackingImage.rows()});
    }

    private void findCurrentFrameCorners() {
        List<DMatch> matchesList = mMatches.toList();

        if(matchesList.size() < 4){
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

        if(minDist > 50.0){
            // The target is completely lost
            // Discard any previously found corners
            mCurrentFrameCorners.create(0,0,mCurrentFrameCorners.type());
            return;
        } else if(minDist > 25.0){
            // The target is lost but maybe it is still close
            // keep any previously found corners
            return;
        }

        // Identify good keypoints based on match distance
        ArrayList<Point> goodTrackingImagePointsList = new ArrayList<Point>();
        ArrayList<Point> goodCurrentFramePointsList = new ArrayList<Point>();

        double maxGoodMatchDist = 1.75 * minDist;
        for(DMatch match : matchesList){
            if(match.distance < maxGoodMatchDist){
                goodTrackingImagePointsList.add(trackingImageKeypointsList.get(match.trainIdx).pt);
                goodCurrentFramePointsList.add(currentFrameKeypointsList.get(match.queryIdx).pt);

            }
        }
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
        goodCurrentFramePoints.fromList(goodTrackingImagePointsList);

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

    public void draw(Mat src, Mat dst){
        if(dst != src){
            src.copyTo(dst);
        }
        Imgproc.line(dst,new Point(mCurrentFrameCorners.get(0,0)), new Point(mCurrentFrameCorners.get(1,0)), mLineColor, 4);
        Imgproc.line(dst,new Point(mCurrentFrameCorners.get(1,0)), new Point(mCurrentFrameCorners.get(2,0)), mLineColor, 4);
        Imgproc.line(dst,new Point(mCurrentFrameCorners.get(2,0)), new Point(mCurrentFrameCorners.get(3,0)), mLineColor, 4);
        Imgproc.line(dst,new Point(mCurrentFrameCorners.get(3,0)), new Point(mCurrentFrameCorners.get(0,0)), mLineColor, 4);

    }

}
