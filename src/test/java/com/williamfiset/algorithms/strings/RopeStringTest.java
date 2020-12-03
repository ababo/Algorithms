package com.williamfiset.algorithms.strings;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Before;
import org.junit.Test;

public class RopeStringTest {

  private RopeString rope;

  @Before
  public void setUp() {
    rope = new RopeString("da");
  }

  @Test
  public void ropeCreatingTest() {
    assertThat(rope.toString()).isEqualTo("da");
    assertThat(rope.root.weight).isEqualTo(2);
  }

  @Test
  public void ropeAppendTest() {
    rope.append("net");
    assertThat(rope.toString()).isEqualTo("danet");
    assertThat(rope.root.weight).isEqualTo(5);
    assertThat(rope.root.left.toString()).isEqualTo("da");
    assertThat(rope.root.right.toString()).isEqualTo("net");
  }

  @Test
  public void insertTest() {
    rope.insert("sobaka ", 0);
    assertThat(rope.toString()).isEqualTo("sobaka da");
    rope.insert(" samokat!", rope.root.weight);
    assertThat(rope.toString()).isEqualTo("sobaka da samokat!");
    rope.insert(" dama", rope.root.weight / 2);
    assertThat(rope.toString()).isEqualTo("sobaka da dama samokat!");
    assertThat(rope.root.weight).isEqualTo(23);
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
    rope.insert(" dama", rope.root.weight / 2);
    rope.delete(3, 5);
    rope.insert(" samokat!", rope.root.weight);
    rope.insert("sobaka ", 0);
    assertThat(rope.root.depth).isEqualTo(5);
    rope.rebalance();
    assertThat(rope.toString()).isEqualTo("sobaka abiamaloeuho samokat!");
    assertThat(rope.root.weight).isEqualTo(28);
    assertThat(rope.root.depth).isEqualTo(3);
  }

  @Test
  public void splitTest() {
    rope.append("net");
    rope.delete(1, 2);
    assertThat(rope.toString()).isEqualTo("det");
    rope.insert("an", 1);
    RopeString.Pair<RopeString> split = rope.split(2);
    assertThat(split.left.toString()).isEqualTo("da");
    assertThat(split.right.toString()).isEqualTo("net");
    rope.append("bim");
    rope.append("beloe");
    rope.append("uho");
    rope.delete(0, 1);
    rope.insert(" dama", rope.root.weight / 2);
    rope.delete(3, 5);
    rope.insert(" samokat!", rope.root.weight);
    rope.insert("sobaka ", 0);
    split = rope.split(rope.root.weight / 2);
    assertThat(split.left.toString()).isEqualTo("sobaka anedamab");
    assertThat(split.right.toString()).isEqualTo("eloeuho samokat!");
  }
}
