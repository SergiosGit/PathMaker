package org.firstinspires.ftc.sim;
import java.awt.Graphics;
import java.awt.Graphics2D;
//import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.io.File;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.firstinspires.ftc.teamcode.pathmaker.RobotPoseSimulation;
import org.firstinspires.ftc.teamcode.pathmaker.GameSetup;
import org.firstinspires.ftc.teamcode.pathmaker.PathDetails;
import org.firstinspires.ftc.teamcode.pathmaker.PathManager;

public class ImageAnimation extends JPanel implements ActionListener {

    private BufferedImage robot, gameField;
    private int x, y;
    private Timer timer;
    private double pxPerInch = 3.5417;
    private int upperLeftX = 54;
    private int upperLeftY = 20;

    public ImageAnimation() {
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
        System.out.println("gameField size = " + gameField.getWidth(null) + "," + gameField.getHeight(null));
        // x,y are initialized in RobotPoseSimulation (see actionPerformed) and updated in actionPerformed
        timer = new Timer(10, this);
        timer.start();
        // initialize robot path
        GameSetup.SIMULATION = true;
        GameSetup.thisTeamColor = GameSetup.TeamColor.BLUE;
        GameSetup.thisTerminal = GameSetup.Terminal.RED;
        RobotPoseSimulation.initializePose(0, 0, 0);
        // start moveRobot on separate thread
        Thread t = new Thread() {
            public void run() {
                try {
                    PathDetails.setPath_startToJunction();
                    PathManager.moveRobot();
                    int thisNumberSteps = 2;
                    for (int i = 0; i < thisNumberSteps; i++) {

                        PathDetails.setPath_junctionDeliver();
                        PathManager.moveRobot();
    
                        PathDetails.setPath_junctionBackOff();
                        PathManager.moveRobot();
    
                        PathDetails.setPath_junctionToStack();
                        PathManager.moveRobot();
    
                        PathDetails.setPath_stack();
                        PathManager.moveRobot();
    
                        PathDetails.setPath_stackToJunction();
                        PathManager.moveRobot();
                    }
                    int setZone = 3;
                    PathDetails.setPath_parking(setZone);
                    PathManager.moveRobot();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        AffineTransform at = new AffineTransform();
        // scale the gamefiled by 2.0
        //at.scale(2.0, 2.0);
        at.translate(x, y);
        g2d.drawImage(gameField, 0, 0, null);
        // scale robot image
        at.scale(0.1, 0.1);
        // rotate robot image
        at.rotate(Math.toRadians(RobotPoseSimulation.fieldAngle), robot.getWidth(null)/2, robot.getHeight(null)/2);   
        g2d.drawImage(robot, at, null);
    }
    
    public void actionPerformed(ActionEvent e) {
        x = (int) (RobotPoseSimulation.fieldX * pxPerInch) + upperLeftX;
        y = (int) (RobotPoseSimulation.fieldY * pxPerInch) + upperLeftY;
        repaint();
    }

    public void graphRobotPosition() {
        
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Power Play Animation");
        frame.add(new ImageAnimation());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 650);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
