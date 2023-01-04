package projectutils;

import java.util.Objects;

/**
 * <p>Maintains a pair link between two types.
 * A Map with just one member (like {@code Map.Entry()}).</p>
 *
 * @param <K> key member in order to identify the object.
 * @param <V> value member that defines the key member.
 *
 * @since 2.0.0
 * @author Mihai Negru
 */
public final class Pair<K, V> {

    /**
     * <p>Creates a new {@code Pair} object containing
     * the key and value pair.</p>
     *
     * @param initKey initial key to create the pair.
     * @param initValue initial value to create the pair.
     */
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

    /**
     * <p>Checks if pair contains the input key.</p>
     *
     * @param searchKey input key to check for equality.
     * @return true if the input key matches the pair's key,
     * false otherwise.
     */
    public boolean containsKey(final K searchKey) {
        return key.equals(searchKey);
    }

    /**
     * <p>Checks if pair contains the input value.</p>
     *
     * @param searchValue input value to check for equality.
     * @return true if the input value matches the pair's key,
     * false otherwise.
     */
    public boolean containsValue(final V searchValue) {
        return value.equals(searchValue);
    }

    /**
     * <p>Checks if pair contains the input key and value.</p>
     *
     * @param searchKey input key to check for equality.
     * @param searchValue input value to check for equality.
     * @return true if both input key and value matches pair's
     * key and value.
     */
    public boolean contains(final K searchKey, final V searchValue) {
        return key.equals(searchKey) && value.equals(searchValue);
    }

    @Override
    public boolean equals(final Object obj) {
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
        return "Pair{"
                + "key=" + key
                + ", value=" + value
                + '}';
    }
}
