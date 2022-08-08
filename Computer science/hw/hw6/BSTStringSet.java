import java.util.*;

/**
 * Implementation of a BST based String Set.
 * @author Charlie Zhou
 */
public class BSTStringSet implements SortedStringSet, Iterable<String> {
    public BSTStringSet() {
        root = null;
    }

    @Override
    public boolean contains(String s) {
        Node last = find(s);
        return last != null && s.equals(last.s);
    }

    @Override
    public void put(String s) {
        Node last = find(s);
        if (last == null) {
            root = new Node(s);
        } else {
            int c = s.compareTo(last.s);
            if (c < 0) {
                last.left = new Node(s);
            } else if (c > 0) {
                last.right = new Node(s);
            }
        }
    }

    @Override
    public List<String> asList() {
        ArrayList<String> result = new ArrayList<>();
        for (String label : this) {
            result.add(label);
        }
        return result;
    }

    @Override
    public Iterator<String> iterator() {
        return new BSTIterator(root);
    }

    @Override
    public Iterator<String> iterator(String low, String high) {
        return new inorderIterator(root,low,high);
    }


    private Node find(String s) {
        if (root == null) {
            return null;
        }
        Node p;
        p = root;
        while (true) {
            int c = s.compareTo(p.s);
            Node next;
            if (c < 0) {
                next = p.left;
            } else if (c > 0) {
                next = p.right;
            } else {
                return p;
            }
            if (next == null) {
                return p;
            } else {
                p = next;
            }
        }
    }


    private static class Node {
        private String s;
        private Node left;
        private Node right;
        public Node(String sp) {
            s = sp;
        }
    }


    private static class BSTIterator implements Iterator<String> {

        private Stack<Node> _toDo = new Stack<>();

        BSTIterator(Node node) {
            addTree(node);
        }

        @Override
        public boolean hasNext() {
            return !_toDo.empty();
        }
        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Node node = _toDo.pop();
            addTree(node.right);
            return node.s;
        }
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private void addTree(Node node) {
            while (node != null) {
                _toDo.push(node);
                node = node.left;
            }
        }
    }

    private static class inorderIterator implements Iterator<String>{

        inorderIterator(Node node, String low, String high) {
            _low = low;
            _high = high;
            addTree(node);
        }

        private ArrayDeque<Node> _toDo = new ArrayDeque<>();

        @Override
        public boolean hasNext() {
            return !_toDo.isEmpty();
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Node node = _toDo.pop();
            addTree(node.right);
            return node.s;
        }

        private void addTree(Node node) {
            if (node != null) {
                if (node.s.compareTo(_low)< 0) {
                    addTree(node.right);
                } else if (node.s.compareTo(_high) >= 0) {
                    addTree(node.left);
                } else {
                    _toDo.push(node);
                    addTree(node.left);
                }
            }
        }

        private String _low;
        private String _high;
    }
    private Node root;
}
