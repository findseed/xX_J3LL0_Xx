package jello;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import jello.gui.JelloGui;
import jello.solute.*;
public class Jello{

    public static JelloGui gui = null;
    public static JelloGrab lastSolution;

    public static void main(String[] args) {
        if(testOutputting)
            startTestIterations();
        else{
            if(args.length == 0){
                JelloGui.updateManagers();
                SwingUtilities.invokeLater(() -> {
                    gui = new JelloGui();
                });
            } else if (args[0].equals("nogui")) {
                Config.getUserInput();
                runSolver();
            }
        }
    }

    //starts the actual algorithm n stuff. ran on a diff thing from jftnalvosek
    public static void runSolver(){
        try {
            lastSolution = null;
            Seal.init();
            Config.initMainDir();
            Jello.printString(String.format("starting solver %d from %.3f x %.3f spd %.0f glider to %.3f. forward: %d", Config.solver, (Config.startPos + Config.startSubPos), Config.startSpeed, Config.startGliderPos, Config.targetPos, Config.mainDir));
            switch (Config.solver) {
                case 0 -> StupidSolver.solve();
                case 1 -> SillySolver.solve();
            }
        }catch(Exception e){
            printString(String.format("error in solver thread i fear.,,\n(%s)", e));
        }
        if (gui != null) {
            SwingUtilities.invokeLater(() -> {
                gui.setScaryEnable(true);
            });
        }
    }

    //just copy the string to clipboard for quick easy and free: pouring riv
    private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    public static void setClipboard(String string){
        clipboard.setContents(new StringSelection(string),null);
        printString("clipboard updated ++");
    }
    public static String getClipboard(){
        printString("getting clipboard --");
        try {
            return (String) clipboard.getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IllegalStateException | IOException ignored) {
            printString("clipboard has bad vibes sry");
            return "";
        }
    }

    //sends string to system out and the gui if possible
    public static void printString(String string){
        System.out.println(string);
        if(gui != null) gui.printToOutput(string);
    }




    public static final boolean testOutputting = false;
    public static PrintWriter pp;
    private static float[] speeds = new float[]{86.66683f, 43.333412f, 65.000114f, 48.750088f, 97.50019f, 54.166763f, 59.58344f, 70.416794f, 75.83347f, 81.25015f, 92.08351f, 102.91687f,/*84.58332f,32.16652f,86.33329f,59.249897f,57.49993f,15.916487f,52.083256f,97.16665f,16.25003f,32.50006f,27.083384f,10.499809f,37.583195f,5.0831323f,37.916737f,68.33328f,70.08325f,46.66658f,90.0f,35.83323f,41.249905f,5.416676f,21.666708f,3.3331647f,10.833353f,64.66657f,48.416546f,91.74997f,79.16664f,8.749842f,106.25004f,108.00001f,62.916607f,75.49993f,100.83336f,102.58333f,73.74996f,95.41668f,30.416552f,80.91661f,53.83322f,14.166519f,42.99987f,26.749842f,21.333164f,24.999874f,19.583197f,55.250114f,98.58354f,17.33338f,39.000088f,104.00022f,93.16686f,28.166735f,71.500145f,1.0833483f,82.3335f,6.5000253f,22.750057f,33.583412f,11.916702f,76.916824f,44.416763f,49.83344f,87.75018f,66.083466f,60.66679f*/};
    private static void startTestIterations(){
        float startx = 0;
        Scanner scanner = new Scanner(System.in);
        startx = scanner.nextFloat();
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter("sillyEX" + startx + ".txt", true);
        } catch (IOException e) {
            System.out.println("common L");
            return;
        }
        pp = new PrintWriter(fileWriter);
        System.out.println(startx);
        float endx = startx + .2f - 0.001f;
        for (int gdrop = 8; gdrop <= 10; gdrop++) {
            for (float x = startx; x < endx; x+= 0.05f) {
                System.out.println(x + "  " + endx);
                //for(float s = 0; s < 110; s+= 1){
                for(float s : speeds){
                    Config.startPos = 10;
                    Config.startSubPos = x;
                    Config.startSpeed = s;
                    Config.startGliderPos = gdrop;
                    Config.targetPos = 1010;
                    Seal.init();
                    Config.initMainDir();
                    SillySolver.solve();
                }
            }
        }
        pp.close();
    }

}