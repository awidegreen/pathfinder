/****************************************************************************************
 * Copyright (c) 2009 Armin Widegreen <armin.widegreen@gmail.com>                       *
 *                    Dirk Reske <email@dirkreske.de>                                   *
 *                                                                                      *
 * This program is free software; you can redistribute it and/or modify it under        *
 * the terms of the GNU General Public License as published by the Free Software        *
 * Foundation, either version 3 of the License, or (at your option) any later           *
 * version.                                                                             *
 *                                                                                      *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY      *
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A      *
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.             *
 *                                                                                      *
 * You should have received a copy of the GNU General Public License along with         *
 * this program.  If not, see <http://www.gnu.org/licenses/>.                           *
 ****************************************************************************************/ 

package net.sf.pathfinder.ui.swing;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.sf.pathfinder.ui.swing.propertytable.SpinnerCellEditor;
import net.sf.pathfinder.ui.swing.propertytable.SpinnerCellRenderer;
import net.sf.pathfinder.util.properties.PropertyInfo;
import net.sf.pathfinder.util.properties.PropertySupport;


/**
 * A property table can display and edit the properties from a
 * {@link PropertySupport} instance
 * 
 * @author Dirk Reske
 * 
 */
public class JPropertyTable extends JTable {

	private PropertySupport target;
	private PropertyTableModel propertyTableModel = new PropertyTableModel();

	/**
	 * Creates a new JPropertyTable
	 */
	public JPropertyTable() {
		setModel(propertyTableModel);
		getColumnModel().getColumn(0).setCellRenderer(
				new PropertyTableCellRenderer());
	}

	/**
	 * Gets the target of the property table
	 * 
	 * @return The target
	 */
	public PropertySupport getTarget() {
		return target;
	}

	/**
	 * Sets the target of the property table
	 * 
	 * @param target
	 *            The target
	 */
	public void setTarget(PropertySupport target) {
		this.target = target;
		propertyTableModel.fireTableDataChanged();
	}

	/**
	 * Gets the cell renderer for the specified row and column index
	 * 
	 * @param row
	 *            The row index
	 * @param column
	 *            The column index
	 * @return The appropriate {@link TableCellRenderer}
	 */
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {

		TableCellRenderer result = super.getCellRenderer(row, column);
		PropertyInfo propertyInfo = target.getPropertyInfos().get(row);
		if (column == 0) {
			return result;
		} else if (Integer.class.equals(propertyInfo.getType())
				&& propertyInfo.getPossibleValues() != null) {
			if (propertyInfo.getPossibleValues()[0].equals(PropertyInfo.RANGE)) {
				SpinnerNumberModel model = new SpinnerNumberModel(
						(Integer) target.getProperty(propertyInfo.getName()),
						(Integer) propertyInfo.getPossibleValues()[1],
						(Integer) propertyInfo.getPossibleValues()[2],
						(Integer) 1);

				return new SpinnerCellRenderer(model);
			}
		} else if (Double.class.equals(propertyInfo.getType())
				&& propertyInfo.getPossibleValues() != null) {
			if (propertyInfo.getPossibleValues()[0].equals(PropertyInfo.RANGE)) {
				SpinnerNumberModel model = new SpinnerNumberModel(
						(Double) target.getProperty(propertyInfo.getName()),
						(Double) propertyInfo.getPossibleValues()[1], // start
						(Double) propertyInfo.getPossibleValues()[2], // stop
						(Double) propertyInfo.getPossibleValues()[3]); // step

				return new SpinnerCellRenderer(model);
			}
		}

		return result;
	}

	/**
	 * Gets the {@link TableCellEditor} for the specified row and column index
	 * 
	 * @param row
	 *            The row index
	 * @param column
	 *            The column index
	 * @return The appropriate cell editor
	 */
	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		if (column == 0) {
			return null;
		}

