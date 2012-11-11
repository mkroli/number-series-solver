package com.github.mkroli.ast

trait AbstractSyntaxTree extends Function[(Seq[Double], Int), Double] {
  def short: AbstractSyntaxTree = this

  val complexity: Int

  val nodes: Int

  def children: Seq[AbstractSyntaxTree]

  def flatten: Seq[AbstractSyntaxTree] = this :: children.toList.flatMap(_.flatten)

  def depth(node: AbstractSyntaxTree, initialDepth: Int = 0): Int

  def replaceNode(node: AbstractSyntaxTree, replacement: AbstractSyntaxTree): AbstractSyntaxTree
}

trait AbstractSyntaxTree0 extends AbstractSyntaxTree {
  override val nodes = 1

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
