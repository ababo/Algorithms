package com.williamfiset.algorithms.datastructures.rope;

import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class RopeTest {

  private Rope rope;

  @Before
  public void setUp() {
    rope = new Rope("da");
  }

  @Test
  public void ropeCreatingTest() {
    assertThat(rope.toString()).isEqualTo("da");
    assertThat(rope.getRoot().getWeight()).isEqualTo(2);
  }

  @Test
  public void ropeAppendTest() {
    rope.append("net");
    assertThat(rope.toString()).isEqualTo("danet");
    assertThat(rope.getRoot().getWeight()).isEqualTo(5);
    assertThat(rope.getRoot().getLeft().toString()).isEqualTo("da");
    assertThat(rope.getRoot().getRight().toString()).isEqualTo("net");
  }

  @Test
  public void insertTest() {
    rope.insert("sobaka ", 0);
    assertThat(rope.toString()).isEqualTo("sobaka da");
    rope.insert(" samokat!", rope.getRoot().getWeight());
    assertThat(rope.toString()).isEqualTo("sobaka da samokat!");
    rope.insert(" dama", rope.getRoot().getWeight() / 2);
    assertThat(rope.toString()).isEqualTo("sobaka da dama samokat!");
    assertThat(rope.getRoot().getWeight()).isEqualTo(23);
  }

  @Test
  public void removeTest() {
    rope.delete(0, 1);
    assertThat(rope.toString()).isEqualTo("a");
    rope.delete(0, 1);
    assertThat(rope.toString()).isEqualTo("");
    rope.append("bim");
    rope.append("beloe");
    rope.append("uho");
    rope.delete(3, 5);
    assertThat(rope.toString()).isEqualTo("bimuho");
  }

  @Test
  public void rebalanceTest() {
    rope.append("bim");
    rope.append("beloe");
    rope.append("uho");
    rope.delete(0, 1);
    rope.insert(" dama", rope.getRoot().getWeight() / 2);
    rope.delete(3, 5);
    rope.insert(" samokat!", rope.getRoot().getWeight());
    rope.insert("sobaka ", 0);
    assertThat(rope.getRoot().getDepth()).isEqualTo(5);
    rope.reBalance();
    assertThat(rope.toString()).isEqualTo("sobaka abiamaloeuho samokat!");
    assertThat(rope.getRoot().getWeight()).isEqualTo(28);
    assertThat(rope.getRoot().getDepth()).isEqualTo(3);
  }

  @Test
  public void splitTest() {
    rope.append("net");
    Rope.Pair<Rope> split = rope.split(2);
    assertThat(split.getLeft().toString()).isEqualTo("da");
    assertThat(split.getRight().toString()).isEqualTo("net");
    rope.append("bim");
    rope.append("beloe");
    rope.append("uho");
    rope.delete(0, 1);
    rope.insert(" dama", rope.getRoot().getWeight() / 2);
    rope.delete(3, 5);
    rope.insert(" samokat!", rope.getRoot().getWeight());
    rope.insert("sobaka ", 0);
    split = rope.split(rope.getRoot().getWeight() / 2);
    assertThat(split.getLeft().toString()).isEqualTo("sobaka anedamab");
    assertThat(split.getRight().toString()).isEqualTo("eloeuho samokat!");
  }
}
