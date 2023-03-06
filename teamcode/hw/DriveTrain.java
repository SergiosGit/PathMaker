// DriveTrain.java
//
// MIT License
// Copyright (c) 2023 bayrobotics.org
//
package org.firstinspires.ftc.teamcode.hw;

import static org.firstinspires.ftc.teamcode.PathMaker.RobotModel;
import static org.firstinspires.ftc.teamcode.PathMaker.robotModel;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class DriveTrain {
    /* Declare OpMode members. */
    private LinearOpMode myOpMode = null;   // gain access to methods in the calling OpMode.

    // Define Motor and Servo objects  (Make them private so they can't be accessed externally)
    private static DcMotor frontLeft   = null;
    private static DcMotor backLeft  = null;
    private static DcMotor backRight = null;
    private static DcMotor frontRight = null;
    public DriveTrain(LinearOpMode opMode){
        myOpMode = opMode;
    }
    public void init(){
        frontLeft = myOpMode.hardwareMap.get(DcMotor.class,"front_left_drive");
        backLeft = myOpMode.hardwareMap.get(DcMotor.class,"back_left_drive");
        backRight = myOpMode.hardwareMap.get(DcMotor.class,"back_right_drive");
        frontRight = myOpMode.hardwareMap.get(DcMotor.class,"front_right_drive");
        initMotor(frontLeft);
        initMotor(backLeft);
        initMotor(backRight);
        initMotor(frontRight);
        // if all motors are plugged in the same you may need to do:
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);

    }

    public DcMotor initMotor(DcMotor motor){
        //motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        return motor;
    }

    public void setMotorPowers(double fl, double bl, double br, double fr){
        frontLeft.setPower(fl);
        backLeft.setPower(bl);
        backRight.setPower(br);
        frontRight.setPower(fr);
    }

    public static int[] getEncoderValues() {
        if (robotModel == RobotModel.ROBOT1) {
//            encLeft = hardwareMap.dcMotor.get("front_left_drive");
//            encRight = hardwareMap.dcMotor.get("back_right_drive");
//            encAux = hardwareMap.dcMotor.get("back_left_drive");
            // 0: left encoder, 1: middle encoder, 2: right encoder
            return new int[]{frontLeft.getCurrentPosition(), backLeft.getCurrentPosition(), backRight.getCurrentPosition()};
        } else {
            return new int[]{backLeft.getCurrentPosition(), 0, backRight.getCurrentPosition()};
        }
    }
}
