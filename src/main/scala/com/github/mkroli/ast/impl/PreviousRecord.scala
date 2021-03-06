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

import com.github.mkroli.ast.AbstractSyntaxTree0

case class PreviousRecord(offset: Int) extends AbstractSyntaxTree0 {
  require(offset > 0)

  def apply(p: (Seq[Double], Int)) = {
    val (s, i) = p
    s(i - offset)
  }

  override val complexity = 1

  override def toString() = "f(x - %s)".format(offset)
}
