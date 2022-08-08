/**
 * Simple Red-Black tree implementation, where the keys are of type T.
 @ author
 */
public class RedBlackTree<T extends Comparable<T>> {

    /** Root of the tree. */
    private RBTreeNode<T> root;

    /**
     * Empty constructor.
     */
    public RedBlackTree() {
        root = null;
    }

    /**
     * Constructor that builds this from given BTree (2-3-4) tree.
     *
     * @param tree BTree (2-3-4 tree).
     */
    public RedBlackTree(BTree<T> tree) {
        BTree.Node<T> btreeRoot = tree.root;
        root = buildRedBlackTree(btreeRoot);
    }

    /**
     * Builds a RedBlack tree that has isometry with given 2-3-4 tree rooted at
     * given node r, and returns the root node.
     *
     * @param r root of the 2-3-4 tree.
     * @return root of the Red-Black tree for given 2-3-4 tree.
     */
    RBTreeNode<T> buildRedBlackTree(BTree.Node<T> r) {
        if (r == null) {
            return null;
        }

        RBTreeNode rb;
        if (r.getItemCount() == 1) {
            rb = new RBTreeNode<T>(true, r.getItemAt(0),
                    buildRedBlackTree(r.getChildAt(0)),
                    buildRedBlackTree(r.getChildAt(1)));
        } else if (r.getItemCount() == 2) {
            rb = new RBTreeNode<T>(true, r.getItemAt(0),
                    buildRedBlackTree(r.getChildAt(0)),
                    new RBTreeNode<T>(false, r.getItemAt(1),
                            buildRedBlackTree(r.getChildAt(1)),
                            buildRedBlackTree(r.getChildAt(2))));
        } else {
            rb = new RBTreeNode(true, r.getItemAt(1),
                    new RBTreeNode(false, r.getItemAt(0),
                            buildRedBlackTree(r.getChildAt(0)),
                            buildRedBlackTree(r.getChildAt(1))),
                    new RBTreeNode(false, r.getItemAt(2),
                            buildRedBlackTree(r.getChildAt(2)),
                            buildRedBlackTree(r.getChildAt(3))));
        }
        return rb;
    }

    /**
     * Rotates the (sub)tree rooted at given node to the right, and returns the
     * new root of the (sub)tree. If rotation is not possible somehow,
     * immediately return the input node.
     *
     * @param node root of the given (sub)tree.
     * @return new root of the (sub)tree.
     */
    RBTreeNode<T> rotateRight(RBTreeNode<T> node) {
        if (node.left == null) {
            return node;
        }
        node.isBlack = !node.isBlack;
        node.left.isBlack = !node.left.isBlack;
        RBTreeNode temp = node.left;
        node.left = temp.right;
        temp.right = node;
        node = temp;
        return node;
    }
    /**
     * Rotates the (sub)tree rooted at given node to the left, and returns the
     * new root of the (sub)tree. If rotation is not possible somehow,
     * immediately return the input node.
     *
     * @param node root of the given (sub)tree.
     * @return new root of the (sub)tree.
     */
    RBTreeNode<T> rotateLeft(RBTreeNode<T> node) {
        if (node.right == null) {
            return node;
        }
        node.isBlack = !node.isBlack;
        node.right.isBlack = !node.right.isBlack;
        RBTreeNode temp = node.right;
        node.right = temp.left;
        temp.left = node;
        node = temp;
        return node;
    }
    /**
     * Flips the color of the node and its children. Assume that the node has
     * both left and right children.
     *
     * @param node tree node
     */
    void flipColors(RBTreeNode<T> node) {
        node.isBlack = !node.isBlack;
        node.left.isBlack = !node.left.isBlack;
        node.right.isBlack = !node.right.isBlack;
    }

    /**
     * Returns whether a given node is red. null nodes (children of leaf) are
     * automatically considered black.
     *
     * @param node node
     * @return node is red.
     */
    private boolean isRed(RBTreeNode<T> node) {
        return node != null && !node.isBlack;
    }

    /**
     * Insert given item into this tree.
     *
     * @param item item
     */
    void insert(T item) {
        root = insert(root, item);
        root.isBlack = true;
    }

    /**
     * Recursivelty insert item into this tree. returns the (new) root of the
     * subtree rooted at given node after insertion. node == null implies that
     * we are inserting a new node at the bottom.
     *
     * @param node node
     * @param item item
     * @return (new) root of the subtree rooted at given node.
     */
     private RBTreeNode<T> insert(RBTreeNode<T> node, T item) {

        if (node == null) {
            return new RBTreeNode<T>(false, item, null, null);
        }
        int comp = item.compareTo(node.item);
        if (comp == 0) {
            return node; 
        } else if (comp < 0) {
            if (node.left == null) {
                node.left = new RBTreeNode<T>(false, item, null, null);
            } else {
                insert(node.left, item);
            }
        } else {
            if (node.right == null) {
                node.right = new RBTreeNode<T>(false, item, null, null);
            } else {
                insert(node.right, item);
            }
        }
        if (isRed(node.right) && !isRed(node.left)) {
            rotateLeft(node);
        }
        if (isRed(node.left) && isRed(node.left.left)) {
            rotateRight(node);
        }
        if (isRed(node.left) && isRed(node.right)) {
            flipColors(node);
        }
        return node;
    }


    /**
     * RedBlack tree node.
     *
     * @param <T> type of item.
     */
    static class RBTreeNode<T> {

        /** Item. */
        protected final T item;

        /** True if the node is black. */
        protected boolean isBlack;

        /** Pointer to left child. */
        protected RBTreeNode<T> left;

        /** Pointer to right child. */
        protected RBTreeNode<T> right;

        /**
         * A node that is black iff BLACK, containing VALUE, with empty
         * children.
         */
        RBTreeNode(boolean black, T value) {
            this(black, value, null, null);
        }

        /**
         * Node that is black iff BLACK, contains VALUE, and has children
         * LFT AND RGHT.
         */
        RBTreeNode(boolean black, T value,
                   RBTreeNode<T> lft, RBTreeNode<T> rght) {
            isBlack = black;
            item = value;
            left = lft;
            right = rght;
        }
    }

}
