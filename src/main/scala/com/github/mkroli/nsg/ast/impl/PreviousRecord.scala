package com.github.mkroli.nsg.ast.impl

import com.github.mkroli.nsg.ast.AbstractSyntaxTree0

case class PreviousRecord(offset: Int) extends AbstractSyntaxTree0 {
  require(offset > 0)

  def apply(p: (Seq[Double], Int)) = {
    val (s, i) = p
    s(i - offset)
  }

  override val complexity = 1

  override def toString() = "PreviousRecord(%s)".format(offset)
}
