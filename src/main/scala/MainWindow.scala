package fake.defender

import scala.swing.{MainFrame,TabbedPane, Table}

import java.awt.Rectangle
import javax.swing.{JTable}
import javax.swing.table.AbstractTableModel

import java.util.{Locale, ResourceBundle}


import fake.util.{Hash,Fake,FakeReport}

object Messages {
	val bundle = ResourceBundle.getBundle("fake.defender.MessagesBundle", Locale.getDefault())
	def apply(key: String): String = bundle.getString(key)
}

class ReportedTableModel(fakes: collection.Set[FakeReport]) extends AbstractTableModel {
	override def getColumnName(col:Int): String = {
		col match {
			case 0 => Messages("Fake_name")
			case 1 => Messages("True_name")
			case 2 => Messages("Hash")
		}
	}
	override def getRowCount = fakes.size
	override def getColumnCount = 3
	override def getValueAt(row:Int, col:Int): Object = {
		val fake = fakes.toSeq(row)
		col match {
			case 0 => fake.file.name
			case 1 => fake.fake.name
			case 2 => fake.fake.hash
		}
	}
	override def isCellEditable(row:Int, col:Int) = false
	override def setValueAt(value:Object, row: Int, col:Int) {}
}

class FakeTableModel(fakes: collection.Iterable[Fake])  extends AbstractTableModel {
	override def getColumnName(col:Int): String = {
		col match {
			case 0 => Messages("Fake_name")
			case 1 => Messages("True_name")
			case 2 => Messages("Hash")
		}
	}
	override def getRowCount = fakes.size
	override def getColumnCount = 3
	override def getValueAt(row:Int, col:Int): Object = {
		val fake = fakes.toSeq(row)
		col match {
			case 0 => fake.wrongName.toString
			case 1 => fake.name
			case 2 => fake.hash
		}
	}
	override def isCellEditable(row:Int, col:Int) = false
	override def setValueAt(value:Object, row: Int, col:Int) {}
}

class MainWindow extends MainFrame {
	val controller = new Controller(onReport)

	title = "Fake Defender"
//	bounds = new Rectangle(100,100,300,100);
	val tabs = new TabbedPane();
	this.contents=tabs; // add the panel to frame
	val reportedTableModel = new ReportedTableModel(controller.reports) 
	tabs.pages+=new TabbedPane.Page(Messages("Detected_fakes"), new Table(){model = reportedTableModel})
	val fakeTableModel = new FakeTableModel(controller.fakes)
	tabs.pages+=new TabbedPane.Page(Messages("Known_fakes"), new Table(){model = fakeTableModel})
	visible = true
	def onReport(report:FakeReport) {}
}
