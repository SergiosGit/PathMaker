// MIT License
// Copyright (c) 2023 bayrobotics.org
//
package org.firstinspires.ftc.teamcode.hw;
import com.qualcomm.hardware.bosch.BHI260IMU;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
public class IMU {
    // see https://stemrobotics.cs.pdx.edu/node/7266.html
    BNO055IMU imu;
    BHI260IMU imu2;
    Orientation             lastAngles = new Orientation();
    double                  globalAngle;
    private LinearOpMode myOpMode = null;   // gain access to methods in the calling OpMode.
    public IMU(LinearOpMode opMode){
        myOpMode = opMode;
    }
    public void setOpMode(LinearOpMode opMode){
        myOpMode = opMode;
    }
    public void initBNO055(){
        // Adjust the orientation parameters to match your robot
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.mode                = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled      = false;
        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        //imu = myOpMode.hardwareMap.get(BNO055IMU.class, "imu");
        imu = myOpMode.hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);

        // sdk v 8 ... ??
        // Retrieve the IMU from the hardware map
        // imu = myOpMode.hardwareMap.get(IMU.class, "imu");
        //IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
        //        RevHubOrientationOnRobot.LogoFacingDirection.UP,
        //        RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));
        // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
    }
    public void calibrate(Telemetry telemetry){
        telemetry.addData("Mode", "calibrating...");
        telemetry.update();

        // make sure the imu gyro is calibrated before continuing.
        while (!myOpMode.isStopRequested() && !imu.isGyroCalibrated())
        {
            myOpMode.sleep(50);
            myOpMode.idle();
        }
        telemetry.addData("Mode", "waiting for start");
        telemetry.addData("imu calib status", imu.getCalibrationStatus().toString());
        telemetry.update();
        resetAngle();;
    }
    public void measurements(Telemetry telemetry){
        telemetry.addData("1 imu heading", lastAngles.firstAngle);
        telemetry.addData("2 global heading", globalAngle);
    }
    public Orientation resetAngle()
    {
        lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        globalAngle = 0;
        return lastAngles;
    }
    public double getAngle_deg()
    {
        // X (firstAngle) axis is for heading with controller logo to the left and USB backwards
        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return -angles.firstAngle;
    }
    public double getAngle_rad(){
        return getAngle_deg() / 180 * Math.PI;
    }
}
