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
package com.github.mkroli

object NumberSeriesSolverApp extends App {
  if (args.isEmpty) {
    println("syntax: NumberSeriesSolver <n1> [<n2>, ...]")
  } else {
    val solver = new NumberSeriesSolver
    val numberSeries = args.toSeq.map(_.toInt).map(_.toDouble)
    val (algorithm, diff) = solver.evolve(numberSeries)
    println("%.2f\t%d\t%.2f\t%s".format(
      diff,
      algorithm.complexity,
      algorithm(numberSeries, numberSeries.size),
      algorithm))
  }
}
