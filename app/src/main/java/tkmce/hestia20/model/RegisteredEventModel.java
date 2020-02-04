package tkmce.hestia20.model;

public class RegisteredEventModel {

    private String id;
    private String title;
    private String f1_hint;
    private String f2_hint;
    private String file1;
    private String file2;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getF1_hint() {
        return f1_hint;
    }

    public void setF1_hint(String f1_hint) {
        if ("null".equals(f1_hint)) this.f1_hint = null;
        else this.f1_hint = f1_hint;
    }

    public String getF2_hint() {
        return f2_hint;
    }

    public void setF2_hint(String f2_hint) {
        if ("null".equals(f2_hint)) this.f2_hint = null;
        else this.f2_hint = f2_hint;
    }

    public String getFile1() {
        return file1;
    }

    public void setFile1(String file1) {
        if ("null".equals(file1)) this.file1 = null;
        else this.file1 = file1;
    }

    public String getFile2() {
        return file2;
    }

    public void setFile2(String file2) {
        if ("null".equals(file2)) this.file2 = null;
        else this.file2 = file2;
    }
}
