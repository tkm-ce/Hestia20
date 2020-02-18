package live.hestia.app.model;


import androidx.annotation.DrawableRes;
import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class MainCategory implements Serializable {

    private String title;
    private String eventNos;
    private int catId;
    @DrawableRes
    private int imageId;

    public MainCategory(String title, int catId, String eventNos, @DrawableRes int imageId) {
        this.title = title;
        this.catId = catId;
        this.eventNos = eventNos;
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEventNos() {
        return eventNos;
    }

    public void setEventNos(String eventNos) {
        this.eventNos = eventNos;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
