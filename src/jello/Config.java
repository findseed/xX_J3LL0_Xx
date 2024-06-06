package jello;

import java.util.*;
import java.util.regex.*;

//just all ur :yawn: stuff :/
public class Config{

    //starting info settings
    public static int solver = 0;
    public static int mainDir = 1; //which way is forward

    public static float startPos = 0;
    public static float startSubPos = 0;
    public static float startSpeed = 0;
    public static float startGliderPos = 0;
    public static float targetPos = 69;

    //JelloGrab granularity stuffs
    public static float branchSpeedSize = 1.0f;
    public static float branchPosSize = .05f;
    //hmf
    public static float minSpeed = 40f;
    public static float maxSpeed = 100f;
    public static float playerLeniency = 0; //how far from perfect pos player can be
    public static float gliderLeniency = 1; //how far from perfect pos glider can be
    public static float branchTrimDist = 3; //how far behind best solution pos player can be
    public static boolean useRepeats = false; //surely useful :smile

    //general constants
    public static final float DELTA_TIME = 0.016666699201f;
    public static final float GLIDER_RADIUS = 13;
    public static final int PICKUP_LENGTH = 13;
    public static final int GRAB_LENGTH = 9;

    //regex 4 tas info. bizarre
    private static final Pattern posRegex = Pattern.compile("Pos:\\s*(-?\\d+\\.\\d+)");
    private static final Pattern speedRegex = Pattern.compile("Speed:\\s*(-?\\d+\\.\\d+)");
    private static final Pattern gliderRegex = Pattern.compile("Glider(?:\\[.*\\])?:\\s*(-?\\d+\\.\\d+)");


    //steal private info with just ez input
    public static void getUserInput(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("picks up right before a pickup animation...");
        System.out.println("player x exact position?");
        double rawPos = scanner.nextDouble();
        parseRawPos(rawPos);
        System.out.println("\nplayer x speed?");
        startSpeed = scanner.nextFloat();
        System.out.println("\ndropped glider x pos?");
        startGliderPos = scanner.nextFloat();
        System.out.println("\ntarget x pos?");
        targetPos = scanner.nextFloat();
        do {
            System.out.println("\nsolver?\n0 - Simple\n1 - Slow");
            solver = scanner.nextInt();
        }while (solver < 0 || solver > 1);
    }

    //determines which direction is forward...(i love math!!!)
    public static void initMainDir(){
        mainDir = targetPos > startPos ? 1 : -1;
    }

    //mainly just to slurp info from gui
    public static void updateStarts(double rawStartPos, float startSpeed, float startGliderPos, float targetPos, int solver){
        parseRawPos(rawStartPos);
        Config.startSpeed = startSpeed;
        Config.startGliderPos = startGliderPos;
        Config.targetPos = targetPos;
        Config.solver = solver;
        initMainDir();
    }

    private static void parseRawPos(double rawStartPos){
        startPos = (float)Math.rint(rawStartPos);
        startSubPos = (float)(rawStartPos - startPos);
    }

    //update our starts based on copied game info from celeste tas studio #V goodly made
    //only needs pos/speed/a clicked glider..
    //*************************************rounded values thingie might ???? idk
    public static void updateFromClipboard(){
        String clip = Jello.getClipboard();
        Matcher posMatch = posRegex.matcher(clip);
        Matcher speedMatch = speedRegex.matcher(clip);
        Matcher gliderMatch = gliderRegex.matcher(clip);
        if(posMatch.find()) {
            try {
                parseRawPos(Double.parseDouble(posMatch.group(1)));
            }catch (Exception e){
                Jello.printString("invalid player pos??");
            }
        }else
            Jello.printString("no copied player pos");
        if(speedMatch.find()) {
            try{
                startSpeed = Float.parseFloat(speedMatch.group(1));
            }catch (Exception e){
                Jello.printString("invalid player speed??");
            }
        }else
            Jello.printString("no copied player speed");
        if(gliderMatch.find()) {
            try{
                startGliderPos = (float)Math.rint(Double.parseDouble(gliderMatch.group(1)));
            }catch (Exception e){
                Jello.printString("invalid glider pos??");
            }
        }else
            Jello.printString("no copied glider pos");
    }

}