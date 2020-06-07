package com.luvbrite.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class OrderStatisticDTO {

	private double totalDiscount;
    private double totalTaxApplied;
    private double salesTax;
    private double exciseTax;
    private double subtotal;
    private double orderAmount;
    private double total;
    private int count;
    private double amount;
    private String label;
    private String mode;
    private double tip;
    private double originalTotal;
    private int dispatchId;
    private String extra;
    private float countF;
    private String status;
    private String note;
    @JsonIgnore
    private String disIds;
}
