package com.example.demo.enums;

import lombok.Getter;

@Getter
public enum StockReason {
    // Stock IN reasons
    PURCHASE("Purchase from supplier"),
    RETURN_FROM_CUSTOMER("Customer return"),
    ADJUSTMENT_INCREASE("Inventory adjustment - increase"),
    TRANSFER_IN("Transfer from another location"),

    // Stock OUT reasons
    SALE("Sold to customer"),
    DAMAGED("Damaged goods"),
    LOST("Lost/Stolen"),
    RETURN_TO_SUPPLIER("Return to supplier"),
    ADJUSTMENT_DECREASE("Inventory adjustment - decrease"),
    TRANSFER_OUT("Transfer to another location"),
    PROMOTION("Promotional giveaway");

    private final String description;

    StockReason(String description) {
        this.description = description;
    }
}
