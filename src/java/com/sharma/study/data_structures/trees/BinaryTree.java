package com.sharma.study.data_structures.trees;

import java.util.*;

public class BinaryTree<T> implements Tree<T> {
    private BinaryTreeNode<T> root;

    public BinaryTree() {}

    public BinaryTree(Collection<T> collection) {
        collection.forEach(this::add);
    }

    /**
     * @return the number of nodes in this tree.
     */
    @Override
    public int size() {
        return size(root);
    }

    /**
     * Node is inserted while preserving balanced invariant.
     * @param data inserts a new node with the specified data point into this tree.
     */
    @Override
    public void add(T data) {
        root = add(root, data);
    }

    private static <T> BinaryTreeNode<T> add(BinaryTreeNode<T> n, T data) {
        if (n == null) return new BinaryTreeNode<>(data);
        if (n.left == null || (n.right != null && (n.left.size <= n.right.size))) {  n.left  = add(n.left,  data); }
        else                                                                      {  n.right = add(n.right, data); }
        if (n.left  != null) {  n.left.parent = n; }
        if (n.right != null) { n.right.parent = n; }
        n.size = 1 + size(n.left) + size(n.right);
        return n;
    }

    /**
     * @return {@code true} if this tree contains no nodes, {@code false} otherwise.
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Removes node with specified data point (if it exists) while maintaining balanced invariant.
     * @param data data point associated with node to remove.
     */
    @Override
    public void remove(T data) {
        root = remove(root, data);
        updateSizes(root);
    }

    private static <T> BinaryTreeNode<T> remove(BinaryTreeNode<T> n, T data) {
        if (n == null) return null;
        if (n.data.equals(data)) {
            if (n.isLeaf()) return null;
            else {
                final int cmp = Integer.compare(size(n.left), size(n.right));
                if (cmp <= 0)   swap(n, n.left);
                else            swap(n, n.right);
            }
        }
        n.left  = remove(n.left,  data);
        n.right = remove(n.right, data);
        return n;
    }

    private static <T> void updateSizes(BinaryTreeNode<T> n) {
        if (n == null) return;
        updateSizes(n.left);
        updateSizes(n.right);
        n.size = 1 + size(n.left) + size(n.right);
    }

    private static <T> void swap(BinaryTreeNode<T> parent, BinaryTreeNode<T> child) {
        if (child == null) return;
        final var t = parent.data;
        parent.data = child.data;
        child.data = t;
    }

    /**
     * @param data data point associated with node to check for existence.
     * @return {@code true} if node associated with the specified data point exists, {@code false} otherwise.
     */
    @Override
    public boolean contains(T data) {
        return contains(root, data);
    }

    private static <T> boolean contains(BinaryTreeNode<T> n, T data) {
        if (n == null) return false;
        return n.data.equals(data) || contains(n.left, data) || contains(n.right, data);
    }

    private static <T> int size(BinaryTreeNode<T> n) {
        return n == null ? 0 : n.size;
    }

    private static <T> BinaryTreeNode<T> findFirst(BinaryTreeNode<T> n, T data) {
        if (n == null) return null;
        if (n.data.equals(data)) return n;
        n.left  = findFirst(n.left,  data);
        n.right = findFirst(n.right, data);
        return n;
    }

    /**
     * Find the lowest common ancestor of the two given data points.
     * @param c0 first data point.
     * @param c1 second data point.
     * @return the data point associated with the lowest common ancestor.
     */
    public T findLCA(T c0, T c1) {
        final var n = findLCA(root, findFirst(root, c0), findFirst(root, c1)).ancestor;
        return n == null ? null : n.data;
    }

    private static <T> LCAStatus<T> findLCA(BinaryTreeNode<T> n, BinaryTreeNode<T> c0, BinaryTreeNode<T> c1) {
        if (n == null) return new LCAStatus<>(null, 0);
        final var l = findLCA(n.left,  c0, c1);
        if (l.numFound == 2) return l;
        final var r = findLCA(n.right, c0, c1);
        if (r.numFound == 2) return r;
        final int numHere = ((n == c0 ? 1 : 0) + (n == c1 ? 1 : 0)) + l.numFound + r.numFound;
        return new LCAStatus<>(numHere == 2 ? n : null, numHere);
    }

