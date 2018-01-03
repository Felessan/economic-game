package ru.necklace.rebel.economic.data;

import ru.necklace.rebel.economic.utils.PropertiesName;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

public class Coefficients implements Serializable {
    private Properties coefficientsMap = new Properties();

    public Coefficients() {
    }

    public Coefficients load(String filePath) {
        try {
            this.coefficientsMap.load(new FileInputStream(filePath));
            if (!this.coefficientsMap.containsKey(PropertiesName.COST_ENLARGE)) {
                JOptionPane.showMessageDialog(null, "Файл с коэффициентами не содержит " + PropertiesName.COST_ENLARGE + "!", "Ошибка коэффициентов", 0);
            }

            if (!this.coefficientsMap.containsKey(PropertiesName.LARGE_INPUT)) {
                JOptionPane.showMessageDialog(null, "Файл с коэффициентами не содержит " + PropertiesName.LARGE_INPUT + "!", "Ошибка коэффициентов", 0);
            }

            if (!this.coefficientsMap.containsKey(PropertiesName.EXTRA_LARGE_INPUT))
                JOptionPane.showMessageDialog(null, "Файл с коэффициентами не содержит " + PropertiesName.EXTRA_LARGE_INPUT + "!", "Ошибка коэффициентов", 0);

            if (!this.coefficientsMap.containsKey(PropertiesName.LARGE_SALES)) {
                JOptionPane.showMessageDialog(null, "Файл с коэффициентами не содержит " + PropertiesName.LARGE_SALES + "!", "Ошибка коэффициентов", 0);
            }

            if (!this.coefficientsMap.containsKey(PropertiesName.EXTRA_LARGE_SALES)) {
                JOptionPane.showMessageDialog(null, "Файл с коэффициентами не содержит " + PropertiesName.EXTRA_LARGE_SALES + "!", "Ошибка коэффициентов", 0);
            }

            return this;
        } catch (IOException var3) {
            JOptionPane.showMessageDialog(null, "Ошибка при загрузке файла с установками игры. Загрузите другой или проверьте этот!", "Ошибка установок", 0);
            return null;
        }
    }

    public double getCoefficient(String coefName) {
        try {
            return (double)Float.parseFloat(this.coefficientsMap.getProperty(coefName)) / 100.0D;
        } catch (NumberFormatException var3) {
            return 0.0D;
        }
    }
}
