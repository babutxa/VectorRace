package aferrer.vectorRace;

import android.graphics.Bitmap;
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
            }
        }
    }

    public Boolean isValidPos(int posX, int posY){
        Track.TypeOfGround typeOfGround = mTrack.getTypeOfGround(posX, posY);
        if(typeOfGround == Track.TypeOfGround.OUT_OF_ROAD)
            return false;
        return true;
    }


    public Boolean checkIfCanContinue(){
        Boolean canContinue = false;

        for(int i = 0; i < mCars.size(); i++){
            if(mCars.get(i).mParticipantId.equals(mCurrParticipantId)){
                Car car = mCars.get(i);

                //current mCar pos
                int currIdx = car.getCurrPosIdx();
                int currX = car.getX();
                int currY = car.getY();


                int nextx = car.x.get(currIdx) + car.getVx();
                int nexty = car.y.get(currIdx) + car.getVy();

                canContinue = canContinue | isValidPos((nextx - 1), (nexty - 1));
                canContinue = canContinue | isValidPos((nextx - 1), (nexty));
                canContinue = canContinue | isValidPos((nextx - 1), (nexty + 1));

                canContinue = canContinue | isValidPos((nextx), (nexty - 1));
                canContinue = canContinue | isValidPos((nextx), (nexty));
                canContinue = canContinue | isValidPos((nextx), (nexty + 1));

                canContinue = canContinue | isValidPos((nextx + 1), (nexty - 1));
                canContinue = canContinue | isValidPos((nextx + 1), (nexty));
                canContinue = canContinue | isValidPos((nextx + 1), (nexty + 1));

                Log.d("*** GameState ", "checkIfCanContinue() carId = " + car.mParticipantId + " --> " + canContinue.toString());
            }
        }
        return canContinue;
    }

    public void replaceOnRoad(){
        for(int i = 0; i < mCars.size(); i++) {
            if (mCars.get(i).mParticipantId.equals(mCurrParticipantId)) {
                Car car = mCars.get(i);
                //TODO alba: posar el cotxe a dins de la carretera amb velositat zero
                car.forceStop();
            }
        }
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
