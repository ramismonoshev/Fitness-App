package ca.stclaircollege.fitgrind.api;

public class Item {
    public static final String GROUP_KEY = "group";
    public static final String NAME_KEY = "name";
    public static final String NDBNO_KEY = "ndbno";

    private String group;
    private String name;
    private int ndbno;

    public Item(String group, String name, int ndbno) {
        this.group = group;
        this.name = (name.indexOf(", UPC") != -1) ? name.substring(0, name.indexOf(", UPC")) : name;
        this.ndbno = ndbno;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public int getNdbno() {
        return ndbno;
    }
}
