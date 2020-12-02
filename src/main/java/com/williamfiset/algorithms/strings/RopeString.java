/**
 * Rope data structure implementation. Rope data structure is a tree data structure (binary tree)
 * which is used to store or manipulate large strings in a more efficient manner.
 *
 * @author kir-push
 */
package com.williamfiset.algorithms.strings;

import java.util.ArrayList;
import java.util.List;

public class RopeString {
  Node root;

  // construct rope from string value with only one leaf node
  public RopeString(String value) {
    this.root = new Node(value);
  }

  // construct rope from node. Node will be root of rope
  public RopeString(Node node) {
    this.root = node;
  }

  // add value to the end
  public void append(String value) {
    root = concatenate(this, new RopeString(value)).root;
  }

  // split rope into two by index
  public Pair<RopeString> split(int index) {
    Node left = new Node();
    Node right = new Node();
    createBranches(left, right, root, index);
    cleanUp(left);
    cleanUp(right);
    return new Pair<>(new RopeString(left), new RopeString(right));
  }

  // insert string in specific position
  public void insert(String value, int position) {
    Pair<RopeString> ropePair = split(position);
    RopeString firstConcat = concatenate(ropePair.left, new RopeString(value));
    RopeString concatenate = concatenate(firstConcat, ropePair.right);
    this.root = concatenate.root;
  }

  // remove specific size of chars, started from position
  public void delete(int position, int size) {
    if (size < 0 || position < 0 || position + size > root.weight) {
      throw new IndexOutOfBoundsException();
    }
    Pair<RopeString> ropePair = split(position);
    Pair<RopeString> resultEnding = ropePair.right.split(size);
    RopeString concatenate = concatenate(ropePair.left, resultEnding.right);
    this.root = concatenate.root;
  }

  // transform value to java string.
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    createString(root, builder);
    return builder.toString();
  }

  // rebalance tree to optimal condition
  public void rebalance() {
    List<Node> nodes = new ArrayList<>();
    collectAll(root, nodes);
    root = combine(nodes, 0, nodes.size());
  }

  // create two branches from left and right node
  // if current node has no child - it's leaf
  private void createBranches(Node left, Node right, Node current, int index) {
    if (current.isLeaf()) {
      splitLeaf(left, right, current, index);
    } else {
      splitNotLeaf(left, right, current, index);
    }
  }

  // recursively goes by rope to index, and create two new ropes in process.
  // Node left and Node right is just two new Node which will be result of split.
  private void splitNotLeaf(Node left, Node right, Node current, int index) {
    left = createNodeIfNull(left);
    right = createNodeIfNull(right);
    current.left = createNodeIfNull(current.left);
    current.right = createNodeIfNull(current.right);
    left.weight = index;
    right.weight = current.weight - index;

    if (index < current.left.weight) {
      right.right = current.right;
      right.left = new Node();
      createBranches(left, right.left, current.left, index);
      right.depth = calculateDepth(right) + 1;
    } else {
      left.left = current.left;
      left.right = new Node();
      createBranches(left.right, right, current.right, index - current.left.weight);
      left.depth = calculateDepth(left) + 1;
    }
  }

  private Node createNodeIfNull(Node node) {
    if (node == null) {
      return new Node();
    }
    return node;
  }

  // if current node leaf, split string, and set left part to left node, and right part to right
  // node.
  private void splitLeaf(Node left, Node right, Node current, int index) {
    String splitValue = current.value;
    int length = splitValue.length();
    if (index > 0) {
      left.value = splitValue.substring(0, index);
      left.weight = left.value.length();
    }
    if (length != index) {
      right.value = splitValue.substring(index);
      right.weight = right.value.length();
    }
    left.depth = 0;
    right.depth = 0;
  }

  // merge two ropes to one parent
  // return new parent
  private RopeString concatenate(RopeString left, RopeString right) {
    if (left == null || right == null) {
      return left != null ? left : right;
    }
    Node rootNode = new Node();
    rootNode.weight = left.root.weight + right.root.weight;
    rootNode.left = left.root;
    rootNode.right = right.root;
    rootNode.depth = calculateDepth(rootNode) + 1;
    cleanUp(rootNode);
    return new RopeString(rootNode);
  }

  // recursively clean from nulled nodes,
  // if node have only one child, delete node, and set child to node's parent.
  private void cleanUp(Node node) {
    if (node == null || node.isLeaf()) {
      return;
    }
    Node left = node.left;
    Node right = node.right;

    if (left != null ^ right != null) {
      Node child = left != null ? left : right;
      cleanUp(child);
      node.value = child.value;
      node.depth = child.depth;
      node.left = child.left;
      node.right = child.right;
    } else {
      cleanUp(node.left);
      cleanUp(node.right);
      node.depth = calculateDepth(node) + 1;
    }

    if (right != null && right.value == null && right.isLeaf()) {
      node.right = null;
    }
    if (left != null && left.value == null && left.isLeaf()) {
      node.left = null;
    }
  }

  // calculates the depth of the node tree (without node itself)
  private int calculateDepth(Node node) {
    Node left = node.left;
    Node right = node.right;
    int leftDepth = left != null ? left.depth : 0;
    int rightDepth = right != null ? right.depth : 0;
    return Math.max(leftDepth, rightDepth);
  }

  // recursively populate stringBuilder by nodes values
  private void createString(Node node, StringBuilder builder) {
    if (node == null) {
      return;
    }
    if (node.isLeaf() && node.value != null) {
      builder.append(node.value);
      return;
    }
    createString(node.left, builder);
    createString(node.right, builder);
  }

  // collect all leafs to nodeList
  private void collectAll(Node node, List<Node> nodeList) {
    if (node == null) {
      return;
    }
    if (node.isLeaf()) {
      nodeList.add(node);
      return;
    }
    collectAll(node.left, nodeList);
    collectAll(node.right, nodeList);
  }

  // combine all nodes to parents
  private Node combine(List<Node> nodeList, int start, int end) {
    int range = end - start;
    switch (range) {
      case 1:
        return nodeList.get(start);
      case 2:
        return createParentNode(nodeList.get(start), nodeList.get(start + 1));
      default:
        int middle = (range / 2) + start;
        return createParentNode(combine(nodeList, start, middle), combine(nodeList, middle, end));
    }
  }

  // create parent node for two orphans
  private Node createParentNode(Node left, Node right) {
    Node node = new Node();
    node.weight = left.weight + right.weight;
    node.left = left;
    node.right = right;
    node.depth = calculateDepth(node) + 1;
    return node;
  }

  // node of the Rope tree
  static class Node {

    Node left;
    Node right;
    // String contained in node, if exists
    String value;
    // Sum of left nodes lengths
    int weight;
    // Length of child chains
    int depth;

    public Node() {}

    public Node(String value) {
      this.value = value;
      this.weight = value.length();
    }

    public boolean isLeaf() {
      return left == null && right == null;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  // inner class for convenience
  public static class Pair<Rope> {
    public Rope left;
    public Rope right;

    public Pair(Rope left, Rope right) {
      this.left = left;
      this.right = right;
    }
  }
}
