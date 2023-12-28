package vttp.ssf.sg.miniproject.services;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class MediaService {

    // @Value("${attractionsapi.key}")
    // private String apiKey;

    //GET
    //https://api.stb.gov.sg/media/download/v2/{media uuid}?fileType=Thumbnail%201080h
    //eg https://api.stb.gov.sg/media/download/v2/1010a2a130a0ae24afd9b02678a051c9a5a?fileType=Thumbnail%201080h


    //extracting url
    public String getMediaUrl(String mediaUUID) {

        final String fileType = "Default";

        String url = UriComponentsBuilder
                .fromUriString("https://api.stb.gov.sg/media/download/v2/")
                .path(mediaUUID)
                .queryParam("fileType", fileType)
                .toUriString();

        String apiKey = "D4Q6iNhqOUU5wAnJoaHiRO7FnZPAkbVG"; // TO DELETE!!!

        // Debugging: Print the constructed URL
        //System.out.println("Constructed URL: " + url);

        //use .header to hide the api key
        RequestEntity<Void> req = RequestEntity.get(url)
            .header("X-Api-Key", apiKey)
            .build();
        RestTemplate template = new RestTemplate();

        try {
            ResponseEntity<byte[]> response = template.exchange(req, byte[].class);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // or handle the exception as needed
        }
    }

    public byte[] fetchImageData(String mediaURL) {

        String apiKey = "D4Q6iNhqOUU5wAnJoaHiRO7FnZPAkbVG"; // TO DELETE!!!

        //use .header to hide the api key
        RequestEntity<Void> req = RequestEntity.get(mediaURL)
            .header("X-Api-Key", apiKey)
            .build();
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(req, byte[].class);
            return responseEntity.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null; // or handle the exception as needed
        }
    }
}