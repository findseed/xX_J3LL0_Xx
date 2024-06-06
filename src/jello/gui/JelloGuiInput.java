package jello.gui;

import javax.swing.*;
import java.awt.*;

//left side with all the input info
class JelloGuiInput extends JPanel{

    private GridBagConstraints c = new GridBagConstraints();

    JLabel[] inputLabels = new JLabel[]{
            new JLabel("player exact x pos:"),
            new JLabel("player x speed:"),
            new JLabel("last glider drop x pos:"),
            new JLabel("target x pos:")
    };

    JTextField[] inputs = new JTextField[]{
            new JTextField("0.0",24),
            new JTextField("0.0",24),
            new JTextField("0.0",24),
            new JTextField("0.0",24),
    };
    JButton copiedInfoButton = new JButton("update from clipboard");
    String copyInfoTip = "auto-input from default celeste studio's copied game info\n(ctrl left click the dropped glider to include it too)";
    JPanel solverPanel = new JPanel();
    JLabel solverLabel = new JLabel("solvers:");
    ButtonGroup solverButtonGroup = new ButtonGroup();
    JRadioButton[] solverButtons = new JRadioButton[]{
            new JRadioButton("simple", true),
            new JRadioButton("slow", false),
    };
    String[] solverTips = new String[]{
            "tries all 9f inputs for each drop, keeping the farthest ones",
            "tries all 9f and 10f inputs for each drop, keeping the farthest/fastest ones"
    };
    JButton advancedButton = new JButton("advanced settings");

    JRadioButton repeatButton = new JRadioButton("use repeats");
    String repeatButtonTip = "uses Repeat input commands if possible. kinda useless since solvers don't actively look for loops";

    JButton runButton = new JButton("  run  ");

    public JelloGuiInput(){
        setLayout(new GridBagLayout());
        setBackground(JelloGui.bgColor);
        c.insets = new Insets(5, 5, 5, 5);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.WEST;
        //inputs
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0;
        c.weightx = 1;
        for (int i = 0; i < inputLabels.length; i++) {
            JelloGui.formatComponent(inputLabels[i], false, false);
            JelloGui.formatComponent(inputs[i], false, false);
            add(inputLabels[i], c);
            add(inputs[i], c);
        }

        //!
        JelloGui.formatComponent(copiedInfoButton, false, true);
        add(copiedInfoButton, c);
        copiedInfoButton.setToolTipText(copyInfoTip);

        //solver select
        c.weighty = 0;
        c.weightx = 1;
        JelloGui.formatComponent(solverLabel, false, false);
        add(solverLabel, c);
        solverPanel.setLayout(new GridBagLayout());
        solverPanel.setBackground(JelloGui.bgColor);
        for (int i = 0; i < solverButtons.length; i++) {
            JelloGui.formatComponent(solverButtons[i], false, false);
            solverButtonGroup.add(solverButtons[i]);
            solverButtons[i].setToolTipText(solverTips[i]);
            solverPanel.add(solverButtons[i]);
        }
        //placeholder to push everything over
        solverPanel.add(new JLabel(), c);
        add(solverPanel, c);

        JelloGui.formatComponent(advancedButton, false, true);
        add(advancedButton, c);

        //extra options??
        JelloGui.formatComponent(repeatButton, false, false);
        repeatButton.setToolTipText(repeatButtonTip);
        add(repeatButton, c);

        //placeholder to push everything to the top
        c.weighty = 1.0;
        add(new JLabel(), c);
        //run button
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.CENTER;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;
        JelloGui.formatComponent(runButton, true, true);
        add(runButton, c);
    }

}