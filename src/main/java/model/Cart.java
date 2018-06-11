package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;

public class Cart {
    private LinkedHashMap<String, Integer> cart;
    private Collection<CartListener> listeners;

    public Cart() {
        cart = new LinkedHashMap<>();
        listeners = new ArrayList<>();
    }

    public void add(String key, Integer value) {
        cart.put(key, value);
        notifyListeners();
    }

    public void remove(String key) {
        cart.remove(key);
        notifyListeners();
    }

    public boolean exists(String key) {
        return cart.containsValue(key);
    }

    public void update(String key, Integer value) {
        cart.put(key, value);
    }

    public int size() {
        return cart.size();
    }

    public Integer getValue(String key) {
        return cart.get(key);
    }

    public Integer getOrDefault(String key, Integer defaultValue) {
        if (!cart.containsKey(key)) {
            return defaultValue;
        }
        return getValue(key);
    }

    public LinkedHashMap<String, Integer> getCart() {
        return new LinkedHashMap<>(cart);
    }

    public void clear() {
        cart.clear();
        notifyListeners();
    }

    public Set<String> getKeys() {
        return cart.keySet();
    }

    public Collection<Integer> getValues() {
        return cart.values();
    }

    public void addListener(CartListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(CartListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (CartListener listener : listeners) {
            if (listener != null) {
                listener.cartSizeHasChanged();
            }
        }
    }
}
