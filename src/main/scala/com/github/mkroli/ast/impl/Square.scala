package com.github.mkroli.ast.impl

import scala.math.pow

import com.github.mkroli.ast.AbstractSyntaxTree
import com.github.mkroli.ast.AbstractSyntaxTree1

case class Square(a: AbstractSyntaxTree) extends AbstractSyntaxTree1 {
  def apply(p: (Seq[Double], Int)) = {
    a(p) * a(p)
  }

  override def short = a.short match {
    case Constant(a) => Constant(pow(a, 2))
    case a => Square(a)
  }

  override def copy(a: AbstractSyntaxTree) = Square(a)

  override def toString() = "%s^2".format(a)
}
