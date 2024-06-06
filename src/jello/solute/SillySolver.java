package jello.solute;

import java.util.*;
import jello.Config;
import jello.Seal;
import jello.Jello;

//very strict thingy to try 9f drops with some extra adjustments.
//hoping to be reasonably fast to run without costing results???
public class SillySolver{

    private static HashMap<JelloGrab, Cello> cellMap;
    private static ArrayList<Cello> cellList;
    private static ArrayList<Cello> completionList;

    private static float farthestPos;
    private static int iteration;

    //these idk mostly vibes and impatience
    private static final int MIN_FRAMES = Config.GRAB_LENGTH;
    public static int maxFrames = 10;
    public static int maxAdjustmens = 24;

    //put everything in the initial state to start iterating
    private static void init(){
        cellMap = new HashMap<JelloGrab, Cello>();
        cellList = new ArrayList<Cello>();
        completionList = new ArrayList<Cello>();
        farthestPos = Config.startPos;
        iteration = 0;
        trackMovement(new JelloGrab(0, 0, null), 0);
    }

    //main loop to start that goes through each drop
    public static void solve(){
        init();
        while(!cellList.isEmpty()){
            advanceBranches();
            Jello.printString(String.format("finished drop %d..(%d branches - %.0f max)", iteration, cellList.size(), farthestPos));
            iteration++;
        }
        if(completionList.isEmpty())
            Jello.printString("ran out of branches :(");
        else{
            Jello.printString("path complete!");
            completeSolutions();
            printSolutions();
        }
    }

    //ticks every branch state and deals with main trimming
    private static void advanceBranches(){
        cellMap.clear();
        ArrayList<Cello> cells = cellList;
        cellList = new ArrayList<Cello>(cells.size());

        //braindead lazy trimming for each score individually
        HashMap<Integer, Float> scoreFarthests = new HashMap<Integer, Float>();
        for(Cello c : cells)
            if(scoreFarthests.get(c.score) == null || Config.mainDir * (scoreFarthests.get(c.score) - c.cell.pos) < 0)
                scoreFarthests.put(c.score, c.cell.pos);

        //tells every cell to tick itself
        for(Cello c : cells){
            if(Config.mainDir * (Config.targetPos - c.cell.pos) <= 0) //hit target dont mess it up
                completionList.add(c);
            else{
                //only try if good
                if(Config.mainDir * (scoreFarthests.get(c.score) - c.cell.pos) < Config.branchTrimDist //not too far behind
                        && c.score - iteration * (Config.PICKUP_LENGTH + Config.GRAB_LENGTH) < maxAdjustmens) //anything more than minimum are adjustments
                    solveMovement(c);
            }
        }
    }

    //tries a bunch of inputs for this grab and keeps what sticks
    private static void solveMovement(Cello c){
        iterateMovementTick(c, c.cell.pos, c.cell.subPos, c.cell.speed, 0, 0);
    }

    //gentrified flavor of the deepest nest you've ever seen
    //iterates itself to search thru every possible movement combo between min and max frames frames lng
    private static void iterateMovementTick(Cello c, float pos, float subPos, float speed, int frame, int inputs){
        frame++;
        //try continuing with a glider tick(if not at max)
        if(frame < maxFrames){
            for (int dir = 0; dir <= 1; dir++) {
                Seal.load(pos, subPos, speed);
                if(frame == 1) Seal.tickMovement(); //last grab frame...
                Seal.tickAirGlider(Config.mainDir * dir);
                if(trimTick())
                    continue;
                iterateMovementTick(c, Seal.pos, Seal.subPos, Seal.speed, frame, inputs | (dir << (frame - 1)));
            }
        }
        //try terminating with a air tick(if past min)
        if(frame >= MIN_FRAMES){
            for (int dir = 0; dir <= 1; dir++) {
                Seal.lastDropPos = pos;
                Seal.load(pos, subPos, speed);
                Seal.tickAir(Config.mainDir * dir);
                if(trimTick())
                    continue;
                //save
                if(Config.mainDir * (Seal.pos - c.cell.gliderPos) >= Config.GLIDER_RADIUS - Config.playerLeniency //player didn't undershoot
                        && Config.mainDir * (Seal.pos - c.cell.gliderPos) <= Config.GLIDER_RADIUS //player didn't overshoot
                        && Config.mainDir * (Seal.lastDropPos - c.cell.gliderPos) >= Config.GLIDER_RADIUS - Config.gliderLeniency) { //glider wasn't too far behind
                    trackMovement(new JelloGrab(inputs | (dir << (frame - 1)), frame, c.cell), c.score + Config.PICKUP_LENGTH + frame);
                }
            }
        }
    }

