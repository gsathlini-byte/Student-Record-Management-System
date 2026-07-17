import java.util.ArrayList;
import java.util.List;

public class CustomHashTable {

    private class Entry {
        String key;
        Student value;
        Entry next;
        Entry(String key, Student value) { this.key = key; this.value = value; }
    }

    private Entry[] buckets;
    private int capacity;
    private int size;
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;

    @SuppressWarnings("unchecked")
    public CustomHashTable() {
        this.capacity = 16;
        this.buckets = new CustomHashTable.Entry[capacity];
        this.size = 0;
    }

    private int hash(String key) {
        int hash = 0;
        for (int i = 0; i < key.length(); i++) {
            hash = (hash * 31 + key.charAt(i)) % capacity;
            if (hash < 0) hash += capacity;
        }
        return hash;
    }

    public void put(String key, Student value) {
        if ((double) size / capacity >= LOAD_FACTOR_THRESHOLD) {
            resize();
        }
        int index = hash(key);
        Entry current = buckets[index];
        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value; // update
                return;
            }
            current = current.next;
        }
        Entry newEntry = new Entry(key, value);
        newEntry.next = buckets[index];
        buckets[index] = newEntry;
        size++;
    }

    public Student get(String key) {
        int index = hash(key);
        Entry current = buckets[index];
        while (current != null) {
            if (current.key.equals(key)) return current.value;
            current = current.next;
        }
        return null;
    }

    public boolean remove(String key) {
        int index = hash(key);
        Entry current = buckets[index];
        Entry prev = null;
        while (current != null) {
            if (current.key.equals(key)) {
                if (prev == null) buckets[index] = current.next;
                else prev.next = current.next;
                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Entry[] oldBuckets = buckets;
        capacity *= 2;
        buckets = new CustomHashTable.Entry[capacity];
        size = 0;
        for (Entry head : oldBuckets) {
            Entry current = head;
            while (current != null) {
                put(current.key, current.value);
                current = current.next;
            }
        }
    }

    public List<Student> values() {
        List<Student> result = new ArrayList<>();
        for (Entry head : buckets) {
            Entry current = head;
            while (current != null) {
                result.add(current.value);
                current = current.next;
            }
        }
        return result;
    }

    public int size() {
        return size;
    }
}