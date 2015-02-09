package aferrer.vectorRace;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by AFerrer on 21/01/2015.
 */
public class GameState {

    private static final String TAG = "EBTurn";
    public static final int numOfCars = 4;

    public int turnCounter;
    public String mTrackId;
    public Car[]  mCars;

    public GameState(){
        mCars = new Car[numOfCars];
        for(int i=0; i<numOfCars; i++){
            mCars[i] = new Car();
        }
    }

    public void updateState(int carIdx, int ax, int ay){
        //aqui haurem de gestionar si el cotxe ha sortit de la carretera
        mCars[carIdx].moveTo(ax, ay);
    }

    public void undoState(int carIdx){
        mCars[carIdx].undo();
    }

    // This is the byte array we will write out to the TBMP API.
    public byte[] persist() {
        JSONObject retVal = new JSONObject();
        try {
            retVal.put("turnCounter", turnCounter);
            retVal.put("trackId", mTrackId);
            //list of cars
            JSONArray jCars = new JSONArray();
            for(int i = 0; i < mCars.length; i++) {
                jCars.put(mCars[i].toJSONObject());
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
                    retVal.mCars[i].fromJSONObject(jCars.getJSONObject(i));
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return retVal;
    }
}
