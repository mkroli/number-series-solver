package com.github.mkroli.ast.impl

import scala.math.Pi
import scala.math.cos

import com.github.mkroli.ast.AbstractSyntaxTree
import com.github.mkroli.ast.AbstractSyntaxTree1

case class CosinusPi(a: AbstractSyntaxTree) extends AbstractSyntaxTree1 {
  def apply(p: (Seq[Double], Int)) = cos(a(p) * Pi)

  override def short = a.short match {
    case Constant(a) => Constant(cos(a * Pi))
    case a => CosinusPi(a)
  }

  override val selfComplexity = 2

  override def copy(a: AbstractSyntaxTree) = CosinusPi(a)

  override def toString() = "Cos(Pi * %s)".format(a)
}
