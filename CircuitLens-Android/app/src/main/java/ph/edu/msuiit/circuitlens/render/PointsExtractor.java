package ph.edu.msuiit.circuitlens.render;

import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vercillius on 6/1/2016.
 */
public class PointsExtractor {

    private static Mat mImageGray;

    // Contours
    private static List<MatOfPoint> mContoursList = new ArrayList<>();
    private static MatOfPoint mLargestContour = new MatOfPoint();
    private static Mat mContoursHierarchy = new Mat();

    // Convex Hull
    private static MatOfInt mConvexHullIndices = new MatOfInt();
    private static MatOfPoint mConvexHullPoints = new MatOfPoint();
    private static Mat mApproxHullCorners;
    private static MatOfPoint2f mApproxHullPoints2D;

    public static void setImage(Mat img){
        mImageGray = new Mat(); //TODO is this necessary?
        mImageGray = img;
        mApproxHullPoints2D = new MatOfPoint2f();
        mApproxHullCorners = new MatOfPoint2f();
    }


    public static void findPoints2D(){
        // Find all contours
        findContours();
        // Find the largest contour
        findLargestContour();
        // Find the convex hull of the largest contour
        findConvexHull();
        // Approximate convex hull to 2D points
        approximateConvexHull();

    }

    private static void findContours() {
        // Output is saved in mContoursList
        Imgproc.findContours(mImageGray.clone(),mContoursList,mContoursHierarchy,Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
    }

    private static void findLargestContour() {
        double largestArea = 0.0;
        int indexLargestContour = -1;

        if(mContoursList.size() > 0){
            for(int i=0; i< mContoursList.size(); i++){
                Mat cnt = mContoursList.get(i);
                double cntArea = Imgproc.contourArea(cnt);
                if(cntArea > largestArea){
                    largestArea = cntArea;
                    indexLargestContour = i;
                }
            }

            mLargestContour = mContoursList.get(indexLargestContour);
        }

        //TODO clear contours list?
    }

    private static void findConvexHull() {

        if(mLargestContour.toList().size() > 3){
            Imgproc.convexHull(mLargestContour,mConvexHullIndices, true);
        }
    }

    private static void approximateConvexHull() {
        mConvexHullPoints = convertHullToMatOfPoint(mConvexHullIndices);
        mApproxHullPoints2D = approxHull(mConvexHullPoints);
    }

    // Helper function of approximateConvexHull()
    private static MatOfPoint convertHullToMatOfPoint(MatOfInt mConvexHullIndices) {
        List<Point> hullPointsList = new ArrayList<>();

        int[] hullIntList = mConvexHullIndices.toArray();
        MatOfPoint result = new MatOfPoint();

        List<Point> largestContourPointsList = mLargestContour.toList();

        for(int i=0; i < hullIntList.length; i++){
            hullPointsList.add(largestContourPointsList.get(hullIntList[i]));
        }

        result.fromList(hullPointsList);
        hullPointsList.clear();

        return result;
    }

    // Helper function of approximateConvexHull()
    private static MatOfPoint2f approxHull(MatOfPoint mConvexHullPoints) {
        MatOfPoint2f curve = new MatOfPoint2f(mConvexHullPoints.toArray());
        MatOfPoint2f result = new MatOfPoint2f();

        double arcLength = Imgproc.arcLength(curve,true);
        double epsilon = 0.01 * arcLength;

        Imgproc.approxPolyDP(curve,result,epsilon,true);

        return result;
    }

    // useful for finding the homography in mapper
    public static MatOfPoint2f getPoints2D(){
        findPoints2D();
        return mApproxHullPoints2D;
    }

}
