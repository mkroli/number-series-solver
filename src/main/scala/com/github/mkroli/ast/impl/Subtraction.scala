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

case class Subtraction(a: AbstractSyntaxTree, b: AbstractSyntaxTree) extends AbstractSyntaxTree2 {
  def apply(p: (Seq[Double], Int)) = a(p) - b(p)

  override def short = (a.short, b.short) match {
    case (Constant(a), Constant(b)) => Constant(a - b)
    case (a, Constant(b)) if b == 0.0 => a
    case (a, Constant(b)) if b < 0 => Addition(a, Constant(-b))
    case (Constant(a), b) if a == 0.0 => b
    case (a, b) if a == b => Constant(0)
    case (a, b) => Subtraction(a, b)
  }

  override def copy(a: AbstractSyntaxTree, b: AbstractSyntaxTree) = Subtraction(a, b)

  override def toString() = "(%s - %s)".format(a, b)
}
