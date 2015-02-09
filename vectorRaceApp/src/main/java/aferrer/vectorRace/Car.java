package aferrer.vectorRace;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by AFerrer on 19/01/2015.
 */

public class Car {
    public ArrayList<Integer> x;
    public ArrayList<Integer> y;
    public ArrayList<Integer> vx;
    public ArrayList<Integer> vy;

    public Car(){

        x = new ArrayList<Integer>();
        y = new ArrayList<Integer>();
        vx = new ArrayList<Integer>();
        vy = new ArrayList<Integer>();

        x.add(10);
        y.add(10);
        vx.add(0);
        vy.add(0);
    }

    public int getNumOfMovements(){
        return x.size();
    }

    public int getCurrPosIdx(){
        return(x.size() - 1);
    }

    public void moveTo(int ax, int ay){
        int size = x.size();
        vx.add(vx.get(size - 1) + ax);
        vy.add(vy.get(size - 1) + ay);
        x.add(x.get(size - 1) + vx.get(size));
        y.add(y.get(size - 1) + vy.get(size));
    }

    //this function removes the las move
    public void undo(){
        int currPos = getCurrPosIdx();
        x.remove(currPos);
        y.remove(currPos);
        vx.remove(currPos);
        vy.remove(currPos);
    }

    public JSONObject toJSONObject() {

        JSONArray jx = new JSONArray();
        JSONArray jy = new JSONArray();
        JSONArray jvx = new JSONArray();
        JSONArray jvy = new JSONArray();

        for(int i=0; i<x.size(); i++){
            jx.put(x.get(i));
            jy.put(y.get(i));
            jvx.put(vx.get(i));
            jvy.put(vy.get(i));
        }

        JSONObject jo = new JSONObject();
        try {
            jo.put("x", jx);
            jo.put("y", jy);
            jo.put("vx", jvx);
            jo.put("vy", jvy);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jo;
    }

    public void fromJSONObject(JSONObject jo) throws JSONException {

        x.clear();
        y.clear();
        vx.clear();
        vy.clear();

        JSONArray jx = (JSONArray) jo.get("x");
        JSONArray jy = (JSONArray) jo.get("y");
        JSONArray jvx = (JSONArray) jo.get("vx");
        JSONArray jvy = (JSONArray) jo.get("vy");

        for (int i = 0; i < jx.length(); i++) {
            x.add(jx.optInt(i));
            y.add(jy.optInt(i));
            vx.add(jvx.optInt(i));
            vy.add(jvy.optInt(i));
        }
    }


}
