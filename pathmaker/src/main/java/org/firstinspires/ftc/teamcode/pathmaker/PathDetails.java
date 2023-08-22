//
// PathDetails.java
//
// The PathDetails class implements the definition of robot paths. Each path is controlled by two
// constants for each of the three degrees of freedom (DOF). From a robot centric view the DOFs are
// forward, strafe, and turn. The two constants that define each DOF are:
// a goal (e.g. forwardGoal_in = 57) that defines the endpoint to be reached at the end of the path,
// and a delay (e.g. forwardDelay_ms = 500) that defines the timing from the beginning of the path
// at which the goal will be activated. The robot will attempt to reach the goal while maximizing
// the the use of the available battery power. The robot will ramp power down when reaching the goal.
// Additionally, the PathTime constant controls the total time allowed for the path. When PathTime
// is larger than the time needed to reach the goal, it has no effect. However, if PathTime is
// reached before the goal, the path is terminated at the current conditions. In other words, if
// the robot is moving it will continue to do so. This can be useful to create a "rolling stop"
// for a smooth transition to the next robot action.
// The coordinate system (COS) that PathMaker uses is the robot centric coordinate system where
// the forward direction is Y and the strafe direction is X. Rotations count positive going
// right. Whenever the encoders are reset, the COS will be reset to the current location.
//
// MIT License
// Copyright (c) 2023 bayrobotics.org
//
package org.firstinspires.ftc.teamcode.pathmaker;

public class PathDetails {
    public static double pathTime_ms;
    public static ParallelAction.ACTION parallelAction;
    // Note: the offsets defined below are used by the PathManager and are useful to set an offset
    // from an arbitrary origin when using the FTC Dashboard. The default (offset=0) defines the
    // robot centric coordinate system at the beginning of the path.
    public static double powerScaling = 1;
    public static double forwardGoal_in, forwardOffset_in=0;
    public static double strafeGoal_in, strafeOffset_in=0;
    public static double turnGoal_deg, turnOffset_deg=0;
    // set initial delay
    public static double forwardDelay_ms;
    public static double strafeDelay_ms;
    public static double turnDelay_ms;

    public static void adjustSignTerminalColor(){
        // adjust for terminal color, RED is default
        if (GameSetup.thisTerminal == GameSetup.Terminal.BLUE){
            int thisTerminalSign = -1;
            strafeGoal_in *= thisTerminalSign;
            turnGoal_deg *= thisTerminalSign;
        }
    }
    public static void setPath_startToJunction()
    {
        // move robot to medium junction (avoid collision at high junction
        // while robot rotates, move cone with signal sleeve out of the way
        pathTime_ms = 3000; parallelAction = ParallelAction.ACTION.NONE;
        powerScaling = 1;
        // forward             strafe              turn
        forwardGoal_in = 48; strafeGoal_in = 0;    turnGoal_deg = 135;
        forwardDelay_ms = 0; strafeDelay_ms = 1500;   turnDelay_ms = 1500;
        adjustSignTerminalColor();
        //
        // parameters for high junction saved for future use
        // forward             strafe              turn
        //        forwardGoal_in = 57; strafeGoal_in = 0;    turnGoal_deg = 45;
        //        forwardDelay_ms = 0; strafeDelay_ms = 0;   turnDelay_ms = 3000;
    }
    public static void setPath_junctionDeliver()
    {
        // this is a short path, maybe 6-12 inches movement
        // robot is approaching junction while raising the intake
        pathTime_ms = 1700; parallelAction = ParallelAction.ACTION.JUNCTION_DELIVER;
        powerScaling = 1;
        // forward             strafe                turn
        forwardGoal_in = 39.5;   strafeGoal_in = 8;    turnGoal_deg = 145;
        forwardDelay_ms = 0;   strafeDelay_ms = 0;   turnDelay_ms = 0;
        adjustSignTerminalColor();
    }
    public static void setPath_junctionBackOff()
    {
        // this is a short path, maybe 6-12 inches movement
        // robot is moving away from junction while lowering the intake
        pathTime_ms = 400; parallelAction = ParallelAction.ACTION.JUNCTION_BACKOFF;
        powerScaling = 1;
        // forward             strafe                turn
        forwardGoal_in = 51;   strafeGoal_in = 1.5;    turnGoal_deg = 145;
        forwardDelay_ms = 0;   strafeDelay_ms = 0;   turnDelay_ms = 0;
        adjustSignTerminalColor();
    }
    public static void setPath_junctionToStack()
    {
        // exit path before reaching goal for rolling handover to the stack portion
        // reduced path time from 2600 (too much) to 1800 (too short)
        pathTime_ms = 2500; parallelAction = ParallelAction.ACTION.NONE;
        powerScaling = 0.5;
        // forward             strafe                  turn
        // was 53,270
        forwardGoal_in = 54;   strafeGoal_in = -30;    turnGoal_deg = 275;
        forwardDelay_ms = 0; strafeDelay_ms = 700;   turnDelay_ms = 0;
        adjustSignTerminalColor();
    }
    public static void setPath_stack()
    {
        // this is a "zero length" path only to grab another cone
        // set strafe goal just past the wall so the robot is
        // slowly trying to move into the wall
        pathTime_ms = 1500; parallelAction = ParallelAction.ACTION.STACK;
        powerScaling = 0.6;
        // forward             strafe                  turn
        forwardGoal_in = 53;   strafeGoal_in = -32;    turnGoal_deg = 270;
        forwardDelay_ms = 0; strafeDelay_ms = 0;   turnDelay_ms = 0;
        adjustSignTerminalColor();
    }
    public static void setPath_stackToJunction()
    {
        // moving robot from stack back to the junction
        pathTime_ms = 1500; parallelAction = ParallelAction.ACTION.NONE;
        powerScaling = 1;
        // forward             strafe                turn
        forwardGoal_in = 51;   strafeGoal_in = 0;    turnGoal_deg = 145;
        forwardDelay_ms = 0;   strafeDelay_ms = 0;   turnDelay_ms = 500;
        adjustSignTerminalColor();
    }
    public static void setPath_parking(int zone){
        // this is a short path, maybe 6-12 inches movement
        // robot is moving away from junction while lowering the intake
        // then it parks in the zone
        int distanceToZone;
        if (zone == 1){
            distanceToZone = -23;
        } else if (zone == 2){
            distanceToZone = 1;
        } else {
            distanceToZone = 24;
        }
        pathTime_ms = 2000; parallelAction = ParallelAction.ACTION.NONE;
        powerScaling = 1;
        // forward             strafe                turn
        forwardGoal_in = 51;   strafeGoal_in = distanceToZone;    turnGoal_deg = 270;
        forwardDelay_ms = 0;   strafeDelay_ms = 0;   turnDelay_ms = 0;
        adjustSignTerminalColor();
        //PathManager.moveRobot();


    }
}
