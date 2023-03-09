package org.firstinspires.ftc.teamcode.hw;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lift {

    public enum LiftPosition {FLOOR, LOW, MEDIUM, HIGH, HOLD, MANUAL,
        FIVE_CONES,FOUR_CONES,THREE_CONES,TWO_CONES,ONE_CONE}

    private class LiftPreset {
        private LiftPosition position;
        private int ticks;
        private double motorSpeed;

        public LiftPreset(LiftPosition position, int ticks, double motorSpeed)
        {
            this.position = position;
            this.ticks = ticks;
            this.motorSpeed = motorSpeed;
        }

        public LiftPosition getPosition(){
            return this.position;
        }
        public int getTicks(){
            return this.ticks;
        }
        public double getSpeed(){
            return this.motorSpeed;
        }

    }
    private DcMotor leftLift,centerLift,rightLift;
    private List<DcMotor> liftMotors;
    private final TouchSensor limitSwitch;

    private ArrayList<LiftPreset> presets = new ArrayList<>();
    private LiftPreset currentLiftPreset;

    public Lift(HardwareMap hardwareMap, boolean stopAndResetEncoders){

        limitSwitch = hardwareMap.get(TouchSensor.class, "limitSwitch");

        leftLift = hardwareMap.get(DcMotor.class, "leftSlideUp");
        centerLift = hardwareMap.get(DcMotor.class, "centerSlideUp");
        rightLift = hardwareMap.get(DcMotor.class, "rightSlideUp");

        leftLift.setDirection(DcMotorSimple.Direction.FORWARD);
        centerLift.setDirection(DcMotorSimple.Direction.REVERSE);
        rightLift.setDirection(DcMotorSimple.Direction.FORWARD);

        liftMotors = Arrays.asList(leftLift,centerLift,rightLift);

        //set mode to RUN_WITHOUT_ENCODERS and set BRAKE mode to FLOAT
        for (DcMotor motor : liftMotors) {
            if (stopAndResetEncoders){
                motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            }
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }
        this.currentLiftPreset = new LiftPreset(LiftPosition.MANUAL,0,1.0);

        presets.add(new LiftPreset(LiftPosition.FLOOR,      0,   1.0));
        presets.add(new LiftPreset(LiftPosition.LOW,        1115,1.0));
        presets.add(new LiftPreset(LiftPosition.MEDIUM,     1900,1.0));
        presets.add(new LiftPreset(LiftPosition.HIGH,       2673,1.0));
        presets.add(new LiftPreset(LiftPosition.HOLD,       0,   0.17));
        presets.add(new LiftPreset(LiftPosition.MANUAL,     0,   1.0));
        presets.add(new LiftPreset(LiftPosition.FIVE_CONES, 320, 1.0));
        presets.add(new LiftPreset(LiftPosition.FOUR_CONES, 270, 1.0));
        presets.add(new LiftPreset(LiftPosition.THREE_CONES,210, 1.0));
        presets.add(new LiftPreset(LiftPosition.TWO_CONES,  80,  1.0));
        presets.add(new LiftPreset(LiftPosition.ONE_CONE,   20,  1.0));
    }



   public void setLiftPosition (LiftPosition position){
       if (position == LiftPosition.MANUAL){
           throw new IllegalArgumentException("Need two more parameters to call with MANUAL position");}

        int ticks = 0;
        double speed = 0.0;

        //find the ticks and the speed for the given position
        for (LiftPreset preset : presets){
            if(preset.getPosition() == position){
                ticks = preset.getTicks();
                speed = preset.getSpeed();
                break;
            }
        }
       for (DcMotor motor : liftMotors){
           if (position != LiftPosition.HOLD){
               motor.setTargetPosition(ticks);
               motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           }
           motor.setPower(speed);
       }
        this.currentLiftPreset = new LiftPreset(position,ticks,speed);
    }

    //call this method with mode MANUAL and gamepad_left and gamepad_right trigger
    public void setLiftPosition (LiftPosition position, double leftTriggerValue, double rightTriggerValue){

        if (position != LiftPosition.MANUAL){
            throw new IllegalArgumentException("Need to call with MANUAL position");}

        this.currentLiftPreset = new LiftPreset(position,0,1.0);

            if (!limitSwitch.isPressed()){
                for (DcMotor motor : liftMotors){
                    motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                }
                leftLift.setPower(-leftTriggerValue);
                centerLift.setPower(-leftTriggerValue);
                rightLift.setPower(-leftTriggerValue);
                leftLift.setPower(rightTriggerValue);
                centerLift.setPower(rightTriggerValue);
                rightLift.setPower(rightTriggerValue);

            }else
            //limitSwitch is not pressed - go up or down
            {
                for (DcMotor motor : liftMotors){
                    motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                }
                leftLift.setPower(rightTriggerValue);
                centerLift.setPower(rightTriggerValue);
                rightLift.setPower(rightTriggerValue);
            }
        }

    public LiftPosition getCurrentLiftPosition(){
        return this.currentLiftPreset.getPosition();
    }
}





