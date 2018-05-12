package org.jugendhackt.tinnitus.util;

public class DataSet<K, V> {

    private int mpId;
    private Tuple<K, V> data;

    public DataSet(int mpId, Tuple<K, V> data) {
        this.mpId = mpId;
        this.data = data;
    }

    public int getMpId() {
        return mpId;
    }

    public void setMpId(int mpId) {
        this.mpId = mpId;
    }

    public Tuple<K, V> getData() {
        return data;
    }

    public void setData(Tuple<K, V> data) {
        this.data = data;
    }

}
