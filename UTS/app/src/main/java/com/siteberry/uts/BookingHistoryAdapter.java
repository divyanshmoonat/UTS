package com.siteberry.uts;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BookingHistoryAdapter extends ArrayAdapter<BookingHistory>{
    Context context;
    int resourceLayoutId;
    BookingHistory data[] = null;

    public BookingHistoryAdapter(@NonNull Context context, int resourceLayoutId, BookingHistory[] data) {
        super(context, resourceLayoutId,data);
        this.context = context;
        this.resourceLayoutId = resourceLayoutId;
        this.data = data;
        Log.i("appstate","constructuor initialized");
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.i("appstate","get view called");
        View row = convertView;
        BookingHolder holder = null;
        if (row == null){
            Log.i("appstate","row is null");
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resourceLayoutId, parent, false);
            holder = new BookingHolder();
            holder.ticketQr = (ImageView)row.findViewById(R.id.ticket_qr);
            holder.sourceStation = (AppCompatTextView)row.findViewById(R.id.booking_history_source_station);
            holder.destinationStation = (AppCompatTextView)row.findViewById(R.id.booking_history_destination_station);
            row.setTag(holder);
        }
        else {
            Log.i("appstate","row is not null");
            holder = (BookingHolder)row.getTag();
        }
        BookingHistory bookingHistory = data[position];
        holder.ticketQr.setImageBitmap(bookingHistory.ticketQr);
        holder.sourceStation.setText(bookingHistory.sourceStation);
        holder.destinationStation.setText(bookingHistory.destinationStation);
        return row;
    }
    static class BookingHolder{
        ImageView ticketQr;
        AppCompatTextView sourceStation,destinationStation;
    }
}
