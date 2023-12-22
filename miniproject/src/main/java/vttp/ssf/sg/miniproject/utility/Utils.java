package vttp.ssf.sg.miniproject.utility;

import java.util.LinkedList;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import vttp.ssf.sg.miniproject.models.Attractions;

public class Utils {
    
    public static List<Attractions> getFavourite(HttpSession sess) {

        //get list from session
        List<Attractions> attractions = (List<Attractions>)sess.getAttribute("attractions");
        //if cart is empty, create a new cart
        if (null == attractions) {
            attractions = new LinkedList<>();
            sess.setAttribute("attractions", attractions);
        }
        return attractions;
    }

}
