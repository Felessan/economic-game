package ru.necklace.rebel.economic.data;

import java.io.Serializable;
import java.util.ArrayList;

public class CommandInfo implements Serializable {
    private String commandName;
    private long currentCash;
    private ArrayList<Long> lastCash = new ArrayList();
    private long probableCash;
    private long expenditure;
    private long income;

    public long getIncome() {
        return this.income;
    }

    public void setIncome(long income) {
        this.income = income;
    }

    public ArrayList<Long> getLastCash() {
        return this.lastCash;
    }

    public void setLastCash(ArrayList<Long> lastCash) {
        this.lastCash = lastCash;
    }

    public CommandInfo(String commandName) {
        this.commandName = commandName;
    }

    public CommandInfo() {
    }

    public long getCurrentCash() {
        return this.currentCash;
    }

    public void setCurrentCash(long currentCash) {
        this.lastCash.add(this.currentCash);
        this.currentCash = currentCash;
    }

    public long getProbableCash() {
        return this.probableCash;
    }

    public void setProbableCash(long probableCash) {
        this.probableCash = probableCash;
    }

    public long getExpenditure() {
        return this.expenditure;
    }

    public void setExpenditure(long expenditure) {
        this.expenditure = expenditure;
    }

    public String getCommandName() {
        return this.commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public void rollBack(int step) {
        this.probableCash = this.currentCash;
        this.currentCash = ((Long)this.lastCash.get(step)).longValue();
        this.expenditure = this.probableCash - this.currentCash;
    }
}