		TableCellEditor currentEditor = null;
		PropertyInfo propertyInfo = target.getPropertyInfos().get(row);
		if (Boolean.class.equals(propertyInfo.getType())) {
			JComboBox comboBox = new JComboBox(new String[] {"True", "False" });
			currentEditor = new DefaultCellEditor(comboBox);
		} else if (String.class.equals(propertyInfo.getType())
				&& propertyInfo.getPossibleValues() != null) {
			JComboBox comboBox = new JComboBox(propertyInfo.getPossibleValues());
			currentEditor = new DefaultCellEditor(comboBox);
		} else if (Integer.class.equals(propertyInfo.getType())
				&& propertyInfo.getPossibleValues() != null) {
			if (propertyInfo.getPossibleValues()[0].equals(PropertyInfo.RANGE)) {
				SpinnerNumberModel model = new SpinnerNumberModel(
						(Integer) target.getProperty(propertyInfo.getName()),
						(Integer) propertyInfo.getPossibleValues()[1],
						(Integer) propertyInfo.getPossibleValues()[2],
						(Integer) 1);

				JSpinner spinner = new JSpinner(model);
				currentEditor = new SpinnerCellEditor(spinner);
			}
		} else if (Double.class.equals(propertyInfo.getType())
				&& propertyInfo.getPossibleValues() != null) {
			if (propertyInfo.getPossibleValues()[0].equals(PropertyInfo.RANGE)) {
				SpinnerNumberModel model = new SpinnerNumberModel(
						(Double) target.getProperty(propertyInfo.getName()),
						(Double) propertyInfo.getPossibleValues()[1], // start
						(Double) propertyInfo.getPossibleValues()[2], // stop
						(Double) propertyInfo.getPossibleValues()[3]); // step

				JSpinner spinner = new JSpinner(model);
				currentEditor = new SpinnerCellEditor(spinner);
			}
		} else {
			currentEditor = new DefaultCellEditor(new JTextField());
		}

		return currentEditor;
	}

	private class PropertyTableModel extends AbstractTableModel {

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public int getRowCount() {
			if (target == null) {
				return 0;
			} else {
				return target.getPropertyInfos().size();
			}
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (target == null) {
				return null;
			} else {
				PropertyInfo propertyInfo = target.getPropertyInfos().get(
						rowIndex);
				if (columnIndex == 0) {
					return propertyInfo.getName();
				} else {
					return target.getProperty(propertyInfo.getName());
				}
			}
		}

		@Override
		public String getColumnName(int column) {
			if (column == 0) {
				return "Property";
			} else {
				return "Value";
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (columnIndex == 1) {
				return true;
			}
			return false;
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			if ((target != null) && (columnIndex == 1)) {
				PropertyInfo propertyInfo = target.getPropertyInfos().get(
						rowIndex);
				Object propertyValue = null;
				if (Integer.class.equals(propertyInfo.getType())) {
					if (value instanceof Integer) {
						propertyValue = value;
					} else {
						propertyValue = Integer.parseInt((String) value);
					}
				}
				if (Double.class.equals(propertyInfo.getType())) {
					if (value instanceof Double) {
						propertyValue = value;
					} else {
						propertyValue = Double.parseDouble((String) value);
					}
				} else if (Boolean.class.equals(propertyInfo.getType())) {
					propertyValue = Boolean.parseBoolean((String) value);
				} else if (String.class.equals(propertyInfo.getType())) {
					propertyValue = (String) value;
				}
				target.setProperty(propertyInfo.getName(), propertyValue);
			}
		}
	}

	private class PropertyTableCellRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Component component = super.getTableCellRendererComponent(table,
					value, isSelected, hasFocus, row, column);

			if ((target != null) && (component instanceof JComponent)) {
				PropertyInfo propertyInfo = target.getPropertyInfos().get(row);
				((JComponent) component).setToolTipText(propertyInfo
						.getDescription());
			}

			return component;
		}

	}
}
