package aferrer.vectorRace;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by AFerrer on 19/01/2015.
 */

public class Car {
    public String mParticipantId;
    public String mColor;
    public ArrayList<Integer> x;
    public ArrayList<Integer> y;

    //not included in JSONObject data
    private Boolean mFutureValid;
    private Integer mFutureX;
    private Integer mFutureY;

    public Car(String participantId, String color, int startX, int startY){

        mParticipantId = participantId;
        mColor = color;
        mFutureValid = false;

        x = new ArrayList<Integer>();
        y = new ArrayList<Integer>();
        x.add(startX);
        y.add(startY);
    }

    public Car(JSONObject jo) throws JSONException {

        mParticipantId = jo.getString("id");
        mColor = jo.getString("color");
        mFutureValid = false;

        JSONArray jx = jo.getJSONArray("x");
        JSONArray jy = jo.getJSONArray("y");

        x = new ArrayList<Integer>();
        y = new ArrayList<Integer>();
        for (int i = 0; i < jx.length(); i++) {
            x.add(jx.optInt(i));
            y.add(jy.optInt(i));
        }
    }

    public Integer getFutureX(){return mFutureX;}
    public Integer getFutureY(){return mFutureY;}

    public void addFuturePos(int ax, int ay){
        Log.d("*** car ", "addFuturePos(): -------------------------------------" + mParticipantId);
        int size = x.size();
        int vx = getVx() + ax;
        int vy = getVy() + ay;
        mFutureX = x.get(size - 1) + vx;
        mFutureY = y.get(size - 1) + vy;
        mFutureValid = true;
    }

    public void move(){
        if(mFutureValid) {
            x.add(mFutureX);
            y.add(mFutureY);
        }
        else {
            Log.d("*** Car ", "move(): the future pos is not valid.");
        }
    }

    public void resetFuturePos(){
        mFutureValid = false;
    }

    public Boolean hasFuturePos(){
        return mFutureValid;
    }

    public int getNumOfMovements(){
        return x.size();
    }

    public int getCurrPosIdx(){
        return(x.size() - 1);
    }

    public int getVx(){
        int size = x.size();
        if(size > 2) {
            return x.get(size - 1) - x.get(size - 2);
        }
        return 0;
    }
    public int getVy(){
        int size = x.size();
        if(size > 2) {
            return y.get(size - 1) - y.get(size - 2);
        }
        return 0;
    }



    public JSONObject toJSONObject() {

        JSONArray jx = new JSONArray();
        JSONArray jy = new JSONArray();

        for(int i=0; i<x.size(); i++){
            jx.put(x.get(i));
            jy.put(y.get(i));
        }

        JSONObject jo = new JSONObject();
        try {
            jo.put("id", mParticipantId);
            jo.put("color", mColor);
            jo.put("x", jx);
            jo.put("y", jy);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jo;
    }
}
