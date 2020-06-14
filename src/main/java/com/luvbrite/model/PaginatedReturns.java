package com.luvbrite.model;

import java.util.List;

public class PaginatedReturns {
    public PaginatedReturns(Pagination pg, List<ReturnsDTO> returnedPackets){
      this.pg=pg;
      this.returnedPackets=returnedPackets;
    }

    private Pagination pg;
    private List<ReturnsDTO> returnedPackets;
}
