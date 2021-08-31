package com.ayaya.drunklevel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Conso {
    
    private long time; // heure de la consomation en secondes
    private Drink drink;
    private float volume; // volume de la boisson consommée en litres
    
    public Conso(long time, Drink drink, float volume) {
        this.time = time;
        this.drink = drink;
        this.volume = volume;
    }
    
    public long getTime() {
        return time;
    }
    
    public Drink getDrink() {
        return drink;
    }
    
    public float getVolume() {
        return volume;
    }
    
    public float getAlcoholMass() { // masse d'alcool en grammes
        return drink.getConcentration()*volume;
    }
    
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("time", time);
            json.put("drink", drink.toJSON());
            json.put("volume", volume);
            return json;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static Conso fromJSON(JSONObject json) {
        try {
            return new Conso(json.getLong("time"), Drink.fromJSON(json.getJSONObject("drink")), (float)json.getDouble("volume"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
