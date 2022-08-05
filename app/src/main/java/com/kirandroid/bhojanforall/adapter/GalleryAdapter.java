package com.kirandroid.bhojanforall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kirandroid.bhojanforall.modals.Image;
import com.kirandroid.bhojanforall.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {

    Context context;
    List<Image> imagemodal;

    public GalleryAdapter(Context c, List<Image> f) {
        context = c;
        imagemodal = f;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.gallery_card, parent, false));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;

        public ViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.uplphoimage);
        }

        public ImageView getImage() {
            return this.image;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        //final Image image = images.get(position);
        holder.name.setText(imagemodal.get(position).getName());
        // holder.date.setText(imagemodal.get(position).getDate());
        holder.area.setText(imagemodal.get(position).getArea());
        //holder.people.setText(imagemodal.get(position).getNoofplaces());
        final String url = imagemodal.get(position).getUrl();
        Picasso.get().load(url).into(holder.gallimage);
    }


    @Override
    public int getItemCount() {
        return imagemodal.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView gallimage;
        TextView name, area, people, date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            area = (TextView) itemView.findViewById(R.id.place);
            //people=(TextView)itemView.findViewById(R.id.noofpeople);
            gallimage = (ImageView) itemView.findViewById(R.id.imag);
        }
    }

    public void filterList(List<Image> filteredList) {
        imagemodal = filteredList;
        notifyDataSetChanged();
    }
}
