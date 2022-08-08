import java.util.ArrayList;

/** 
  * @author Charlie Zhou*/

public class ArrayHeap<T> {



    /* An ArrayList that stores the nodes in this binary heap. */
    private ArrayList<Node> contents;

    /* A constructor that initializes an empty ArrayHeap. */
    public ArrayHeap() {
        contents = new ArrayList<>();
        contents.add(null);
    }

    /* Returns the number of elments in the priority queue. */
    public int size() {
        return contents.size() - 1;
    }

    /* Returns the node at index INDEX. */
    private Node getNode(int index) {
        if (index >= contents.size()) {
            return null;
        } else {
            return contents.get(index);
        }
    }

    /* Sets the node at INDEX to N */
    private void setNode(int index, Node n) {
        // In the case that the ArrayList is not big enough
        // add null elements until it is the right size
        while (index + 1 > contents.size()) {
            contents.add(null);
        }
        contents.set(index, n);
    }

    /* Returns and removes the node located at INDEX. */
    private Node removeNode(int index) {
        if (index >= contents.size()) {
            return null;
        } else {
            return contents.remove(index);
        }
    }

    /* Swap the nodes at the two indices. */
    private void swap(int index1, int index2) {
        Node node1 = getNode(index1);
        Node node2 = getNode(index2);
        this.contents.set(index1, node2);
        this.contents.set(index2, node1);
    }

    /* Prints out the heap sideways. Use for debugging. */
    @Override
    public String toString() {
        return toStringHelper(1, "");
    }

    /* Recursive helper method for toString. */
    private String toStringHelper(int index, String soFar) {
        if (getNode(index) == null) {
            return "";
        } else {
            String toReturn = "";
            int rightChild = getRightOf(index);
            toReturn += toStringHelper(rightChild, "        " + soFar);
            if (getNode(rightChild) != null) {
                toReturn += soFar + "    /";
            }
            toReturn += "\n" + soFar + getNode(index) + "\n";
            int leftChild = getLeftOf(index);
            if (getNode(leftChild) != null) {
                toReturn += soFar + "    \\";
            }
            toReturn += toStringHelper(leftChild, "        " + soFar);
            return toReturn;
        }
    }

    /* A Node class that stores items and their associated priorities. */
    public class Node {
        private T item;
        private double priority;

        private Node(T item, double priority) {
            this.item = item;
            this.priority = priority;
        }

        public T item() {
            return this.item;
        }

        public double priority() {
            return this.priority;
        }

        public void setPriority(double priority) {
            this.priority = priority;
        }

        @Override
        public String toString() {
            return this.item.toString() + ", " + this.priority;
        }
    }



    /* Returns the index of the node to the left of the node at i. */
    private int getLeftOf(int i) {
        return i * 2;
    }

    /* Returns the index of the node to the right of the node at i. */
    private int getRightOf(int i) {
        return i * 2 + 1;
    }

    /* Returns the index of the node that is the parent of the node at i. */
    private int getParentOf(int i) {
        return i / 2;
    }

    /* Adds the given node as a left child of the node at the given index. */
    private void setLeft(int index, Node n) {
        setNode(getLeftOf(index), n);
    }

    /* Adds the given node as the right child of the node at the given index. */
    private void setRight(int index, Node n) {
        setNode(getRightOf(index), n);
    }

    /** Returns the index of the node with smaller priority. Precondition: not
      * both nodes are null. */
    private int min(int index1, int index2) {
        Node node1 = getNode(index1);
        Node node2 = getNode(index2);

        if (node1 == null) return index2;
        else if (node2 == null) return index1;
        else if (node1.priority() < node2.priority()) return index1;
        else return index2;
    }

    /* Returns the Node with the smallest priority value, but does not remove it
     * from the heap. */
    public Node peek() {
        if (size() > 0) return getNode(1);
        else return null;
    }

    /* Bubbles up the node currently at the given index. */
    private void bubbleUp(int index) {
        int i = index;
        int j = getParentOf(i);
        while (getNode(j) != null && getNode(i) != null && getNode(j).priority() > getNode(i).priority()) {
            swap(j, i);
            i = j;
            j = getParentOf(i);
        }
    }

    /* Bubbles down the node currently at the given index. */
    private void bubbleDown(int index) {
        int i = index;
        while (getNode(getLeftOf(i)) != null || getNode(getRightOf(i)) != null) {
            int temp = min(getLeftOf(i), getRightOf(i));
            if (getNode(temp).priority() < getNode(i).priority()) {
                swap(temp, i);
                i = temp;
            } else {
                return;
            }
        }
    }

    /* Inserts an item with the given priority value. Same as enqueue, or offer. */
    public void insert(T item, double priority) {
        if (item == null) return;
        setNode(size() + 1, new Node(item, priority));
        bubbleUp(size());

    }

    /* Returns the element with the smallest priority value, and removes it from
     * the heap. Same as dequeue, or poll. */
    public T removeMin() {
        T result;
        if (size() > 0) {
            result = getNode(1).item();
        } else {
            return null;
        }

        swap(1, size());
        removeNode(size());
        bubbleDown(1);

        return result;
    }

    /* Changes the node in this heap with the given item to have the given
     * priority. You can assume the heap will not have two nodes with the same
     * item. Check for item equality with .equals(), not == */
    public void changePriority(T item, double priority) {
        for (int i = 1; i <= size(); i ++) {
            if (getNode(i).item().equals(item)) {
                getNode(i).setPriority(priority);
                bubbleUp(i);
                return;
            }
        }
    }

}
