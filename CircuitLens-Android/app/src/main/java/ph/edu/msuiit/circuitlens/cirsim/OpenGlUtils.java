package ph.edu.msuiit.circuitlens.cirsim;

import android.util.Log;

import org.rajawali3d.Object3D;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.Plane;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.util.GLU;

public class OpenGlUtils {
    public static void getWorldPosition(float x, float y, int[] viewPort, Camera camera, Object3D object3D, Vector3 hitPoint) {
        double[] nearPos = new double[4];
        double[] farPos = new double[4];

        Matrix4 viewMatrix = camera.getViewMatrix();
        Matrix4 projectionMatrix = camera.getProjectionMatrix();

        // map screen coordinates to near plane (winZ=0)
        GLU.gluUnProject(x, viewPort[3] - y, 0,
                viewMatrix.getDoubleValues(), 0,
                projectionMatrix.getDoubleValues(), 0,
                viewPort, 0, nearPos, 0);

        // map screen coordinates to far plane (winZ=0)
        GLU.gluUnProject(x, viewPort[3] - y, 1.f,
                viewMatrix.getDoubleValues(), 0,
                projectionMatrix.getDoubleValues(), 0,
                viewPort, 0, farPos, 0);

        // convert 4D to 3D
        Vector3 nearVec = new Vector3(nearPos[0] / nearPos[3], nearPos[1] / nearPos[3], nearPos[2] / nearPos[3]);
        Vector3 farVec = new Vector3(farPos[0] / farPos[3], farPos[1] / farPos[3], farPos[2] / farPos[3]);

        double factor = (object3D.getZ() + nearVec.z) / (camera.getFarPlane() - camera.getNearPlane());

        Vector3 position = Vector3.subtractAndCreate(farVec, nearVec);
        position.multiply(factor);
        position.add(camera.getPosition());

        // get 3 co-planar points
        Vector3 child1 = object3D.getWorldPosition();
        Vector3 child2 = object3D.getChildAt(0).getWorldPosition();
        Vector3 child3 = object3D.getChildAt(1).getWorldPosition();

        Plane plane = new Plane(child1, child2, child3);

        Vector3 rayDir = Vector3.subtractAndCreate(nearVec, farVec);

        double denorm = rayDir.dot(plane.getNormal());
        if (denorm != 0) {
            double t = -(position.dot(plane.getNormal()) + plane.getD()) / denorm;
            if (hitPoint != null) hitPoint.addAndSet(position, Vector3.scaleAndCreate(rayDir, t));
        } else if (plane.getPointSide(position) == Plane.PlaneSide.ONPLANE) {
            if (hitPoint != null) hitPoint.setAll(position);
        }

        // apply rotation of circuitCanvas3D to hitPoint
        hitPoint.rotateBy(object3D.getOrientation());

        // invert y since scaleY is negative
        hitPoint.y = -hitPoint.y;

        Log.d("OpenGlUtils", "hitPoint: "+hitPoint);
    }
}
