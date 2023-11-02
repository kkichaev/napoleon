package com.serviko.sales;

import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.serviko.dataobjects.Basket;
import com.serviko.dataobjects.Partner;
import com.serviko.dataobjects.PartnerList;

public abstract class BaseActivityOld extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    protected abstract int getLayoutID();
    protected abstract int getBottomMenuID();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(getLayoutID());

        PartnerList.addHandler(selectPartner);

        BottomNavigationView bv = findViewById(R.id.btMenu);
        bv.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bv = findViewById(R.id.btMenu);
        bv.setSelectedItemId(getBottomMenuID());

        refreshPartner();
    }

    protected boolean canFinish() { return true; }
    protected void refreshPartner() { onPartnerSelect(PartnerList.getCurrent()); }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PartnerList.removeHandler(selectPartner);
    }

    PartnerList.Events selectPartner = new PartnerList.Events() {
        @Override public void onNewList() { }

        @Override
        public void onCurrentChanged(final Partner newCurrent) {
            runOnUiThread(new Runnable() {
                @Override public void run() { onPartnerSelect(newCurrent); }
            });
        }
    };

    protected void onPartnerSelect(Partner newPartner) {
        setPartnerText(newPartner);
        if(newPartner != null) {
            newPartner.basket.commit();
            newPartner.basket.setCanRemove(true);
            updateBasket(newPartner.basket);
            newPartner.basket.setHandler(new Basket.Handler() {
                @Override public void Changed(Basket basket) { onBasketChanged(basket); }
            });
        }
    }

    protected void onBasketChanged(Basket basket) {
        updateBasket(basket);
    }

    protected void setPartnerText(Partner partner) {
        String text = getString(R.string.select_partner);
        final TextView tv = findViewById(R.id.tvPartner);
        if(partner != null) {
            text = "<u>" + partner.toText() + "</u>";
        }

        tv.setText(Html.fromHtml(text));
        tv.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { selectCurrentPartner(); }
        });

        View v = findViewById(R.id.ivPartnerExpand);

        if (v != null)
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv.setSingleLine(!tv.isSingleLine());
                }
            });
    }

    protected void selectCurrentPartner() {
//        if (PartnerList.partners().size() == 1) {
//            PartnerList.setCurrentOld(PartnerList.partners().get(0));
//        } else {
//            DialogFragment dlg = PartnerList.selectPartnerDialog();
//            dlg.show(getSupportFragmentManager(), "");
//        }
    }

    void updateBasket(Basket basket) {
        BottomNavigationView bv = findViewById(R.id.btMenu);
        if(bv == null)
            return;

        if(basket.size() == 0) {
            BadgeDrawable b = bv.getBadge(R.id.itBasket);
            if(b != null) {
                b.setVisible(false);
                b.clearNumber();
            }
        } else {
            BadgeDrawable b = bv.getOrCreateBadge(R.id.itBasket);
            b.setVisible(true);
            b.setNumber(basket.size());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == getBottomMenuID())
            return true;

        boolean ret = false;
//        if(id == R.id.itBasket) {
//            BasketActivity.open(this);
//        } else if(id == R.id.itMain) {
//            MainActivityOld.open(this);
//        } else if(id == R.id.itOrder) {
//            OrderActivity.open(this);
//        } else if(id == R.id.itActions) {
//            ActionsActivity.open(this);
//        } else if(id == R.id.itMessages) {
//            MessageActivity.open(this);
//        }

        if(ret && canFinish()) {
            finish();
        }
        return ret;
    }
}
