package vttp.ssf.sg.miniproject.services;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp.ssf.sg.miniproject.models.Attractions;
import vttp.ssf.sg.miniproject.repo.AttractionsRepo;

@Service
public class AttractionsService {
    
    // @Value("${attractionsapi.key}")
    // private String apiKey;

    @Autowired
    private AttractionsRepo attRepo;

    @Autowired
    private MediaService mdSvc;

    //GET
    ///content/attractions/v2/search?searchType=keyword&searchValues={searchValue}
    public List<Attractions> getAttractions(String searchValues) {

        String payload;
        JsonArray data;

        //build the url
        String url = UriComponentsBuilder
                .fromUriString("https://api.stb.gov.sg/content/attractions/v2/search?searchType=keyword")
                .queryParam("searchValues", searchValues)
                .toUriString();

        String apiKey = "D4Q6iNhqOUU5wAnJoaHiRO7FnZPAkbVG"; //TO DELETE!!!

        //use .header to hide the api key
        RequestEntity<Void> req = RequestEntity.get(url)
            .header("X-Api-Key", apiKey)
            .build();

        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = null;
        
        try {

            resp = template.exchange(req, String.class);

        } catch (Exception ex) {
            ex.printStackTrace();
            return new LinkedList<>();
        }

        payload = resp.getBody();
        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonObject result = reader.readObject();
        // data: [{}]
        data = result.getJsonArray("data");

        //go to "images" and extract the list of mediaUUID
        List<String> mediaUUIDs = data.getValuesAs(JsonObject.class)
                    .stream()
                    .flatMap(attractionJson -> attractionJson.getJsonArray("images").getValuesAs(JsonObject.class).stream())
                    .map(imageJson -> imageJson.getString("uuid"))
                    .toList();

        return data.stream()
            .map(j -> j.asJsonObject())
            .map(o -> {
                String uuid = o.getString("uuid");
                String name = o.getString("name");
                String type = o.getString("type");
                String description = o.getString("description");
                String body = o.getString("body");
                double rating = o.getJsonNumber("rating").doubleValue();
                String officialWebsite = o.getString("officialWebsite");

                String medialURL = 
                
                return new Attractions(uuid, name, type, description, body, rating, officialWebsite, mediaURL);
            })
            .toList();
    }

    //getting single attraction
    public Attractions getAttractionDetailsByUUID(String uuid) {
        String payloaddetails;
        JsonArray datadetails;

        String url = UriComponentsBuilder
                .fromUriString("https://api.stb.gov.sg/content/attractions/v2/search?searchType=uuids")
                .queryParam("searchValues", uuid)
                .toUriString();

        String apiKey = "D4Q6iNhqOUU5wAnJoaHiRO7FnZPAkbVG"; // TO DELETE!!!

        //use .header to hide the api key
        RequestEntity<Void> req = RequestEntity.get(url)
            .header("X-Api-Key", apiKey)
            .build();

        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp;

        try {

            resp = template.exchange(req, String.class);

        } catch (Exception ex) {
            ex.printStackTrace();
            return new Attractions();
        }

        payloaddetails = resp.getBody();
        JsonReader reader = Json.createReader(new StringReader(payloaddetails));
        JsonObject result = reader.readObject();
        // data: [{}]
        datadetails = result.getJsonArray("data");

        if (datadetails.isEmpty()) {
            return null; // or throw an exception indicating no data found
        }

        JsonObject attractionJson = datadetails.getJsonObject(0); // there is only one attraction in the response
        // Extract the "uuid" from the "images" array
        JsonArray imagesArray = attractionJson.getJsonArray("images");

        String mediaUUID = null;
        String mediaURL = null;

        if (!imagesArray.isEmpty()) {
            JsonObject imageJson = imagesArray.getJsonObject(0); // assuming there is at least one image
            mediaUUID = imageJson.getString("uuid");
            // Move getMediaUrl inside the if block
            mediaURL = mdSvc.getMediaUrl(mediaUUID);
        } else {
            return null; // or throw an exception indicating no image found
        }

        return new Attractions(
                attractionJson.getString("uuid"),
                attractionJson.getString("name"),
                attractionJson.getString("type"),
                attractionJson.getString("description"),
                attractionJson.getString("body"),
                attractionJson.getJsonNumber("rating").doubleValue(),
                attractionJson.getString("officialWebsite"),
                mediaURL
        );
    }

    //get favourites based on the username
    public List<Attractions> getFavourite(String username) {
        if (attRepo.hasFavourite(username))
            //return list of favourites attractions
            return attRepo.getFavAtt(username);
            //otherwise return empty list
        return new LinkedList<>();
    }

    //save favourites
    public void save(String username, List<Attractions> attractions) {
        attRepo.deleteFavourite(username);
        attRepo.addFavourite(username, attractions);
    }
}