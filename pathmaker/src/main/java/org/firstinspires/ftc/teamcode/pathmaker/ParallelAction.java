//
// MIT License
// Copyright (c) 2023 bayrobotics.org
//
package org.firstinspires.ftc.teamcode.pathmaker;


public class ParallelAction {

    public enum ACTION {JUNCTION_DELIVER,JUNCTION_BACKOFF,STACK,NONE};
    private static long startTime, elapsedTime;
    private static int phaseCounter;
    private static int stackCounter;



    public static void init(){
        // Only non-blocking calls
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
                startTime = System.currentTimeMillis();
                phaseCounter++;
            } else if (phaseCounter == 1) {
                elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime > 600) {
                    startTime = System.currentTimeMillis();
                    phaseCounter++;
                }
            } else if (phaseCounter == 2) {
                elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime > 800) { // Wait for cone to drop
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
                    startTime = System.currentTimeMillis();
                    phaseCounter++;
                }
            } else if (phaseCounter == 1) {
                elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime > 500) { // Wait to gain some distance from junction
                    //intake.setIntakePosition(Intake.IntakePosition.HOLD);
                    //lift.setLiftPosition(Lift.LiftPosition.LOW);
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
                    //intake.setIntakePosition(Intake.IntakePosition.GRAB);
                    if (stackCounter == 0) {
                        //lift.setLiftPosition(Lift.LiftPosition.FIVE_CONES);
                        stackCounter++;
                    } else if (stackCounter == 1) {
                        //lift.setLiftPosition(Lift.LiftPosition.FOUR_CONES);
                        stackCounter++;
                    } else if (stackCounter == 2) {
                        //lift.setLiftPosition(Lift.LiftPosition.THREE_CONES);
                        stackCounter++;
                    }
                    startTime = System.currentTimeMillis();       // drop to intake onto stack
                    phaseCounter++;
                }
            } else if (phaseCounter == 1) {
                elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime > 1000) { // allow time to lift intake
                    //lift.setLiftPosition(Lift.LiftPosition.LOW);
                    //intake.setIntakePosition(Intake.IntakePosition.HOLD);
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
