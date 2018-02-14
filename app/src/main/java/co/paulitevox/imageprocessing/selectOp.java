package co.paulitevox.imageprocessing;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;

public class selectOp extends AppCompatActivity {

    private ImageView image;
    private InterstitialAd mInterstitialAd;
    ExpandableHeightGridView  grid;
    String[] web = {
            "LABEL",
            "FACE",
            "LANDMARK",
            "WEB",
            "TEXT",
            "LOGO",
            "CROP HINTS",
            "PALTTE"

    } ;
    int[] imageId = new int[]{
            R.drawable.ic_label_black_48dp,
            R.drawable.ic_face_black_48dp,
            R.drawable.ic_landscape_black_48dp,
            R.drawable.ic_language_black_48dp,
            R.drawable.ic_text_fields_black_48dp,
            R.drawable.ic_android_black_48dp,
            R.drawable.ic_crop_black_48dp,
            R.drawable.ic_palette_black_48dp,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_op);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-1736689032211248/1532995567");
        AdRequest request = new AdRequest.Builder().addTestDevice("DB87789ADD286D94F9D5F938BA2BC5A6").build();
        request.isTestDevice(selectOp.this);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        image=(ImageView) findViewById(R.id.image);
        Bitmap choose = null;
        Bitmap click=null;
        String filename = getIntent().getStringExtra("image");
        Bitmap fileclick = (Bitmap) getIntent().getParcelableExtra("BitmapImage");

        if(filename!=null) {
            FileInputStream is=null;
            try {
                is = this.openFileInput(filename);
                choose=BitmapFactory.decodeStream(is);
                is.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            image.setImageBitmap(choose);
        }
        else
        {
            image.setImageBitmap(fileclick);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        CustomGrid adapter = new CustomGrid(selectOp.this, web, imageId);

        grid=(ExpandableHeightGridView)findViewById(R.id.grid);

        grid.setAdapter(adapter);
        grid.setExpanded(true);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
                //Toast.makeText(selectOp.this, "You Clicked at " +web[+ position], Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        finish();
        overridePendingTransition( R.anim.slide_out_right, R.anim.slide_in_left );
    }


    }



