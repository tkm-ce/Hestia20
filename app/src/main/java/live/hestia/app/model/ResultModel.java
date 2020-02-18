package live.hestia.app.model;

import androidx.annotation.Keep;

@Keep
public class ResultModel {
    private String label;
    private String name;
    private String college;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if ("null".equals(name)) {
            this.name = null;
        } else {
            this.name = name;
        }
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }
}
