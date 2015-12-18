package JavaBean;

/**
 * Created by hp on 2015/12/8.
 */
public class Album {
    private String id;
    private String name;
    private String introducation;
    private String firstImgUrl;


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

    public Album(String id, String name, String introducation) {
        this.id = id;
        this.name = name;
        this.introducation = introducation;
        firstImgUrl="./static/data/test.jpg";
    }

    public String getFirstImgUrl() {
        return firstImgUrl;
    }

    public void setFirstImgUrl(String firstImgUrl) {
        this.firstImgUrl = firstImgUrl;
    }
}
