package vttp.ssf.sg.miniproject.models;

public class Attractions {

    private String name;
    
    private String type;

    private String description;

    private String body;
    
    private double rating;

    public Attractions(String name, String type, String description, String body, double rating) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.body = body;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }


    @Override
    public String toString() {
        return "Attraction{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", body='" + body + '\'' +
                ", rating=" + rating +
                '}';
    }
    
}
