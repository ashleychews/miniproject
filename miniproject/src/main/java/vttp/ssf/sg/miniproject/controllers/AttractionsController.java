package vttp.ssf.sg.miniproject.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import vttp.ssf.sg.miniproject.models.User;
import vttp.ssf.sg.miniproject.repo.AttractionsRepo;
import vttp.ssf.sg.miniproject.services.AttractionsService;

@Controller
@RequestMapping
public class AttractionsController {

    @Autowired
    private AttractionsService attSvc;

    @Autowired
    private AttractionsRepo attRepo;

    //signin page
    @GetMapping
    public String getLogin(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "index";
    }

    //validating signin page
    @PostMapping("/login")
    public String signIn(@Valid @ModelAttribute("user") User user, BindingResult result, Model model, HttpSession sess) {

        String username = user.getUsername().toLowerCase();

        if(result.hasErrors()){
            return "index"; //stay on index page
        }
        // Retrieve the password from the user object
        String password = user.getPassword();

        // User exists, validate the password
        if (attRepo.retrieveUser(username, password)) {
            // Password is correct, set the username in the session
            sess.setAttribute("username", username);
            sess.setAttribute("password", password);
            model.addAttribute("user", user);
            return "redirect:/search";
        } else {
            // Password is incorrect, return to login page with an error message
            result.rejectValue("password", "error.user", "Invalid password");
            return "index";
        }
    }

    //signup page
    @GetMapping("/signup") 
    public String signUp (Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "signup";
    }

    //post signup form
    @PostMapping("/signupform") 
    public String postsignUp (@ModelAttribute("user") @Valid User user, BindingResult result, Model model){

        String username = user.getUsername().toLowerCase();
        String password = user.getPassword();

        if (result.hasErrors()) {
            return "signup";
        }

        if (attRepo.hasUser(username)) {
            result.rejectValue("username", "error.user", "Username already exists");
            return "signup";
        } else {
            model.addAttribute("user", user);
            attRepo.saveUser(username, password);
            return "successful";
        }
    }

    //search attractions
    //shows the list of attractions based on search
    @GetMapping("/search")
    public String getSearch(Model model, HttpSession sess) {
        
        //retrieve user from session
        String username = (String) sess.getAttribute("username");
        model.addAttribute("username", username);
        return "search";
    }


    //populate the list of attractions on search form
    @GetMapping("/search/attractions")
    public ModelAndView getAttractions(@RequestParam String searchValues, HttpSession sess) {
        ModelAndView mav = new ModelAndView("searchresults");
        List<Attractions> attractions = attSvc.getAttractions(searchValues);

        // Retrieve the username from the session
        String username = (String) sess.getAttribute("username"); 

        mav.addObject("username", username);
        mav.addObject("attractions", attractions);
        return mav;
    }

    //display the individual attractions
    @GetMapping("search/attractions/{uuid}")
    public ModelAndView getAttractionDetails(@PathVariable String uuid, HttpSession sess) {
        ModelAndView mav = new ModelAndView("attraction");

        // Store the uuid in the session
        sess.setAttribute("uuid", uuid);

        // Retrieve the username from the session
        String username = (String) sess.getAttribute("username");

        mav.addObject("username", username);
    
        // Fetch details of a specific attraction by UUID
        Attractions attraction = attSvc.getAttractionDetailsByUUID(uuid);
    
        if (attraction != null) 
            mav.addObject("attraction", attraction);
        return mav;
    }

    //favourite page
    @GetMapping("search/favourite") 
    public String getFav(Model model, HttpSession sess) {

        // Retrieve the username from the session
        String username = (String) sess.getAttribute("username");

        //retrieve list of favourited attractions based on username
        List<Attractions> favourite = attSvc.getFavourite(username);

        model.addAttribute("favourite", favourite);
        model.addAttribute("username", username);

        return "favourite";
    }
    
    //when clicking add to favourites button, stay on the page
    @PostMapping("search/addtofavorites")
    public String addToFavorites(
            @Valid @ModelAttribute("attraction") Attractions attraction,
            BindingResult bindings,
            HttpSession sess,
            @RequestParam String username, Model model) {

        // Retrieve the username from the session
        String user = (String) sess.getAttribute("username");

        //retrieve the uuid
        String uuid = (String) sess.getAttribute("uuid");

        String password = (String) sess.getAttribute("password");


        if (bindings.hasErrors()) {
            return "error";
        }

        //retrieve list of favourited attractions based on username
        List<Attractions> favAttractions = attSvc.getFavourite(username);

        favAttractions.add(attraction);

        // Update the session with the modified list
        sess.setAttribute("favourite", favAttractions);

        // Add the list of favorite attractions and the username to the Model
        model.addAttribute("uuid", uuid);
        model.addAttribute("favourite", favAttractions);
        model.addAttribute("username", user);

        // Save the updated favorites to the database
        attSvc.save(user, favAttractions, password);

        return "redirect:/search/attractions/" + uuid;
    }

    //delete button to delete favourites
    @PostMapping("/search/deletefavorite")
    public String deleteFavorite(@RequestParam String username, @RequestParam String uuid,
        HttpSession sess,
        Model model) {

            
    // Retrieve the username from the session
    String user = (String) sess.getAttribute("username");
    String password = (String) sess.getAttribute("password");

    // Get the list of favorited attractions
    List<Attractions> favAttractions = attSvc.getFavourite(username);

    // Remove the attraction with the specified UUID
    for (int i = 0; i < favAttractions.size(); i++) {
        if (favAttractions.get(i).getUuid().equals(uuid)) {
            favAttractions.remove(i);
        }
    }

    // Update the session with the modified list
    sess.setAttribute("favourite", favAttractions);

    // Add the list of favorite attractions and the username to the Model
    model.addAttribute("favourite", favAttractions);
    model.addAttribute("username", user);

    // Save the updated favorites to the database
    attSvc.save(user, favAttractions, password);

    // Redirect back to the favorite attractions page
    return "redirect:/search/favourite";

    }
}