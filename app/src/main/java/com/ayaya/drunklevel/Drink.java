package com.ayaya.drunklevel;

import org.json.JSONException;
import org.json.JSONObject;

public class Drink {
    
    private String name;
    private float concentration; // concentration d'alcool en g/L
    private String imageName;
    
    public Drink(String name, float concentration, String imageName) {
        this.name = name;
        this.concentration = concentration;
        this.imageName = imageName;
    }
    
    public String getName() {
        return name;
    }
    
    public float getConcentration() {
        return concentration;
    }
    
    public String getImageName() {
        return imageName;
    }
    
    public float getDegree() {
        return concentrationToDegree(getConcentration());
    }
    
    public static float concentrationToDegree(float concentration) {
        return concentration*100/789;
    }
    
    public static float degreeToConcentration(float degree) {
        return degree*789/100;
    }
    
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("concentration", concentration);
            json.put("imageName", imageName);
            return json;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static Drink fromJSON(String json) {
        try {
            return fromJSON(new JSONObject(json));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Drink fromJSON(JSONObject json) {
        try {
            return new Drink(json.getString("name"), (float)json.getDouble("concentration"), json.getString("imageName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
