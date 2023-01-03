package projectutils;

import java.util.Objects;

public class Pair<K, V> {
    public Pair(final K initKey, final V initValue) {
        key = initKey;
        value = initValue;
    }

    private K key;
    private V value;

    public K getKey() {
        return key;
    }

    public void setKey(final K newKey) {
        key = newKey;
    }

    public V getValue() {
        return value;
    }

    public void setValue(final V newValue) {
        value = newValue;
    }

    public boolean containsKey(final K searchKey) {
        return key.equals(searchKey);
    }

    public boolean containsValue(final V searchValue) {
        return value.equals(searchValue);
    }

    public boolean contains(final K searchKey, final V searchValue) {
        return key.equals(searchKey) && value.equals(searchValue);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        return obj instanceof Pair<?, ?> aPair
                && Objects.equals(key, aPair.key)
                && Objects.equals(value, aPair.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key) ^ Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "Pair{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
