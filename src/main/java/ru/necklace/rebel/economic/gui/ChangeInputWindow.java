package ru.necklace.rebel.economic.gui;

import ru.necklace.rebel.economic.model.EconomicSituation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChangeInputWindow extends JDialog implements ActionListener {
    private JLabel resName = new JLabel("Название товара");
    private JComboBox<String> resourceList;
    private JLabel inputChange = new JLabel("Изменение добычи");
    private JTextField inputChangeField = new JTextField(10);
    private JButton applyButton = new JButton("Применить");
    private EconomicSituation info;

    public ChangeInputWindow(JFrame parent, EconomicSituation gameLogic, int fontSize) {
        this.resourceList = new JComboBox(gameLogic.getItemsIncomeCorrection().keySet().toArray(new String[0]));
        this.resourceList.setFont(new Font(this.resourceList.getFont().getName(), Font.BOLD, fontSize));
        this.resourceList.addActionListener(this);
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        this.resName.setFont(this.resourceList.getFont());
        panel.add(this.resName);
        panel.add(this.resourceList);
        this.inputChange.setFont(this.resourceList.getFont());
        panel.add(this.inputChange);
        this.inputChangeField.setFont(this.resourceList.getFont());
        panel.add(this.inputChangeField);
        this.applyButton.addActionListener(this);
        this.applyButton.setFont(this.resourceList.getFont());
        panel.add(this.applyButton);
        this.setContentPane(panel);
        this.info = gameLogic;
        this.validate();
        this.pack();
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(this.resourceList)) {
            this.inputChangeField.setText(String.valueOf(this.info.getItemsIncomeCorrection().get(this.resourceList.getSelectedItem())));
        } else if (actionEvent.getSource().equals(this.applyButton)) {
            if (this.inputChangeField.getText().length() > 0) {
                try {
                    this.info.getItemsIncomeCorrection().put((String)this.resourceList.getSelectedItem(), Integer.parseInt(this.inputChangeField.getText()));
                } catch (NumberFormatException var3) {
                    JOptionPane.showMessageDialog(this, "Некорректное значение!");
                    this.inputChangeField.setText("");
                }
            } else {
                this.info.getItemsIncomeCorrection().put((String)this.resourceList.getSelectedItem(), 0);
            }
        }

    }
}
