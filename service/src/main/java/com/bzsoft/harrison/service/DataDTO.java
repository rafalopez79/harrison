package com.bzsoft.harrison.service;

import java.awt.Button;
import java.io.Serializable;
import java.math.BigDecimal;

public class DataDTO implements Serializable {

	private static final long	serialVersionUID	= 1L;
	private byte			id;
	private BigDecimal	price;
	private final int d;
	private final transient Button button;

	public DataDTO(final byte id, final BigDecimal price, final int d, final Button button) {
		this.id = id;
		this.price = price;
		this.d = d;
		this.button = button;
	}

	public byte getId() {
		return id;
	}

	public void setId(final byte id) {
		this.id = id;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(final BigDecimal price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "DataDTO [id = " + id + ", price = " + price + ", d = "+d+", button = "+button+"]";
	}

}
