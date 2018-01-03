package ru.necklace.rebel.economic.data;

import java.io.Serializable;

public class ItemInfo implements Serializable{
    private String itemName;
    private long available = 0L;
    private long baseAvailable = -1L;
    private long cost = 0L;
    private long baseCost = -1L;
    private long income = 0L;
    private long baseIncome = -1L;
    private long sold = 0L;

    public ItemInfo() {
    }

    public String getItemName() {
        return this.itemName;
    }

    public void copyBaseSettingsTo(ItemInfo item) {
        item.baseAvailable = this.baseAvailable;
        item.baseCost = this.baseCost;
        item.baseIncome = this.baseIncome;
    }

    public long getBaseAvailable() {
        return this.baseAvailable;
    }

    public long getBaseCost() {
        return this.baseCost;
    }

    public long getBaseIncome() {
        return this.baseIncome;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public long getAvailable() {
        return this.available;
    }

    public void setAvailable(long available) {
        this.available = available;
        if (this.baseAvailable < 0L) {
            this.baseAvailable = available;
        }

    }

    public long getCost() {
        return this.cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
        if (this.baseCost < 0L) {
            this.baseCost = cost;
        }

    }

    public long getIncome() {
        return this.income;
    }

    public void setIncome(long income) {
        this.income = income;
        if (this.baseIncome < 0L) {
            this.baseIncome = income;
        }

    }

    public long getSold() {
        return this.sold;
    }

    public void setSold(long sold) {
        this.sold = sold;
    }

    public ItemInfo getCopy() {
        ItemInfo copy = new ItemInfo();
        copy.setItemName(this.itemName);
        copy.setAvailable(this.available);
        copy.setCost(this.cost);
        copy.setIncome(this.income);
        copy.setSold(this.sold);
        this.copyBaseSettingsTo(copy);
        return copy;
    }
}
