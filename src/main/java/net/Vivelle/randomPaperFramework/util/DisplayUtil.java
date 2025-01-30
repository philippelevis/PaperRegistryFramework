package net.Vivelle.randomPaperFramework.util;

import org.bukkit.util.Transformation;
import org.joml.*;

import java.lang.Math;

public class DisplayUtil {
    public static Transformation CenteredTransform(float Xsize, float Ysize, float Zsize){
        return new Transformation(
                new Vector3f(-Xsize/2,-Ysize/2,-Zsize/2),
                new AxisAngle4f(0f,0f,0f,1f),
                new Vector3f(Xsize,Ysize,Zsize),
                new AxisAngle4f(0f,0f,0f,1f));
    }

    public static AxisAngle4f VectorToAxisAngle(Vector3f direction, float roll) {
        // Define the reference direction (e.g., positive Z-axis)
        Vector3f reference = new Vector3f(0, 0, 1);

        // Normalize the input direction vector
        Vector3f normalizedDirection = new Vector3f(direction).normalize();

        // Calculate the angle using the dot product
        double dotProduct = reference.dot(normalizedDirection);
        double angle = Math.acos(dotProduct); // Angle in radians

        // If the angle is 0, no rotation is needed
        if (angle == 0) {
            return new AxisAngle4f(0, 0, 0, 0); // No rotation
        }

        // Calculate the rotation axis using the cross product
        Vector3f rotationAxis = new Vector3f();
        rotationAxis.cross(reference, normalizedDirection).normalize();
        AxisAngle4f res = new AxisAngle4f((float) rotationAxis.x, (float) rotationAxis.y, (float) rotationAxis.z, (float) angle);
        // Create the AxisAngle4f representation
        return res;
    }

    public static Vector2f rotateVector(Vector2f vector, float angle) {
        // Convert angle from degrees to radians
        float angleRadians = angle;

        // Calculate the sine and cosine of the angle
        float cosTheta = (float) Math.cos(angleRadians);
        float sinTheta = (float) Math.sin(angleRadians);

        // Apply the rotation matrix
        float rotatedX = cosTheta * vector.x - sinTheta * vector.y;
        float rotatedY = sinTheta * vector.x + cosTheta * vector.y;

        // Return the new rotated vector
        return new Vector2f(rotatedX, rotatedY).sub(vector);
    }

    public static AxisAngle4f eulerToAxisAngle(double roll, double pitch, double yaw) {
        // Convert degrees to radians if necessary
        double rollRad = Math.toRadians(roll);
        double pitchRad = Math.toRadians(pitch);
        double yawRad = Math.toRadians(yaw);

        // Create a rotation matrix from the Euler angles
        Matrix3d rotationMatrix = new Matrix3d();
        rotationMatrix.rotationYXZ((float) yawRad, (float) pitchRad, (float) rollRad);

        // Extract the rotation axis and angle from the rotation matrix
        AxisAngle4f axisAngle = new AxisAngle4f();
        rotationMatrix.getRotation(axisAngle);

        return axisAngle;
    }
    public static AxisAngle4f VectorToAxisAngleWithRoll(Vector3f direction, float roll) {
        // Calculate the length of the directional vector
        double length = direction.length();

        // If the length is zero, return a default axis-angle (no rotation)
        if (length == 0) {
            return new AxisAngle4f(0, 0, 0, 0); // No rotation
        }

        // Normalize the directional vector to get the axis
        Vector3f normalizedDirection = new Vector3f(direction).normalize();

        // Calculate the angle using the dot product
        // Here, we assume the reference direction is the positive Z-axis
        Vector3f reference = new Vector3f(0, 0, 1);
        double dotProduct = reference.dot(normalizedDirection);
        double angle = Math.acos(dotProduct); // Angle in radians

        // Calculate the rotation axis using the cross product
        Vector3f rotationAxis = new Vector3f();
        rotationAxis.cross(reference, normalizedDirection).normalize();

        // Create the initial rotation from the directional vector
        AxisAngle4f directionRotation = new AxisAngle4f((float) rotationAxis.x, (float) rotationAxis.y, (float) rotationAxis.z, (float) angle);

        // Create the roll rotation around the forward direction (Z-axis)
        AxisAngle4f rollRotation = new AxisAngle4f(0, 0, 1, roll); // Roll around the Z-axis

        // Combine the rotations
        Matrix3f directionRotationMatrix = new Matrix3f();
        directionRotationMatrix.rotation(directionRotation);

        Matrix3f rollRotationMatrix = new Matrix3f();
        rollRotationMatrix.rotation(rollRotation);

        // Combine the rotations
        Matrix3f combinedRotationMatrix = new Matrix3f();
        combinedRotationMatrix.mul(rollRotationMatrix, directionRotationMatrix);

        // Convert the combined rotation matrix back to AxisAngle4f
        AxisAngle4f combinedAxisAngle = new AxisAngle4f();
        combinedRotationMatrix.getRotation(combinedAxisAngle);

        return combinedAxisAngle;
    }
    public static float[] VectorToEuler(Vector3f direction) {
        // Normalize the directional vector
        double length = direction.length();
        if (length == 0) {
            return new float[]{0, 0};
        }
        Vector3d normalizedDirection = new Vector3d(direction).normalize();

        // Calculate yaw (rotation around the Y-axis)
        double yaw = Math.atan2(normalizedDirection.x, normalizedDirection.z); // Yaw in radians

        // Calculate pitch (rotation around the X-axis)
        double pitch = Math.asin(normalizedDirection.y); // Pitch in radians

        // Roll can be set to zero or derived from context
        float roll = 0.0f; // Assuming no roll for this conversion

        // Convert radians to degrees
        float yawDegrees = (float) Math.toDegrees(yaw);
        float pitchDegrees = (float) Math.toDegrees(pitch);

        return new float[]{yawDegrees, pitchDegrees};
    }

    public static Vector3f perpendicularXZ(Vector3f vector) {
        // Create a new vector that is perpendicular in the x-z plane
        return new Vector3f(vector.z, 0, -vector.x).normalize();
    }

}
