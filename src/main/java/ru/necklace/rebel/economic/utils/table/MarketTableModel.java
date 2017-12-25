package ru.necklace.rebel.economic.utils.table;

import ru.necklace.rebel.economic.data.ItemInfo;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class MarketTableModel extends AbstractTableModel {
    private String[] columnNames = new String[]{"Товар", "Доступно", "Цена", "Куплено", "Добыча"};
    private String[][] data = new String[0][0];

    public MarketTableModel() {
    }

    public int getColumnCount() {
        return this.columnNames.length;
    }

    public void load(ArrayList<ItemInfo> items) {
        this.data = new String[items.size()][5];

        for(int i = 0; i < items.size(); ++i) {
            this.data[i][0] = ((ItemInfo)items.get(i)).getItemName();
            this.data[i][1] = String.valueOf(((ItemInfo)items.get(i)).getAvailable());
            this.data[i][2] = String.valueOf(((ItemInfo)items.get(i)).getCost());
            this.data[i][3] = String.valueOf(((ItemInfo)items.get(i)).getSold());
            this.data[i][4] = String.valueOf(((ItemInfo)items.get(i)).getIncome());
        }

    }

    public int getRowCount() {
        return this.data.length;
    }

    public String getColumnName(int col) {
        return this.columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return this.data[row][col];
    }

    public Class getColumnClass(int c) {
        return this.getValueAt(0, c).getClass();
    }

    public boolean isCellEditable(int row, int col) {
        return col != 0;
    }

    public void setValueAt(Object value, int row, int col) {
        this.data[row][col] = (String)value;
        this.fireTableCellUpdated(row, col);
    }
}
