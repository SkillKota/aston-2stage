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

    private Entry<Key, Value>[] table;

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        table = new Entry[DEFAULT_CAPACITY];
    }

    private int getIndex(Key key) {
        return Math.abs(Objects.hashCode(key)) % table.length;
    }

    /**
     * Добавление или обновление значения
     */
    public void put(Key key, Value value) {
        int index = getIndex(key);

        Entry<Key, Value> current = table[index];

        // Если корзина пустая
        if (current == null) {
            table[index] = new Entry<>(key, value);
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
    }
}
