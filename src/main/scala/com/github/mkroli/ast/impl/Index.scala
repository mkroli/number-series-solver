package com.github.mkroli.ast.impl

import com.github.mkroli.ast.AbstractSyntaxTree0

case class Index() extends AbstractSyntaxTree0 {
  override def apply(p: (Seq[Double], Int)) =
    p match { case (_, i) => (i + 1).toDouble }

  override val complexity = 0

  override def toString() = "Index"
}
