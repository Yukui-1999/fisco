package org.fisco.bcos.asset.client;

import java.util.Objects;

public class file {
    private String id;
    private String time;
    private String total_hash;
    private String part_hash;
    private int result;

    public file() {
    }

    public file(String id,String time, String total_hash, String part_hash, int result) {
        this.id = id;
        this.time = time;
        this.total_hash = total_hash;
        this.part_hash = part_hash;
        this.result = result;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public String getTotal_hash() { return total_hash; }

    public void setTotal_hash(String total_hash) {
        this.total_hash = total_hash;
    }

    public String getPart_hash() {
        return part_hash;
    }

    public void setResult(String part_hash) {
        this.part_hash = part_hash;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }


    @Override
    public String toString() {
        return "file{" +
                "id='" + id + '\'' +
                ", time='" + time + '\'' +
                ", total_hash='" + total_hash + '\'' +
                ", part_hash='" + part_hash + '\'' +
                ", result=" + result +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        file file = (file) o;
        return result == file.result && Objects.equals(id, file.id) && Objects.equals(time, file.time) && Objects.equals(total_hash, file.total_hash) && Objects.equals(part_hash, file.part_hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, time, total_hash, part_hash, result);
    }

}
