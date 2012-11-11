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
import com.github.mkroli.ast.impl.Subtraction

object NumberSeriesSolver extends App {
  val generations = 10000
  val generationSize = 100
  val eliteRatio = .01
  val crossoverRatio = .2
  val mutantsRatio = .2

  implicit val r = Random

  def diff(s: Seq[Double])(a: AbstractSyntaxTree): Double = {
    def diffList(s: Seq[Double], a: AbstractSyntaxTree) = {
      def distance(a: Double, b: Double) = abs(a - b)

      (0 until s.size).map { i =>
        try {
          distance(s(i), a(s, i))
        } catch {
          case t =>
            if (i < 2) 0.0
            else (i.toDouble - 1.0)
        }
      }
    }

    def diffFromList(dl: Seq[Double]): Double =
      if (dl.isEmpty) Double.NaN else dl.sum / dl.size

    diffFromList(diffList(s, a))
  }

  def evolve(numberSeries: Seq[Double], generations: Int, minDiff: Double = 0.0, generationsAfterSolved: Int = 100) = {
    def randomFunction(s: Seq[Double], depth: Int = 0)(implicit r: Random): AbstractSyntaxTree = {
      def randomFunc[T](l: List[T])(implicit r: Random): T = {
        val rand = r.nextDouble * l.size
        def func(i: Int, l: List[T]): T = l match {
          case Nil => throw new RuntimeException
          case _ :: tail if i < rand.toInt => func(i + 1, tail)
          case t :: _ => t
        }
        func(0, l)
      }

      lazy val ra = randomFunction(s, depth + 1)
      lazy val rb = randomFunction(s, depth + 1)
      lazy val rsi = r.nextInt(3) + 1
      lazy val rbi = r.nextInt(10).toDouble
      val funcSet = if (depth < 6) {
        (() => Addition(ra, rb)) ::
          (() => Subtraction(ra, rb)) ::
          (() => Multiplication(ra, rb)) ::
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
      randomFunc(funcSet)(r)()
    }

    def randomStream(numberSeries: Seq[Double]): Stream[AbstractSyntaxTree] =
      randomFunction(numberSeries) #:: randomStream(numberSeries)

    def sorted(s: Seq[Double], l: Seq[AbstractSyntaxTree]) =
      l.sortBy(_.complexity).sortBy(diff(s))

    def randomElement[A](s: Seq[A])(implicit r: Random): A =
      if (s.size == 1) s(0)
      else (s(r.nextInt(s.size - 1) + 1))

    def randomNode(root: AbstractSyntaxTree): AbstractSyntaxTree =
      randomElement(root.flatten)

    def mutate(numberSeries: Seq[Double])(a: AbstractSyntaxTree): AbstractSyntaxTree = {
      val rn = randomNode(a)
      a.replaceNode(rn, randomFunction(numberSeries, a.depth(rn)))
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
    def evolve(numberSeries: Seq[Double], remaining: Int, population: Seq[AbstractSyntaxTree], generationsAfterSolved: Int): Seq[AbstractSyntaxTree] = {
      val sortedPopulation = sorted(numberSeries, population).map(_.short)
      val d = diff(numberSeries)(sortedPopulation.head)

      if ((generations - remaining + 1) % 10 == 0)
        println("Generation %d diff = %.2f %s %d => %s".format(generations - remaining + 1, d, if (d <= minDiff) "(solved)" else "", sortedPopulation.head(numberSeries, numberSeries.size).toInt, sortedPopulation.head))

      if (d <= minDiff && generationsAfterSolved <= 1) sortedPopulation
      else {
        val elite = sortedPopulation.take((generationSize * eliteRatio).toInt)
        val pairs = randomElementTuples(sortedPopulation, 2, (generationSize / 2 * crossoverRatio).toInt * 2).flatMap {
          case a :: b :: Nil =>
            val (pa, pb) = crossover((a, b))
            pa :: pb :: Nil
          case _ => Nil
        }
        val mutants = randomElementTuples(sortedPopulation, 1, (generationSize * mutantsRatio).toInt).flatten.map(mutate(numberSeries))
        val newRandomPopulation = sorted(numberSeries, randomStream(numberSeries).take(generationSize - elite.size - pairs.size - mutants.size))
        val newPopulation = elite ++ pairs ++ mutants ++ newRandomPopulation

        if (remaining == 1)
          sorted(numberSeries, newPopulation).map(_.short)
        else if (d > minDiff)
          evolve(numberSeries, remaining - 1, newPopulation, generationsAfterSolved)
        else
          evolve(numberSeries, remaining - 1, newPopulation, generationsAfterSolved - 1)
      }
    }

    evolve(numberSeries,
      generations,
      randomStream(numberSeries).take(generationSize),
      generationsAfterSolved).head
  }

  if (args.isEmpty) {
    println("syntax: NumberSeriesSolver <n1> [<n2>, ...]")
  } else {
    val numberSeries = args.toSeq.map(_.toInt).map(_.toDouble)
    val algorithm = evolve(numberSeries, generations)
    println("%.2f\t%d\t%.2f\t%s".format(
      diff(numberSeries)(algorithm),
      algorithm.complexity,
      algorithm(numberSeries, numberSeries.size),
      algorithm))
  }
}
