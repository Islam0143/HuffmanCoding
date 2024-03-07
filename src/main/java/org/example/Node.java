package org.example;
public class Node {
    int frequency;
    String word;
    Node left;
    Node right;

    public Node(String word, Node left, Node right) {
        this.word = word;
        this.left = left;
        this.right = right;
    }

    public Node(int frequency, String word) {
        this.frequency = frequency;
        this.word = word;
    }

    public Node(int frequency, Node left, Node right) {
        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }
}
