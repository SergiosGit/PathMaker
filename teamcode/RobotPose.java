// RobotPose.java
//
// This method keeps track of where the robot is on the field. It uses the robot centric
// coordinate system (COS) that is defined as Y in forward direction, X in strafe direction, and
// right turns measured in positive degrees.
//
// initializePose:
//      Create COS that is referenced by path parameters defined in PathDetails.
//
// updatePose:
//      apply power calculated in PowerManager towards reaching goals for Y (forward in original COS),
//      X (strafe in original COS) and turn (also with respect to original COS).
//      updatePose maps those powers according to the actual pose of the robot (super-position).
//
// readPose:
//      Reads encoder values and computes the values in inches and degrees. This is only done once
//      per time step.
//
// getHeadingAngle_deg, getForward_in, getStrafe_in:
//      Functions to query the actual pose parameters. These function can be called as much as
//      needed within a time step without actually reading encoder values. They just store the
//      values obtained by the prior readPose() call
//
// MIT License
// Copyright (c) 2023 bayrobotics.org
//
package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hw.DriveTrain;
import org.firstinspires.ftc.teamcode.hw.IMU;
import static org.firstinspires.ftc.teamcode.PathMaker.RobotModel;
import static org.firstinspires.ftc.teamcode.PathMaker.robotModel;
import static org.firstinspires.ftc.teamcode.hw.DriveTrain.getEncoderValues;

public class RobotPose {
    private static double headingAngle_rad = 0, lastHeadingAngle_rad = 0;
    private static double headingAngle_deg = 0, lastHeadingAngle_deg = 0;
    private static double forward_in = 0, lastForward_in = 0, strafe_in = 0, lastStrafe_in = 0;
    private static double pathForward_in = 0, pathStrafe_in = 0;
    private static double imuAngle_rad = 0, lastImuAngle_rad = 0;
    private static double imuAngle_deg = 0, lastImuAngle_deg = 0;

    private static int currentRightPosition = 0;
    private static int currentLeftPosition = 0;
    private static int currentAuxPosition = 0;

    //to keep track of the previous encoder values
    private static int previousRightPosition = 0;
    private static int previousLeftPosition = 0;
    private static int previousAuxPosition = 0;

    final static double L = 32.9438; // distance between left and right encoders in cm - LATERAL DISTANCE
    final static double B = 11.724; // distance between midpoints of left and right encoders and encoder aux
    final static double R = 1.9; // odometry wheel radius in cm
    final static double N = 8192; // REV encoders tic per revolution
    final static double cm_per_tick = (2.0 * Math.PI * R)/N;

