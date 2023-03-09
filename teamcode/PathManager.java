//
// PathManager.java
//
// This method calculates the motor powers to drive the robot along the path. The use of
// battery power is maximized for each DOF. For example, if the path only consists of
// forward motion, the absolute power applied will be 1. However, if forward, strafe, and turn
// are all applied, the absolute power for each DOF will be 1/3.
// Power transitions are ramped with a maximum change of maxPowerStep each timeStep_ms.
// When the robot is within "reach" of the goal, the power is ramped down to 0. The reach
// in each DOF is defined by forwardRampReach_in, strafeRampReach_in, and turnRampReach_deg,
// respectively.
// The path will terminate after pathTime_ms defined in PathDetails without applying
// a ramp-down of power.
//
// MIT License
// Copyright (c) 2023 bayrobotics.org
//


package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
public class PathManager {
    static double maxPowerStep = 0.05;
    public static long timeStep_ms = 50;
    public static double forwardRampReach_in = 24;
    public static double strafeRampReach_in = 12;
    public static double turnRampReach_deg = 45;

    public static double forwardPower, forwardPowerLast;
    public static double strafePower, strafePowerLast;
    public static double turnPower, turnPowerLast;
    private enum THISDOF {FORWARD, STRAFE, TURN}

    // set path time
    public static double elapsedTime_ms;
    private static double deltaIsShouldForward, deltaIsShouldStrafe, deltaIsShouldAngle;


    public static void moveRobot(FtcDashboard dashboard, Telemetry telemetry) throws InterruptedException {
        // initialize
        elapsedTime_ms = 0;
        forwardPower = 0;
        strafePower = 0;
        turnPower = 0;
        forwardPowerLast = 0;
        strafePowerLast = 0;
        turnPowerLast = 0;
        // double actualStartTime_ms = System.currentTimeMillis();
        ParallelAction.initPath();
        while (elapsedTime_ms < PathDetails.pathTime_ms) {
            // move robot
            elapsedTime_ms += timeStep_ms;
            // double actualElapsedTime_ms = System.currentTimeMillis() - actualStartTime_ms;
            // calculate remaining forward movement in original COS
            if (elapsedTime_ms > PathDetails.forwardDelay_ms) {
                // calculate distance to goal
                deltaIsShouldForward = calculateIsShould(THISDOF.FORWARD);
                // calculate correction power, proportional to distance to goal but limited by maxPowerStep
                forwardPower = calculateCorrectionPower(THISDOF.FORWARD);
            }
            // calculate remaining strafe movement in original COS
            if (elapsedTime_ms > PathDetails.strafeDelay_ms) {
                // calculate distance to goal
                deltaIsShouldStrafe = calculateIsShould(THISDOF.STRAFE);
                // calculate correction power, proportional to distance to goal but limited by maxPowerStep
                strafePower = calculateCorrectionPower(THISDOF.STRAFE);
            }
            // calculate remaining turn movement in original COS
            if (elapsedTime_ms > PathDetails.turnDelay_ms) {
                // calculate distance to goal
                deltaIsShouldAngle = calculateIsShould(THISDOF.TURN);
                // calculate correction power, proportional to distance to goal but limited by maxPowerStep
                turnPower = calculateCorrectionPower(THISDOF.TURN);
            }
            balancePower(); // balance power so it doesn't exceed 1
            if (GameSetup.SIMULATION) {
                // move robot in simulation mode
                RobotPoseSimulation.updatePose(forwardPower, strafePower, turnPower, timeStep_ms);
            } else {
                // move real robot
                RobotPose.updatePose(forwardPower, strafePower, turnPower);
            }
            Thread.sleep(timeStep_ms);
            // update telemetry after timeStep_ms to measure how far
            // the robot moved with the new settings
            // this is only necessary for the "real" robot
            if (!GameSetup.SIMULATION) {
                RobotPose.readPose();
            }
            ParallelAction.execute(PathDetails.parallelAction, deltaIsShouldForward, deltaIsShouldStrafe);
            UpdateTelemetry.params(telemetry);
            UpdateTelemetry.pose(dashboard);
        }
        ParallelAction.finish(PathDetails.parallelAction);
    }

    private static void balancePower() {
        // balance power so it doesn't exceed 1
        // remember max |power| is 1 but will be distributed evenly on the 3 DOFs
        double sumPower = Math.abs(forwardPower) + Math.abs(strafePower) + Math.abs(turnPower);
        if (sumPower != 0 & sumPower > 1) {
            forwardPower /= sumPower;
            strafePower /= sumPower;
            turnPower /= sumPower;
        }
    }

    private static double calculateCorrectionPower(THISDOF dof) {
        double rampReach;
        double deltaIsShould;
        double power;
        double signumIsShould;
        double lastPower;
        double maxPowerFinal;
        if (dof == THISDOF.FORWARD) {
            deltaIsShould = deltaIsShouldForward;
            signumIsShould = Math.signum(deltaIsShouldForward);
            rampReach = forwardRampReach_in;
            lastPower = forwardPowerLast;
        } else if (dof == THISDOF.STRAFE) {
            deltaIsShould = deltaIsShouldStrafe;
            signumIsShould = Math.signum(deltaIsShouldStrafe);
            rampReach = strafeRampReach_in;
            lastPower = strafePowerLast;
        } else {
            deltaIsShould = deltaIsShouldAngle;
            signumIsShould = Math.signum(deltaIsShouldAngle);
            rampReach = turnRampReach_deg;
            lastPower = turnPowerLast;
        }
        if (Math.abs(deltaIsShould) > rampReach) {
            // outside reach value: move with maximum available power
            power = signumIsShould;
        } else {
            // within reach value: reduce power proportional to distance
            power = deltaIsShould / rampReach;
        }
        // check if power is increasing too fast
        if (Math.abs(power) > Math.abs(lastPower) + maxPowerStep) {
            power = lastPower + signumIsShould * maxPowerStep;
        }
        if (dof == THISDOF.FORWARD) {
            forwardPowerLast = power;
        } else if (dof == THISDOF.STRAFE) {
            strafePowerLast = power;
        } else {
            turnPowerLast = power;
        }
        return power;
    }


    private static double calculateIsShould(THISDOF dof) {
        if (GameSetup.SIMULATION) {
            if (dof == THISDOF.FORWARD) {
                return PathDetails.forwardGoal_in - RobotPoseSimulation.forward;
            } else if (dof == THISDOF.STRAFE) {
                return PathDetails.strafeGoal_in - RobotPoseSimulation.strafe;
            } else {
                return PathDetails.turnGoal_deg - RobotPoseSimulation.angle;
            }
        } else {
            // robot moves in real life!
            if (dof == THISDOF.FORWARD) {
                return PathDetails.forwardGoal_in - RobotPose.getForward_in();
            } else if (dof == THISDOF.STRAFE) {
                return PathDetails.strafeGoal_in - RobotPose.getStrafe_in();
            } else {
                return PathDetails.turnGoal_deg - RobotPose.getHeadingAngle_deg();
            }
        }
    }
}
