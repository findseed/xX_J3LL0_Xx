package jello.solute;

import java.util.*;
import jello.Config;
import jello.Seal;

//keeps info about 1 grab movement branch node thingie
public class JelloGrab{

    public final int frames; //how many frames of input does it store
    public final int inputs; //for a frame f, if this & (1 << f) == 0 then hold neutral, otherwise hold forward
    public final JelloGrab prev;

    public final float pos;
    public final float subPos;
    public final float speed;
    public final float gliderPos;

    //idk
    public final int posID;
    public final int speedID;
    private final int hashcode;

    //just for easy access in the end
    ArrayList<JelloGrab> completePath;

    JelloGrab(float pos, float subPos, float speed, float gliderPos, int inputs, int frames, JelloGrab prev) {
        this.pos = pos;
        this.subPos = subPos;
        this.speed = speed;
        this.gliderPos = gliderPos;
        this.inputs = inputs;
        this.frames = frames;
        this.prev = prev;
        //me when i make up stuff that is worse than not doing stuff
        this.posID = (int)(Config.mainDir * (getFullPos() - Config.startPos) / Config.branchPosSize);
        this.speedID = Math.abs((int)(speed / Config.branchSpeedSize));
        hashcode = (posID << 10) ^ speedID;
    }

    JelloGrab(int inputs, int frames, JelloGrab prev){
        this(Seal.pos, Seal.subPos, Seal.speed, Seal.lastDropPos, inputs, frames, prev);
    }

    public float getFullPos(){
        return pos + subPos;
    }

    //populate your future(path) with all that came before(nodes)
    public void calcPath(){
        JelloGrab j = this;
        completePath = new ArrayList<>();
        while(j.prev != null){
            completePath.add(0, j);
            j = j.prev;
        }
    }

    //other path dependent things..

    public int getTotalFrames(){
        int time = 0;
        for(JelloGrab j : completePath)
            time += Config.PICKUP_LENGTH + j.frames;
        return time;
    }

    //shortcut to decide whether a JelloGrab's has the same input/length as another
    //if i ever actually consider looping prolly make this trim inputs to frames before checking in case its Loung
    private boolean getInputsEqual(JelloGrab j){
        return j.inputs == this.inputs && j.frames == this.frames;
    }

    //-------find any repeating inputs for Repat,X EndRepeat commands
    //(this v gross but it was the best my brain could put in english and it doesnt matter anyway. so Elle im afraid)
    private ArrayList<int[]> getRepeats(){
        //each repeatList index holds 1 repeat(in order). [0] is start index. [1] is loop count. [2] is 1st loop end index. [3] is entire loop's end index
        ArrayList<int[]> repeatList = new ArrayList<int[]>();
        ls: for(int in0 = 0; in0 < completePath.size(); in0++){
            int maxLoop = (completePath.size() - in0) / 2; //max repeat length. maybe limit this to something
            for(int loopLength = 1; loopLength <= maxLoop; loopLength++){
                int loopCount = 1;
                looper: for(;;loopCount++){ //how many times does it loop(if at all?(looping 1nce is just. the thing 1nce))
                    for (int inLoop = 0; inLoop < loopLength; inLoop++) { //check if contents are equal
                        int lid = in0 + (loopLength * loopCount) + inLoop; //current loop index
                        if(lid >= completePath.size() || !completePath.get(in0 + inLoop).getInputsEqual(completePath.get(lid)))
                            break looper; //contents did not match. loop K1LL3D>>>>
                    }
                }
                if(loopCount > 1){ //looped at least once! from in0 to in0 + loopLength - 1 loopCount times
                    repeatList.add(new int[]{in0, loopCount, in0 + loopLength - 1, in0 + (loopCount * loopLength)});
                    in0+= loopCount * loopLength; //skip over what we just looped
                    continue ls;
                }
            }
        }
        return repeatList;
    }

    //gets allll the inputs semi-formatted enough so they kinda work
    //this is terribly gross but it doesn't matter ig
    ///can/ do repeats but really shouldn't if the alg doesn't look for loops
    public String getFormattedPathInputs(){
        ArrayList<int[]> repeatList = Config.useRepeats ? getRepeats() : new ArrayList<int[]>();
        int repeatIndex = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < completePath.size(); i++) {
            JelloGrab j = completePath.get(i);
            if(repeatIndex < repeatList.size() && repeatList.get(repeatIndex)[0] == i) //repeat start matches. add swag
                sb.append("Repeat,").append(repeatList.get(repeatIndex)[1]).append('\n');
            int frameCount = 1, heldInput = (j.inputs & 1) * Config.mainDir; //really bad input combining
            boolean end = i == completePath.size() - 1; //is this the last grab(no drop)
            sb.append(Config.PICKUP_LENGTH + ",G\n"); //pickup state
            for (int f = 1; f <= j.frames; f++) { //each input(+ 1 extra to clean up)
                int dir = (j.inputs & (1 << f)) != 0 ? Config.mainDir : 0;
                if(f == j.frames) dir = 69;//janky clean up
                if(heldInput == dir && !(f == j.frames - 1 && !end)) //combine same + auto separate for drop frame
                    frameCount++;
                else{
                    sb.append(frameCount);
                    if(heldInput == 1)
                        sb.append(",R");
                    if(heldInput == -1)
                        sb.append(",L");
                    if(f == j.frames && !end) //drop frame son or grab frame daughter
                        sb.append(",D\n");
                    else
                        sb.append(",G\n");
                    heldInput = dir; //reset combine stufff
                    frameCount = 1;
                }
            }
            if(repeatIndex < repeatList.size() && repeatList.get(repeatIndex)[2] == i) { //repeat End matches. add unswag
                sb.append("EndRepeat\n");
                //skip to end of repeat
                i = repeatList.get(repeatIndex)[3] - 1;
                repeatIndex++;
            }
        }
        return sb.toString();
    }

    public int getHashcode(){
        return hashcode;
    }

    //?
    @Override
    public boolean equals(Object o){
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;
        JelloGrab j = (JelloGrab) o;
        return this.posID == j.posID && this.speedID == j.speedID;
    }

    @Override
    public int hashCode(){
        return hashcode;
    }

    @Override
    public String toString(){
        return String.format("x %.1f.%.3f(%d)) spd %.3f(%d) glider %.1f(%.0f) input %s(%d).%d", pos, subPos, posID, speed, speedID, gliderPos, pos - gliderPos, Integer.toBinaryString(inputs), frames, completePath != null ? completePath.size() : 0);
    }

}