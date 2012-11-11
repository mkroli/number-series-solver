package com.github.mkroli.nsg.ast.impl

import com.github.mkroli.nsg.ast.AbstractSyntaxTree0

case class Constant(a: Double) extends AbstractSyntaxTree0 {
  def apply(p: (Seq[Double], Int)) = a

  override val complexity = 0

  override def toString() = a.toString
}
