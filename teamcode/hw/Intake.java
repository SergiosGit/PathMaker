package org.firstinspires.ftc.teamcode.hw;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.ArrayList;

public class Intake {

    public enum IntakePosition {GRAB, HOLD, IDLE, DELIVER}

    private class IntakePreset{
        private IntakePosition intakePosition;
        private double motorSpeed;

        public IntakePreset(IntakePosition intakePosition, double motorSpeed)
        {
            this.intakePosition = intakePosition;
            this.motorSpeed = motorSpeed;
        }

        public IntakePosition getIntakePosition() {return intakePosition;}
        public double getSpeed() {return motorSpeed;}

    }

    private DcMotor intakeMotor;

    private ArrayList<IntakePreset> presets = new ArrayList<>();
    private IntakePreset currentIntakePreset;

    public Intake(HardwareMap hardwareMap){

        intakeMotor = hardwareMap.get(DcMotor.class, "flywheelIntake");
        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        this.currentIntakePreset = new IntakePreset(IntakePosition.IDLE, 0.0);

        presets.add(new IntakePreset(IntakePosition.GRAB,    1.0));
        presets.add(new IntakePreset(IntakePosition.HOLD,    0.32));
        presets.add(new IntakePreset(IntakePosition.DELIVER, -0.7));
    }



    public void setIntakePosition(IntakePosition position){

        double speed = 0.0;

        for (IntakePreset preset : presets){
            if(preset.getIntakePosition() == position){
                speed = preset.getSpeed();
                break;
            }
        }
        intakeMotor.setPower(speed);
        this.currentIntakePreset = new IntakePreset(position,speed);
    }
    public IntakePosition getIntakePosition(){return currentIntakePreset.getIntakePosition();}
}
