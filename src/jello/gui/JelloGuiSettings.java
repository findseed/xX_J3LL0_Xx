package jello.gui;

import jello.Config;
import jello.Jello;
import jello.solute.SillySolver;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//exta window for AdvANcEd SEtTinGs
public class JelloGuiSettings extends JFrame {

    private GridBagConstraints c = new GridBagConstraints();

    JLabel title = new JLabel("shared branch settings---");
    JPanel settings0Panel = new JPanel();
    JLabel[] settings0Labels = new JLabel[]{
            new JLabel("cell pos size:"),
            new JLabel("cell speed size:"),
    };
    JTextField[] settings0inputs = new JTextField[]{
            new JTextField("0.0",24),
            new JTextField("0.0",24),
    };
    String[] settings0Tips = new String[]{
            "smallest difference in pos tracked(smaller=more branches)",
            "smallest difference in speed tracked(smaller=more branches)",
    };

    JLabel sillyTitle = new JLabel("slow specific settings---");
    JPanel settings1Panel = new JPanel();
    JLabel[] settings1Labels = new JLabel[]{
            new JLabel("extra adjustment frames:"),
            new JLabel("max total adjustment frames:"),
    };
    JTextField[] settings1inputs = new JTextField[]{
            new JTextField("0.0",24),
            new JTextField("0.0",24),
    };
    String[] settings1Tips = new String[]{
            "max possible extra adjustment frames per drop(bigger = more compute&branches)",
            "max total adjustment frames allowed(bigger = more branches)",
    };

    JLabel scaryTitle = new JLabel("!!!scary settings+++");
    JPanel settings2Panel = new JPanel();
    JLabel[] settings2Labels = new JLabel[]{
            new JLabel("min speed:"),
            new JLabel("max speed:"),
            new JLabel("player leniency:"),
            new JLabel("glider leniency:"),
            new JLabel("trim distance:"),
    };
    JTextField[] settings2inputs = new JTextField[]{
            new JTextField("0.0",24),
            new JTextField("0.0",24),
            new JTextField("0.0",24),
            new JTextField("0.0",24),
            new JTextField("0.0",24),
    };
    String[] settings2Tips = new String[]{
            "lowest absolute speed before ignoring a branch",
            "highest absolute speed before ignoring a branch",
            "how suboptimal player pos can be before ignoring",
            "how unoptimal glider drop pos can be before ignoring",
            "how far behind a branch can be before ignoring",
    };

    JButton cancelButton = new JButton("cancel eminem");
    JButton confirmButton = new JButton("  confirm  ");

    public JelloGuiSettings() {
        setSize(500, 500);
        setLocationRelativeTo(Jello.gui);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setAlwaysOnTop(true);
        requestFocus();
        getRootPane().setBorder(new MatteBorder(3, 3, 3, 3, JelloGui.funColor));

        getContentPane().setBackground(JelloGui.bgColor);
        setLayout(new GridBagLayout());
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;

        constructSettingsPanel(title, settings0Panel, settings0Labels, settings0inputs, settings0Tips);
        constructSettingsPanel(sillyTitle, settings1Panel, settings1Labels, settings1inputs, settings1Tips);
        constructSettingsPanel(scaryTitle, settings2Panel, settings2Labels, settings2inputs, settings2Tips);


        //placeholder to push everything to the top
        c.gridx = 0;
        c.weighty = 1;
        c.gridwidth = 2;
        add(new JLabel(), c);

        //confirm/whatever idek man
        c.gridwidth = 1;
        c.gridx = 0;
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        JelloGui.formatComponent(confirmButton, true, true);
        JelloGui.formatComponent(cancelButton, true, true);
        add(cancelButton, c);
        c.gridx = 1;
        add(confirmButton, c);

        initListeners();
        updateValues();
        setVisible(true);
    }

    //every settings section is the same might as well automate it
    private void constructSettingsPanel(JLabel title, JPanel panel, JLabel[] labels, JTextField[] inputs, String[] tips){
        //settings 0
        c.fill = GridBagConstraints.NONE;
        c.weightx = 1;
        c.gridwidth = 2;
        JelloGui.formatComponent(title, true, false);
        add(title, c);
        panel.setLayout(new GridLayout(inputs.length, 2));
        JelloGui.formatComponent(panel, false, false);
        for (int i = 0; i < inputs.length; i++) {
            JelloGui.formatComponent(labels[i], false, false);
            JelloGui.formatComponent(inputs[i], false, false);
            labels[i].setToolTipText(tips[i]);
            panel.add(labels[i], c);
            panel.add(inputs[i], c);
        }
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.weightx = 1;
        add(panel, c);
    }

