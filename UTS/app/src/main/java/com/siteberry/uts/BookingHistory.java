package com.siteberry.uts;

import android.graphics.Bitmap;

public class BookingHistory {
    public Bitmap ticketQr;
    public String sourceStation,destinationStation;
    public BookingHistory(){
        super();
    }

    public BookingHistory(Bitmap ticketQr, String sourceStation, String destinationStation) {
        super();
        this.ticketQr = ticketQr;
        this.sourceStation = sourceStation;
        this.destinationStation = destinationStation;
    }
}