    private static Telemetry poseTelemetry;
    private static DriveTrain poseDriveTrain;
    private static IMU imu = new IMU(null);
    public static void initializePose(LinearOpMode opMode, DriveTrain driveTrain, Telemetry telemetry){
        driveTrain.init();
        if (robotModel == RobotModel.ROBOT2) {
            imu.setOpMode(opMode);
            imu.initBNO055();
            imu.calibrate(telemetry);
            imu.resetAngle();
            imu.getAngle_deg();
        }
        poseTelemetry = telemetry;
        poseDriveTrain = driveTrain;
        headingAngle_rad = PathDetails.turnOffset_deg / 180. * Math.PI;
        lastHeadingAngle_rad = 0;
        headingAngle_deg = 0;
        lastHeadingAngle_deg = 0;
        forward_in = 0;
        lastForward_in = 0;
        strafe_in = 0;
        lastStrafe_in = 0;
        imuAngle_rad = 0;
        pathForward_in = PathDetails.forwardOffset_in;
        pathStrafe_in = PathDetails.strafeOffset_in;
        currentAuxPosition = 0;
        currentRightPosition = 0;
        currentLeftPosition = 0;
        previousAuxPosition = 0;
        previousRightPosition = 0;
        previousLeftPosition = 0;
    }
    public static void updatePose(double forwardDrive, double strafeDrive, double rotateDrive){
        double powerFL, powerFR, powerBL, powerBR;
        double radians = 0;
        // get the angle in which the robot is actually headed
        if (robotModel == RobotModel.ROBOT2) {
            radians = getIMUAngle_rad();
        } else {
            radians = getHeadingAngle_rad();
        }
        // project the desired forwardDrive (which is relative to the original
        // forward direction in the coordinate system at the beginning of the
        // path) onto the the actual drive train. This will not change the
        // maximum power seen at any of the Mecanum wheels
        double forwardHeading = forwardDrive * Math.cos(radians) + strafeDrive * Math.sin(radians);
        double strafeHeading = -forwardDrive * Math.sin(radians) + strafeDrive * Math.cos(radians);
        // now add the power components for the drive train
        // forward power is the same on all wheels
        powerFL = forwardHeading; powerFR = forwardHeading;
        powerBL = forwardHeading; powerBR = forwardHeading;
        // add strafe power
        powerFL += strafeHeading; powerFR -= strafeHeading;
        powerBL -= strafeHeading; powerBR += strafeHeading;
        // add turn power
        powerFL += rotateDrive; powerFR -= rotateDrive;
        powerBL += rotateDrive; powerBR -= rotateDrive;
        poseDriveTrain.setMotorPowers(powerFL,powerBL,powerBR,powerFR);
        poseTelemetry.addData("all angles", headingAngle_rad);
        poseTelemetry.update();
    }
    public static void readPose(){
        int[] encoderValues = getEncoderValues();
        if (robotModel == RobotModel.ROBOT1) {
//            encLeft = hardwareMap.dcMotor.get("front_left_drive");
//            encRight = hardwareMap.dcMotor.get("back_right_drive");
//            encAux = hardwareMap.dcMotor.get("back_left_drive");


            currentRightPosition = encoderValues[0];
            currentLeftPosition =  encoderValues[2];
            currentAuxPosition = encoderValues[1];


            int dn1 = currentLeftPosition - previousLeftPosition;
            int dn2 = currentRightPosition - previousRightPosition;
            int dn3 = currentAuxPosition - previousAuxPosition;

            //find out robot movement in cm
            double dtheta = cm_per_tick * (dn2 - dn1)/L;
            double dy = cm_per_tick * (dn1+dn2)/2.0;
            double dx = cm_per_tick * (-dn3 + (dn2-dn1) * B/L);

            double dx_in = dx / 2.54;
            double dy_in = dy / 2.54;

            lastHeadingAngle_rad = headingAngle_rad;
            headingAngle_rad += dtheta;

            lastForward_in = forward_in;
            forward_in += dy_in;

            lastStrafe_in = strafe_in;
            strafe_in += dx_in;

            previousLeftPosition = currentLeftPosition;
            previousRightPosition = currentRightPosition;
            previousAuxPosition = currentAuxPosition;

        } else {
            // Robot 2, motor encoders
            // "4 in" wheels = 3.75" OD -> 11.78" circumference
            // 190 ticks per rotation
            // 16.13 ticks per inch
            double ticksPerInch =  16.13;
            // rotation 3071 ticks per 360 deg
            double ticksPerDegree = 8.53;
            // headingAngle_deg = (encoderValues[0] - encoderValues[2])/ticksPerDegree;
            headingAngle_deg = imu.getAngle_deg();;
            headingAngle_rad = headingAngle_deg / 180.0 * Math.PI;
            lastForward_in = forward_in;
            forward_in = 0.5 * (encoderValues[0] + encoderValues[2]) / ticksPerInch;
            // no encoder, simulate instead
            //lastStrafe_in = strafe_in;
            lastStrafe_in = 0; // for simulation we only get the delta back
            strafe_in = RobotPoseSimulation.updateStrafe_in(PathManager.strafePower,PathManager.timeStep_ms);
        }
        // book keeping for forward and strafe direction movement in the
        // robot coordinate system (COS at start)
        double deltaPathForward_in = forward_in - lastForward_in;
        double deltaPathStrafe_in = strafe_in - lastStrafe_in;
        double sin = Math.sin(headingAngle_rad);
        double cos = Math.cos(headingAngle_rad);
        pathForward_in += deltaPathForward_in * cos - deltaPathStrafe_in * sin;
        pathStrafe_in += deltaPathStrafe_in * cos + deltaPathForward_in * sin;
    }
    public static double getHeadingAngle_rad(){
        // call readPose first (but only once for all encoders, imu)
        return headingAngle_rad;
    }
    public static double getHeadingAngle_deg(){
        return getHeadingAngle_rad() / Math.PI * 180;
    }
    public static double getIMUAngle_rad() {
        return imuAngle_rad;
    }
    public static double getForward_in(){
        // call readPose first (but only once for all encoders, imu)
        // get actual forward position of the robot in the coordinate system
        // defined at the beginning of the path.
        return pathForward_in;
    }
    public static double getStrafe_in(){
        // call readPose first (but only once for all encoders, imu)
        // get actual strafe (lateral) position of the robot in the
        // coordinate system defined at the beginning of the path
        return pathStrafe_in;
    }
}
