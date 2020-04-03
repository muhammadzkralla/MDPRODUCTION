package com.vuducminh.nicefood.eventbus;

public class HideFABCart {
    private boolean hidden;

    public HideFABCart(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