    private void initListeners(){
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        cancelButton.addActionListener(e -> onCancel());
        confirmButton.addActionListener(e -> onConfirm());
    }

    private void onCancel(){
        Jello.gui.setScaryEnable(true);
        Jello.printString("discarding settings");
        dispose();
    }

    private void onConfirm(){
        Jello.gui.setScaryEnable(true);
        saveValues();
        Jello.printString("saving settings");
        dispose();
    }

    //god save me
    public float getBranchPosSize(boolean silent){
        try{
            return Float.parseFloat(settings0inputs[0].getText());
        }catch (Exception e){
            if(!silent)
                Jello.printString("bad cell pos size input");
            return Config.branchPosSize;
        }
    }
    public float getBranchSpeedSize(boolean silent){
        try{
            return Float.parseFloat(settings0inputs[1].getText());
        }catch (Exception e){
            if(!silent)
                Jello.printString("bad cell pos size input");
            return Config.branchSpeedSize;
        }
    }

    public int getSillyMaxFrames(boolean silent){
        try{
            return Integer.parseInt(settings1inputs[0].getText()) + Config.GRAB_LENGTH;
        }catch (Exception e){
            if(!silent)
                Jello.printString("bad adjustment frames input");
            return SillySolver.maxFrames;
        }
    }
    public int getSillyMaxAdjustments(boolean silent){
        try{
            return Integer.parseInt(settings1inputs[1].getText());
        }catch (Exception e){
            if(!silent)
                Jello.printString("bad max total adjustment input");
            return SillySolver.maxAdjustmens;
        }
    }



    public float getMinSpeed(boolean silent){
        try{
            return Float.parseFloat(settings2inputs[0].getText());
        }catch (Exception e){
            if(!silent)
                Jello.printString("bad min speed input");
            return Config.minSpeed;
        }
    }
    public float getMaxSpeed(boolean silent){
        try{
            return Float.parseFloat(settings2inputs[1].getText());
        }catch (Exception e){
            if(!silent)
                Jello.printString("bad max speed input");
            return Config.maxSpeed;
        }
    }
    public float getPlayerLeniency(boolean silent){
        try{
            return Integer.parseInt(settings2inputs[2].getText());
        }catch (Exception e){
            if(!silent)
                Jello.printString("bad player leniency input");
            return Config.playerLeniency;
        }
    }
    public float getGliderLeniency(boolean silent){
        try{
            return Integer.parseInt(settings2inputs[3].getText());
        }catch (Exception e){
            if(!silent)
                Jello.printString("bad glider leniency input");
            return Config.gliderLeniency;
        }
    }
    public float getBranchTrimDist(boolean silent){
        try{
            return Integer.parseInt(settings2inputs[4].getText());
        }catch (Exception e){
            if(!silent)
                Jello.printString("bad trim dist input");
            return Config.branchTrimDist;
        }
    }



    //updates all our text inputs with the actual values
    private void updateValues(){
        //gen
        settings0inputs[0].setText(Float.toString(Config.branchPosSize));
        settings0inputs[1].setText(Float.toString(Config.branchSpeedSize));
        //silly
        settings1inputs[0].setText(Integer.toString(SillySolver.maxFrames - Config.GRAB_LENGTH));
        settings1inputs[1].setText(Integer.toString(SillySolver.maxAdjustmens));
        //dont touches
        settings2inputs[0].setText(Float.toString(Config.minSpeed));
        settings2inputs[1].setText(Float.toString(Config.maxSpeed));
        settings2inputs[2].setText(Integer.toString((int)Config.playerLeniency));
        settings2inputs[3].setText(Integer.toString((int)Config.gliderLeniency));
        settings2inputs[4].setText(Integer.toString((int)Config.branchTrimDist));
    }

    //saves all the text inputs to the actual values
    private void saveValues(){
        Config.branchPosSize = getBranchPosSize(false);
        Config.branchSpeedSize = getBranchSpeedSize(false);
        SillySolver.maxFrames = getSillyMaxFrames(false);
        SillySolver.maxAdjustmens = getSillyMaxAdjustments(false);
        Config.minSpeed = getMinSpeed(false);
        Config.maxSpeed = getMaxSpeed(false);
        Config.playerLeniency = getPlayerLeniency(false);
        Config.gliderLeniency = getGliderLeniency(false);
        Config.branchTrimDist = getBranchTrimDist(false);
    }

}
