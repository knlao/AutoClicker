package autoclicker;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.Timer;

public class Clicker {
    
    public static int DEFAULT_RATE = 300;
    public static int DEFAULT_VAR = 50;
    
    public int mouse = InputEvent.BUTTON1_MASK;
    
    private int rate = DEFAULT_RATE;
    private int variance = DEFAULT_VAR;
    private int actual;
    private boolean running = false;
    private Robot robot;
    private Timer timer;
    private Random random;
    
    public Clicker() {
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Point pos = MouseInfo.getPointerInfo().getLocation();
                robot.mouseMove(pos.x, pos.y);
                robot.mousePress(mouse);
                robot.mouseRelease(mouse);
                updateTimer();
            }
        };
        
        try {
            robot = new Robot();
            timer = new Timer(rate, al);
            random = new Random();
        } catch (AWTException err) {}
    }
    
    public void setValues(int rate, int variance)
    {
        this.rate = rate;
        this.variance = variance;
    }

    public void start()
    {
        running = true;
        actual = 0;
        timer.setDelay(actual);
        timer.start();
    }

    public void stop()
    {
        running = false;
        timer.stop();
    }

    private void updateTimer()
    {
        actual = rate + (variance > 0 ? random.nextInt(variance * 2) - variance : 0);
        timer.setDelay(actual);
    }

    public boolean isRunning()
    {
        return running;
    }
    
}
