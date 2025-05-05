package com.example.sparkweather;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class TimeXAdapter extends AbstractItem<TimeXAdapter, TimeXAdapter.ViewHolder> {

    String Ttime;
    int Timage;
    String Ttemp;

    public TimeXAdapter(String ttime, int timage, String ttemp) {
        Ttime = ttime;
        Timage = timage;
        Ttemp = ttemp;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.time_item;
    }

    public static class ViewHolder extends FastAdapter.ViewHolder<TimeXAdapter> {

        TextView tvTime, tvTemp;
        ImageView tvImage;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTime = itemView.findViewById(R.id.tvTimeCurrentTime);
            tvImage = itemView.findViewById(R.id.tvTimeCurrentImage);
            tvTemp = itemView.findViewById(R.id.tvTimeCurrentTemp);

        }

        @Override
        public void bindView(TimeXAdapter item, List<Object> payloads) {

            tvTime.setText(item.Ttime);
            tvTemp.setText(item.Ttemp);

            Glide.with(itemView.getContext())
                    .load(item.Timage)
                    .error(R.drawable.barometer)
                    .into(tvImage);

        }

        @Override
        public void unbindView(TimeXAdapter item) {
            tvTemp.setText(null);
            tvTime.setText(null);
            tvImage.setImageResource(R.drawable.view);
        }
    }
}
