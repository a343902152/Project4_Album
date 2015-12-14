package domain;

/**
 * Created by hp on 2015/12/8.
 */
public class Photo {
    public String id;
    public String name;
//    public String image;
    public String url;
    public String introducation;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroducation() {
        return introducation;
    }

    public void setIntroducation(String introducation) {
        this.introducation = introducation;
    }

    public Photo(String id, String name, String url, String introducation) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.introducation = introducation;
    }
}
