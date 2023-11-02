package com.novotek.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.novotek.dataobjects.Brand;
import com.novotek.sales.MainActivity;
import com.novotek.sales.R;
import com.novotek.sales.main_views.BrandsMain;

import java.util.List;

public class BrandsAdapter extends RecyclerView.Adapter<BrandsAdapter.Holder> {
    List<Brand> brands;
    MainActivity context;
    ImageGetController images;
    int layoutId;

    public BrandsAdapter(List<Brand> src, MainActivity context, ImageGetController images, int layoutId) {
        brands = src;
        this.context = context;
        this.images = images;
        this.layoutId = layoutId;
    }

    class Holder extends RecyclerView.ViewHolder {

        public Holder(@NonNull View itemView) {
            super(itemView);
        }

        void updateView(Brand b) {
            ImageView img = itemView.findViewById(R.id.image);
            TextView tv = itemView.findViewById(R.id.name);

            if (b.url.trim().length() == 0){
                img.setVisibility(View.INVISIBLE);

                if(tv != null) {
                    tv.setVisibility(View.VISIBLE);
                    tv.setText(b.name.toString());
                }
            }else {
                if (tv != null)
                    tv.setVisibility(View.INVISIBLE);
                img.setVisibility(View.VISIBLE);
                images.setImage(b.url, img);
            }

            img.setOnClickListener(view -> context.openBrand(b));
        }
    }

    @NonNull
    @Override
    public BrandsAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new BrandsAdapter.Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandsAdapter.Holder holder, int position) {
        Brand b = brands.get(position);
        holder.updateView(b);
    }

    @Override
    public int getItemCount() {
        return brands.size();
    }
}
