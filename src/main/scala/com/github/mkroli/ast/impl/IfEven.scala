/*
 * Copyright 2012 Michael Krolikowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mkroli.ast.impl

import com.github.mkroli.ast.AbstractSyntaxTree
import com.github.mkroli.ast.AbstractSyntaxTree2

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

  override def toString() = "if (x %% 2 == 0) %s else %s)".format(a, b)
}
