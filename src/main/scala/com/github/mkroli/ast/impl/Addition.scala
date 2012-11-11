package com.github.mkroli.ast.impl

import com.github.mkroli.ast.AbstractSyntaxTree
import com.github.mkroli.ast.AbstractSyntaxTree2

case class Addition(a: AbstractSyntaxTree, b: AbstractSyntaxTree) extends AbstractSyntaxTree2 {
  def apply(p: (Seq[Double], Int)) = a(p) + b(p)

  override def short = (a.short, b.short) match {
    case (Constant(a), Constant(b)) => Constant(a + b)
    case (a, Constant(b)) if b == 0.0 => a
    case (a, Constant(b)) if b < 0 => Subtraction(a, Constant(-b))
    case (Constant(a), b) if a == 0.0 => b
    case (a, b) if a == b => Multiplication(Constant(2), a)
    case (a, b) => Addition(a, b)
  }

  override def copy(a: AbstractSyntaxTree, b: AbstractSyntaxTree) = Addition(a, b)

  override def toString() = "(%s + %s)".format(a, b)
}
