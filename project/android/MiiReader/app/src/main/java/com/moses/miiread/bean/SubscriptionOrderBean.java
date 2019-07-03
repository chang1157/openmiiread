package com.moses.miiread.bean;

public class SubscriptionOrderBean {
    public String typeName;
    public String typeDesc;
    public String typeSummary;
    public boolean isVip;
    public boolean isBestOffer;
    public float cost;
    public int coins;
    public int freeCoins;
    public boolean checked = false;

    public SubscriptionOrderBean(String typeName, String typeDesc, String typeSummary, boolean isVip, boolean isBestOffer, float cost, int coins, int freeCoins) {
        this.typeName = typeName;
        this.typeDesc = typeDesc;
        this.typeSummary = typeSummary;
        this.isVip = isVip;
        this.isBestOffer = isBestOffer;
        this.cost = cost;
        this.coins = coins;
        this.freeCoins = freeCoins;
    }

    public SubscriptionOrderBean(String typeName, String typeDesc, String typeSummary, boolean isVip, boolean isBestOffer, float cost) {
        this.typeName = typeName;
        this.typeDesc = typeDesc;
        this.typeSummary = typeSummary;
        this.isVip = isVip;
        this.isBestOffer = isBestOffer;
        this.cost = cost;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    public boolean isBestOffer() {
        return isBestOffer;
    }

    public void setBestOffer(boolean bestOffer) {
        isBestOffer = bestOffer;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getFreeCoins() {
        return freeCoins;
    }

    public void setFreeCoins(int freeCoins) {
        this.freeCoins = freeCoins;
    }

    public String getTypeSummary() {
        return typeSummary;
    }

    public void setTypeSummary(String typeSummary) {
        this.typeSummary = typeSummary;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
