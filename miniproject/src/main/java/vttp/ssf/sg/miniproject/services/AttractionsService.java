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

        return data.stream()
            .map(j -> j.asJsonObject())
            .map(o -> {
                String name = o.getString("name");
                String type = o.getString("type");
                String description = o.getString("description");
                String body = o.getString("body");
                double rating = o.getJsonNumber("rating").doubleValue();
                return new Attractions(name, type, description, body, rating);
            })
            .toList();

    }

    public List<Attractions> getFavourite(String username) {
        if (attRepo.hasFavourite(username))
            //return list of favourites attractions
            return attRepo.getFavAtt(username);
            //otherwise return empty list
        return new LinkedList<>();
    }

}
