package org.jugendhackt.tinnitus.util;

public class DataSet<K, V, B> {

    private int mpId;
    private Triple<K, V, B> data;

    public DataSet(int mpId, Triple<K, V, B> data) {
        this.mpId = mpId;
        this.data = data;
    }

    public int getMpId() {
        return mpId;
    }

    public void setMpId(int mpId) {
        this.mpId = mpId;
    }

    public Triple<K, V, B> getData() {
        return data;
    }

    public void setData(Triple<K, V, B> data) {
        this.data = data;
    }

}
