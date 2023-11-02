package com.grsoft.napoleon.dostavka;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.grsoft.camera.BarcodeHandler;
import com.grsoft.camera.CameraActivity;
import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ItemDef;
import com.grsoft.dataobjects.RouteItemRow;
import com.grsoft.dataobjects.Waybill;
import com.grsoft.napoleon.documents.DShipmentDoc;

public class MainEx extends Main {
    public final String BARCODE_KEY = "barcode";
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean res = super.onCreateOptionsMenu(menu);

        final MenuItem item = menu.findItem(R.id.itBarcode);

        if (item != null)
            item.setVisible(true);

        return res;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.itBarcode) {
            CameraActivity.openBCScanner(this, new BarcodeHandler() {
                @Override
                public void initActivity(Activity owner) {

                }

                @Override
                public boolean onReadBarcode(Activity owner,String barcode, int type, long elapsesMs) {
                    boolean res =  tryOpenWaybill(barcode);

                    if (!res) {
                        Bundle bundle = new Bundle();
                        bundle.putString(BARCODE_KEY, barcode);
                        runOnUiThread(() -> showDialog(R.id.not_barcode_dlg, bundle));
                    }

                    return true;
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == R.id.not_barcode_dlg)
            return createNotBarcodeDlg();
        return super.onCreateDialog(id);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        if (id == R.id.not_barcode_dlg)
            prepareNotBarcodeDlg(dialog, args.getString(BARCODE_KEY));

        super.onPrepareDialog(id, dialog, args);
    }

    private void prepareNotBarcodeDlg(Dialog dialog, String barcode) {
        ((AlertDialog)dialog).setMessage(String.format("Ўтрих код '%s' не найден", barcode));
    }

    private Dialog createNotBarcodeDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert);
        builder.setMessage("");
        builder.setPositiveButton(R.string.ok, null);

        return builder.create();
    }

    private boolean tryOpenWaybill(String barcode) {
        boolean res = false;

        Cursor c = null;
        try{
            c = DataBaseManager.getDataBase().query(DataObjectInfo.getInstance().getTableName(Waybill.class),
                    new String[]{"number"}, "barcode=?", new String[]{barcode}, null, null, null);

            if (c.moveToNext()){
                String number = c.getString(0);

                if (number.length() > 0){
                    for(int i = 0; i < adapter.getCount(); i++){
                        RouteItemRow r = (RouteItemRow) adapter.getItem(i);
                        if (r != null && r.item != null)
                            for(ItemDef def : r.item.docs){
                                if (def.type.equals(DShipmentDoc.instance().getObjectName()) && def.number.equals(number)) {
                                    runOnUiThread(() -> RoutePointView.open(MainEx.this, r.item, number));
                                    res = true;
                                    break;
                                }
                            }
                    }

                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (c != null)
                c.close();
        }

        return res;
    }
}
