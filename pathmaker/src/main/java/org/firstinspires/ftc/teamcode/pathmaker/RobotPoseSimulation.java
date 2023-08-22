// RobotPoseSimulation.java
//
// This class generates a visual simulation of the robot position on the playing field using
// the path definitions in PathDetails. The visualization can be rendered with FTC Dashboard.
//
// MIT License
// Copyright (c) 2023 bayrobotics.org
//
package org.firstinspires.ftc.teamcode.pathmaker;

public class RobotPoseSimulation {

    public static double angle = 0, forward = 0, strafe = 0;
    public static double fieldX, fieldY, fieldAngle;
    static double SIDE_WIDTH = 12;
    static double SIDE_LENGTH = 16;
    static double l = SIDE_LENGTH / 2;
    static double w = SIDE_WIDTH / 2;
    public static double [] bxPoints  = { l, -l, -l, l };
    public static double [] byPoints = { w, w, -w, -w };
    public static int counter;
    public static double forwardPowerToInchRate = 0.035;
    public static double strafePowerToInchRate = 0.035;
    public static double turnPowerToDegRate = 0.4;


    public static void initializePose(double initForward, double initStrafe, double initAngle){
        angle = initAngle;
        forward = initForward;
        strafe = initStrafe;
        counter = 0;
    }
    static void updatePose(double forwardPower, double strafePower, double turnPower, double timeStep_ms) {
        forward += forwardPower * forwardPowerToInchRate * timeStep_ms;
        strafe += strafePower * strafePowerToInchRate * timeStep_ms;
        angle += turnPower * turnPowerToDegRate * timeStep_ms;
        counter++;
        setFieldCoordinates(forward, strafe, angle);
    }
    static public double updateStrafe_in(double strafePower, double timeStep_ms){
        // used in RobotPose to simulate missing encoder
        return strafePower * strafePowerToInchRate * timeStep_ms;
    }
    public static void setFieldCoordinates(double forward, double strafe, double angle){
        int xMultiplierTeam, yMultiplierTeam;
        int yMultiplierTerminal;
        int yoffset = 100; // 36;
        int xoffset = 128;// 72;

        if (GameSetup.thisTeamColor == GameSetup.TeamColor.RED) {
            xMultiplierTeam = 1;
            yMultiplierTeam = 1;
        } else {
            xMultiplierTeam = -1;
            yMultiplierTeam = -1;
        }
        if (GameSetup.thisTerminal == GameSetup.Terminal.RED){
            yMultiplierTerminal = -1;
        } else {
            yMultiplierTerminal = 1;
        }
        fieldX = (forward - xoffset) * xMultiplierTeam;
        fieldY = (strafe + yMultiplierTerminal*yoffset) * yMultiplierTeam;
        fieldAngle = angle;
    }
    private static void rotatePoints(double[] xPoints, double[] yPoints, double angle) {
        for (int i = 0; i < xPoints.length; i++) {
            double x = xPoints[i];
            double y = yPoints[i];
            double radians = angle / 180 * Math.PI;
            xPoints[i] = x * Math.cos(radians) - y * Math.sin(radians);
            yPoints[i] = x * Math.sin(radians) + y * Math.cos(radians);
        }
    }
    public static void createPoints(){
        int [] xmul = {1,-1,-1,1};
        int [] ymul = {1,1,-1,-1};
        double xCenter = fieldX;
        double yCenter = fieldY;
        double angle = fieldAngle;
        for (int i = 0; i < 4; i++) {
            bxPoints[i] = xmul[i]*w;
            byPoints[i] = ymul[i]*l;
        }
        rotatePoints(bxPoints,byPoints,angle);
        for (int i = 0; i < 4; i++) {
            bxPoints[i] += xCenter;
            byPoints[i] += yCenter;
        }
    }
}
