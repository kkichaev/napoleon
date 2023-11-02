package com.novotek.sales;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.grsoft.camera.CameraActivity;
import com.novotek.dataobjects.Brand;
import com.novotek.dataobjects.Order;
import com.novotek.dataobjects.Price;
import com.novotek.dataobjects.ProjectData;
import com.novotek.dataobjects.priceTree.FolderBase;
import com.novotek.dataobjects.priceTree.FolderSrc;
import com.novotek.dataobjects.priceTree.SubFolder;
import com.novotek.dataobjects.ws.ReqCodeParam;
import com.novotek.sales.login_views.LoadData;
import com.novotek.sales.main_views.BaseView;
import com.novotek.sales.main_views.Basket;
import com.novotek.sales.main_views.BasketDetail;
import com.novotek.sales.main_views.BasketError;
import com.novotek.sales.main_views.BasketSendOK;
import com.novotek.sales.main_views.Brands;
import com.novotek.sales.main_views.Catalog;
import com.novotek.sales.main_views.CompanyCard;
import com.novotek.sales.main_views.DeliveryDateSelect;
import com.novotek.sales.main_views.FavPrice;
import com.novotek.sales.main_views.Feedback;
import com.novotek.sales.main_views.Main;
import com.novotek.sales.main_views.Model;
import com.novotek.sales.main_views.OrderDetail;
import com.novotek.sales.main_views.Orders;
import com.novotek.sales.main_views.ProductDetail;
import com.novotek.sales.main_views.Profile;
import com.novotek.utils.Favorites;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    public static final String APP_TOKEN = "app_token";
    static String appId;
    static String devId;
    private static String appToken = "";

    static boolean askingLogin = false;
    static Model activeModel = null;
    static int lastSelectedItem = -1;
    public static String CHANNEL_ID = "";

    BaseView curFragment;
    Model model;
    boolean reenter = false;

    String[] REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE,
    };
    private final int REQUEST_CODE_PERMISSIONS = 10;

    public static ReqCodeParam getProgParams() {
        ReqCodeParam ret = new ReqCodeParam();
//        ret.appId = appId;
//        ret.deviceId = devId;
        return ret;
    }

    private  FolderSrc currentFolder = null;
    private  FolderBase currentSubFolder = null;
    private String searchCondition = null;
    private boolean brandOpenned = false;
    private Brand currentBrand = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new ViewModelProvider(this).get(Model.class);
        if(activeModel != null) {
            if(ProjectData.partners().size() > 0) {
                model.setFrom(activeModel);
                reenter = true;
            }
        }
        activeModel = model;

        createNotificationChannel();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.main_activity);

        BottomNavigationView bv = findViewById(R.id.btMenu);
        bv.setOnNavigationItemSelectedListener(this);

        if(!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        model.getBasketQty().observe(this, qty -> {
            updateBasketBadge(qty);
        });
    }

    private void updateBasketBadge(Integer qty) {
        BottomNavigationView bv = findViewById(R.id.btMenu);
        if(bv == null || qty == null)
            return;

        if(qty == 0) {
            BadgeDrawable b = bv.getBadge(R.id.itBasket);
            if(b != null) {
                b.setVisible(false);
                b.clearNumber();
            }
        } else {
            BadgeDrawable b = bv.getOrCreateBadge(R.id.itBasket);
            b.setVisible(true);
            b.setNumber(qty);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        model.bindPicHandler();
    }

    @Override
    protected void onStop() {
        super.onStop();
        model.unbindPicHandler();
    }

    public void loadFragment(BaseView cf) {
        curFragment = cf;
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out
                )
                .replace(R.id.frmChild, cf, cf.getFragmentTag())
                .commit();
    }

    public void loadFragment(BaseView cf, boolean addToBackStack) {
       loadAnimFragment(cf, addToBackStack, true);
    }

    public void loadAnimFragment(BaseView cf, boolean addToBackStack, boolean animation) {
        curFragment = cf;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (animation)
                ft.setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out
                );

        ft.replace(R.id.frmChild, cf, cf.getFragmentTag());

        if(addToBackStack) {
            ft.addToBackStack(cf.getFragmentTag());
            ft.setReorderingAllowed(true);
        }
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initAppData();

        if(ProjectData.partners().size() == 0 && allPermissionsGranted()) {
            if(askingLogin == false) {
                Login.open(this);
//                if (appToken.length() == 0)
//                    Login.open(this);
//                else
//                    loadFragment(new LoadData());
                askingLogin = true;
            }
        }

        if(reenter) {
            reenter = false;
            if(model.getPartner().getValue() != null) {
                if(lastSelectedItem == -1)
                    lastSelectedItem = R.id.itHome;
                openItem(lastSelectedItem);
            } else {
                selectCurrentPartner();
                openItem(R.id.itHome);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Login.REQ_CODE) {
            askingLogin = false;
            if(resultCode == RESULT_OK) {
                loadFragment(new Main());
                selectCurrentPartner();
            } else if(resultCode == Activity.RESULT_CANCELED) {
                finish();
            }
        } else if(requestCode == SelectPartner.REQ_CODE) {
            if(resultCode == RESULT_OK) {
                model.setPartner(ProjectData.getCurrent());
            }
        } else if(requestCode == CameraActivity.REQ_CODE) {
            if(resultCode == RESULT_OK) {
                String barcode = data.getStringExtra(CameraActivity.BARCODE_TAG);
                Price p = ProjectData.findBarcode(barcode, model.getPartner().getValue());
                if(p != null) {
                    openPriceItem(p);
                } else {
                    Toast.makeText(this, R.string.item_not_found, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void createNotificationChannel() {
        CHANNEL_ID = getString(R.string.channel_id);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    void selectCurrentPartner() {
        if(!SelectPartner.open(this, false)) {
            model.setPartner(ProjectData.getCurrent());
        }
    }

    private Boolean allPermissionsGranted() {
        for(String p : REQUIRED_PERMISSIONS) {
            if(ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    public static String getAppToken(){
        return appToken;
    }

    public static void setAppToken(Context context, String token){
        appToken = token;
        SharedPreferences.Editor ed = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).edit();
        ed.putString(APP_TOKEN, appToken);
        ed.commit();
    }

    @SuppressLint("MissingPermission")
    private void initAppData() {
        SharedPreferences pref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        appToken = pref.getString(APP_TOKEN, "");

        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            task.getException().printStackTrace();
                            return;
                        }
                        appId = task.getResult();
                    }
                });
        fm.subscribeToTopic("news");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            devId = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
        } else {
            TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            devId = tm.getDeviceId();
        }
        if(devId == null) {
            devId = "0123456789012345";
        }
    }

    public void openItem(int id) {
        BottomNavigationView bv = findViewById(R.id.btMenu);
        bv.setSelectedItemId(id);
    }

    public int basketSteps = 0;
    public int MAX_STEP_STORE_DATA = 2;

    public void resetStoredData(){
        currentFolder = null;
        currentSubFolder = null;
        com.novotek.sales.main_views.Price.state = null;
        com.novotek.sales.main_views.Price.selectedFilter = null;
        searchCondition = null;
        brandOpenned = false;
        currentBrand = null;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        BaseView cf = null;
        int id = item.getItemId();
        lastSelectedItem = id;

        if (id== R.id.itCatalog) {
            basketSteps = 0;
        }else{
            basketSteps++;
        }

        if (basketSteps >= MAX_STEP_STORE_DATA){
            resetStoredData();
        }

        if(id == R.id.itHome) cf = new Main();
        else if(id == R.id.itCatalog) cf = getCatalogFragment();
        else if(id == R.id.itOrders) cf = new Orders();
        else if(id == R.id.itBasket) cf = new Basket();
        else if(id == R.id.itProfile) cf = new Profile();

        if(cf != null && cf.getClass() != curFragment.getClass()) {
            FragmentManager fm = getSupportFragmentManager();
            while(fm.getBackStackEntryCount() > 0)
                fm.popBackStackImmediate();

            loadFragment(cf);
        }
        return true;
    }

    @NonNull
    private BaseView getCatalogFragment() {
        if (searchCondition != null) {
            ArrayList<String> products = model.getPartner().getValue().getPrice().allProducts();
            String title = getString(R.string.all_products);

            if (currentFolder != null) {
                products.clear();
                title = currentFolder.name.toString();

                for (SubFolder f : currentFolder.folders) {
                    for (Price p : f.items) {
                        products.add(p.id);
                    }
                }
            }

            return new com.novotek.sales.main_views.Price(searchCondition, products, title, currentFolder == null ? "" : currentFolder.name.name_en, brandOpenned);
        }

        if (currentBrand != null){
            ArrayList<String> src = model.getPartner().getValue().getPrice().products(currentBrand);
            return new com.novotek.sales.main_views.Price(src, currentBrand.name.toString());
        }

        if (brandOpenned == true)
            return new Brands();

        if (currentFolder == null)
            return new Catalog();

        if (currentSubFolder == null)
            return new Catalog(currentFolder);

        return new com.novotek.sales.main_views.Price(currentFolder, currentSubFolder);
    }

    public void logout() {
        model.logout();

        Login.open(this);
        openItem(R.id.itHome);
    }

    public void openCatalog(){
        loadFragment(new Brands());
    }

    public void openPriceItem(Price p) {
        ProductDetail pd = new ProductDetail(p);
        loadFragment(pd, true);
    }

    public void openFolder(FolderBase f, FolderSrc parent) {
        brandOpenned = false;
        searchCondition = null;
        BaseView c;
        if(parent != null) {
            c = new com.novotek.sales.main_views.Price(parent, f);
            currentSubFolder = f;
            currentFolder = parent;
        } else {
            c = f == null ? new Catalog() : new Catalog(f);
            currentFolder = (FolderSrc) f;
            currentSubFolder = null;
        }

        loadFragment(c);
    }

    public void openBrand(Brand b) {
        brandOpenned = true;
        currentBrand = b;
        ArrayList<String> src = model.getPartner().getValue().getPrice().products(b);
        com.novotek.sales.main_views.Price p = new com.novotek.sales.main_views.Price(src, b.name.toString());
        loadFragment(p, false);
    }

    public void openBrands() {
        resetStoredData();
        Brands b = new Brands();
        loadAnimFragment(b, false, false);
    }

    public void openFavorites() {
        Favorites f = new Favorites(this);
        FavPrice p = new FavPrice(f.get(), getString(R.string.favorites));
        loadFragment(p, true);
    }

    public void openFeedback() {
        Feedback f = new Feedback();
        loadFragment(f, true);
    }

    public void makeCall(String phone) {
        try {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + com.novotek.sales.login_views.Model.toPhoneNumber(phone))));
        } catch (Exception e) {
            Toast.makeText(this, R.string.cand_dial, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void openCompanyCard() {
        CompanyCard cc = new CompanyCard();
        loadFragment(cc, true);
    }

    public void openOrder(Order o) {
        model.setOrder(o);
        OrderDetail od = new OrderDetail();
        loadFragment(od, true);
    }

    public void openBasketDetail() {
        BasketDetail bd = new BasketDetail();
        loadFragment(bd, true);
    }

    public void selectDeliveryDate() {
        DeliveryDateSelect dds = new DeliveryDateSelect();
        loadFragment(dds, true);
    }

    public void openProductsSearch(String text, ArrayList<String> items, String title, boolean brand) {
        searchCondition = text;
        com.novotek.sales.main_views.Price p = new com.novotek.sales.main_views.Price(text, items, title, currentFolder == null ? "" : currentFolder.name.name_en, brand);
        loadFragment(p, true);
    }

    public void openBasketError() {
        BasketError err = new BasketError();
        loadFragment(err, true);
    }

    public void openBasketSent() {
        BasketSendOK b = new BasketSendOK();
        loadFragment(b, true);
    }
}
