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

import scala.annotation.tailrec
import scala.collection.immutable.Stream.consWrapper
import scala.math.abs
import scala.math.pow
import scala.util.Random

import com.github.mkroli.ast.AbstractSyntaxTree
import com.github.mkroli.ast.impl.Abs
import com.github.mkroli.ast.impl.Addition
import com.github.mkroli.ast.impl.Constant
import com.github.mkroli.ast.impl.CosinusPi
import com.github.mkroli.ast.impl.Division
import com.github.mkroli.ast.impl.IfEven
import com.github.mkroli.ast.impl.Index
import com.github.mkroli.ast.impl.Multiplication
import com.github.mkroli.ast.impl.PreviousRecord
import com.github.mkroli.ast.impl.Square
import com.github.mkroli.ast.impl.Subtraction

class NumberSeriesSolver(
  generationSize: Int = 100,
  eliteRatio: Double = 0.01,
  crossoverRatio: Double = 0.16,
  mutantsRatio: Double = 0.16,
  finished: (Int, Double, AbstractSyntaxTree) => Boolean) {
  implicit val r = Random

  def evolve(numberSeries: Seq[Double]) = {
    def diff(a: AbstractSyntaxTree): Double = {
      val diffList = {
        def distance(a: Double, b: Double) = abs(a - b)

        (0 until numberSeries.size).map { i =>
          try {
            distance(numberSeries(i), a(numberSeries, i))
          } catch {
            case t =>
              if (i < 2) 0.0
              else (i.toDouble - 1.0)
          }
        }
      }
      if (diffList.isEmpty) Double.NaN
      else diffList.sum / diffList.size
    }

    def randomFunction(depth: Int = 0)(implicit r: Random): AbstractSyntaxTree = {
      def randomFunc[T](l: List[T]): T = {
        val rand = r.nextDouble * l.size
        def func(i: Int, l: List[T]): T = l match {
          case Nil => throw new RuntimeException
          case _ :: tail if i < rand.toInt => func(i + 1, tail)
          case t :: _ => t
        }
        func(0, l)
      }

      lazy val ra = randomFunction(depth + 1)
      lazy val rb = randomFunction(depth + 1)
      lazy val rsi = r.nextInt(2) + 1
      lazy val rbi = r.nextInt(10).toDouble
      val funcSet = if (depth < 6) {
        (() => Addition(ra, rb)) ::
          (() => Subtraction(ra, rb)) ::
          (() => Multiplication(ra, rb)) ::
          (() => Square(ra)) ::
          (() => Division(ra, rb)) ::
          (() => CosinusPi(ra)) ::
          (() => Abs(ra)) ::
          (() => IfEven(ra, rb)) ::
          (() => PreviousRecord(rsi)) ::
          (() => Index()) ::
          (() => Constant(rbi)) ::
          Nil
      } else {
        (() => PreviousRecord(rsi)) ::
          (() => Index()) ::
          (() => Constant(rbi)) ::
          Nil
      }
      randomFunc(funcSet)()
    }

    def randomStream: Stream[AbstractSyntaxTree] =
      randomFunction() #:: randomStream

    def sorted(l: Seq[AbstractSyntaxTree]) =
      l.sortBy(_.complexity).sortBy(diff)

    def randomNode(root: AbstractSyntaxTree): AbstractSyntaxTree = {
      def randomElement[A](s: Seq[A])(implicit r: Random): A =
        if (s.size == 1) s(0)
        else (s(r.nextInt(s.size - 1) + 1))

      randomElement(root.flatten)
    }

    def mutate(a: AbstractSyntaxTree): AbstractSyntaxTree = {
      val rn = randomNode(a)
      a.replaceNode(rn, randomFunction(a.depth(rn)))
    }

    def crossover(parents: (AbstractSyntaxTree, AbstractSyntaxTree)): (AbstractSyntaxTree, AbstractSyntaxTree) = {
      val (pa, pb) = parents
      val (rna, rnb) = (randomNode(pa), randomNode(pb))
      (pa.replaceNode(rna, rnb), pb.replaceNode(rnb, rna))
    }

    def randomElementTuples[A](s: Seq[A], tupleSize: Int, num: Int)(implicit r: Random): Seq[Seq[A]] = {
      def randomIndex = (pow(r.nextDouble, 2) * s.size).toInt

      def randomIndexStream: Stream[Int] = randomIndex #:: randomIndexStream

      def randomDistinctTupleStream: Stream[Seq[A]] =
        randomIndexStream.take(tupleSize).toList.sorted.map(i => s(i)) #::
          randomDistinctTupleStream

      randomDistinctTupleStream.take(num).toList
    }

    @tailrec
    def evolve(population: Seq[AbstractSyntaxTree], generation: Int): Seq[AbstractSyntaxTree] = {
      val sortedPopulation = sorted(population).map(_.short)
      val d = diff(sortedPopulation.head)

      val done = finished(
        generation,
        d,
        sortedPopulation.head)

      if (done) sortedPopulation.map(_.short)
      else {
        val elite = sortedPopulation.take((generationSize * eliteRatio).toInt)
        val pairs = randomElementTuples(sortedPopulation, 2, (generationSize / 2 * crossoverRatio).toInt * 2).flatMap {
          case a :: b :: Nil =>
            val (pa, pb) = crossover((a, b))
            pa :: pb :: Nil
          case _ => Nil
        }
        val mutants = randomElementTuples(sortedPopulation, 1, (generationSize * mutantsRatio).toInt).flatten.map(mutate)
        val newRandomPopulation = sorted(randomStream.take(generationSize - elite.size - pairs.size - mutants.size))
        val newPopulation = elite ++ pairs ++ mutants ++ newRandomPopulation

        evolve(newPopulation, generation + 1)
      }
    }

    val algorithm = evolve(
      randomStream.take(generationSize),
      0).head
    val d = diff(algorithm)

    (algorithm, d)
  }
}
