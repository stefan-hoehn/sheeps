package com.hoehn.sheeps;

public class Sheep {
    public String eid;
    public String vid;
    public String datum;
    public String zeit;
    public String anmerkung;
    
    public String toString() {
        return eid + ":" + vid + ":" +datum + " " + zeit + "    " +anmerkung;
    }
}
