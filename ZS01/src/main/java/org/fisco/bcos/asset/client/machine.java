package org.fisco.bcos.asset.client;

import java.util.Objects;

public class machine {
    private String id="";
    private String name="";
    private String lateststate="";
    private String sensortype="";
    private String intro="";
    private String imageurl="";

    public machine(String id, String lateststate) {
        this.id = id;
        this.lateststate = lateststate;
    }

    public machine(String id, String name, String lateststate, String sensortype, String intro, String imageurl) {
        this.id = id;
        this.name = name;
        this.lateststate = lateststate;
        this.sensortype = sensortype;
        this.intro = intro;
        this.imageurl = imageurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLateststate() {
        return lateststate;
    }

    public void setLateststate(String lateststate) {
        this.lateststate = lateststate;
    }

    public String getSensortype() {
        return sensortype;
    }

    public void setSensortype(String sensortype) {
        this.sensortype = sensortype;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof machine)) return false;
        machine machine = (machine) o;
        return Objects.equals(getId(), machine.getId()) && Objects.equals(getName(), machine.getName()) && Objects.equals(getLateststate(), machine.getLateststate()) && Objects.equals(getSensortype(), machine.getSensortype()) && Objects.equals(getIntro(), machine.getIntro()) && Objects.equals(getImageurl(), machine.getImageurl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getLateststate(), getSensortype(), getIntro(), getImageurl());
    }

    @Override
    public String toString() {
        return "machine{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", lateststate='" + lateststate + '\'' +
                ", sensortype='" + sensortype + '\'' +
                ", intro='" + intro + '\'' +
                ", imageurl='" + imageurl + '\'' +
                '}';
    }
}
