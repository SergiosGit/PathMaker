//
// MIT License
// Copyright (c) 2023 bayrobotics.org
//
package org.firstinspires.ftc.teamcode.op;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;



public class ParallelAction {

    public enum ACTION {JUNCTION_DELIVER,JUNCTION_BACKOFF,STACK,NONE};
    private static LinearOpMode myOpMode = null;   // gain access to methods in the calling OpMode.
    public ParallelAction(LinearOpMode opMode){
        myOpMode = opMode;
    }
    private static long startTime, elapsedTime;
    private static int phaseCounter;
    private static int stackCounter;



    public static void setOpMode(LinearOpMode opMode){
        myOpMode = opMode;
    }


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

        return;
    }
    public static void finish(ACTION action){
        // Only non-blocking calls
        return;
    }
}
