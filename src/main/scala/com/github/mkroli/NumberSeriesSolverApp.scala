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

import scopt.immutable.OptionParser

object NumberSeriesSolverApp extends App {
  case class Config(numberSeries: List[Double] = Nil,
    verbose: Boolean = false)

  val parser = new OptionParser[Config]("number-series-solver", "0.1") {
    def options = Seq(
      help("h", "help", "Display help message"),
      flag("v", "verbose", "Will print additional information during computing") { c =>
        c.copy(verbose = true)
      },
      arglist("number...", "The list of numbers which should be processed") { (n, c) =>
        c.copy(numberSeries = c.numberSeries ::: n.toDouble :: Nil)
      })
  }

  parser.parse(args, Config()).map { c =>
    val solver = new NumberSeriesSolver(verbose = c.verbose)
    val (algorithm, diff) = solver.evolve(c.numberSeries)
    if (c.verbose) {
      println("%.2f\t%d\t%.2f\t%s".format(
        diff,
        algorithm.complexity,
        algorithm(c.numberSeries, c.numberSeries.size),
        algorithm))
    } else {
      println("%.2f\t%s".format(
        algorithm(c.numberSeries, c.numberSeries.size),
        algorithm))
    }
  }
}
