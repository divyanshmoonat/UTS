package com.siteberry.uts;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ShareActivity extends AppCompatActivity {
    private ImageView shareQR;
    private ContentResolver contentResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        shareQR = (ImageView)findViewById(R.id.share_qr);
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = 1F;
        getWindow().setAttributes(layout);
        new GetQR().execute();

    }
    public class GetQR extends AsyncTask<String,Void,Bitmap>{
        @Override
        protected Bitmap doInBackground(String... strings) {
            String urlParams =ShareActivity.this.getString(R.string.get_qr)+"&chl="+ShareActivity.this.getString(R.string.share_app)+"&chs=320x320&chld=L|0";
            String result = "";
            InputStream stream = null;
            Bitmap bm = null;
            try {
                URL url = new URL(urlParams);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                stream = connection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(stream);
                bm = BitmapFactory.decodeStream(bis);
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            shareQR.setImageBitmap(bitmap);
        }
    }
}
