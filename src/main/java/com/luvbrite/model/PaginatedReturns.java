package com.luvbrite.model;

import java.util.List;

public class PaginatedReturns {
    public PaginatedReturns(Pagination pg, List<ReturnsDTO> returnedPackets){
      this.pg=pg;
      this.returnedPackets=returnedPackets;
    }
    private Pagination pg;
    private List<ReturnsDTO> returnedPackets;

    public Pagination getPg() {
        return pg;
    }

    public void setPg(Pagination pg) {
        this.pg = pg;
    }

    public List<ReturnsDTO> getReturnedPackets() {
        return returnedPackets;
    }

    public void setReturnedPackets(List<ReturnsDTO> returnedPackets) {
        this.returnedPackets = returnedPackets;
    }


}
