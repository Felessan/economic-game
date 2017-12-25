package ru.necklace.rebel.economic.utils.table;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

public class TableColumnAdjuster implements PropertyChangeListener, TableModelListener {
    private JTable table;
    private int spacing;
    private boolean isColumnHeaderIncluded;
    private boolean isColumnDataIncluded;
    private boolean isOnlyAdjustLarger;
    private boolean isDynamicAdjustment;
    private Map<TableColumn, Integer> columnSizes;

    public TableColumnAdjuster(JTable table) {
        this(table, 6);
    }

    public TableColumnAdjuster(JTable table, int spacing) {
        this.columnSizes = new HashMap();
        this.table = table;
        this.spacing = spacing;
        this.setColumnHeaderIncluded(true);
        this.setColumnDataIncluded(true);
        this.setOnlyAdjustLarger(false);
        this.setDynamicAdjustment(false);
        this.installActions();
    }

    public void adjustColumns() {
        TableColumnModel tcm = this.table.getColumnModel();

        for(int i = 0; i < tcm.getColumnCount(); ++i) {
            this.adjustColumn(i);
        }

        this.updateRowHeights();
    }

    public void adjustColumn(int column) {
        TableColumn tableColumn = this.table.getColumnModel().getColumn(column);
        if (tableColumn.getResizable()) {
            int columnHeaderWidth = this.getColumnHeaderWidth(column);
            int columnDataWidth = this.getColumnDataWidth(column);
            int preferredWidth = Math.max(columnHeaderWidth, columnDataWidth);
            this.updateTableColumn(column, preferredWidth);
        }
    }

    private int getColumnHeaderWidth(int column) {
        if (!this.isColumnHeaderIncluded) {
            return 0;
        } else {
            TableColumn tableColumn = this.table.getColumnModel().getColumn(column);
            Object value = tableColumn.getHeaderValue();
            TableCellRenderer renderer = tableColumn.getHeaderRenderer();
            if (renderer == null) {
                renderer = this.table.getTableHeader().getDefaultRenderer();
            }

            Component c = renderer.getTableCellRendererComponent(this.table, value, false, false, -1, column);
            return c.getPreferredSize().width;
        }
    }

    private int getColumnDataWidth(int column) {
        if (!this.isColumnDataIncluded) {
            return 0;
        } else {
            int preferredWidth = 0;
            int maxWidth = this.table.getColumnModel().getColumn(column).getMaxWidth();

            for(int row = 0; row < this.table.getRowCount(); ++row) {
                preferredWidth = Math.max(preferredWidth, this.getCellDataWidth(row, column));
                if (preferredWidth >= maxWidth) {
                    break;
                }
            }

            return preferredWidth;
        }
    }

    private int getCellDataWidth(int row, int column) {
        TableCellRenderer cellRenderer = this.table.getCellRenderer(row, column);
        Component c = this.table.prepareRenderer(cellRenderer, row, column);
        return c.getPreferredSize().width + this.table.getIntercellSpacing().width;
    }

    private void updateTableColumn(int column, int width) {
        TableColumn tableColumn = this.table.getColumnModel().getColumn(column);
        if (tableColumn.getResizable()) {
            width += this.spacing;
            if (this.isOnlyAdjustLarger) {
                width = Math.max(width, tableColumn.getPreferredWidth());
            }

            this.columnSizes.put(tableColumn, tableColumn.getWidth());
            this.table.getTableHeader().setResizingColumn(tableColumn);
            tableColumn.setWidth(width);
        }
    }

    private void updateRowHeights() {
        try {
            for(int row = 0; row < this.table.getRowCount(); ++row) {
                int rowHeight = this.table.getRowHeight();

                for(int column = 0; column < this.table.getColumnCount(); ++column) {
                    Component comp = this.table.prepareRenderer(this.table.getCellRenderer(row, column), row, column);
                    rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
                }

                this.table.setRowHeight(row, rowHeight + 10);
            }
        } catch (ClassCastException var5) {
            //LogManager.getLogger(this.getClass());
        }

    }

    public void restoreColumns() {
        TableColumnModel tcm = this.table.getColumnModel();

        for(int i = 0; i < tcm.getColumnCount(); ++i) {
            this.restoreColumn(i);
        }

    }

    private void restoreColumn(int column) {
        TableColumn tableColumn = this.table.getColumnModel().getColumn(column);
        Integer width = (Integer)this.columnSizes.get(tableColumn);
        if (width != null) {
            this.table.getTableHeader().setResizingColumn(tableColumn);
            tableColumn.setWidth(width.intValue());
        }

    }

    public void setColumnHeaderIncluded(boolean isColumnHeaderIncluded) {
        this.isColumnHeaderIncluded = isColumnHeaderIncluded;
    }

