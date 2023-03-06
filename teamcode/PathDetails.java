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
package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;

@Config
public class PathDetails {
    public static double pathTime_ms;
    // Note: the offsets defined below are used by the PathManager and are useful to set an offset
    // from an arbitrary origin when using the FTC Dashboard. The default (offset=0) defines the
    // robot centric coordinate system at the beginning of the path.
    public static double forwardGoal_in, forwardOffset_in=0;
    public static double strafeGoal_in, strafeOffset_in=0;
    public static double turnGoal_deg, turnOffset_deg=0;
    // set initial delay
    public static double forwardDelay_ms;
    public static double strafeDelay_ms;
    public static double forwardPowerFinal=0, strafePowerFinal=0, turnPowerFinal=0;
    public static double turnDelay_ms;
    // set path time
    // path 1
    public static void setPath_1()
    {
        // duration
        pathTime_ms = 5000;
        // forward
        forwardGoal_in = 57;;
        forwardDelay_ms = 0;
        // strafe
        strafeGoal_in = 0;;
        strafeDelay_ms = 0;
        // turn
        turnGoal_deg = 45;;
        turnDelay_ms = 3000;
    }

    public static void setPath_2()
    {
        // stopping the clock before reaching the defined distance goal
        // causes the robot to roll into camera guided mode
        // and not waste time by stopping at goal point
        pathTime_ms = 2800;
        // forward
        forwardGoal_in = 53;
        forwardDelay_ms = 500;
        // strafe
        strafeGoal_in = -20;
        strafeDelay_ms = 1500;
        // turn
        turnGoal_deg = -90;
        turnDelay_ms = 0;
    }
    public static void setPath_3()
    {
        // duration
        pathTime_ms = 2600;
        // forward
        forwardGoal_in = 53;
        forwardDelay_ms = 0;
        // strafe
        strafeGoal_in = 0;
        strafeDelay_ms = 0;
        strafePowerFinal = 0.1;
        // turn
        turnGoal_deg = 45;
        turnDelay_ms = 900;
    }
}
