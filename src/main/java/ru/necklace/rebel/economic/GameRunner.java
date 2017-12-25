package ru.necklace.rebel.economic;

import ru.necklace.rebel.economic.data.Coefficients;
import ru.necklace.rebel.economic.gui.ControlWindow;
import ru.necklace.rebel.economic.model.EconomicSituation;

import java.io.File;

public class GameRunner {
    public static void main(String[] args) {
        EconomicSituation gameLogic = new EconomicSituation((new Coefficients()).load("Conf" + File.separator + "coeficient.properties"));
        ControlWindow mainWindow = new ControlWindow(gameLogic);
        mainWindow.pack();
        mainWindow.setVisible(true);
    }
}
