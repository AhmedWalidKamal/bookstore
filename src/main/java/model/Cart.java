package model;

import java.util.LinkedHashMap;

public class Cart {
    private LinkedHashMap<String, Integer> cart;

    public Cart() {
        cart = new LinkedHashMap<>();
    }

    public LinkedHashMap<String, Integer> getCart() {
        return cart;
    }

    public void add(String key, Integer value) {
        cart.put(key, value);
    }

    public void remove(String key) {
        cart.remove(key);
    }

    public boolean exists(String key) {
        return cart.containsValue(key);
    }

    public void update(String key, Integer value) {
        cart.put(key, value);
    }

    public Integer getValue(String key) {
        return cart.get(key);
    }
}
