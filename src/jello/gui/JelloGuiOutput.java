package jello.gui;

import javax.swing.*;
import java.awt.*;

//right side that has the text output whatever cuz lazy surely this isnt super gross
class JelloGuiOutput extends JPanel{

    JScrollPane scroller = new JScrollPane();
    JTextArea output = new JTextArea("<- input info from glider drop frame(e.g. frame before a 13,g)\n");

    private GridBagConstraints c = new GridBagConstraints();
    public JelloGuiOutput() {

        setLayout(new GridBagLayout());
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.weightx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = GridBagConstraints.REMAINDER;
        setBackground(JelloGui.funColor);

        //output thingie
        JelloGui.formatComponent(output, false, true);
        output.setFont(JelloGui.cfont);
        output.setEditable(false);
        scroller.setViewportView(output);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(scroller, c);
    }
}