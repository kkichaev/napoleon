package com.novotek.sales.main_views;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.novotek.dataobjects.Action;
import com.novotek.dataobjects.Partner;
import com.novotek.sales.R;
import com.novotek.utils.DotsController;
import com.novotek.utils.ImageGetControllerBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Actions extends Fragment {

    RecyclerView items;
    DotsController dotsCtrl;
//    LinearLayout dots;
//    ImageView[] dotViews;
    Model model;

    ImageGetControllerBase<Pair<ImageView, TextView>> images = new ImageGetControllerBase<Pair<ImageView, TextView>>() {
        @Override
        protected void onImage(Pair<ImageView, TextView> image, String key, Bitmap b) {
            image.second.setVisibility(View.GONE);
            image.first.setImageBitmap(b);
        }
    };

    public static String TAG = Actions.class.toString();

    public String getFragmentTag() { return TAG; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        model = new ViewModelProvider(getActivity()).get(Model.class);

        model.getPartner().observe(this, this::onNewPartner);

        model.getPicEvent().observe(this, ctr -> images.update());

        View ret = inflater.inflate(R.layout.actions_view, container, false);

        items = ret.findViewById(R.id.items);

        dotsCtrl = new DotsController(items, ret.findViewById(R.id.action_count));
        return ret;
    }

    void onNewPartner(Partner partner) {
        Adapter a = new Adapter(partner);
        items.setAdapter(a);
        items.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        dotsCtrl.update(a.getItemCount());
    }

    class Holder extends RecyclerView.ViewHolder {

        public Holder(@NonNull View itemView) {
            super(itemView);
        }

        public void update(Action action) {
            TextView tv = itemView.findViewById(R.id.action_text);
            ImageView iv = itemView.findViewById(R.id.action_image);

            tv.setText(action.description);
            images.setImage(action.url, new Pair<>(iv, tv));
        }
    }

    class Adapter extends RecyclerView.Adapter<Holder> {

        List<Action> actions;
        public Adapter(Partner partner) {
            actions = new ArrayList<>(partner.getPrice().actions.keySet());
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.action_tile, parent, false);
            Holder h = new Holder(v);
            return h;
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.update(actions.get(position));
        }

        @Override
        public int getItemCount() {
            return actions.size();
        }
    }
}
