package fake.defender

import scala.swing.{MainFrame,TabbedPane, Table}

import java.awt.Rectangle
import javax.swing.{JTable}
import javax.swing.table.AbstractTableModel

import java.util.{Locale, ResourceBundle}


import fake.util.{Hash,Fake}

object Messages {
	val bundle = ResourceBundle.getBundle("fake.defender.MessagesBundle", Locale.getDefault())
	def apply(key: String): String = bundle.getString(key)
}

class ReportedTableModel(fakes: collection.Set[ReportedFake]) extends AbstractTableModel {
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

class FakeTableModel(fakes: collection.Map[Hash, Fake])  extends AbstractTableModel {
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
		val fake = fakes.toSeq(row)._2
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
	val repoted = new scala.collection.mutable.HashSet[ReportedFake]()
	val fakes = new scala.collection.mutable.HashMap[Hash, Fake]()

	title = "Fake Defender"
//	bounds = new Rectangle(100,100,300,100);
	val tabs = new TabbedPane();
	this.contents=tabs; // add the panel to frame
	val reportedTableModel = new ReportedTableModel(repoted) 
	tabs.pages+=new TabbedPane.Page(Messages("Detected_fakes"), new Table(){model = reportedTableModel})
	val fakeTableModel = new FakeTableModel(fakes)
	tabs.pages+=new TabbedPane.Page(Messages("Known_fakes"), new Table(){model = fakeTableModel})
	visible = true
	def report(fake:ReportedFake) {
		repoted += fake
		reportedTableModel.fireTableDataChanged
		// println("Reported fake:"+repoted)
	}
	def addFake(fake:Fake) {
		val oldSize = fakes.size
		fakes += ((fake.hash, fake))
		if (oldSize != fakes.size)
			fakeTableModel.fireTableDataChanged
	}
}

class FakesTable(fakes: collection.Set[ReportedFake]) extends Table {
}