    //takes our solution list and optimizes/trims them to the targetPos
    private static void completeSolutions(){
        ArrayList<Cello> cells = completionList;
        completionList = new ArrayList<Cello>(cells.size());
        for(Cello c : cells){
            Seal.load(c.cell.prev);
            Seal.tickMovement(); //grab...
            for (int f = 0; f < 16; f++) { //hold forward till we get there. i dont think going backwards will ever be faster lol
                Seal.tickAirGlider(Config.mainDir);
                if(Config.mainDir * (Config.targetPos - Seal.pos) <= 0){
                    Cello c1 = new Cello(Integer.MAX_VALUE, f + 1, c.cell.prev, c.score - c.cell.frames + f + 1);
                    c1.cell.calcPath();
                    completionList.add(c1);
                    break; //aww done :3
                }
            }
        }
    }

    //find the imposter and eject them
    private static void printSolutions(){
        Cello fastest = new Cello(0, 0, null, Integer.MAX_VALUE);
        int fastestAdjustments = 0;
        for(Cello c : completionList){ //find fastest/fastest completeion
            int cAdjustments = c.score - c.cell.completePath.size() * (Config.PICKUP_LENGTH + Config.GRAB_LENGTH);
            if(c.score < fastest.score
                    || (c.score == fastest.score && (cAdjustments < fastestAdjustments
                        || (cAdjustments == fastestAdjustments && Config.mainDir * c.cell.speed > Config.mainDir * fastest.cell.speed)))){
                fastest = c;
                fastestAdjustments = cAdjustments;
            }
        }
        //actually print...
        if(!Jello.testOutputting){
            for (int i = 0; i < fastest.cell.completePath.size(); i++) { //whole solution for fun
                JelloGrab j = fastest.cell.completePath.get(i);
                Jello.printString(j.toString());
            }
            Jello.lastSolution = fastest.cell;
            String formattedInputs = fastest.cell.getFormattedPathInputs();
            Jello.printString(formattedInputs);
            Jello.setClipboard(formattedInputs);
            Jello.printString(String.format("hit %.3f pos %.3f spd in %df(%d drops + %d : %dsc) w/ %s", fastest.cell.getFullPos(), fastest.cell.speed, fastest.cell.getTotalFrames(), fastest.cell.completePath.size(), fastest.cell.frames, fastest.score, getOptimizationStats(fastest.cell)));

        }else{ //bad!! no good!! alas! the lazy!
            int totalDrops = 0, goodDrops = 0; //movements where you drop the glider goidly
            int extraFrames = 0;
            for (JelloGrab j1 : fastest.cell.completePath){
                totalDrops++;
                if(j1.gliderPos == j1.pos)
                    goodDrops++;
                if(j1.frames > MIN_FRAMES){
                    extraFrames++;
                }
            }
            Jello.pp.printf("(%f,%f,%f,%d,%f,%d)\n", (Config.startPos + Config.startSubPos), Config.startSpeed, Config.startGliderPos, fastest.cell.getTotalFrames(), (float)goodDrops/totalDrops, extraFrames);
            Jello.pp.flush();
        }
    }


    //store this drop movement if its good/better than anything we have
    private static void trackMovement(JelloGrab j, int score){
        Cello c = cellMap.get(j);
        if(c == null){
            c = new Cello(j, score);
            cellList.add(c);
            cellMap.put(j, c);
        } else if (score < c.score) {
            c.score = score;
            c.cell = j;
        }else
            return;
        if(Config.mainDir * (j.pos - farthestPos) > 0)
            farthestPos = j.pos;
    }

    //tick by tick bounds
    //true if it should be yoinked
    private static boolean trimTick(){
        return iteration > 2 && (Config.mainDir * Seal.speed < Config.minSpeed ||  Config.mainDir * Seal.speed > Config.maxSpeed);
    }

    //info on how well we did
    //how many good drops we did, how many adjustments, etc
    private static String getOptimizationStats(JelloGrab j){
        int totalDrops = 0, goodDrops = 0; //movements where you drop the glider goidly
        int extraFrames = 0, extraFrameCount = 0; //just adjustments
        for (JelloGrab j1 : j.completePath){
            totalDrops++;
            if(j1.gliderPos == j1.pos)
                goodDrops++;
            if(j1.frames > MIN_FRAMES){
                extraFrames++;
                extraFrameCount+= j1.frames - MIN_FRAMES;
            }
        }
        return String.format("%d/%d good drops & %d extra adjustments(%df)", goodDrops, totalDrops, extraFrames, extraFrameCount);
    }

}