package ru.sobr.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import ru.sobr.app.R;

/**
 * Created with IntelliJ IDEA.
 * User: ryashentsev
 * Date: 03.07.13
 * Time: 13:09
 * To change this template use File | Settings | File Templates.
 */
public class SobrAssistActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobr_assist);
        Button reg1, reg2;
        reg1 = (Button) findViewById(R.id.registration1);
        reg2 = (Button) findViewById(R.id.registration2);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SobrAssistActivity.this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.URL, "http://location.sobr-a.ru/client-portal/?action=register");
                startActivity(intent);
            }
        };
        reg1.setOnClickListener(listener);
        reg2.setOnClickListener(listener);
    }
}
