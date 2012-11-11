package com.github.mkroli.ast.impl

import scala.math.abs

import com.github.mkroli.ast.AbstractSyntaxTree
import com.github.mkroli.ast.AbstractSyntaxTree1

case class Abs(a: AbstractSyntaxTree) extends AbstractSyntaxTree1 {
  def apply(p: (Seq[Double], Int)) = abs(a(p))

  override def short = a.short match {
    case Constant(a) => Constant(abs(a))
    case Abs(a @ Abs(_)) => a
    case a => Abs(a)
  }

  override def toString() = "Abs(%s)".format(a)

  override def copy(a: AbstractSyntaxTree) = Abs(a)
}
