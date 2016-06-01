package com.bzsoft.harrison.service;

import java.io.Serializable;
import java.util.Date;

public class DataPO implements Serializable {

	private static final long	serialVersionUID	= 1L;
	private final String			name;
	private final Date			time;
	private long					id;

	public DataPO(final String name, final Date time, final long id) {
		this.name = name;
		this.time = time;
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public Date getTime() {
		return time;
	}

	@Override
	public String toString() {
		return "DataPO [name=" + name + ", time=" + time + ", id=" + id + "]";
	}

}
