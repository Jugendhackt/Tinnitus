package org.jugendhackt.tinnitus.util;

public class Triple<K, V, B> {

    private K key;
    private V val1;
    private B val2;

    public Triple(K key, V val1, B val2) {
        this.key = key;
        this.val1 = val1;
        this.val2 = val2;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue1() {
        return val1;
    }

    public void setValue1(V value) {
        this.val1 = value;
    }

    public B getValue2() {
        return val2;
    } 
    
    public void setValue2(B value) {
        val2 = value;
    }

}
