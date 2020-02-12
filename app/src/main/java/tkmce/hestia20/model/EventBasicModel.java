package tkmce.hestia20.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EventBasicModel implements Serializable {

    private String event_id;
    private String cat_id;
    private String title;
    private String prize;
    @SerializedName("image_name")
    private String img;
    private String file1;
    private String file2;

    private String f1_hint;
    private String f2_hint;

    public String getF1_hint() {
        return f1_hint;
    }

    public void setF1_hint(String f1_hint) {
        this.f1_hint = f1_hint;
    }

    public String getF2_hint() {
        return f2_hint;
    }

    public void setF2_hint(String f2_hint) {
        this.f2_hint = f2_hint;
    }

    public String getFile2() {
        return file2;
    }

    public void setFile2(String file2) {
        this.file2 = file2;
    }


    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }


    public String getFile1() {
        return file1;
    }

    public void setFile1(String file1) {
        this.file1 = file1;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
