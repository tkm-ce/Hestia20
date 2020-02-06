package tkmce.hestia20.model;


import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class ScheduleEventModel implements Serializable {

    private String event_name;
    private String event_label;
    private String event_time;
    private String event_venue;

    public ScheduleEventModel() {
    }

    public ScheduleEventModel(String event_name, String event_label, String event_time, String event_venue) {
        this.event_name = event_name;
        setEvent_label(event_label);
        this.event_time = event_time;
        this.event_venue = event_venue;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_label() {
        return event_label;
    }

    public void setEvent_label(String event_label) {
        if ("null".equals(event_label)) this.event_label = null;
        else this.event_label = event_label;
    }

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }

    public String getEvent_venue() {
        return event_venue;
    }

    public void setEvent_venue(String event_venue) {
        this.event_venue = event_venue;
    }
}
