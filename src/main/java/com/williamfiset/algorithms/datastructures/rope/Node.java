package com.williamfiset.algorithms.datastructures.rope;

/**
 * Node of the Rope Tree
 *
 * @author kir-push
 */
class Node {

  private Node left;
  private Node right;
  // String contained in node, if exists
  private String value;
  // Sum of left nodes lengths
  private int weight;
  // Length of child chains
  private int depth;

  public Node() {}

  public Node(String value) {
    this.value = value;
    this.weight = value.length();
  }

  public boolean hasNoChilds() {
    return left == null && right == null;
  }

  public Node getLeft() {
    return left;
  }

  public void setLeft(Node left) {
    this.left = left;
  }

  public Node getRight() {
    return right;
  }

  public void setRight(Node right) {
    this.right = right;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  public int getDepth() {
    return depth;
  }

  public void setDepth(int depth) {
    this.depth = depth;
  }

  @Override
  public String toString() {
    return value;
  }
}
