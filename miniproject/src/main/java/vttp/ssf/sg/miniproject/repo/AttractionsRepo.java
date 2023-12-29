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
import java.util.Base64;
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
        return template.opsForHash().hasKey(username, "attractions");
    }

    public List<Attractions> getFavAtt(String username) {
        if (hasFavourite(username)) {
            String jsonString = (String) template.opsForHash().get(username, "attractions");
            return convertJsonStringToObjectList(jsonString);
        }
        return List.of();
    }

    public void deleteFavourite(String username) {
        template.delete(username);
    }

    public void addFavourite(String username, List<Attractions> attractions) {
        String jsonString = convertListToJsonString(attractions);

        // Store "username" as the key, "attractions" as the hashkey, and jsonString as the hashvalue
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

        // Fetch media URL for each attraction
        String mediaUUID = jsonObject.getJsonArray("images")
                .getJsonObject(0) // Assuming there is at least one image
                .getString("uuid");
        String mediaURL = mdSvc.getMediaUrl(mediaUUID);
        byte[] imageData = mdSvc.fetchImageData(mediaURL);
        String base64imageData = Base64.getEncoder().encodeToString(imageData);


        return new Attractions(uuid, name, type, description, body, rating, officialWebsite, mediaURL, imageData, base64imageData);
    }

    private String convertListToJsonString(List<Attractions> attractionsList) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        
        for (Attractions attraction : attractionsList) {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder()
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