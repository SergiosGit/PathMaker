//
// PathMaker.java
//
// This is the main module showing the use of the PathMaker library that can be used
// for autonomous control of a Mecanum wheel drive train for the FTC competition.
// This implementation uses the FTC Dashboard for developing the robot path settings.
//
// MIT License
// Copyright (c) 2023 Sergio Lemaitre
//
package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hw.DriveTrain;


@Config
@Autonomous
public class PathMaker extends LinearOpMode {

    public enum RobotModel {ROBOT1,ROBOT2}
    public static RobotModel robotModel = RobotModel.ROBOT1;
    public enum Terminal {RED,BLUE}
    public static Terminal thisTerminal = Terminal.RED;
    public enum TeamColor {RED,BLUE}
    public static TeamColor thisTeamColor = TeamColor.RED;
    public static double thisForwardPower = 0;
    public static double thisStrafePower = 0;
    public static double thisTurnPower = 0.2;
    public static double thisHeadingDrive = 0;
    public static int thisPathNumber = 0;
    public static int runTest_ms = 100;
    public static boolean runSimulation = false;
    public static boolean runDriveTest = true;
    public static int thisNumberSteps = 1;
    public static boolean SIMULATION = false; // used in PathManager to switch to simulation mode



    static FtcDashboard dashboard = FtcDashboard.getInstance();
    static Telemetry telemetry = dashboard.getTelemetry();
    DriveTrain driveTrain = new DriveTrain(this);


    @Override
    public void runOpMode() throws InterruptedException {

        waitForStart();
        if (runSimulation) {
            RobotPoseSimulation.initializePose(0, 0, 0);
        } else {
            RobotPose.initializePose(this, driveTrain, telemetry);
        }

        if (opModeIsActive()) {
            if (!runSimulation) {
                if (isStopRequested()) return;
                if (runDriveTest) {
                    RobotPose.initializePose(this, driveTrain, telemetry);
                    for (int i = 0; i < thisNumberSteps; i++) {
                        if (thisPathNumber == -1){
                            WheelPowerManager.setDrivePower(driveTrain, thisForwardPower, thisStrafePower, thisTurnPower, thisHeadingDrive);
                            RobotPose.readPose();
                        } else if (thisPathNumber == 0){
                            // use dashboard parameters
                            PathManager.moveRobot(dashboard, telemetry);
                        } else if (thisPathNumber==1){
                            PathDetails.setPath_1();
                            PathManager.moveRobot(dashboard, telemetry);
                        } else if (thisPathNumber == 2){
                            PathDetails.setPath_2();
                            PathManager.moveRobot(dashboard, telemetry);
                        } else if (thisPathNumber == 3){
                            PathDetails.setPath_3();
                            PathManager.moveRobot(dashboard, telemetry);
                        }
                        UpdateTelemetry.params(telemetry);
                        sleep(runTest_ms);
                    }
                    WheelPowerManager.setDrivePower(driveTrain, 0, 0, 0, 0);
                } else {
                    PathDetails.setPath_1();
                    PathManager.moveRobot(dashboard, telemetry);
                    sleep(1000);
                    for (int i = 0; i < thisNumberSteps; i++) {
                        PathDetails.setPath_2();
                        PathManager.moveRobot(dashboard, telemetry);
                        sleep(1000);
                        PathDetails.setPath_3();
                        PathManager.moveRobot(dashboard, telemetry);
                        sleep(1000);
                    }
                }
            } else {
                PathDetails.setPath_1();
                PathManager.moveRobot(dashboard, telemetry);
                sleep(1000);
                for (int i = 0; i < 5; i++) {
                    PathDetails.setPath_2();
                    PathManager.moveRobot(dashboard, telemetry);
                    sleep(1000);
                    PathDetails.setPath_3();
                    PathManager.moveRobot(dashboard, telemetry);
                    sleep(1000);
                }
            }
        }
    }
}
