package vttp.ssf.sg.miniproject.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AttractionsService {
    
    @Value("${newsapi.key}")
    private String apiKey;
}
