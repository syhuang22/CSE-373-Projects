package deques;

public class LinkedDeque<T> extends AbstractDeque<T> {
    private int size;
    // IMPORTANT: Do not rename these fields or change their visibility.
    // We access these during grading to test your code.
    Node<T> front;
    Node<T> back;

    // Feel free to add any additional fields you may need, though.

    public LinkedDeque() {
        size = 0;
        front = new Node<>(null, null, null);
        back = new Node<>(null, null, null);
        front.next = back;
        back.prev = front;
    }

    public void addFirst(T item) {
        size += 1;
        Node<T> oldFirst = new Node<>(item, front, front.next);
        oldFirst.next.prev = oldFirst;
        front.next = oldFirst;
    }

    public void addLast(T item) {
        size += 1;
        Node<T> oldLast = new Node<>(item, back.prev, back);
        oldLast.prev.next = oldLast;
        back.prev = oldLast;
    }

    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        size -= 1;
        T first = front.next.value;
        front.next = front.next.next;
        front.next.prev = front;
        return first;
    }

    public T removeLast() {
        if (size == 0) {
            return null;
        }
        size -= 1;
        T last = back.prev.value;
        back.prev = back.prev.prev;
        back.prev.next = back;
        return last;
    }

    public T get(int index) {
        if ((index >= size) || (index < 0)) {
            return null;
        }

        Node<T> curr = front.next;
        Node<T> curr2 = back.prev;
        if (index <= size/2) {
            for (int i = 0; i < index; i++) {
                curr = curr.next;
            }
            return curr.value;
        } else {
            for (int i = size - 1; i > index; i--) {
                curr2 = curr2.prev;
            }
            return curr2.value;
        }
    }

    public int size() {
        return size;
    }
}
