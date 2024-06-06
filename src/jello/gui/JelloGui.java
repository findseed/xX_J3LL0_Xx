package jello.gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.*;
import java.awt.*;
import jello.Config;
import jello.Jello;

//main gui thing. i think. idk ive never guid before
public class JelloGui extends JFrame {

    static final Color bgColor = Color.black;
    static final Color fgColor = Color.cyan;
    static final Color fontColor = Color.white;
    static final Color fontColor2 = Color.black;
    static final Color funColor = Color.lightGray;
    static final Font dfont = new Font("Comic Sans MS", Font.BOLD, 14);
    static final Font bfont = new Font("Comic Sans MS", Font.BOLD, 20);
    static final Font cfont = new Font("Comic Sans MS", Font.BOLD, 13);
    private final JelloGuiInput left = new JelloGuiInput();
    private final JelloGuiOutput right = new JelloGuiOutput();

    public JelloGui() {
        setTitle("xX_J3LL0_Xx");
        setSize(800, 690);
        setupActions();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(bgColor);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        splitPane.setDividerSize(3);
        add(splitPane);
        updateStartsFromConfig();
        setVisible(true);
    }

    //starts the solver and disables scary stuff so i dont mess anything up
    private void onRunButton(){
        setScaryEnable(false);
        clearOutput();
        Config.updateStarts(getRawPlayerStartPos(false), getPlayerStartSpeed(false), getGliderStartPos(false), getTargetPos(false), getSolver());
        updateStartsFromConfig();
        new Thread(() -> Jello.runSolver()).start();
    }

    //update everything based on copied tas info
    private void onInfoButton(){
        Config.updateStarts(getRawPlayerStartPos(true), getPlayerStartSpeed(true), getGliderStartPos(true), getTargetPos(true), getSolver());
        Config.updateFromClipboard();
        updateStartsFromConfig();
        Jello.printString("thxxx");
    }

    //whether to use repeats. reformats everything if necessary
    //i dont think this will do much but have to try to find out ig
    private void onUseRepeats(){
        Config.useRepeats = left.repeatButton.isSelected();
        if(Jello.lastSolution != null) {
            clearOutput();
            String formattedInputs = Jello.lastSolution.getFormattedPathInputs();
            Jello.printString(formattedInputs);
            Jello.setClipboard(formattedInputs);
            Jello.printString(String.format("reformatted inputs with%s repeats", Config.useRepeats ? "" : "out"));
        }
    }

    //opens extra advanced settings window
    private void onOpenSettings(){
        setScaryEnable(false);
        new JelloGuiSettings();
    }

    //disable buttons n stuff so nothing gets messed with during alg/whatever
    public void setScaryEnable(boolean enable){
        left.runButton.setEnabled(enable);
        left.copiedInfoButton.setEnabled(enable);
        left.repeatButton.setEnabled(enable);
        left.advancedButton.setEnabled(enable);
        for(JRadioButton j : left.solverButtons){
            j.setEnabled(enable);
        }
    }

    //get info from the inputs::::

    public double getRawPlayerStartPos(boolean silent){
        try{
            return Double.parseDouble(left.inputs[0].getText());
        }catch (Exception e){
            if(!silent)
                Jello.printString("bad player pos input");
            return 0.0f;
        }
    }

    public float getPlayerStartSpeed(boolean silent){
        try{
            return Float.parseFloat(left.inputs[1].getText());
        }catch (Exception e){
            if(!silent)
                Jello.printString("bad player speed input");
            return 0.0f;
        }
    }

    public float getGliderStartPos(boolean silent){
        try{
            float glider = Float.parseFloat(left.inputs[2].getText());
            if((int)glider != glider){
                if(!silent)
                    Jello.printString("{!}converting non-integer glider pos...");
                return (float)Math.rint(glider);
            }
            return glider;
        }catch (Exception e){
            if(!silent)
                Jello.printString("bad glider pos input");
            return 0.0f;
        }
    }

    public float getTargetPos(boolean silent){
        try{
            return Float.parseFloat(left.inputs[3].getText());
        }catch (Exception e){
            if(!silent)
                Jello.printString("bad target pos input");
            return 0.0f;
        }
    }

    public int getSolver(){
        for (int i = 0; i < left.solverButtons.length; i++) {
            if(left.solverButtons[i].isSelected())
                return i;
        }
        return 0;
    }

    //update gui inputs based on actual values
    public void updateStartsFromConfig(){
        //pos
        left.inputs[0].setText(Double.toString((double)Config.startPos + Config.startSubPos));
        //speed
        left.inputs[1].setText(Float.toString(Config.startSpeed));
        //glider
        left.inputs[2].setText(Float.toString(Config.startGliderPos));
        //target
        left.inputs[3].setText(Float.toString(Config.targetPos));
    }

    //init buttons
    public void setupActions(){
        left.runButton.addActionListener(e -> onRunButton());
        left.copiedInfoButton.addActionListener(e -> onInfoButton());
        left.repeatButton.addActionListener(e -> onUseRepeats());
        left.advancedButton.addActionListener(e -> onOpenSettings());
    }

    //puts string onto right's text output
    public void printToOutput(String s){
        right.output.append(s + "\n");
        right.output.setCaretPosition(right.output.getDocument().getLength());
    }

    public void clearOutput(){
        right.output.setText("");
    }
    
    private static void formatComponent(JComponent j, Font f, Color fg, Color bg){
        j.setFont(f);
        j.setForeground(fg);
        j.setBackground(bg);
    }

    public static void formatComponent(JComponent j, boolean bigFont, boolean fun){
        formatComponent(j, bigFont ? bfont : dfont, fun ? fontColor2 : fontColor, fun ? funColor :bgColor);
    }

    public static void updateManagers(){
        UIManager.put("TextField.selectionBackground", new ColorUIResource(fgColor));
        UIManager.put("TextField.caretForeground", new ColorUIResource(fontColor));
        UIManager.put("TextArea.selectionBackground", new ColorUIResource(fgColor));
        UIManager.put("Button.select", new ColorUIResource(fgColor));
        UIManager.put("Button.focus", new ColorUIResource(bgColor));
        UIManager.put("Button.border", BorderFactory.createLineBorder(fontColor));
        UIManager.put("RadioButton.select", new ColorUIResource(Color.blue));
        UIManager.put("RadioButton.focus", new ColorUIResource(fgColor));
        UIManager.put("ToolTip.background", new ColorUIResource(bgColor));
        UIManager.put("ToolTip.foreground", new ColorUIResource(fgColor));
        UIManager.put("ToolTip.border", new LineBorder(fontColor));
        UIManager.put("ToolTip.font", dfont);
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
    }

}