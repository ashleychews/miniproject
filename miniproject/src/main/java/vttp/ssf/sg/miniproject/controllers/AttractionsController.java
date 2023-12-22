package vttp.ssf.sg.miniproject.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import reactor.core.Scannable.Attr;
import vttp.ssf.sg.miniproject.models.Attractions;
import vttp.ssf.sg.miniproject.services.AttractionsService;
import vttp.ssf.sg.miniproject.utility.Utils;

@Controller
@RequestMapping
public class AttractionsController {

    @Autowired
    private AttractionsService attSvc;

    @GetMapping("/search")
    public String getSearch(@RequestParam String username, Model model, HttpSession sess) {

        //retrieve list of favourited attractions
        List<Attractions> favourite = attSvc.getFavourite(username);

        sess.setAttribute("favourite", favourite);

        model.addAttribute("username", username);

        return "search";
    }

    //get attraction page
    @GetMapping("/attractions")
    public ModelAndView getAttractions(@RequestParam String searchValues) {
        ModelAndView mav = new ModelAndView("attractions");
        List<Attractions> attractions = attSvc.getAttractions("searchValues");

        mav.addObject("attractions", attractions);
        return mav;
    }

    //save places
    // @PostMapping("/save")
    // public String postSave() {

    // }

    // @PostMapping
    // public ModelAndView postAttractions(){

    // }



}
