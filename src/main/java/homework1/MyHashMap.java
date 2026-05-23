package homework1;

import java.util.Objects;

public class MyHashMap<Key, Value> {

    private static class Entry<Key, Value> {
        final Key key;
        Value value;
        Entry<Key, Value> next;

        Entry(Key key, Value value) {
            this.key = key;
            this.value = value;
        }
    }

    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    private Entry<Key, Value>[] table;
    private int size;

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        table = new Entry[DEFAULT_CAPACITY];
    }

    private int getIndex(Key key) {
        return getIndex(key, table.length);
    }

    private int getIndex(Key key, int tableLength) {
        return (Objects.hashCode(key) & 0x7fffffff) % tableLength;
    }

    /**
     * Добавление или обновление значения
     */
    public void put(Key key, Value value) {
        if ((size + 1) > table.length * LOAD_FACTOR) {
            resize();
        }

        int index = getIndex(key);

        Entry<Key, Value> current = table[index];

        // Если корзина пустая
        if (current == null) {
            table[index] = new Entry<>(key, value);
            size++;
            return;
        }

        // Поиск существующего ключа
        Entry<Key, Value> prev = null;

        while (current != null) {
            if (Objects.equals(current.key, key)) {
                current.value = value;
                return;
            }

            prev = current;
            current = current.next;
        }

        // Добавление в конец списка
        prev.next = new Entry<>(key, value);
        size++;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<Key, Value>[] oldTable = table;
        table = new Entry[oldTable.length * 2];

        for (Entry<Key, Value> entry : oldTable) {
            while (entry != null) {
                Entry<Key, Value> next = entry.next;
                int newIndex = getIndex(entry.key, table.length);

                entry.next = table[newIndex];
                table[newIndex] = entry;

                entry = next;
            }
        }
    }

    /**
     * Получение значения по ключу
     */
    public Value get(Key key) {
        int index = getIndex(key);

        Entry<Key, Value> current = table[index];

        while (current != null) {
            if (Objects.equals(current.key, key)) {
                return current.value;
            }

            current = current.next;
        }

        return null;
    }

    /**
     * Удаление элемента по ключу
     */
    public Value remove(Key key) {
        int index = getIndex(key);

        Entry<Key, Value> current = table[index];
        Entry<Key, Value> prev = null;

        while (current != null) {
            if (Objects.equals(current.key, key)) {

                // Если удаляем первый элемент
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }

                size--;
                return current.value;
            }

            prev = current;
            current = current.next;
        }

        return null;
    }

    // Для теста
    public static void main(String[] args) {
        MyHashMap<String, Integer> map = new MyHashMap<>();

        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);

        System.out.println(map.get("two")); // 2

        map.put("two", 22);
        System.out.println(map.get("two")); // 22

        map.remove("two");
        System.out.println(map.get("two")); // null

        //чек resize
        MyHashMap<Integer, String> resizeMap = new MyHashMap<>();

        for (int i = 0; i < 100; i++) {
            resizeMap.put(i, "value-" + i);
        }

        boolean resizeWorks = true;

        for (int i = 0; i < 100; i++) {
            if (!Objects.equals(resizeMap.get(i), "value-" + i)) {
                resizeWorks = false;
                break;
            }
        }

        System.out.println(resizeWorks); // true
    }
}
