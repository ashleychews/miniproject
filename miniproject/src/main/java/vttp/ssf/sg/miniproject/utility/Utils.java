package vttp.ssf.sg.miniproject.utility;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import vttp.ssf.sg.miniproject.models.Attractions;

public class Utils implements Serializable {
    
    public static List<Attractions> getFavourite(HttpSession sess) {

        //get list from session
        List<Attractions> favourite = (List<Attractions>)sess.getAttribute("favourite");

        // if attractions list is empty or not present, create a new list
        if (null == favourite) {
            favourite = new LinkedList<>();
            sess.setAttribute("favourite", favourite);
        }
        return favourite;
    }
}
