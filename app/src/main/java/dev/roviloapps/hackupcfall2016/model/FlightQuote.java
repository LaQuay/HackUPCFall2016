package dev.roviloapps.hackupcfall2016.model;

/**
 * Created by alejandro on 8/10/16.
 */

public class FlightQuote {
    private double minPrice;
    private boolean direct;
    private Flight inboundLeg;
    private Flight outboundLeg;

    public FlightQuote(double minPrice, boolean direct, Flight inboundLeg, Flight outboundLeg) {
        this.minPrice = minPrice;
        this.direct = direct;
        this.inboundLeg = inboundLeg;
        this.outboundLeg = outboundLeg;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public boolean isDirect() {
        return direct;
    }

    public void setDirect(boolean direct) {
        this.direct = direct;
    }

    public Flight getInboundLeg() {
        return inboundLeg;
    }

    public void setInboundLeg(Flight inboundLeg) {
        this.inboundLeg = inboundLeg;
    }

    public Flight getOutboundLeg() {
        return outboundLeg;
    }

    public void setOutboundLeg(Flight outboundLeg) {
        this.outboundLeg = outboundLeg;
    }
}
