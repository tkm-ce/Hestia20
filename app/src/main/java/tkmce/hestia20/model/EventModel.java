package tkmce.hestia20.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class EventModel implements Serializable {

    private String event_id;
    private String cat_id;
    private String title;
    private String short_desc;
    private String details;
    private int min_memb;
    private int max_memb;
    private String venue;
    private Integer reg_fee;
    private String prize;
    private String file1;
    private String file2;
    private String col1_name;
    private String col1_no;
    private String fee_type;
    private String co2_name;
    private String co2_no;
    private int seats;
    private Date reg_start;
    private Date reg_end;
    @SerializedName("image_name")
    private String img;

    public EventModel() {
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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

    public String getShort_desc() {
        return short_desc;
    }

    public void setShort_desc(String short_desc) {
        this.short_desc = short_desc;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getMin_memb() {
        return min_memb;
    }

    public void setMin_memb(int min_memb) {
        this.min_memb = min_memb;
    }

    public int getMax_memb() {
        return max_memb;
    }

    public void setMax_memb(int max_memb) {
        this.max_memb = max_memb;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public Integer getReg_fee() {
        return reg_fee;
    }

    public void setReg_fee(Integer reg_fee) {
        this.reg_fee = reg_fee;
    }

    public void setReg_fee(int reg_fee) {
        this.reg_fee = reg_fee;
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

    public String getFile2() {
        return file2;
    }

    public void setFile2(String file2) {
        this.file2 = file2;
    }

    public String getCol1_name() {
        return col1_name;
    }

    public void setCol1_name(String col1_name) {
        this.col1_name = col1_name;
    }

    public String getCol1_no() {
        return col1_no;
    }

    public void setCol1_no(String col1_no) {
        this.col1_no = col1_no;
    }

    public String getCo2_name() {
        return co2_name;
    }

    public void setCo2_name(String co2_name) {
        this.co2_name = co2_name;
    }

    public String getCo2_no() {
        return co2_no;
    }

    public void setCo2_no(String co2_no) {
        this.co2_no = co2_no;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public Date getReg_start() {
        return reg_start;
    }

    public void setReg_start(Date reg_start) {
        this.reg_start = reg_start;
    }

    public Date getReg_end() {
        return reg_end;
    }

    public void setReg_end(Date reg_end) {
        this.reg_end = reg_end;
    }

    public String getFee_type() {
        return fee_type;
    }

    public void setFee_type(String fee_type) {
        this.fee_type = fee_type;
    }
}