    public void setColumnDataIncluded(boolean isColumnDataIncluded) {
        this.isColumnDataIncluded = isColumnDataIncluded;
    }

    public void setOnlyAdjustLarger(boolean isOnlyAdjustLarger) {
        this.isOnlyAdjustLarger = isOnlyAdjustLarger;
    }

    public void setDynamicAdjustment(boolean isDynamicAdjustment) {
        if (this.isDynamicAdjustment != isDynamicAdjustment) {
            if (isDynamicAdjustment) {
                this.table.addPropertyChangeListener(this);
                this.table.getModel().addTableModelListener(this);
            } else {
                this.table.removePropertyChangeListener(this);
                this.table.getModel().removeTableModelListener(this);
            }
        }

        this.isDynamicAdjustment = isDynamicAdjustment;
    }

    public void propertyChange(PropertyChangeEvent e) {
        if ("model".equals(e.getPropertyName())) {
            TableModel model = (TableModel)e.getOldValue();
            model.removeTableModelListener(this);
            model = (TableModel)e.getNewValue();
            model.addTableModelListener(this);
            this.adjustColumns();
        }

    }

    public void tableChanged(TableModelEvent e) {
        if (this.isColumnDataIncluded) {
            if (e.getType() == 0) {
                int column = this.table.convertColumnIndexToView(e.getColumn());
                if (this.isOnlyAdjustLarger) {
                    int row = e.getFirstRow();
                    TableColumn tableColumn = this.table.getColumnModel().getColumn(column);
                    if (tableColumn.getResizable()) {
                        int width = this.getCellDataWidth(row, column);
                        this.updateTableColumn(column, width);
                    }

                    this.updateRowHeights();
                } else {
                    this.adjustColumn(column);
                }
            } else {
                this.adjustColumns();
            }

        }
    }

    private void installActions() {
        this.installColumnAction(true, true, "adjustColumn", "control ADD");
        this.installColumnAction(false, true, "adjustColumns", "control shift ADD");
        this.installColumnAction(true, false, "restoreColumn", "control SUBTRACT");
        this.installColumnAction(false, false, "restoreColumns", "control shift SUBTRACT");
        this.installToggleAction(true, false, "toggleDynamic", "control MULTIPLY");
        this.installToggleAction(false, true, "toggleLarger", "control DIVIDE");
    }

    private void installColumnAction(boolean isSelectedColumn, boolean isAdjust, String key, String keyStroke) {
        Action action = new TableColumnAdjuster.ColumnAction(isSelectedColumn, isAdjust);
        KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
        this.table.getInputMap().put(ks, key);
        this.table.getActionMap().put(key, action);
    }

    private void installToggleAction(boolean isToggleDynamic, boolean isToggleLarger, String key, String keyStroke) {
        Action action = new TableColumnAdjuster.ToggleAction(isToggleDynamic, isToggleLarger);
        KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
        this.table.getInputMap().put(ks, key);
        this.table.getActionMap().put(key, action);
    }

    class ToggleAction extends AbstractAction {
        private boolean isToggleDynamic;
        private boolean isToggleLarger;

        public ToggleAction(boolean isToggleDynamic, boolean isToggleLarger) {
            this.isToggleDynamic = isToggleDynamic;
            this.isToggleLarger = isToggleLarger;
        }

        public void actionPerformed(ActionEvent e) {
            if (this.isToggleDynamic) {
                TableColumnAdjuster.this.setDynamicAdjustment(!TableColumnAdjuster.this.isDynamicAdjustment);
            } else {
                if (this.isToggleLarger) {
                    TableColumnAdjuster.this.setOnlyAdjustLarger(!TableColumnAdjuster.this.isOnlyAdjustLarger);
                }

            }
        }
    }

    class ColumnAction extends AbstractAction {
        private boolean isSelectedColumn;
        private boolean isAdjust;

        public ColumnAction(boolean isSelectedColumn, boolean isAdjust) {
            this.isSelectedColumn = isSelectedColumn;
            this.isAdjust = isAdjust;
        }

        public void actionPerformed(ActionEvent e) {
            if (this.isSelectedColumn) {
                int[] columns = TableColumnAdjuster.this.table.getSelectedColumns();
                int[] arr$ = columns;
                int len$ = columns.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    int column = arr$[i$];
                    if (this.isAdjust) {
                        TableColumnAdjuster.this.adjustColumn(column);
                    } else {
                        TableColumnAdjuster.this.restoreColumn(column);
                    }
                }
            } else if (this.isAdjust) {
                TableColumnAdjuster.this.adjustColumns();
            } else {
                TableColumnAdjuster.this.restoreColumns();
            }

        }
    }
}
