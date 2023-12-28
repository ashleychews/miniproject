package vttp.ssf.sg.miniproject.services;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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

        // build the URL
        String url = UriComponentsBuilder
                .fromUriString("https://api.stb.gov.sg/content/attractions/v2/search?searchType=keyword")
                .queryParam("searchValues", searchValues)
                .toUriString();

        String apiKey = "D4Q6iNhqOUU5wAnJoaHiRO7FnZPAkbVG"; // TO DELETE!!!

        // use .header to hide the API key
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

        // Fetch media UUIDs for attractions
        List<String> mediaUUIDs = data.getValuesAs(JsonObject.class)
                .stream()
                .flatMap(attractionJson -> attractionJson.getJsonArray("images").getValuesAs(JsonObject.class).stream())
                .map(imageJson -> imageJson.getString("uuid"))
                .toList();

        // Debugging: Print out the fetched media UUIDs
        System.out.println("Media UUIDs: " + mediaUUIDs);

        List<String> mediaURLs = IntStream.range(0, data.size())
        .mapToObj(idx -> {
            JsonObject attractionJson = data.getJsonObject(idx);
            JsonArray imagesArray = attractionJson.getJsonArray("images");

            if (imagesArray != null && !imagesArray.isEmpty()) {
                JsonObject imageJson = imagesArray.getJsonObject(0); // Extract the first image
                String mediaUUID = imageJson.getString("uuid");

                // Debugging: Print the mediaUUID for each attraction
                System.out.println("Media UUID for attraction " + idx + ": " + mediaUUID);

                if (mediaUUID != null && !mediaUUID.trim().isEmpty()) {
                    // Media UUID is not empty, proceed with handling it
                    String mediaURL = mdSvc.getMediaUrl(mediaUUID);
                    System.out.println("Media URL for attraction " + idx + ": " + mediaURL);
                    return mediaURL;
                } else {
                    // Media UUID is empty, retrieve the URL directly
                    String imageUrl = imageJson.getString("url");
                    if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                        // Image URL is present, handle it accordingly
                        System.out.println("Image URL for attraction " + idx + ": " + imageUrl);
                        return imageUrl;
                    } else {
                        // Both Media UUID and Image URL are empty, use a default URL or handle as needed
                        System.out.println("Both Media UUID and Image URL are empty for attraction " + idx);
                        return "https://example.com/default-image-url.jpg";
                    }
                }
            } else {
                // No images, use a default URL or return null as needed
                return "https://example.com/default-image-url.jpg";
            }
        })
        .toList();

        // Debugging: Print out the fetched media URLs
        System.out.println("Media URLs: " + mediaURLs);

        return IntStream.range(0, data.size())
                .mapToObj(index -> {
                    JsonObject o = data.getJsonObject(index);
                    String uuid = o.getString("uuid");
                    String name = o.getString("name");
                    String type = o.getString("type");
                    String description = o.getString("description");
                    String body = o.getString("body");
                    double rating = o.getJsonNumber("rating").doubleValue();
                    String officialWebsite = o.getString("officialWebsite");

                    // Fetch media URL for each attraction
                    String mediaURL = mediaURLs.get(index);

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