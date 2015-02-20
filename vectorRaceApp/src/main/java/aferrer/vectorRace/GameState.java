package aferrer.vectorRace;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    private int mTurnCounter;
    private String mTrackId;
    private ArrayList<Car> mCars;

    //not serialisable stuff
    public String mCurrParticipantId;
    private Track mTrack;

    public GameState(){
        mTurnCounter = 0;
        mTrackId = "";
        mCars = new ArrayList<Car>();
    }

    public void setTrackId(String trackId){
        mTrackId = trackId;
    }

    public String getTrackId(){
        return mTrackId;
    }

    public void setTrackMask(Bitmap mask){
        mTrack = new Track(mask);
    }

    public int getTurnCounter(){
        return mTurnCounter;
    }

    public int worldToImage(int i){
       return mTrack.worldToImage(i);
    }

    public void addCar(String participantId){
        Car newCar = new Car(participantId, getColor(mCars.size()), mTrack.getStartX(), mTrack.getStartY());
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

    public void updateFutureState(int ax, int ay){
        Log.d("*** GameState ", "updateFutureState(): -------------------------------------");
        for(int i = 0; i < mCars.size(); i++){
            if(mCars.get(i).mParticipantId.equals(mCurrParticipantId)){
                mCars.get(i).addFuturePos(ax, ay);
                //TODO alba: aqui haurem de gestionar si el cotxe ha sortit de la carretera
                checkWillCrash(mCars.get(i));
            }
        }
    }

    public void checkWillCrash(Car car){
        Track.TypeOfGround typeOfGround = mTrack.getTypeOfGround(car.getFutureX(), car.getFutureY());
        Log.d("*** GameState ", "checkWillCrash(): ---------- pos = (" + car.getFutureX() + ", " + car.getFutureY() + ") -> " + typeOfGround.toString());
    }

    public void updateState(){
        Log.d("*** GameState ", "updateState(): -------------------------------------");
        for(int i = 0; i < mCars.size(); i++){
            if(mCars.get(i).mParticipantId.equals(mCurrParticipantId)){
                mCars.get(i).move();
            }
        }
        mTurnCounter = mTurnCounter + 1;
    }

    // This is the byte array we will write out to the TBMP API.
    public byte[] persist() {
        JSONObject retVal = new JSONObject();
        try {
            retVal.put("turnCounter", mTurnCounter);
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
                retVal.mTurnCounter = obj.getInt("turnCounter");
            }
            if(obj.has("trackId")){
                retVal.setTrackId(obj.getString("trackId"));
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
