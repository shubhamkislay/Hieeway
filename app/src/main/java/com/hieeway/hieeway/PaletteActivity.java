package com.hieeway.hieeway;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class PaletteActivity extends AppCompatActivity {

    Bitmap bitmap;
    ImageView imageView;
    RelativeLayout backgroundLayout;
    private Palette.Swatch vibrantSwatch;
    private Palette.Swatch mutedSwatch;
    private Palette.Swatch lightVibrantSwatch;
    private Palette.Swatch darkVibrantSwatch;
    private Palette.Swatch dominantSwatch;
    private Palette.Swatch lightMutedSwatch;
    private Palette.Swatch darkMutedSwatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette);


        imageView = findViewById(R.id.image_view);
        backgroundLayout = findViewById(R.id.background_layout);


        bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();


        setPaletteColor();


    }

    private void setPaletteColor() {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@Nullable Palette palette) {

                vibrantSwatch = palette.getVibrantSwatch();
                darkVibrantSwatch = palette.getDarkVibrantSwatch();
                lightVibrantSwatch = palette.getLightVibrantSwatch();
                mutedSwatch = palette.getMutedSwatch();
                darkMutedSwatch = palette.getDarkMutedSwatch();
                lightMutedSwatch = palette.getLightMutedSwatch();
                dominantSwatch = palette.getDominantSwatch();


                Palette.Swatch currentSwatch = null;
                if (vibrantSwatch != null)
                    currentSwatch = vibrantSwatch;
                else if (darkVibrantSwatch != null)
                    currentSwatch = darkVibrantSwatch;
                else if (lightVibrantSwatch != null)
                    currentSwatch = lightVibrantSwatch;
                else if (mutedSwatch != null)
                    currentSwatch = mutedSwatch;
                else if (darkMutedSwatch != null)
                    currentSwatch = darkMutedSwatch;
                else if (lightMutedSwatch != null)
                    currentSwatch = lightMutedSwatch;
                else if (dominantSwatch != null)
                    currentSwatch = dominantSwatch;


                if (currentSwatch != null) {
                    backgroundLayout.setBackgroundColor(currentSwatch.getRgb());

                    // ...
                } else {
                    Toast.makeText(PaletteActivity.this, "Null color", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }


}
