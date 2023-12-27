package vttp.ssf.sg.miniproject.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import vttp.ssf.sg.miniproject.models.Attractions;
import vttp.ssf.sg.miniproject.services.MediaService;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AttractionsRepo {

    // Connect to Redis
    @Autowired
    @Qualifier("attRedis")
    private RedisTemplate<String, String> template;

    @Autowired
    private MediaService mdSvc;

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

    public void deleteFavourite(String username) {
        template.delete(username);
    }

    public void addFavourite(String username, List<Attractions> attractions) {
        String jsonString = convertListToJsonString(attractions);
        template.opsForValue().set(username, jsonString);


        // Log relevant information for testing
        System.out.println("Stored favorites for username: " + username);
        System.out.println("Favorites content: " + jsonString);
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
        String uuid = jsonObject.getString("uuid");
        String name = jsonObject.getString("name");
        String type = jsonObject.getString("type");
        String description = jsonObject.getString("description");
        String body = jsonObject.getString("body");
        double rating = jsonObject.getJsonNumber("rating").doubleValue();
        String officialWebsite = jsonObject.getString("officialWebsite");


        return new Attractions(uuid, name, type, description, body, rating, officialWebsite, mediaUrl);
    }

    private String convertListToJsonString(List<Attractions> attractionsList) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        
        for (Attractions attraction : attractionsList) {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder()
                    .add("uuid", attraction.getUuid())
                    .add("name", attraction.getName())
                    .add("type", attraction.getType())
                    .add("description", attraction.getDescription())
                    .add("body", attraction.getBody())
                    .add("rating", attraction.getRating())
                    .add("officialWebsite", attraction.getOfficialWebsite());
            arrayBuilder.add(objectBuilder);
        }

        JsonArray jsonArray = arrayBuilder.build();
        return jsonArray.toString();
    }
}