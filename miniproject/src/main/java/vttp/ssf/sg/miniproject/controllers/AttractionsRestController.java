package vttp.ssf.sg.miniproject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vttp.ssf.sg.miniproject.services.AttractionsService;


//for json
@RestController
@RequestMapping
public class AttractionsRestController {

    @Autowired
    private AttractionsService attSvc;

    // @PostMapping(path="/searchAttractions", produces="application/json")
    // public String processAttractionSearch() {

        
    // }

}