    private static final class LCAStatus<T> {
        private BinaryTreeNode<T> ancestor;
        private int numFound;

        private LCAStatus(BinaryTreeNode<T> ancestor, int numFound) {
            this.ancestor = ancestor;
            this.numFound = numFound;
        }
    }

    public boolean isBalanced() {
        return isBalanced(root).isBalanced;
    }

    private static <T> BalancedStatus isBalanced(BinaryTreeNode<T> n) {
        if (n == null) return new BalancedStatus(true, -1); // one level below lowest leaf.
        final var l = isBalanced(n.left);
        if (!l.isBalanced) return l; // early termination from left.
        final var r = isBalanced(n.right);
        if (!r.isBalanced) return r; // early termination from right.
        return new BalancedStatus(
                Math.max(l.height, r.height) <= 1,
                   Math.abs(l.height - r.height) + 1
        );
    }

    private static final class BalancedStatus {
        private boolean isBalanced;
        private int height;

        private BalancedStatus(boolean isBalanced, int height) {
            this.isBalanced = isBalanced;
            this.height = height;
        }
    }

    /**
     * Defines an interface for retrieving the traversal path.
     * @param <T> type of data stored in {@link BinaryTree} nodes.
     */
    interface TraversalPath<T> {
        List<T> recursive(BinaryTree<T> tree);
        List<T> iterative(BinaryTree<T> tree);
    }

    public static class InOrderPath<T> implements TraversalPath<T> {

        @Override
        public List<T> recursive(BinaryTree<T> tree) {
            final var path = new LinkedList<T>();
            recursive(tree.root, path);
            return path;
        }

        private void recursive(BinaryTreeNode<T> n, List<T> path) {
            if (n != null) {
                recursive(n.left,  path);   // go left.
                path.add(n.data);           // go up.
                recursive(n.right, path);   // go right.
            }
        }

        @Override
        public List<T> iterative(BinaryTree<T> tree) {
            if (tree == null || tree.root == null) return Collections.emptyList();
            var n = tree.root;
            final var stack = new ArrayDeque<BinaryTreeNode<T>>();
            final var path = new LinkedList<T>();
            while (!stack.isEmpty() || n != null) {
                if (n != null) {
                    stack.push(n);
                    n = n.left;         // go left.
                } else {
                    n = stack.pop();    // go up.
                    path.add(n.data);
                    n = n.right;        // go right.
                }
            }
            return path;
        }
    }

    public static class PreOrderPath<T> implements TraversalPath<T> {

        @Override
        public List<T> recursive(BinaryTree<T> tree) {
            final var path = new LinkedList<T>();
            recursive(tree.root, path);
            return path;
        }

        private void recursive(BinaryTreeNode<T> n, List<T> path) {
            if (n != null) {
                path.add(n.data);           // go up.
                recursive(n.left,  path);   // go left.
                recursive(n.right, path);   // go right.
            }
        }

        @Override
        public List<T> iterative(BinaryTree<T> tree) {
            if (tree == null || tree.root == null) return Collections.emptyList();
            final var stack = new ArrayDeque<>(List.of(tree.root));
            final var path = new LinkedList<T>();
            while (!stack.isEmpty()) {
                final var n = stack.pop();
                path.add(n.data);
                if (n.right != null) stack.push(n.right);
                if (n.left  != null) stack.push(n.left);
            }
            return path;
        }
    }

    public static class PostOrderPath<T> implements TraversalPath<T> {

        @Override
        public List<T> recursive(BinaryTree<T> tree) {
            final var path = new LinkedList<T>();
            recursive(tree.root, path);
            return path;
        }

        private void recursive(BinaryTreeNode<T> n, List<T> path) {
            if (n != null) {
                recursive(n.left,  path);   // go left.
                recursive(n.right, path);   // go right.
                path.add(n.data);           // go up.
            }
        }

