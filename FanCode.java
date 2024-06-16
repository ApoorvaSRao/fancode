package com.example.interview;


import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;


public class FanCode {

    String baseUrl = "https://jsonplaceholder.typicode.com/";


    @Test
    public void task50() {
        //Get the response of the users api
        RestAssured.baseURI = baseUrl;
        Response rs = responseBody("/users");
        ArrayList<Integer> id = new ArrayList<>();
        JSONArray users = new JSONArray(rs.asString());

        //Get the list of Fancode cities
        id = listOfIDsFancodeCity(users);

        //Print all the id's of Fancode city
        System.out.println(id);

        //Get the response of todos api
        Response rs1 = responseBody("/todos");
        JSONArray todos = new JSONArray(rs1.asString());

        //Iterate through list of ids of Fancode city, compare it with userId and task status to calculate the percentage
        for (int i = 0; i < id.size(); i++) {
            int count = 0;
            int size = 0;
            for (int j = 0; j < todos.length(); j++) {
                JSONObject task = todos.getJSONObject(j);
                int userId = task.getInt("userId");
                boolean completed = task.getBoolean("completed");

                if (id.get(i).equals(userId)) {
                    size++;
                }
                if (id.get(i).equals(userId) && completed) {
                    count++;
                }
            }
            System.out.println("User ID: " + id.get(i) + " have completed todo task of percentage - " + percent(count, size));

            Assert.assertFalse(percent(count, size) < 50, "Fancode UserId : " + id.get(i) + " does not have more than half of their todos task completed.");
        }
        System.out.println("All fancode users have more than half of their todos task completed.");
    }

    /**
     * Calculate percentage for users
     *
     * @param count
     * @param userId
     * @return
     */
    public double percent(double count, double userId) {
        double percentage;
        percentage = (count / userId) * 100;
        return percentage;
    }

    /**
     * Calculte list of Fancode cities
     *
     * @param users
     * @return
     */
    public ArrayList listOfIDsFancodeCity(JSONArray users) {
        ArrayList<Integer> id = new ArrayList<>();
        String lat_value, lng_value;

        for (int i = 0; i < users.length(); i++) {
            JSONObject jo = (JSONObject) users.get(i);
            lat_value = jo.getJSONObject("address").getJSONObject("geo").getString("lat");
            lng_value = jo.getJSONObject("address").getJSONObject("geo").getString("lng");

            if (cityFancode(lat_value, lng_value)) {
                id.add((Integer) jo.get("id"));
            }
        }

        return id;
    }

    /**
     * Calculate lat and lng to determine whether city is Fancode or not
     *
     * @param lat_value
     * @param lng_value
     * @return
     */
    public boolean cityFancode(String lat_value, String lng_value) {
        float lat = Float.parseFloat(lat_value);
        float lng = Float.parseFloat(lng_value);

        //lat between ( -40 to 5) and long between ( 5 to 100)
        return (lat >= -40 && lat <= 5) && (lng >= 5 && lng <= 100);
    }

    /**
     * Fetch the response for GET request with provided url
     *
     * @param url
     * @return
     */
    public Response responseBody(String url) {
        RequestSpecification rp = RestAssured.given();
        Response response = rp.get(url);
        if (response.statusCode() == 200) {
            return response;
        } else
            return null;
    }

}
