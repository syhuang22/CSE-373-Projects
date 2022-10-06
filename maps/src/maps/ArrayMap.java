package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;


/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ArrayMap<K, V> extends AbstractIterableMap<K, V> {

    private static final int DEFAULT_INITIAL_CAPACITY = 128;
    /*
    Warning:
    You may not rename this field or change its type.
    We will be inspecting it in our secret tests.
     */
    SimpleEntry<K, V>[] entries;

    int size;
    int dataLength;

    // You may add extra fields or helper methods though!

    public ArrayMap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public ArrayMap(int initialCapacity) {
        this.entries = this.createArrayOfEntries(initialCapacity);
        size = initialCapacity;
        dataLength = 0;
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code Entry<K, V>} objects.
     *
     * Note that each element in the array will initially be null.
     *
     * Note: You do not need to modify this method.
     */
    @SuppressWarnings("unchecked")
    private SimpleEntry<K, V>[] createArrayOfEntries(int arraySize) {
        /*
        It turns out that creating arrays of generic objects in Java is complicated due to something
        known as "type erasure."

        We've given you this helper method to help simplify this part of your assignment. Use this
        helper method as appropriate when implementing the rest of this class.

        You are not required to understand how this method works, what type erasure is, or how
        arrays and generics interact.
        */
        return (SimpleEntry<K, V>[]) (new SimpleEntry[arraySize]);
    }

    @Override
    public V get(Object key) {
        for (int i = 0; i < size; i++) {
            if (entries[i]!= null && Objects.equals(entries[i].getKey(), key)) {
                return entries[i].getValue();
            }

        }
        return null;
    }

    public V put2(K key, V value) {
        if (dataLength > size) {
            resize();
        }
        if (!containsKey(key)) {
            for (int i = 0; i < size; i++) {
                if (entries[i] == null) {
                    entries[i] = new SimpleEntry<K, V>(key, value);
                    return null;
                }
            }
        }
        for (int i = 0; i < size; i++) {
            if (entries[i]!= null && Objects.equals(entries[i].getKey(), key)) {
                V oldV = entries[i].getValue();
                entries[i].setValue(value);
                return oldV;
            }
        }
        return null;
    }

    private void resize() {
        SimpleEntry<K, V>[] newEntries = createArrayOfEntries(size * 2);
        System.arraycopy(this.entries, 0, newEntries, 0, size);
        size = 2 * size;
        entries = newEntries;
    }

    @Override
    public V put(K key, V value) {
        if (!containsKey(key) && dataLength == size) {
            resize();
        }

        if (!containsKey(key)) {
            for (int i = 0; i < size; i++) {
                if (entries[i] == null) {
                    entries[i] = new SimpleEntry<K, V>(key, value);
                    dataLength = dataLength + 1;
                    return null;
                }
            }
        }

        for (int i = 0; i < size; i++) {
            if (entries[i]!= null && Objects.equals(entries[i].getKey(), key)) {
                V oldV = entries[i].getValue();
                entries[i].setValue(value);
                return oldV;
            }
        }
        return null;
    }

    public V put0(K key, V value) {
        V oldV = null;
        if (get(key) != null) {
            for (int i = 0; i < size; i++) {
                if (entries[i].getKey().equals(key)) {
                    oldV = entries[i].getValue();
                    entries[i].setValue(value);
                    return oldV;
                }
            }
        } else {
            SimpleEntry<K, V> putNew = new SimpleEntry<K, V>(key, value);
            if (size == 0) {
                entries = createArrayOfEntries(1);
                size = 1;
            }
            if (entries[size - 1] == null) {
                for (int i = 0; i < size; i++) {
                    if (entries[i] == null) {
                        entries[i] = putNew;
                        dataLength = dataLength + 1;
                        return null;
                    }
                }
            } else {
                SimpleEntry<K, V>[] newEntries = createArrayOfEntries(size * 2);
                System.arraycopy(this.entries, 0, newEntries, 0, dataLength);
                newEntries[size] = putNew;
                size = 2 * size;
                entries = newEntries;
                dataLength = dataLength + 1;
            }
        }
        return null;
    }

    @Override
    public V remove(Object key) {
        V removeV = null;
        //int runtime = dataLength;
        for (int i = 0; i < dataLength; i++) {
            if (entries[i] != null && Objects.equals(entries[i].getKey(), key)) {
                removeV = entries[i].getValue();
                entries[i] = entries[dataLength - 1];
                entries[dataLength - 1] = null;
                dataLength = dataLength - 1;
                return removeV;
            }
        }
        return removeV;
    }

    @Override
    public void clear() {
        SimpleEntry<K, V>[] clearEntries = createArrayOfEntries(size);
        entries = clearEntries;
        dataLength = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (dataLength == 0) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (entries[i] != null && Objects.equals(entries[i].getKey(), key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return dataLength;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: you won't need to change this method (unless you add more constructor parameters)
        return new ArrayMapIterator<>(this.entries);
    }


    // Doing so will give you a better string representation for assertion errors the debugger.

    // @Override
    // public String toString() {
    //     return super.toString();
    // }

    private static class ArrayMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private final SimpleEntry<K, V>[] entries;
        // You may add more fields and constructor parameters
        private int currIndex;

        public ArrayMapIterator(SimpleEntry<K, V>[] entries) {
            this.entries = entries;
            currIndex = 0;
        }

        @Override
        public boolean hasNext() {
            return (currIndex < entries.length && entries[currIndex] != null);
        }

        @Override
        public Map.Entry<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Map.Entry<K, V> entry = entries[currIndex];
            currIndex = currIndex + 1;
            return entry;
        }
    }
}
