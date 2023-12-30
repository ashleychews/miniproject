package vttp.ssf.sg.miniproject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vttp.ssf.sg.miniproject.models.Attractions;
import vttp.ssf.sg.miniproject.services.AttractionsService;


//for json
@RestController
@RequestMapping
public class AttractionsRestController {

    @Autowired
    private AttractionsService attSvc;

    // @GetMapping(path = "/searchAttractions", produces = "application/json")
    // public ResponseEntity<String> attractionSearch(@RequestParam String uuid) {
    //     Attractions attraction = attSvc.getAttractionDetailsByUUID(uuid);



    //     return 
    // }

}
