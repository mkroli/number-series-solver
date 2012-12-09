package com.github.mkroli

import scala.swing.BorderPanel
import scala.swing.BorderPanel.Position.Center
import scala.swing.BorderPanel.Position.North
import scala.swing.BoxPanel
import scala.swing.Button
import scala.swing.Dialog
import scala.swing.Dialog.Message.Error
import scala.swing.Label
import scala.swing.MainFrame
import scala.swing.Orientation
import scala.swing.ScrollPane
import scala.swing.SimpleSwingApplication
import scala.swing.Table
import scala.swing.TextField
import scala.swing.event.ButtonClicked

import javax.swing.table.DefaultTableModel

object NumberSeriesSolverWindow extends SimpleSwingApplication {
  val evolutionTableModel = new DefaultTableModel {
    override def isCellEditable(x: Int, y: Int) = false
  }
  evolutionTableModel.addColumn("Generation")
  evolutionTableModel.addColumn("Error")
  evolutionTableModel.addColumn("Solution")
  evolutionTableModel.addColumn("Next Number")
  evolutionTableModel.addColumn("Algorithm")

  val numbersTextField = new TextField

  val solveButton = new Button("Solve")
  solveButton.subscribe(this.reactions)

  val tableScrollPane = new ScrollPane(new Table {
    model = evolutionTableModel
  })

  def top = new MainFrame {
    title = "Number-Series-Solver"

    contents = new BorderPanel() {
      layout(tableScrollPane) = Center

      layout(new BoxPanel(Orientation.Horizontal) {
        contents += new Label("Numbers:")
        contents += numbersTextField
        contents += solveButton
      }) = North
    }
  }

  reactions += {
    case ButtonClicked(btn) if btn == solveButton => {
      val numberSeries = try {
        numbersTextField.text.split("""\s+""").toSeq.map(s => s.toDouble)
      } catch {
        case t => Nil
      }

      if (numberSeries.size < 2) {
        Dialog.showMessage(
          tableScrollPane,
          """You need to enter at least two numbers separated by " """",
          "Error",
          Error)
      } else {
        solveButton.enabled = false
        (1 to evolutionTableModel.getRowCount())
          .foreach(_ => evolutionTableModel.removeRow(0))
        val solver = new NumberSeriesSolver(finished = { (generation, diff, algorithm) =>
          evolutionTableModel.addRow(Array(
            generation.toString,
            diff.toString,
            if (diff == 0.0) "Yes" else "No",
            algorithm(numberSeries, numberSeries.size).toString,
            algorithm))

          tableScrollPane.verticalScrollBar.value =
            tableScrollPane.verticalScrollBar.maximum

          generation >= 1000 || diff == 0.0
        })
        new Thread() {
          override def run() = {
            solver.evolve(numberSeries)
            solveButton.enabled = true
          }
        }.start
      }
    }
  }
}
