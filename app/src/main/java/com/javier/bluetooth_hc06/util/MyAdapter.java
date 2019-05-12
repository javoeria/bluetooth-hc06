package com.javier.bluetooth_hc06.util;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.javier.bluetooth_hc06.R;
import com.javier.bluetooth_hc06.RoomActivity;

import java.util.List;

import static com.javier.bluetooth_hc06.DeviceActivity.EXTRA_ROOM;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView text1, text2, textRoom;
        private final ImageView image1, image2, image3, image4, image5, image6;
        private final ImageButton imageButton;

        public ViewHolder(View itemView) {
            super(itemView);

            textRoom = itemView.findViewById(R.id.textViewRoom);
            text1 = itemView.findViewById(R.id.textViewA1);
            text2 = itemView.findViewById(R.id.textViewA2);
            image1 = itemView.findViewById(R.id.imageViewA1);
            image2 = itemView.findViewById(R.id.imageViewA2);
            image3 = itemView.findViewById(R.id.imageViewA3);
            image4 = itemView.findViewById(R.id.imageViewA4);
            image5 = itemView.findViewById(R.id.imageViewA5);
            image6 = itemView.findViewById(R.id.imageViewA6);
            imageButton = itemView.findViewById(R.id.imageButton);
        }
    }

    private final List<Room> mRooms;

    public MyAdapter(List<Room> rooms) {
        mRooms = rooms;
    }

    private String getLetter(int position) {
        String str;
        if (position == 0) {
            str = "A";
        } else if (position == 1) {
            str = "B";
        } else {
            str = "C";
        }
        return str;
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder viewHolder, final int position) {
        Room room = mRooms.get(position);
        viewHolder.textRoom.setText("ROOM " + getLetter(position));
        viewHolder.text1.setText(room.getTemperature() + "ºC");
        viewHolder.text2.setText(room.getHumidity() + "%");
        if (room.getTemperature() > room.getOldTemperature()) {
            viewHolder.image1.setImageResource(R.drawable.ic_arrow_upward_green_600_24dp);
        } else if (room.getTemperature() < room.getOldTemperature()){
            viewHolder.image1.setImageResource(R.drawable.ic_arrow_downward_red_600_24dp);
        } else {
            viewHolder.image1.setImageResource(0);
        }
        if (room.getHumidity() > room.getOldHumidity()) {
            viewHolder.image2.setImageResource(R.drawable.ic_arrow_upward_green_600_24dp);
        } else if (room.getHumidity() < room.getOldHumidity()){
            viewHolder.image2.setImageResource(R.drawable.ic_arrow_downward_red_600_24dp);
        } else {
            viewHolder.image2.setImageResource(0);
        }
        if (room.isLight()) {
            viewHolder.image3.setImageResource(R.drawable.light_on_96);
        } else {
            viewHolder.image3.setImageResource(R.drawable.light_off_96);
        }
        if (room.isPresence()) {
            viewHolder.image4.setImageResource(R.drawable.presence_on_96);
        } else {
            viewHolder.image4.setImageResource(R.drawable.presence_off_96);
        }
        if (room.isMusic()) {
            viewHolder.image5.setImageResource(R.drawable.music_on_96);
        } else {
            viewHolder.image5.setImageResource(R.drawable.music_off_96);
        }
        if (room.isAlarm()) {
            viewHolder.image6.setImageResource(R.drawable.alarm_on_96);
        } else {
            viewHolder.image6.setImageResource(R.drawable.alarm_off_96);
        }
        viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent i = new Intent(v.getContext(), RoomActivity.class);
                i.putExtra(EXTRA_ROOM, getLetter(position));
                v.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRooms.size();
    }
}