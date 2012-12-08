/*
 * Copyright 2012 Michael Krolikowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mkroli.ast

import math.max

trait AbstractSyntaxTree extends Function[(Seq[Double], Int), Double] {
  def short: AbstractSyntaxTree = this

  val complexity: Int

  val nodes: Int

  val height: Int

  def children: Seq[AbstractSyntaxTree]

  def flatten: Seq[AbstractSyntaxTree] = this :: children.toList.flatMap(_.flatten)

  def depth(node: AbstractSyntaxTree, initialDepth: Int = 0): Int

  def replaceNode(node: AbstractSyntaxTree, replacement: AbstractSyntaxTree): AbstractSyntaxTree
}

trait AbstractSyntaxTree0 extends AbstractSyntaxTree {
  override val nodes = 1

  override val height = 1

  override def children = Nil

  override def depth(node: AbstractSyntaxTree, initialDepth: Int) = {
    if (this eq node) initialDepth
    else -1
  }

  override def replaceNode(node: AbstractSyntaxTree, replacement: AbstractSyntaxTree) = {
    if (this eq node) replacement
    else this
  }
}

trait AbstractSyntaxTree1 extends AbstractSyntaxTree {
  val a: AbstractSyntaxTree

  val selfComplexity = 1

  override val complexity = selfComplexity + a.complexity

  override val nodes = 1 + a.nodes

  override val height = 1 + a.height

  override def children = a :: Nil

  def copy(a: AbstractSyntaxTree = this.a): AbstractSyntaxTree

  override def depth(node: AbstractSyntaxTree, initialDepth: Int) = {
    if (this eq node) initialDepth
    else a.depth(node, initialDepth + 1)
  }

  override def replaceNode(node: AbstractSyntaxTree, replacement: AbstractSyntaxTree) = {
    if (this eq node) replacement
    else copy(a.replaceNode(node, replacement))
  }
}

trait AbstractSyntaxTree2 extends AbstractSyntaxTree {
  val a: AbstractSyntaxTree
  val b: AbstractSyntaxTree

  val selfComplexity = 1

  override val complexity = selfComplexity + a.complexity + b.complexity

  override val nodes = 1 + a.nodes + b.nodes

  override val height = 1 + max(a.height, b.height)

  override def children = a :: b :: Nil

  def copy(a: AbstractSyntaxTree = this.a, b: AbstractSyntaxTree = this.b): AbstractSyntaxTree2

  override def depth(node: AbstractSyntaxTree, initialDepth: Int) = {
    if (this eq node) initialDepth
    else {
      val ad = a.depth(node, initialDepth + 1)
      if (ad < 0) b.depth(node, initialDepth + 1)
      else ad
    }
  }

  override def replaceNode(node: AbstractSyntaxTree, replacement: AbstractSyntaxTree) = {
    if (this eq node) replacement
    else copy(a.replaceNode(node, replacement), b.replaceNode(node, replacement))
  }
}
