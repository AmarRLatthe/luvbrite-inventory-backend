package com.luvbrite.model;

import java.util.List;

public class PaginatedPackets {
	public PaginatedPackets(Pagination pg, List<PacketExtDTO>  packets) {
		this.pg = pg;
		this.packets = packets;
	}

	private Pagination pg;
	private List<PacketExtDTO> packets;

	public Pagination getPg() {
		return pg;
	}

	public void setPg(Pagination pg) {
		this.pg = pg;
	}

	public List<PacketExtDTO> getPackets() {
		return packets;
	}

	public void setPackets(List<PacketExtDTO> packets) {
		this.packets = packets;
	}



}