        @Override
        public List<T> iterative(BinaryTree<T> tree) {
            if (tree == null || tree.root == null) return Collections.emptyList();
            final var stack = new ArrayDeque<>(List.of(tree.root));
            final var path = new LinkedList<T>();
            while (!stack.isEmpty()) {
                final var n = stack.pop();
                if (n.left  != null) stack.push(n.left);
                if (n.right != null) stack.push(n.right);
                path.push(n.data);
            }
            return path;
        }
    }

    public static BinaryTree<Integer> merge(BinaryTree<Integer> first, BinaryTree<Integer> second) {
        if (first == null || first.isEmpty() || second == null || second.isEmpty()) return null;
        final var merged = new BinaryTree<Integer>();
        merged.root = merge(first.root, second.root);
        return merged;
    }

    private static BinaryTreeNode<Integer> merge(BinaryTreeNode<Integer> n0, BinaryTreeNode<Integer> n1) {
        if (n0 == null && n1 == null) return null;
        final var n2 = new BinaryTreeNode<>((n0 == null ? 0 : n0.data) + (n1 == null ? 0 : n1.data));
        n2.left  = merge(n0 == null ? null : n0.left,  n1 == null ? null : n1.left);
        n2.right = merge(n0 == null ? null : n0.right, n1 == null ? null : n1.right);
        return n2;
    }


    public boolean isSymmetric() {
        return root == null || isSymmetric(root.left, root.right);
    }

    private static <T> boolean isSymmetric(BinaryTreeNode<T> n0, BinaryTreeNode<T> n1) {
        if (n0 == null && n1 == null) return true;
        return (n0 != null && n1 != null)
                && n0.equals(n1)
                && isSymmetric(n0.left, n1.right)
                && isSymmetric(n0.right, n1.left);
    }

    public void invert() {
        root = invert(root);
    }

    private static <T> BinaryTreeNode<T> invert(BinaryTreeNode<T> n) {
        if (n == null) return null;
        final var right = n.right;
        n.right = invert(n.left);
        n.left  = invert(right);
        return n;
    }

    static class BinaryTreeNode<T> {
        private BinaryTreeNode<T> left, right, parent, next;
        private int size = 1;
        private T data;

        BinaryTreeNode(T data) {
            this.data = data;
        }

        boolean isLeaf() {
            return this.left == null && this.right == null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BinaryTreeNode<?> that = (BinaryTreeNode<?>) o;
            return Objects.equals(data, that.data);
        }

        @Override
        public int hashCode() {
            return Objects.hash(data);
        }

        @Override
        public String toString() {
            return String.valueOf(data);
        }
    }

    @Override
    public String toString() {
        final var sb = new StringBuilder();
        toString(root, "", sb, true);
        return sb.toString();
    }

    private static <T> void toString(BinaryTreeNode<T> n, String pre, StringBuilder sb, boolean isLeft) {
        if (n == null) sb.append("Empty Tree.");
        else {
            if (n.right != null) toString(n.right, pre + (isLeft ? "│   " : "    "), sb, false);
            sb.append(pre).append(isLeft ? "└── " : "┌── ").append(n).append("\n");
            if (n.left  != null) toString(n.left,  pre + (isLeft ? "    " : "│   "), sb, true);
        }
    }

    public static void main(String[] args) {
        final var binTree = new BinaryTree<>(List.of(1, 2, 3, 4, 5, 6, 7));
        final var binTree2 = new BinaryTree<>(List.of(1, 2, 3, 4, 5, 6, 7));
        System.out.println(binTree);
        final var preOrderPath = new BinaryTree.PreOrderPath<Integer>();
        final var postOrderPath = new BinaryTree.PostOrderPath<Integer>();
        final var inOrderPath = new BinaryTree.InOrderPath<Integer>();
        System.out.println(preOrderPath.iterative(binTree));
        System.out.println(postOrderPath.iterative(binTree));
        System.out.println(inOrderPath.iterative(binTree));
        System.out.println(merge(binTree, binTree2));
    }
}
