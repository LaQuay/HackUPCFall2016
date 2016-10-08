package dev.roviloapps.hackupcfall2016.model;

import android.support.annotation.NonNull;

public class FlightQuote implements Comparable<FlightQuote> {
    private int minPrice;
    private boolean direct;
    private Flight inboundLeg;
    private Flight outboundLeg;

    public FlightQuote(int minPrice, boolean direct, Flight inboundLeg, Flight outboundLeg) {
        this.minPrice = minPrice;
        this.direct = direct;
        this.inboundLeg = inboundLeg;
        this.outboundLeg = outboundLeg;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(int minPrice) {
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

    @Override
    public int compareTo(@NonNull FlightQuote fq) {
        if (minPrice < fq.getMinPrice()) return -1;
        else if (minPrice == fq.getMinPrice()) return 0;
        return 1;
    }
}
