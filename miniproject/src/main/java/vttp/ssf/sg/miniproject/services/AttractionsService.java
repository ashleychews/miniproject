package vttp.ssf.sg.miniproject.services;

import java.io.StringReader;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

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

        String defaultMediaURL = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/Placeholder_view_vector.svg/310px-Placeholder_view_vector.svg.png";

        List<Attractions> attractions = IntStream.range(0, data.size())
            .mapToObj(idx -> {
                JsonObject attractionJson = data.getJsonObject(idx);
                JsonArray imagesArray = attractionJson.getJsonArray("images");

            String mediaUUID = null;
            String mediaURL = defaultMediaURL;
            byte[] imageData = null;

            if (imagesArray != null && !imagesArray.isEmpty()) {
                JsonObject imageJson = imagesArray.getJsonObject(0); // Extract the first image
                mediaUUID = imageJson.getString("uuid");

                // if mediaUUID is not empty
                if (mediaUUID != null && !mediaUUID.trim().isEmpty()) {
                    try {
                        mediaURL = mdSvc.getMediaUrl(mediaUUID);
                        imageData = mdSvc.fetchImageData(mediaURL);

                        // Check if imageData is not null before encoding
                        if (imageData != null) {
                            // Convert imageData to base64-encoded string
                            String base64ImageData = Base64.getEncoder().encodeToString(imageData);
                            // Update the Attractions constructor to include the base64ImageData
                            return new Attractions(
                                    attractionJson.getString("uuid"),
                                    attractionJson.getString("name"),
                                    attractionJson.getString("type"),
                                    attractionJson.getString("description"),
                                    attractionJson.getString("body"),
                                    attractionJson.getJsonNumber("rating").doubleValue(),
                                    attractionJson.getString("officialWebsite"),
                                    mediaURL,
                                    imageData,
                                    base64ImageData
                            );
                        }
                    } catch (Exception e) {
                        mediaURL = defaultMediaURL;
                    }
                }

                // Media UUID is empty, retrieve the image URL directly
                String imageUrl = imageJson.getString("url");
                if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                    mediaURL = imageUrl;
                }
            }

                // No images or error fetching image, use a default URL
                return new Attractions(
                        attractionJson.getString("uuid"),
                        attractionJson.getString("name"),
                        attractionJson.getString("type"),
                        attractionJson.getString("description"),
                        attractionJson.getString("body"),
                        attractionJson.getJsonNumber("rating").doubleValue(),
                        attractionJson.getString("officialWebsite"),
                        mediaURL,
                        imageData,
                        null // Set base64ImageData to null if not available
                );
            })
            .toList();

        return attractions;
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

        String defaultMediaURL = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/Placeholder_view_vector.svg/310px-Placeholder_view_vector.svg.png";

        JsonObject attractionJson = datadetails.getJsonObject(0); // there is only one attraction in the response
        // Extract the "uuid" from the "images" array
        JsonArray imagesArray = attractionJson.getJsonArray("images");

        String mediaUUID = null;
        String mediaURL = null;
        byte[] imageData = null;

        //check if imagesArray is empty
        if (imagesArray != null && !imagesArray.isEmpty()) {
            JsonObject imageJson = imagesArray.getJsonObject(0); // assuming there is at least one image
            mediaUUID = imageJson.getString("uuid");

            // if mediaUUID is not empty
            if (mediaUUID != null && !mediaUUID.trim().isEmpty()) {
                try {
                    mediaURL = mdSvc.getMediaUrl(mediaUUID);
                    imageData = mdSvc.fetchImageData(mediaURL);

                    // Check if imageData is not null before encoding
                    if (imageData != null) {
                        // Convert imageData to base64-encoded string
                        String base64ImageData = Base64.getEncoder().encodeToString(imageData);
                        // Update the Attractions constructor to include the base64ImageData
                        return new Attractions(
                                attractionJson.getString("uuid"),
                                attractionJson.getString("name"),
                                attractionJson.getString("type"),
                                attractionJson.getString("description"),
                                attractionJson.getString("body"),
                                attractionJson.getJsonNumber("rating").doubleValue(),
                                attractionJson.getString("officialWebsite"),
                                mediaURL,
                                imageData,
                                base64ImageData
                            );
                        }
                    } catch (Exception e) {
                        mediaURL = defaultMediaURL;
                    }
                }

                // Media UUID is empty, retrieve the image URL directly
                String imageUrl = imageJson.getString("url");
                if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                    mediaURL = imageUrl;
                }
            }

    
            // No images or error fetching image, use a default URL
            return new Attractions(
                    attractionJson.getString("uuid"),
                    attractionJson.getString("name"),
                    attractionJson.getString("type"),
                    attractionJson.getString("description"),
                    attractionJson.getString("body"),
                    attractionJson.getJsonNumber("rating").doubleValue(),
                    attractionJson.getString("officialWebsite"),
                    mediaURL,
                    imageData,
                    null
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