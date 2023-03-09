//
// MIT License
// Copyright (c) 2023 bayrobotics.org
//
package org.firstinspires.ftc.teamcode;
import android.graphics.Paint;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.hw.Alignment;
import org.firstinspires.ftc.teamcode.hw.Lift;
import org.firstinspires.ftc.teamcode.hw.Intake;


public class ParallelAction {

    public enum ACTION {JUNCTION_DELIVER,JUNCTION_BACKOFF,STACK,NONE};
    private static LinearOpMode myOpMode = null;   // gain access to methods in the calling OpMode.
    public ParallelAction(LinearOpMode opMode){
        myOpMode = opMode;
    }
    private static Lift lift;
    private static Intake intake;
    private static Alignment alignment;
    private static long startTime, elapsedTime;
    private static int phaseCounter;
    private static int stackCounter;



    public static void setOpMode(LinearOpMode opMode){
        myOpMode = opMode;
    }


    public static void init(){
        // Only non-blocking calls
        lift = new Lift(myOpMode.hardwareMap, true);
        intake = new Intake(myOpMode.hardwareMap);
        alignment = new Alignment(myOpMode.hardwareMap);
        alignment.setAlignmentPosition(Alignment.AlignmentPosition.RETRACTED);
        lift.setLiftPosition(Lift.LiftPosition.FLOOR);
        intake.setIntakePosition(Intake.IntakePosition.GRAB);
        //myOpMode.sleep(700);
        //intake.setIntakePosition(Intake.IntakePosition.HOLD);
        return;
    }
    public static void initPath(){
        startTime = System.currentTimeMillis();
        phaseCounter = 0;
        stackCounter = 0;
    }
    public static void execute(ACTION action, double dx, double dy){
        // Only non-blocking calls

        if (action == ACTION.JUNCTION_DELIVER) {
            // while robot is approaching junction, lift intake to high position
            // and deliver cone
            if (phaseCounter == 0) {
                intake.setIntakePosition(Intake.IntakePosition.HOLD);
                lift.setLiftPosition(Lift.LiftPosition.MEDIUM);
                startTime = System.currentTimeMillis();
                phaseCounter++;
            } else if (phaseCounter == 1) {
                elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime > 600) {
                    alignment.setAlignmentPosition(Alignment.AlignmentPosition.EXTENDED);
                    startTime = System.currentTimeMillis();
                    phaseCounter++;
                }
            } else if (phaseCounter == 2) {
                elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime > 800) { // Wait for cone to drop
                    intake.setIntakePosition(Intake.IntakePosition.DELIVER);
                    startTime = System.currentTimeMillis();
                    phaseCounter++;
                }
            }
        } else if (action == ACTION.JUNCTION_BACKOFF){
            // while robot is backing off of junction, lower lift to "driving" position
            // i.e. the correct height to pick up next cone
            if (phaseCounter == 0) {
                elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime > 500) { // Wait for cone to drop
                    alignment.setAlignmentPosition(Alignment.AlignmentPosition.RETRACTED);
                    startTime = System.currentTimeMillis();
                    phaseCounter++;
                }
            } else if (phaseCounter == 1) {
                elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime > 500) { // Wait to gain some distance from junction
                    intake.setIntakePosition(Intake.IntakePosition.HOLD);
                    lift.setLiftPosition(Lift.LiftPosition.LOW);
                    startTime = System.currentTimeMillis();
                    phaseCounter++;
                }
            }

        } else if (action == ACTION.STACK){
            // robot arrived at cone stack, drop intake and grab another cone
            // wait 500ms at first to make sure robot is at field perimeter
            if (phaseCounter == 0) {
                elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime > 500) {
                    intake.setIntakePosition(Intake.IntakePosition.GRAB);
                    if (stackCounter == 0) {
                        lift.setLiftPosition(Lift.LiftPosition.FIVE_CONES);
                        stackCounter++;
                    } else if (stackCounter == 1) {
                        lift.setLiftPosition(Lift.LiftPosition.FOUR_CONES);
                        stackCounter++;
                    } else if (stackCounter == 2) {
                        lift.setLiftPosition(Lift.LiftPosition.THREE_CONES);
                        stackCounter++;
                    }
                    startTime = System.currentTimeMillis();       // drop to intake onto stack
                    phaseCounter++;
                }
            } else if (phaseCounter == 1) {
                elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime > 1000) { // allow time to lift intake
                    lift.setLiftPosition(Lift.LiftPosition.LOW);
                    intake.setIntakePosition(Intake.IntakePosition.HOLD);
                    startTime = System.currentTimeMillis();
                    phaseCounter++;
                }
            }

        } else {
            // do nothing
        }
        return;
    }
    public static void finish(ACTION action){
        // Only non-blocking calls
        return;
    }
}
