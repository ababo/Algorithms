package com.williamfiset.algorithms.datastructures.rope;

import java.util.ArrayList;
import java.util.List;

/**
 * Rope implementation
 *
 * @author kir-push
 */
public class Rope {
  private Node root;

  public Rope(String value) {
    this.root = new Node(value);
  }

  public Rope(Node node) {
    this.root = node;
  }

  public Node getRoot() {
    return root;
  }
  // add value to the end
  public void append(String value) {
    root = concatenate(this, new Rope(value)).getRoot();
  }

  // split rope into two by index
  public Pair<Rope> split(int index) {
    Node left = new Node();
    Node right = new Node();
    split(left, right, getRoot(), index);
    cleanUp(left);
    cleanUp(right);
    return new Pair<Rope>(new Rope(left), new Rope(right));
  }

  // insert string in specific position
  public void insert(String value, int position) {
    Pair<Rope> ropePair = split(position);
    Rope firstConcat = concatenate(ropePair.getLeft(), new Rope(value));
    Rope concatenate = concatenate(firstConcat, ropePair.getRight());
    this.root = concatenate.getRoot();
  }

  // remove specific size of chars, started from position
  public void delete(int position, int size) {
    if (position + size > getRoot().getWeight()) {
      throw new IndexOutOfBoundsException();
    }
    Pair<Rope> ropePair = split(position);
    Pair<Rope> resultEnding = ropePair.getRight().split(size);
    Rope concatenate = concatenate(ropePair.getLeft(), resultEnding.getRight());
    this.root = concatenate.getRoot();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    createString(root, builder);
    return builder.toString();
  }

  // rebalance tree to optimal condition
  public void reBalance() {
    List<Node> nodes = new ArrayList<Node>();
    collectAll(getRoot(), nodes);
    root = combine(nodes, 0, nodes.size());
  }

  // inner class for convenience
  public static class Pair<Rope> {
    private final Rope left;
    private final Rope right;

    public Pair(Rope left, Rope right) {
      this.left = left;
      this.right = right;
    }

    public Rope getLeft() {
      return left;
    }

    public Rope getRight() {
      return right;
    }
  }

  // if current node has no child - it's leaf
  private void split(Node left, Node right, Node current, int index) {
    if (current.hasNoChilds()) {
      splitLeaf(left, right, current, index);
    } else {
      splitNotLeaf(left, right, current, index);
    }
  }

  // recursively goes by rope to index, and create two new ropes in process.
  // Node left and Node right is just two new Node which will be result of split.
  private void splitNotLeaf(Node left, Node right, Node current, int index) {
    left.setWeight(index);
    right.setWeight(current.getWeight() - index);
    if (current.getLeft() == null) {
      current.setLeft(new Node());
    } else if (current.getRight() == null) {
      current.setRight(new Node());
    }

    if (index < current.getLeft().getWeight()) {
      right.setRight(current.getRight());
      right.setLeft(new Node());
      split(left, right.getLeft(), current.getLeft(), index);
      right.setDepth(calculateDepth(right) + 1);
    } else {
      left.setLeft(current.getLeft());
      left.setRight(new Node());
      split(left.getRight(), right, current.getRight(), index - current.getLeft().getWeight());
      left.setDepth(calculateDepth(left) + 1);
    }
  }

  // if current node leaf, split string, and set left part to left node, and right part to right
  // node.
  private void splitLeaf(Node left, Node right, Node current, int index) {
    String splitValue = current.getValue();
    int length = splitValue.length();
    if (index > 0) {
      left.setValue(splitValue.substring(0, index));
      left.setWeight(left.getValue().length());
    }
    if (length != index) {
      right.setValue(splitValue.substring(index));
      right.setWeight(right.getValue().length());
    }
    left.setDepth(0);
    right.setDepth(0);
  }

  // merge two ropes to one parent
  // return new parent
  private Rope concatenate(Rope left, Rope right) {
    if (left == null || right == null) {
      return left != null ? left : right;
    }
    Node rootNode = new Node();
    rootNode.setWeight(left.getRoot().getWeight() + right.getRoot().getWeight());
    rootNode.setLeft(left.getRoot());
    rootNode.setRight(right.getRoot());
    rootNode.setDepth(calculateDepth(rootNode) + 1);
    cleanUp(rootNode);
    return new Rope(rootNode);
  }

  // recursively clean from nulled nodes,
  // if node have only one child, delete node, and set child to node's parent.
  private void cleanUp(Node node) {
    if (node == null || node.hasNoChilds()) {
      return;
    }
    Node left = node.getLeft();
    Node right = node.getRight();

    if ((left == null && right != null) || (left != null && right == null)) {
      Node child = left != null ? left : right;
      cleanUp(child);
      node.setValue(child.getValue());
      node.setDepth(child.getDepth());
      node.setLeft(child.getLeft());
      node.setRight(child.getRight());
    } else {
      cleanUp(node.getLeft());
      cleanUp(node.getRight());
      node.setDepth(calculateDepth(node) + 1);
    }

    if (right != null && right.getValue() == null && right.hasNoChilds()) {
      node.setRight(null);
    }
    if (left != null && left.getValue() == null && left.hasNoChilds()) {
      node.setLeft(null);
    }
  }

  // calculates the depth of the node tree (without node itself)
  private int calculateDepth(Node node) {
    Node left = node.getLeft();
    Node right = node.getRight();
    int leftDepth = left != null ? left.getDepth() : 0;
    int rightDepth = right != null ? right.getDepth() : 0;
    return Math.max(leftDepth, rightDepth);
  }

  // recursively populate stringBuilder by nodes values
  private void createString(Node node, StringBuilder builder) {
    if (node == null) {
      return;
    }
    if (node.hasNoChilds() && node.getValue() != null) {
      builder.append(node.getValue());
      return;
    }
    createString(node.getLeft(), builder);
    createString(node.getRight(), builder);
  }

  // collect all leafs to nodeList
  private void collectAll(Node node, List<Node> nodeList) {
    if (node == null) {
      return;
    }
    if (node.hasNoChilds()) {
      nodeList.add(node);
      return;
    }
    collectAll(node.getLeft(), nodeList);
    collectAll(node.getRight(), nodeList);
  }

  // combine all nodes to parents
  private Node combine(List<Node> nodeList, int start, int end) {
    int range = end - start;
    if (range == 1) {
      return nodeList.get(start);
    } else if (range == 2) {
      return createParentNode(nodeList.get(start), nodeList.get(start + 1));
    } else {
      int middle = (range / 2) + start;
      return createParentNode(combine(nodeList, start, middle), combine(nodeList, middle, end));
    }
  }

  // create parent node for two orphans
  private Node createParentNode(Node left, Node right) {
    Node node = new Node();
    node.setWeight(left.getWeight() + right.getWeight());
    node.setLeft(left);
    node.setRight(right);
    node.setDepth(calculateDepth(node) + 1);
    return node;
  }
}
