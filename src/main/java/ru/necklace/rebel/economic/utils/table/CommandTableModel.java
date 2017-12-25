package ru.necklace.rebel.economic.utils.table;

import ru.necklace.rebel.economic.data.CommandInfo;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class CommandTableModel  extends AbstractTableModel {
    private String[] columnNames = new String[]{"Команда", "Баланс", "Потрачено в этом ходу", "Получено в этом ходу", "Будущий баланс"};
    private String[][] data = new String[0][0];

    public CommandTableModel() {
    }

    public int getColumnCount() {
        return this.columnNames.length;
    }

    public void load(ArrayList<CommandInfo> commands) {
        this.data = new String[commands.size()][5];

        for(int i = 0; i < commands.size(); ++i) {
            this.data[i][0] = commands.get(i).getCommandName();
            this.data[i][1] = String.valueOf(commands.get(i).getCurrentCash());
            this.data[i][2] = String.valueOf(commands.get(i).getExpenditure());
            this.data[i][3] = String.valueOf(commands.get(i).getIncome());
            this.data[i][4] = String.valueOf(commands.get(i).getProbableCash());
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
