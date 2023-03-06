// WheelPowerManager.java
//
// MIT License
// Copyright (c) 2023 Sergio Lemaitre
//
package org.firstinspires.ftc.teamcode;


import org.firstinspires.ftc.teamcode.hw.DriveTrain;
public class WheelPowerManager {
    public static void setDrivePower(DriveTrain driveTrain, double forwardDrive, double strafeDrive,double rotateDrive, double headingDrive){
        double powerFL, powerFR, powerBL, powerBR;
        double radians = headingDrive / 180 * Math.PI;
        double forwardHeading = forwardDrive * Math.cos(radians) + strafeDrive * Math.sin(radians);
        double strafeHeading = -forwardDrive * Math.sin(radians) + strafeDrive * Math.cos(radians);
        // forward power is the same on all wheels
        powerFL = forwardHeading; powerFR = forwardHeading;
        powerBL = forwardHeading; powerBR = forwardHeading;
        // add strafe power
        powerFL += strafeHeading; powerFR -= strafeHeading;
        powerBL -= strafeHeading; powerBR += strafeHeading;
        // add turn power
        powerFL += rotateDrive; powerFR -= rotateDrive;
        powerBL += rotateDrive; powerBR -= rotateDrive;
        driveTrain.setMotorPowers(powerFL,powerBL,powerBR,powerFR);
    }

}
