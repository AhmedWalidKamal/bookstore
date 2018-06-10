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
}
