package de.rusticprism.kreiscraftbot.music;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Queue {
    private final List<QueuedTrack> list = new ArrayList<>();
    private final Set<Long> set = new HashSet<>();

    public int add(QueuedTrack item) {
        int lastIndex;
        for (lastIndex = list.size() - 1; lastIndex > -1; lastIndex--)
            if (list.get(lastIndex).getIdentifier() == item.getIdentifier())
                break;
        lastIndex++;
        set.clear();
        for (; lastIndex < list.size(); lastIndex++) {
            if (set.contains(list.get(lastIndex).getIdentifier()))
                break;
            set.add(list.get(lastIndex).getIdentifier());
        }
        list.add(lastIndex, item);
        return lastIndex;
    }

    public void addAt(int index, QueuedTrack item) {
        if (index >= list.size())
            list.add(item);
        else
            list.add(index, item);
    }

    public int size() {
        return list.size();
    }

    public QueuedTrack pull() {
        return list.remove(0);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public List<QueuedTrack> getList() {
        return list;
    }

    public QueuedTrack get(int index) {
        return list.get(index);
    }

    public QueuedTrack remove(int index) {
        return list.remove(index);
    }

    public int removeAll(long identifier) {
        int count = 0;
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).getIdentifier() == identifier) {
                list.remove(i);
                count++;
            }
        }
        return count;
    }

    public void clear() {
        list.clear();
    }

    public int shuffle(long identifier) {
        List<Integer> iset = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getIdentifier() == identifier)
                iset.add(i);
        }
        for (int j = 0; j < iset.size(); j++) {
            int first = iset.get(j);
            int second = iset.get((int) (Math.random() * iset.size()));
            QueuedTrack temp = list.get(first);
            list.set(first, list.get(second));
            list.set(second, temp);
        }
        return iset.size();
    }

    public void skip(int number) {
        if (number > 0) {
            list.subList(0, number).clear();
        }
    }

    /**
     * Move an item to a different position in the list
     *
     * @param from The position of the item
     * @param to   The new position of the item
     * @return the moved item
     */
    public QueuedTrack moveItem(int from, int to) {
        QueuedTrack item = list.remove(from);
        list.add(to, item);
        return item;
    }
}
