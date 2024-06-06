package jello.solute;

import java.util.*;
import jello.Config;
import jello.Seal;
import jello.Jello;

//primitive raw path finding thing to just try every 9f drop and treat them all as equals
//and also only keep the goods and un-keep the un-goods
public class StupidSolver{

    private static HashSet<JelloGrab> currentBranches;
    private static float farthestPos;
    private static int iteration;

    //put everything in the initial state to start iterating
    private static void init(){
        currentBranches = new HashSet<JelloGrab>();
        farthestPos = Config.startPos;
        iteration = 0;
        trackMovement(new JelloGrab(0, 0, null));
    }

    //main loop to start that goes through each drop
    public static void solve(){
        init();
        while(!currentBranches.isEmpty() && Config.mainDir * (Config.targetPos - farthestPos) > 0){ //stuff to stuff and not at target...
            advanceBranches();
            Jello.printString(String.format("finished drop %d..(%d branches - %.0f max)", iteration, currentBranches.size(), farthestPos));
            iteration++;
        }
        if(currentBranches.isEmpty())
            Jello.printString("ran out of branches :(");
        else{
            Jello.printString("path complete!");
            completeSolutions();
            printSolutions();
        }
    }

    //clean up the branches and progress/update them with the next layer
    private static void advanceBranches(){
        HashSet<JelloGrab> branches = currentBranches;
        currentBranches = new HashSet<JelloGrab>(branches.size());
        float f = farthestPos;
        for(JelloGrab j : branches)
            if(Config.mainDir * (f - j.pos) < Config.branchTrimDist)
                solveMovement(j);
    }

    //tries a bunch of(all) inputs for this graababababb and keeps what sticks :ls
    private static void solveMovement(JelloGrab j){
        iterateMovementTick(j, j.pos, j.subPos, j.speed, 0, 0);
    }


    //iterates through itself for each frame's movement
    private static void iterateMovementTick(JelloGrab j, float pos, float subPos, float speed, int frame, int inputs){
        frame++;
        //keep going with glider movement
        if(frame < Config.GRAB_LENGTH){
            for (int dir = 0; dir <= 1; dir++) {
                Seal.load(pos, subPos, speed);
                if(frame == 1) Seal.tickMovement(); //last grab frame...
                Seal.tickAirGlider(Config.mainDir * dir);
                if(trimTick())
                    continue;
                iterateMovementTick(j, Seal.pos, Seal.subPos, Seal.speed, frame, inputs | (dir << (frame - 1)));
            }
        }
        //last input frame! just air movement
        else{
            for (int dir = 0; dir <= 1; dir++) {
                Seal.lastDropPos = pos;
                Seal.load(pos, subPos, speed);
                Seal.tickAir(Config.mainDir * dir);
                if(trimTick())
                    continue;
                //save
                if(Config.mainDir * (Seal.pos - j.gliderPos) >= Config.GLIDER_RADIUS - Config.playerLeniency //player didn't undershoot(myb let first few undershoot?)
                        && Config.mainDir * (Seal.pos - j.gliderPos) <= Config.GLIDER_RADIUS //player didn't overshoot
                        && Config.mainDir * (Seal.lastDropPos - j.gliderPos) >= Config.GLIDER_RADIUS - Config.gliderLeniency) { //glider wasn't too far behind
                    trackMovement(new JelloGrab(inputs | (dir << (frame - 1)), frame, j));
                }
            }
        }
    }

    //takes our branches and trims them to the targetPos
    private static void completeSolutions(){
        HashSet<JelloGrab> branches = currentBranches;
        currentBranches = new HashSet<JelloGrab>(branches.size());
        for(JelloGrab j : branches){
            Seal.load(j.prev);
            Seal.tickMovement(); //grab...
            for (int f = 0; f < 16; f++) { //hold forward till we get there. i dont think going backwards will ever be faster lol
                Seal.tickAirGlider(Config.mainDir);
                if(Config.mainDir * (Config.targetPos - Seal.pos) <= 0){
                    JelloGrab j1 = new JelloGrab(Integer.MAX_VALUE, f + 1, j.prev);
                    j1.calcPath();
                    currentBranches.add(j1);
                    break; //aww done :3
                }
            }
        }
    }

    //gets and prints our best solution/branch n stuff
    private static void printSolutions(){
        //find the fastest one...
        JelloGrab fastest = new JelloGrab(0, Integer.MAX_VALUE, null); //lazy
        for(JelloGrab j : currentBranches){
            if((j.frames < fastest.frames)
                    || (j.frames == fastest.frames && Config.mainDir * j.speed > Config.mainDir * fastest.speed)){
                fastest = j;
            }
        }
        //actually print...
        if(!Jello.testOutputting){
            for (int i = 0; i < fastest.completePath.size(); i++) { //whole solution for fun
                JelloGrab j = fastest.completePath.get(i);
                Jello.printString(j.toString());
            }
            Jello.lastSolution = fastest;
            String formattedInputs = fastest.getFormattedPathInputs();
            Jello.printString(formattedInputs);
            Jello.setClipboard(formattedInputs);
            Jello.printString(String.format("hit %.3f pos %.3f spd in %d(%d drops + %d) w/ %s", fastest.getFullPos(), fastest.speed, fastest.getTotalFrames(), fastest.completePath.size(), fastest.frames, getOptimizationStats(fastest)));

        }else{ //bad!! no good!! alas! the lazy!
            int totalDrops = 0, goodDrops = 0; //movements where you drop the glider goidly
            for (JelloGrab j1 : fastest.completePath){
                totalDrops++;
                if(j1.gliderPos == j1.pos)
                    goodDrops++;
            }
            Jello.pp.printf("(%f,%f,%f,%d,%f)\n", (Config.startPos + Config.startSubPos), Config.startSpeed, Config.startGliderPos, fastest.getTotalFrames(), (float)goodDrops/totalDrops);
            Jello.pp.flush();
        }
    }


    //updates our yummies with yummy if it's yummy
    private static void trackMovement(JelloGrab j){
        if(currentBranches.add(j))
            if(Config.mainDir * (farthestPos - j.pos) < 0)
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
        for (JelloGrab j1 : j.completePath){
            totalDrops++;
            if(j1.gliderPos == j1.pos)
                goodDrops++;
        }
        return String.format("%d/%d good drops %s", goodDrops, totalDrops, ((float)goodDrops/totalDrops < .4f ? "(bad starting conditions?)" : ""));
    }

}