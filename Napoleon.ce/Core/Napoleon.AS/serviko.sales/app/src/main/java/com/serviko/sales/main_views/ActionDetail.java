package com.serviko.sales.main_views;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.serviko.dataobjects.actionTree.ActionDef;
import com.serviko.dataobjects.actionTree.ActionGoods;
import com.serviko.sales.R;
import com.serviko.utils.PriceController;

public class ActionDetail extends BaseView {

    public static String TAG = ActionDetail.class.toString();

    @Override
    int getResourceId() {
        return R.layout.action_detail;
    }

    @Override
    public String getFragmentTag() { return TAG; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        v.findViewById(R.id.back).setOnClickListener(view -> getParentFragmentManager().popBackStack());

        ActionDef action = model.currentAction;

        TextView tv = v.findViewById(R.id.title);
        tv.setText(action.title());

        tv = v.findViewById(R.id.text);
        tv.setText(action.text());

        RecyclerView rv = v.findViewById(R.id.lvItems);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new Adapter(action));
        return v;
    }

    class Adapter extends RecyclerView.Adapter<PriceController> {
        ActionDef action;
        public Adapter(ActionDef action) {
            this.action = action;
        }

        @NonNull
        @Override
        public PriceController onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.price_row_view, parent, false);
            model.getBasket().setCanRemove(true);
            PriceController vh = new PriceController(model.getBasket(), v, true, false, this::getItemBitmap);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull PriceController holder, int position) {
            holder.updateView(((ActionGoods)action.getChildren().get(position)).getItem());
        }

        @Override
        public int getItemCount() { return action.getChildren().size(); }

        private Bitmap getItemBitmap(com.serviko.dataobjects.Price price, ImageView imageView) {
            String url = model.makeUrl(price.code, false);
            Bitmap bmp = model.getPhoto(url);
            if(bmp == null) {
                requestImage(url, imageView);
            }
            return bmp;
        }
    }
}
