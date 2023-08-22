package org.firstinspires.ftc;

import java.io.File;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class App 
{
    public static void main( String[] args )
    {
        BufferedImage robot, gameField;
        System.out.println( "Hello World!" );
        // print working directory
        String workingDir = System.getProperty("user.dir");
        System.out.println("Current working directory : " + workingDir);
                // define path for images        
        String imagePath = "E:\\8_FTC\\Software\\Pathmaker\\pathmaker\\src\\main\\java\\org\\firstinspires\\ftc\\sim\\";
        try {
            // read image from file
            robot = ImageIO.read(new File(imagePath+"Robot.png"));
            System.out.println("robot size = " + robot.getWidth(null) + "," + robot.getHeight(null));
        } catch (Exception e) {
            System.out.println("Image not found");
            e.printStackTrace();
        }
        try {
            // read image from file
            gameField = ImageIO.read(new File(imagePath+"GameField.png"));
            System.out.println("gameField size = " + gameField.getWidth(null) + "," + gameField.getHeight(null));
        } catch (Exception e) {
            System.out.println("Image not found");
            e.printStackTrace();
        }

    }
}
