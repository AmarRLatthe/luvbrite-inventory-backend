package com.luvbrite.model;

import java.util.List;

public class OrderTaxDTO {
	private double applicableTax = 0d;
	private List<TaxComponentDTO> taxComponents;


	public double getApplicableTax() {
		return applicableTax;
	}
	public void setApplicableTax(double applicableTax) {
		this.applicableTax = applicableTax;
	}
	public List<TaxComponentDTO> getTaxComponents() {
		return taxComponents;
	}
	public void setTaxComponents(List<TaxComponentDTO> taxComponents) {
		this.taxComponents = taxComponents;
	}





}
