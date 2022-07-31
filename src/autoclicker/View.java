package autoclicker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;

import java.awt.event.*;
import javax.swing.*;

public class View extends JFrame implements NativeKeyListener, NativeMouseInputListener {
    
    private Clicker c;
    private JTextField rateTextField;
    private JTextField varianceTextField;
    private JButton playBtn;
    private JComboBox hotkeyCb;
    private JRadioButton holdRb;
    private JRadioButton toggleRb;
    private JRadioButton leftRb;
    private JRadioButton rightRb;
    
    private boolean hold;
    private int hotkey = 7;
    
    public View(Clicker c) {
        super();
        this.c = c;
        
        try {
                GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
                System.err.println("There was a problem registering the native hook.");
                System.err.println(ex.getMessage());

                System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(this);
        GlobalScreen.addNativeMouseListener(this);
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {     
                JLabel rateLabel = new JLabel("Click rate (in milliseconds): ");
                rateLabel.setBounds(25, 25, 225, 30);
                
                JLabel varianceLabel = new JLabel("Click variance (in milliseconds): ");
                varianceLabel.setBounds(25, 75, 225, 30);
                
                rateTextField = new JTextField(Integer.toString(Clicker.DEFAULT_RATE), 5);
                rateTextField.setBounds(255, 25, 100, 30);
                
                varianceTextField = new JTextField(Integer.toString(Clicker.DEFAULT_VAR), 5);
                varianceTextField.setBounds(255, 75, 100, 30);
                
                leftRb = new JRadioButton("Left");
                rightRb = new JRadioButton("Right");
                leftRb.setBounds(25, 125, 80, 30);
                rightRb.setBounds(25, 150, 80, 30);
                ButtonGroup clickBtng = new ButtonGroup();
                clickBtng.add(leftRb);
                clickBtng.add(rightRb);
                leftRb.setSelected(true);
                
                holdRb = new JRadioButton("Hold");
                toggleRb = new JRadioButton("Toggle");
                holdRb.setBounds(120, 125, 80, 30);
                toggleRb.setBounds(120, 150, 80, 30);
                ButtonGroup modeBtng = new ButtonGroup();
                modeBtng.add(holdRb);
                modeBtng.add(toggleRb);
                holdRb.setSelected(true);
                
                JLabel hotkeyLabel = new JLabel("Start/Stop hotkey: ");
                hotkeyLabel.setBounds(225, 125, 150, 30);
                
                hotkeyCb = new JComboBox(Keycode.keys);
                hotkeyCb.setBounds(225, 156, 100, 20);
                hotkeyCb.setSelectedIndex(hotkey);
                
                playBtn = new JButton("Start (" + hotkeyCb.getSelectedItem() + ")");
                playBtn.setBounds(50, 200, 300, 30);
                
                leftRb.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (leftRb.isSelected()) {
                            c.mouse = InputEvent.BUTTON1_MASK;
                        }
                    }
                });
                
                rightRb.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (rightRb.isSelected()) {
                            c.mouse = InputEvent.BUTTON3_MASK;
                        }
                    }
                });
                
                hotkeyCb.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        hotkey = hotkeyCb.getSelectedIndex();
                        playBtn.setText("Start (" + hotkeyCb.getSelectedItem() + ")");
                    }
                });
                
                playBtn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnClicked();
                    }
                });
                
                add(rateLabel);
                add(varianceLabel);
                add(rateTextField);
                add(varianceTextField);
                add(holdRb);
                add(toggleRb);
                add(playBtn);
                add(hotkeyLabel);
                add(hotkeyCb);
                add(leftRb);
                add(rightRb);

                setTitle("Ultimate Auto Clicker");
                setSize(400, 300);
                setResizable(false);
                setAlwaysOnTop(true);
                setLayout(null);
                setVisible(true);
                
                addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        c.stop();
                        dispose();
                        System.exit(0);
                    }
                });
            }
        });
    }
    
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (hotkey < 60 &&((e.getKeyCode() == Keycode.codes[hotkey] && hold && !c.isRunning()) || (e.getKeyCode() == Keycode.codes[hotkey] && !hold))) {
            btnClicked();
        }
    }
    
    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        if (hotkey < 60 &&(e.getKeyCode() == Keycode.codes[hotkey] && hold)) {
            btnClicked();
        }
    }
    
    @Override
    public void nativeMousePressed(NativeMouseEvent e) {
        if (hotkey >= 60 &&((e.getButton() == Keycode.codes[hotkey] && hold && !c.isRunning()) || (e.getButton() == Keycode.codes[hotkey] && !hold))) {
            btnClicked();
        }
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
        if (hotkey >= 60 &&(e.getButton() == Keycode.codes[hotkey] && hold)) {
            btnClicked();
        }
    }
    
    public void btnClicked() {
        if (!c.isRunning()) {
            int rate = 0;
            int variance = 0;

            try {
                rate = Integer.parseInt(rateTextField.getText());
                variance = Integer.parseInt(varianceTextField.getText());
                hold = holdRb.isSelected();
            } catch (NumberFormatException err) {
                rateTextField.setText(Integer.toString(Clicker.DEFAULT_RATE));
                varianceTextField.setText(Integer.toString(Clicker.DEFAULT_RATE));
            } finally {
                rateTextField.setEnabled(false);
                varianceTextField.setEnabled(false);
                leftRb.setEnabled(false);
                rightRb.setEnabled(false);
                holdRb.setEnabled(false);
                toggleRb.setEnabled(false);
                hotkeyCb.setEnabled(false);
                playBtn.setText("Stop (" + hotkeyCb.getSelectedItem() + ")");
                c.setValues(rate, variance);
                c.start();
            }
        } else {
            c.stop();
            playBtn.setText("Start (" + hotkeyCb.getSelectedItem() + ")");
            rateTextField.setEnabled(true);
            varianceTextField.setEnabled(true);
            leftRb.setEnabled(true);
            rightRb.setEnabled(true);
            holdRb.setEnabled(true);
            toggleRb.setEnabled(true);
            hotkeyCb.setEnabled(true);
        }
    }
}
