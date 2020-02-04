package tkmce.hestia20.model;

import android.support.annotation.DrawableRes;
import android.support.annotation.Keep;

import java.io.Serializable;

@Keep
public class SubCategoryModel implements Serializable {

    private String title;
    private int catId;
    @DrawableRes
    private int imageId, backgroundId;

    public SubCategoryModel() {

    }

    public SubCategoryModel(String title, int catId, @DrawableRes int imageId, @DrawableRes int backgroundId) {
        this.title = title;
        this.catId = catId;
        this.imageId = imageId;
        this.backgroundId = backgroundId;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getBackgroundId() {
        return backgroundId;
    }

    public void setBackgroundId(int backgroundId) {
        this.backgroundId = backgroundId;
    }
}