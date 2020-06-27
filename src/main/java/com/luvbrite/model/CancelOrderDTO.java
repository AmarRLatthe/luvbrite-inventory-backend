package com.luvbrite.model;

public class CancelOrderDTO {

    private String packetCode;
    private String reason;

    public String getPacketCode() {
        return packetCode;
    }

    public void setPacketCode(String packetCode) {
        this.packetCode = packetCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

	@Override
	public String toString() {
		return "CancelOrderDTO [packetCode=" + packetCode + ", reason=" + reason + "]";
	}
    
    

}
