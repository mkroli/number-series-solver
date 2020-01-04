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

import javax.swing.UIManager
import javax.swing.table.DefaultTableModel

import scala.swing.BorderPanel.Position.{Center, North}
import scala.swing.Dialog.Message.Error
import scala.swing.event.{ButtonClicked, Key, KeyPressed}
import scala.swing.{BorderPanel, BoxPanel, Button, Dialog, Label, MainFrame, Orientation, ScrollPane, SimpleSwingApplication, Table, TextField}

object NumberSeriesSolverWindow extends SimpleSwingApplication {
  try {
    UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel")
  } catch {
    case _: Throwable => UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
  }

  val evolutionTableModel = new DefaultTableModel {
    override def isCellEditable(x: Int, y: Int) = false
  }
  evolutionTableModel.addColumn(NumberSeriesSolverLabels.generation)
  evolutionTableModel.addColumn(NumberSeriesSolverLabels.error)
  evolutionTableModel.addColumn(NumberSeriesSolverLabels.solution)
  evolutionTableModel.addColumn(NumberSeriesSolverLabels.next_number)
  evolutionTableModel.addColumn(NumberSeriesSolverLabels.algorithm)
  var solving = false

  val numbersTextField = new TextField
  listenTo(numbersTextField.keys)

  val solveButton = new Button(NumberSeriesSolverLabels.solve)
  listenTo(solveButton)

  val tableScrollPane = new ScrollPane(new Table {
    model = evolutionTableModel
  })

  def top = new MainFrame {
    title = "Number-Series-Solver"

    contents = new BorderPanel() {
      layout(tableScrollPane) = Center

      layout(new BoxPanel(Orientation.Horizontal) {
        contents += new Label(NumberSeriesSolverLabels.numbers)
        contents += numbersTextField
        contents += solveButton
      }) = North
    }
  }

  def solve {
    val numberSeries = try {
      numbersTextField.text.split("""\s+""").toSeq.map(s => s.toDouble)
    } catch {
      case _: Throwable => Nil
    }

    if (numberSeries.size < 2) {
      Dialog.showMessage(
        tableScrollPane,
        NumberSeriesSolverLabels.too_few_numbers,
        NumberSeriesSolverLabels.error,
        Error)
    } else {
      def startSolving() {
        solving = true
        solveButton.text = NumberSeriesSolverLabels.cancel
      }

      def stopSolving() {
        solving = false
        solveButton.text = NumberSeriesSolverLabels.solve
      }

      if (solving) {
        stopSolving()
      } else {
        startSolving()
        (1 to evolutionTableModel.getRowCount())
          .foreach(_ => evolutionTableModel.removeRow(0))
        val solver = new NumberSeriesSolver(finished = { (generation, diff, algorithm) =>
          evolutionTableModel.addRow(Array(
            generation.toString,
            diff.toString,
            if (diff == 0.0) NumberSeriesSolverLabels.yes else NumberSeriesSolverLabels.no,
            algorithm(numberSeries, numberSeries.size).toString,
            algorithm))

          tableScrollPane.verticalScrollBar.value =
            tableScrollPane.verticalScrollBar.maximum

          if (diff == 0.0) {
            stopSolving()
          }
          !solving
        })
        val thread = new Thread("Solver") {
          override def run() = {
            solver.evolve(numberSeries)
          }
        }
        thread.setDaemon(true)
        thread.start
      }
    }
  }

  reactions += {
    case ButtonClicked(btn) if btn == solveButton => solve
    case KeyPressed(fld, Key.Enter, _, _) if fld == numbersTextField => solve
  }
}
