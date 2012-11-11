package com.github.mkroli.nsg.ast.impl

import com.github.mkroli.nsg.ast.AbstractSyntaxTree
import com.github.mkroli.nsg.ast.AbstractSyntaxTree2

case class Multiplication(a: AbstractSyntaxTree, b: AbstractSyntaxTree) extends AbstractSyntaxTree2 {
  def apply(p: (Seq[Double], Int)) = a(p) * b(p)

  override def short = (a.short, b.short) match {
    case (Constant(a), Constant(b)) => Constant(a * b)
    case (_, Constant(b)) if b == 0.0 => Constant(0.0)
    case (Constant(a), _) if a == 0.0 => Constant(0.0)
    case (a, Constant(b)) if b == 1.0 => a
    case (Constant(a), b) if a == 1.0 => b
    case (a, b) => Multiplication(a, b)
  }

  override def copy(a: AbstractSyntaxTree, b: AbstractSyntaxTree) = Multiplication(a, b)

  override def toString() = "(%s * %s)".format(a, b)
}
