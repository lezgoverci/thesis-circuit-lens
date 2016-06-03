package ph.edu.msuiit.circuitlens.circuit;

import org.rajawali3d.Object3D;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.util.GLU;

public class OpenGLUtils {
    public static void getWorldPosition(float x, float y, int[] viewPort, Camera camera, Object3D object3D, Vector3 position) {
        double[] nearPos = new double[4];
        double[] farPos = new double[4];

        Matrix4 viewMatrix = camera.getViewMatrix();
        Matrix4 projectionMatrix = camera.getProjectionMatrix();

        GLU.gluUnProject(x, viewPort[3] - y, 0,
                viewMatrix.getDoubleValues(), 0,
                projectionMatrix.getDoubleValues(), 0,
                viewPort, 0, nearPos, 0);
        GLU.gluUnProject(x, viewPort[3] - y, 1.f,
                viewMatrix.getDoubleValues(), 0,
                projectionMatrix.getDoubleValues(), 0,
                viewPort, 0, farPos, 0);

        // convert 4D to 3D
        Vector3 nearVec = new Vector3(nearPos[0] / nearPos[3], nearPos[1] / nearPos[3], nearPos[2] / nearPos[3]);
        Vector3 farVec = new Vector3(farPos[0] / farPos[3], farPos[1] / farPos[3], farPos[2] / farPos[3]);

        double factor = (Math.abs(object3D.getZ()) + nearVec.z) / (camera.getFarPlane() - camera.getNearPlane());
        position.setAll(farVec);
        position.subtract(nearVec);
        position.multiply(factor);
        position.add(camera.getPosition());

        /*
        Plane plane = new Plane();
        plane.
        Vector3 rayDir = Vector3.subtractAndCreate(nearVec, farVec);
        double denorm = rayDir.dot(plane.getNormal());
        if (denorm != 0) {
            double t = -(rayStart.dot(plane.getNormal()) + plane.getD()) / denorm;
            if (t < 0) return false;
            if (hitPoint != null) hitPoint.addAndSet(rayStart, Vector3.scaleAndCreate(rayDir, t));
                return true;
            } else if (plane.getPointSide(rayStart) == Plane.PlaneSide.ONPLANE) {
            if (hitPoint != null) hitPoint.setAll(rayStart);
                return true;
            } else {
                return false;
           }
        */
    }
}
