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

package org.firstinspires.ftc.sim;

import org.firstinspires.ftc.teamcode.pathmaker.PathDetails;

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


    public static void initializePose(){
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
    }
    public static void readPose(){
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
