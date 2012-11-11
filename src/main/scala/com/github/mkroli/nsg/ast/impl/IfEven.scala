package com.github.mkroli.nsg.ast.impl

import com.github.mkroli.nsg.ast.AbstractSyntaxTree
import com.github.mkroli.nsg.ast.AbstractSyntaxTree2

case class IfEven(a: AbstractSyntaxTree, b: AbstractSyntaxTree) extends AbstractSyntaxTree2 {
  def apply(p: (Seq[Double], Int)) = {
    val (s, i) = p
    if (i % 2 == 0) a(p) else b(p)
  }

  override def short = (a.short, b.short) match {
    case (a, b) if a == b => a
    case (IfEven(aa, ab), b) => IfEven(aa, b)
    case (a, IfEven(ba, bb)) => IfEven(a, bb)
    case (a, b) => IfEven(a, b)
  }

  override val selfComplexity = 2

  override def copy(a: AbstractSyntaxTree, b: AbstractSyntaxTree) = IfEven(a, b)

  override def toString() = "IfEven(%s, %s)".format(a, b)
}
