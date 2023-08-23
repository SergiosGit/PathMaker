package org.firstinspires.ftc.sim;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StopwatchApp extends JFrame {

    private static Timer timer;
    private int seconds = 0;
    private boolean isRunning = false;

    public StopwatchApp() {
        setTitle("Stopwatch");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 350);
        setResizable(false);

        StopwatchPanel stopwatchPanel = new StopwatchPanel();
        add(stopwatchPanel, BorderLayout.CENTER);

        JButton startStopButton = new JButton("Start");
        startStopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isRunning) {
                    timer.start();
                    startStopButton.setText("Stop");
                } else {
                    timer.stop();
                    startStopButton.setText("Start");
                }
                isRunning = !isRunning;
            }
        });
        add(startStopButton, BorderLayout.SOUTH);

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seconds++;
                // show the elapsed seconds next to the title
                setTitle("Stopwatch " + seconds + " sec");
                stopwatchPanel.repaint();
            }
        });

        // don't center like setLocationRelativeTo(null);
        // Set the location of the frame to the upper left corner
        setLocation(0, 0);
}

    // create a function to start stopwatch app programmatically
    public static void start() {
        timer.start();
    }
    // create a function to stop stopwatch app programmatically
    public static void stop() {
        timer.stop();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StopwatchApp().setVisible(true);
        });
    }

    class StopwatchPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            // Draw stopwatch circle
            g.setColor(Color.BLACK);
            g.drawOval(centerX - 100, centerY - 100, 200, 200);

            // Calculate angle for seconds hand
            double secondAngle = Math.toRadians(6 * seconds);

            // Draw seconds hand
            int handLength = 80;
            int handX = centerX + (int) (handLength * Math.sin(secondAngle));
            int handY = centerY - (int) (handLength * Math.cos(secondAngle));
            g.setColor(Color.RED);
            g.drawLine(centerX, centerY, handX, handY);
        }
    }
}
