package vttp.ssf.sg.miniproject.repo;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import vttp.ssf.sg.miniproject.models.Attractions;

@Repository
public class AttractionsRepo {

    //connect to redis
    @Autowired @Qualifier ("attRedis")
    private RedisTemplate<String, String> template;

    public boolean hasFavourite(String username) {
        return template.hasKey(username);
    }

    public List<Attractions> getFavAtt(String username) {
        if (hasFavourite(username)) {
            String jsonString = template.opsForValue().get(username);
            return convertJsonStringToObjectList(jsonString);
        }
        return List.of();
    }

    public void saveFavourite(String username, Attractions attraction) {
        String jsonString = convertObjectToJsonString(attraction);
        template.opsForValue().set(username, jsonString);
    }

    private List<Attractions> convertJsonStringToObjectList(String jsonString) {
        List<Attractions> attractions = new ArrayList<>();
        JsonArray jsonArray = Json.createReader(new StringReader(jsonString)).readArray();

        for (JsonValue jsonValue : jsonArray) {
            JsonObject jsonObject = (JsonObject) jsonValue;
            Attractions attraction = convertJsonObjectToObject(jsonObject);
            attractions.add(attraction);
        }

        return attractions;
    }

    private Attractions convertJsonObjectToObject(JsonObject jsonObject) {
        // Extract properties from JsonObject and create an Attractions object
        String name = jsonObject.getString("name");
        String type = jsonObject.getString("type");
        String description = jsonObject.getString("description");
        String body = jsonObject.getString("body");
        double rating = jsonObject.getJsonNumber("rating").doubleValue();

        return new Attractions(name, type, description, body, rating);
    }

    private String convertObjectToJsonString(Attractions attraction) {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("name", attraction.getName());
        jsonObjectBuilder.add("type", attraction.getType());
        jsonObjectBuilder.add("description", attraction.getDescription());
        jsonObjectBuilder.add("body", attraction.getBody());
        jsonObjectBuilder.add("rating", attraction.getRating());

        return jsonObjectBuilder.build().toString();
    }



}