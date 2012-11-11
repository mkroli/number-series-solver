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
