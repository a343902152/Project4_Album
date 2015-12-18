package JavaBean;

/**
 * Created by hp on 2015/12/8.
 */
public class Photo {
    private String userid;
    private String albumid;
    private String photoid;
    private String photoname;
    private String albumname;
    private String url;

    public Photo(){}
    public String getAlbumid() {
        return albumid;
    }

    public void setAlbumid(String albumid) {
        this.albumid = albumid;
    }

    public Photo(String userid, String albumid, String photoid, String photoname, String albumname) {
        this.userid = userid;
        this.albumid = albumid;
        this.photoid = photoid;
        this.photoname = photoname;
        this.albumname = albumname;
    }

    public String getUserid() {

        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPhotoid() {
        return photoid;
    }

    public void setPhotoid(String photoid) {
        this.photoid = photoid;
    }

    public String getPhotoname() {
        return photoname;
    }

    public void setPhotoname(String photoname) {
        this.photoname = photoname;
    }

    public String getAlbumname() {
        return albumname;
    }

    public void setAlbumname(String albumname) {
        this.albumname = albumname;
    }

    public String getUrl() {
        url=userid+"/"+albumname+"/"+photoname;
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
