//
// MIT License
// Copyright (c) 2023 bayrobotics.org
//
package org.firstinspires.ftc.teamcode.pathmaker;

public class GameSetup {
    public static boolean SIMULATION = false; // used to switch to simulation mode

    public enum RobotModel {ROBOT1,ROBOT2}
    public static RobotModel robotModel = RobotModel.ROBOT1;
    public enum Terminal {RED,BLUE}
    public static Terminal thisTerminal = Terminal.RED;
    public enum TeamColor {RED,BLUE}
    public static TeamColor thisTeamColor = TeamColor.RED;

    public int terminalSign(TeamColor teamColor){
        if (teamColor == TeamColor.RED){
            return 1;
        } else {
            return -1;
        }

    }
}
