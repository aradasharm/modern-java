package com.sharma.study.data_structures.trees;

public interface Tree<T> {

    /**
     * @return the number of nodes in this tree.
     */
    int size();

    /**
     * @param data inserts a new node with the specified data point into this tree.
     */
    void add(T data);

    /**
     * @return {@code true} if this tree contains no nodes, {@code false} otherwise.
     */
    boolean isEmpty();

    /**
     * @param data data point associated with node to remove.
     */
    void remove(T data);

    /**
     * @param data data point associated with node to check for existence.
     * @return {@code true} if node associated with the specified data point exists, {@code false} otherwise.
     */
    boolean contains(T data);
}
