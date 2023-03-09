package org.firstinspires.ftc.teamcode.hw;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;

public class Alignment {

    private Servo alignmentServo;
    public enum AlignmentPosition {RETRACTED, EXTENDED}

    private class AlignmentPreset {
        private AlignmentPosition alignmentPosition;
        private double position;

        private AlignmentPreset(AlignmentPosition alignmentPosition, double position) {
            this.alignmentPosition = alignmentPosition;
            this.position = position;
        }

        private AlignmentPosition getAlignmentPresetPosition() {
            return this.alignmentPosition;
        }

        private double getPosition() {
            return this.position;
        }
    }
        private ArrayList<AlignmentPreset> presets = new ArrayList<>();
        AlignmentPreset currentAlignmentPreset;

        public Alignment (HardwareMap hardwareMap){
            alignmentServo = hardwareMap.get(Servo.class,"alignment");
            this.currentAlignmentPreset = new AlignmentPreset(AlignmentPosition.RETRACTED,0.0);
            presets.add(new AlignmentPreset(AlignmentPosition.RETRACTED,1.0));
            presets.add(new AlignmentPreset(AlignmentPosition.EXTENDED,0.0));
        }

       public void setAlignmentPosition(AlignmentPosition alignmentPosition) {

           double servoPosition = 0.0;
           for (AlignmentPreset preset : presets){
               if(preset.getAlignmentPresetPosition() == alignmentPosition){
                   servoPosition = preset.getPosition();
                   break;
               }
           }

           this.currentAlignmentPreset = new AlignmentPreset(alignmentPosition, servoPosition);

           alignmentServo.setPosition(servoPosition);

       }

       public AlignmentPosition getAlignmentPosition (){
           return this.currentAlignmentPreset.getAlignmentPresetPosition();
       }

    }

