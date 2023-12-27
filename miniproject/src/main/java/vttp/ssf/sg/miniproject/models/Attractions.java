package vttp.ssf.sg.miniproject.models;

public class Attractions {

    private String uuid;

    private String name;
    
    private String type;

    private String description;

    private String body;
    
    private double rating;

    private String officialWebsite;

    private String mediaURL;


    // default constructor
    public Attractions() {

    }

    public Attractions(String uuid, String name, String type, String description, String body,
        double rating, String officialWebsite, String mediaURL) {
        this.uuid = uuid;
        this.name = name;
        this.type = type;
        this.description = description;
        this.body = body;
        this.rating = rating;
        this.officialWebsite = officialWebsite;
        this.mediaURL = mediaURL;
    }

    public String getUuid() {
        return uuid;
    }


    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getOfficialWebsite() {
        return officialWebsite;
    }

    public void setOfficialWebsite(String officialWebsite) {
        this.officialWebsite = officialWebsite;
    }

    public String getMediaURL() {
        return mediaURL;
    }

    public void setMediaURL(String mediaURL) {
        this.mediaURL = mediaURL;
    }


    @Override
    public String toString() {
        return "Attraction{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", body='" + body + '\'' +
                ", rating=" + rating + '\'' +
                ", officialWebsite=" + officialWebsite +
                '}';
    }
    
}
