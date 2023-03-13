import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
//import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.firstinspires.ftc.teamcode.op.GameSetup;
import org.firstinspires.ftc.teamcode.pathmaker.RobotPoseSimulation;
import org.firstinspires.ftc.teamcode.util.FtcDashboard;
import org.firstinspires.ftc.teamcode.util.Telemetry;
import org.firstinspires.ftc.teamcode.pathmaker.PathDetails;
import org.firstinspires.ftc.teamcode.pathmaker.PathManager;

public class ImageAnimation extends JPanel implements ActionListener {

    private Image robot, gameField;
    private int x, y;
    private Timer timer;
    private double pxPerInch = 1; // 3.5417;
    private int upperLeftX = 54;
    private int upperLeftY = 20;
    private int lowerRightX = 559;
    private int lowerRightY = 531;

    public ImageAnimation() {
        // image = Toolkit.getDefaultToolkit().getImage("image.jpg");
        robot = new ImageIcon("org/firstinspires/ftc/teamcode/sim/Robot.png").getImage();
        gameField = new ImageIcon("org/firstinspires/ftc/teamcode/sim/GameField.png").getImage();
        x = upperLeftX;
        // convert to integer pixels
        y = upperLeftY + (int)(72*pxPerInch);
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
                    PathManager.moveRobot(new FtcDashboard(), new Telemetry(new FtcDashboard()));
                    Telemetry telemetry = new Telemetry(new FtcDashboard());
                    FtcDashboard dashboard = new FtcDashboard();
                    int thisNumberSteps = 1;
                    for (int i = 0; i < thisNumberSteps; i++) {

                        PathDetails.setPath_junctionDeliver();
                        PathManager.moveRobot(dashboard, telemetry);
    
                        PathDetails.setPath_junctionBackOff();
                        PathManager.moveRobot(dashboard, telemetry);
    
                        PathDetails.setPath_junctionToStack();
                        PathManager.moveRobot(dashboard, telemetry);
    
                        PathDetails.setPath_stack();
                        PathManager.moveRobot(dashboard, telemetry);
    
                        PathDetails.setPath_stackToJunction();
                        PathManager.moveRobot(dashboard, telemetry);
                    }
                    int setZone = 1;
                    PathDetails.setPath_parking(setZone);
                    PathManager.moveRobot(dashboard,telemetry);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
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
        at.translate(x, y);
        g2d.drawImage(gameField, 0, 0, null);
        // scale robot image
        at.scale(0.1, 0.1);
        g2d.drawImage(robot, at, null);
    }
    
    public void actionPerformed(ActionEvent e) {
        // x++;
        // if (x > getWidth()) {
        //     x = upperLeftX;
        // }
        x = (int) (RobotPoseSimulation.fieldX * pxPerInch) + upperLeftX;
        y = (int) (RobotPoseSimulation.fieldY * pxPerInch) + upperLeftY;
        // print x,y
        System.out.println("x,y = " + x + "," + y);
        repaint();
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
