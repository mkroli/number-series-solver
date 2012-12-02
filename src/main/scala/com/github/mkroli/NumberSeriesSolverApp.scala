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

  val parser = new OptionParser[Config](BuildInfo.name, BuildInfo.version) {
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
    val solver = new NumberSeriesSolver(finished = { (generation, error, algorithm) =>
      if (c.verbose) {
        println("Generation %d diff = %.2f%s %d => f(x) = %s".format(
          generation,
          error,
          if (error == 0.0) " (solved)" else "",
          algorithm(c.numberSeries, c.numberSeries.size).toInt,
          algorithm))
      }
      error == 0.0 || generation >= 10000
    })
    val (algorithm, error) = solver.evolve(c.numberSeries)
    if (c.verbose) {
      println("%.2f\t%d\t%.2f\tf(x) = %s".format(
        error,
        algorithm.complexity,
        algorithm(c.numberSeries, c.numberSeries.size),
        algorithm))
    } else {
      println("%.2f\tf(x) = %s".format(
        algorithm(c.numberSeries, c.numberSeries.size),
        algorithm))
    }
  }
}
