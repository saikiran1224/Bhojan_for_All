package com.kirandroid.bhojanforall.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kirandroid.bhojanforall.R;
import com.kirandroid.bhojanforall.modals.Banners;

import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class BannerAdapter extends SliderViewAdapter<BannerAdapter.SliderAdapterVH> {

    private static final String TAG = "RESPONSE_DATA";
    Context context;
    private List<Banners> dataBeans;

    public BannerAdapter(Context context, List<Banners> dataBeans) {
        this.context = context;
        this.dataBeans = dataBeans;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        @SuppressLint("InflateParams")
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        viewHolder.textViewDescription.setText("");

/*

        Picasso.get()
                .load(dataBeans.get(position).getUrl())
                .placeholder(R.drawable.image_not_available)
                .error(R.drawable.image_not_available)
                .into(viewHolder.imageViewBackground);
*/


        Glide.with(viewHolder.itemView)
                .load(dataBeans.get(position).getUrl())
                .apply(new RequestOptions().placeholder(R.drawable.image_not_available).error(R.drawable.image_not_available))
                .into(viewHolder.imageViewBackground);

    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size

        //return Math.min(dataBeans.size(), 3);

        return dataBeans.size();


    }

    static class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            this.itemView = itemView;
        }
    }
}
