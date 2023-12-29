package vttp.ssf.sg.miniproject.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import vttp.ssf.sg.miniproject.models.Attractions;
import vttp.ssf.sg.miniproject.services.AttractionsService;
import vttp.ssf.sg.miniproject.utility.Utils;

@Controller
@RequestMapping("/search")
public class AttractionsController {

    @Autowired
    private AttractionsService attSvc;

    //search attractions
    //shows the list of attractions based on search
    @GetMapping
    public String getSearch(@RequestParam String username, Model model, HttpSession sess) {

        // Store the username in the session
        sess.setAttribute("username", username);
        model.addAttribute("username", username);
        return "search";
    }

    //populate the list of attractions on search form
    @GetMapping("/attractions")
    public ModelAndView getAttractions(@RequestParam String searchValues, HttpSession sess) {
        ModelAndView mav = new ModelAndView("search");
        List<Attractions> attractions = attSvc.getAttractions(searchValues);

        // Retrieve the username from the session
        String username = (String) sess.getAttribute("username");

        mav.addObject("username", username);
        mav.addObject("attractions", attractions);
        return mav;
    }

    //display the individual attractions
    @GetMapping("/attractions/{uuid}")
    public ModelAndView getAttractionDetails(@PathVariable String uuid) {
        ModelAndView mav = new ModelAndView("attraction");
    
        // Fetch details of a specific attraction by UUID
        Attractions attraction = attSvc.getAttractionDetailsByUUID(uuid);
    
        if (attraction != null) {
            mav.addObject("attraction", attraction);
        } else {
            // Handle the case where no attraction is found with the given UUID
            mav.addObject("attractionNotFound", true);
        }
    
        return mav;
    }

    @GetMapping("/favourite") 
    public String getFav(@RequestParam String username, Model model, HttpSession sess) {
        //retrieve list of favourited attractions based on username
        List<Attractions> favourite = attSvc.getFavourite(username);
        sess.setAttribute("favourite", favourite);

        return "favourite";
    }
    
    @PostMapping("/addtofavorites")
    public ModelAndView addToFavorites(
            @Valid @ModelAttribute("attraction") Attractions attraction,
            BindingResult bindings, HttpSession sess, @RequestParam String username) {

        ModelAndView mav = new ModelAndView("redirect:/favourites");

        if (bindings.hasErrors()) {
            mav.setStatus(HttpStatusCode.valueOf(400));
            return mav;
        }

        // Get the list of favorite attractions from the session
        List<Attractions> favAttractions = Utils.getFavourite(sess);

        // Add the new attraction to the list of favorite attractions
        favAttractions.add(attraction);

        // Update the session with the modified list
        sess.setAttribute("favourite", favAttractions);

        // Add the list of favorite attractions and the username to the ModelAndView
        mav.addObject("favourite", favAttractions);
        mav.addObject("username", username);

        mav.setStatus(HttpStatusCode.valueOf(200));
        return mav;
    }

    // Save attractions to favorites
    // Add favorites to the favorite page
    @PostMapping("/savefavorites")
    public String postSave(HttpSession sess, @RequestParam String username) {

        List<Attractions> attractions = Utils.getFavourite(sess);
        attSvc.save(username, attractions);
        sess.invalidate();

        return "redirect:/favorites";
    }
}