package aferrer.vectorRace;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by AFerrer on 21/01/2015.
 */
public class GameState {

    private static final String TAG = "EBTurn";

    public int turnCounter;
    public String mTrackId;
    private ArrayList<Car> mCars;

    public GameState(){
        turnCounter = 0;
        mTrackId = "";
        mCars = new ArrayList<Car>();
    }

    public void addCar(String participantId){
        Car newCar = new Car(participantId, getColor(mCars.size()), 10, 10);
        //TODO alba: falta mTrackId. Quant el sapiguem, haurem de posar a cada cotxe la posicio inicial
        mCars.add(newCar);
    }

    public int getNumOfCars(){
        return mCars.size();
    }
    public Car getCar(int idx){
        return mCars.get(idx);
    }

    // cyclic list of colors
    public String getColor(int idx){
        int modIdx = idx%3;
        switch(modIdx){
            case 0: return "#ffff0000"; //red
            case 1: return "#ff00ff00"; //green
            case 2: return "#ff0000ff"; //blue
        }
        return "#ffffffff"; //default
    }

    public void updateFutureState(String participantId, int ax, int ay){
        Log.d("*** GameState ", "updateFutureState(): -------------------------------------" + participantId);
        for(int i = 0; i < mCars.size(); i++){
            if(mCars.get(i).mParticipantId.equals(participantId)){
                mCars.get(i).addFuturePos(ax, ay);
            }
        }
    }

    public void updateState(String participantId){
        Log.d("*** GameState ", "updateState(): -------------------------------------" + participantId);
        for(int i = 0; i < mCars.size(); i++){
            if(mCars.get(i).mParticipantId.equals(participantId)){
                //TODO alba: aqui haurem de gestionar si el cotxe ha sortit de la carretera
                mCars.get(i).move();
            }
        }
    }

    // This is the byte array we will write out to the TBMP API.
    public byte[] persist() {
        JSONObject retVal = new JSONObject();
        try {
            retVal.put("turnCounter", turnCounter);
            retVal.put("trackId", mTrackId);
            //list of cars
            JSONArray jCars = new JSONArray();
            for(int i = 0; i < mCars.size(); i++) {
                jCars.put(mCars.get(i).toJSONObject());
            }
            retVal.put("cars",jCars);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String st = retVal.toString();
        Log.d(TAG, "==== PERSISTING\n" + st);
        return st.getBytes(Charset.forName("UTF-8"));
    }

    // Creates a new instance of GameState.
    static public GameState unpersist(byte[] byteArray) {
        if (byteArray == null) {
            Log.d(TAG, "Empty array---possible bug.");
            return new GameState();
        }
        String st = null;
        try {
            st = new String(byteArray, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }
        Log.d(TAG, "====UNPERSIST \n" + st);
        GameState retVal = new GameState();
        try {
            JSONObject obj = new JSONObject(st);
            if (obj.has("turnCounter")) {
                retVal.turnCounter = obj.getInt("turnCounter");
            }
            if(obj.has("trackId")){
                retVal.mTrackId = obj.getString("trackId");
            }
            if(obj.has("cars")){
                JSONArray jCars = obj.getJSONArray("cars");
                for(int i = 0; i < jCars.length(); i++){
                    Car newCar = new Car(jCars.getJSONObject(i));
                    retVal.mCars.add(newCar);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return retVal;
    }
}
