package vttp.ssf.sg.miniproject.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import vttp.ssf.sg.miniproject.models.Attractions;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Repository
public class AttractionsRepo {

    // Connect to Redis
    @Autowired
    @Qualifier("attRedis")
    private RedisTemplate<String, String> template;


    public boolean hasUser(String username) {
        return template.hasKey(username);
    }

    // Save user information to Redis
    public void saveUser(String username, String password) {
        HashOperations<String, String, String> hashOps = template.opsForHash();
        hashOps.put(username, "password", password);
    }

    public boolean retrieveUser(String username, String password) {
        HashOperations<String, String, String> hashOps = template.opsForHash();
    
        if (hasUser(username)) {
            String userPassword = hashOps.get(username, "password");
            return userPassword !=null && userPassword.equals(password);
        }
        return false;
    }
    
    public boolean hasFavourite(String username) {
        return template.opsForHash().hasKey(username, "attractions");
    }

    public List<Attractions> getFavAtt(String username) {
        if (hasFavourite(username)) {
            String jsonString = (String) template.opsForHash().get(username, "attractions");
            return convertJsonStringToObjectList(jsonString);
        }
        return new LinkedList<>() ;
    }

    public void deleteFavourite(String username) {
        template.delete(username);
    }

    public void addFavourite(String username, List<Attractions> attractions) {
        List<Attractions> existingAttractions = getFavAtt(username);

        // Check for duplicates and add only new attractions
        for (Attractions newAttraction : attractions) {
            if (!containsAttraction(existingAttractions, newAttraction)) {
                existingAttractions.add(newAttraction);
            } else {
                // Log a message or handle the case where the attraction already exists
                System.out.println("Attraction already exists in favorites: " + newAttraction.getName());
            }
        }
    
        String jsonString = convertListToJsonString(existingAttractions);
    
        // Update the Redis set
        template.opsForHash().put(username, "attractions", jsonString);
    
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

        return new Attractions(uuid, name, type, description, body, rating, officialWebsite);
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


    // Check if a single attraction exists in the list
    private boolean containsAttraction(List<Attractions> existingAttractions, Attractions newAttraction) {
        for (Attractions existingAttraction : existingAttractions) {
            // Check if uuid is the same
            if (existingAttraction.getUuid().equals(newAttraction.getUuid())) {
                return true; // The attraction already exists
            }
        }
        return false; // The attraction does not exist in the list
    }

}