package jello;

import jello.solute.JelloGrab;

//do the Celeste logic stuffsss
//def doesnt need to be float precise(or even have limits over min/max speed?) but oh well its what i have on hand
public class Seal{

    public static float pos;
    public static float subPos;
    public static float speed;
    public static float lastDropPos;


    public static void init(){
        pos = Config.startPos;
        subPos = Config.startSubPos;
        speed = Config.startSpeed;
        lastDropPos = Config.startGliderPos;
    }

    public static void load(JelloGrab j){
        pos = j.pos;
        subPos = j.subPos;
        speed = j.speed;
    }

    public static void load(float pos, float subPos, float speed){
        Seal.pos = pos;
        Seal.subPos = subPos;
        Seal.speed = speed;
    }

    //general celeste physics methods
    //air speed physics
    public static void tickAir(int dir){
        float accel;
        float targetSpeed = dir * 90f;
        if(Math.abs(speed) > 90f && sign(speed) == dir)
            accel = (float)(400.0 * 0.65f * Config.DELTA_TIME);
        else
            accel = (float)(1000.0 * 0.65f * Config.DELTA_TIME);
        if(speed <= targetSpeed)
            speed = Math.min((speed + accel), targetSpeed);
        else
            speed = Math.max((speed - accel), targetSpeed);
        tickMovement();
    }
    //air speed physics when holding glider
    public static void tickAirGlider(int dir){
        float accel;
        float targetSpeed = dir * 108.00001f;
        if(Math.abs(speed) > 108.00001f && sign(speed) == dir)
            accel = (float)(400.0 * (0.65f * .5f) * Config.DELTA_TIME);
        else
            accel = (float)(1000.0 * (0.65f * .5f) * Config.DELTA_TIME);
        if(speed <= targetSpeed)
            speed = Math.min((speed + accel), targetSpeed);
        else
            speed = Math.max((speed - accel), targetSpeed);
        tickMovement();
    }

    //actual move by speed logic no collision
    public static void tickMovement(){
        subPos+= (double)(float)(speed * (double)Config.DELTA_TIME);
        float fullMove = (float)Math.rint(subPos);
        subPos-= fullMove;
        pos+= fullMove;
    }

    //default friendlies for love and compassion
    private static int sign(float d){
        if (d == 0.0)
            return 0;
        return d > 0.0 ? 1 : -1;
    }
    public static int ceil(float f) {
        int i = (int)f;
        return f > (float)i ? i + 1 : i;
    }
}