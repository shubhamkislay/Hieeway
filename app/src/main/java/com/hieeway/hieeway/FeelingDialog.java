package com.hieeway.hieeway;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hieeway.hieeway.Interface.AddFeelingFragmentListener;
import com.hieeway.hieeway.Interface.FeelingListener;

import static com.hieeway.hieeway.VerticalPageActivityPerf.ANGRY;
import static com.hieeway.hieeway.VerticalPageActivityPerf.BORED;
import static com.hieeway.hieeway.VerticalPageActivityPerf.CONFUSED;
import static com.hieeway.hieeway.VerticalPageActivityPerf.EXCITED;
import static com.hieeway.hieeway.VerticalPageActivityPerf.HAPPY;
import static com.hieeway.hieeway.VerticalPageActivityPerf.SAD;


public class FeelingDialog extends Dialog {


    String feelingNow;
    FeelingListener feelingListener;
    AddFeelingFragmentListener addFeelingFragmentListener;
    TextView happy_emoji;
    TextView bored_emoji;
    TextView sad_emoji;
    TextView excited_emoji;
    TextView angry_emoji;
    TextView confused_emoji;
    TextView happy_txt;
    TextView sad_txt;
    TextView confused_txt;
    TextView excited_txt;
    TextView angry_txt;
    TextView bored_txt;
    RelativeLayout add_feeling_layout, add_feeling_btn, custom_feel_bacl;
    Context context;


    public FeelingDialog(Context context, FeelingListener feelingListener, String feelingNow, AddFeelingFragmentListener addFeelingFragmentListener) {
        super(context);
        this.context = context;
        this.feelingListener = feelingListener;
        this.feelingNow = feelingNow;
        this.addFeelingFragmentListener = addFeelingFragmentListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fellings_emoticon_dailog);

        happy_emoji = findViewById(R.id.happy_emoji);
        bored_emoji = findViewById(R.id.bored_emoji);
        sad_emoji = findViewById(R.id.sad_emoji);
        excited_emoji = findViewById(R.id.excited_emoji);
        angry_emoji = findViewById(R.id.angry_emoji);
        confused_emoji = findViewById(R.id.confused_emoji);
        add_feeling_layout = findViewById(R.id.add_feeling_layout);

        happy_txt = findViewById(R.id.happy_txt);
        sad_txt = findViewById(R.id.sad_txt);
        confused_txt = findViewById(R.id.confused_txt);
        excited_txt = findViewById(R.id.excited_txt);
        angry_txt = findViewById(R.id.angry_txt);
        bored_txt = findViewById(R.id.bored_txt);

        custom_feel_bacl = findViewById(R.id.custom_feel_bacl);

        add_feeling_btn = findViewById(R.id.add_feeling_btn);

        add_feeling_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFeelingFragmentListener.setFeelingChange(false, "", "");
                dismiss();
            }
        });

        add_feeling_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFeelingFragmentListener.setFeelingChange(false, "", "");
                dismiss();
            }
        });


        happy_txt.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        sad_txt.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        confused_txt.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        excited_txt.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        angry_txt.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        bored_txt.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans-bold.otf"));


        happy_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //happy_emoji.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.feelingDeactiveColor )));
                happy_emoji.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimaryDark)));
                removeHighlight(feelingNow);
                feelingListener.changeFeeling(HAPPY);
                dismiss();
            }
        });

        bored_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // bored_emoji.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.feelingDeactiveColor )));
                happy_emoji.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimaryDark)));
                removeHighlight(feelingNow);
                feelingListener.changeFeeling(BORED);
                dismiss();
            }
        });

        sad_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sad_emoji.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimaryDark)));
                removeHighlight(feelingNow);
                feelingListener.changeFeeling(SAD);
                dismiss();
            }
        });

        confused_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confused_emoji.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimaryDark)));
                removeHighlight(feelingNow);
                feelingListener.changeFeeling(CONFUSED);
                dismiss();
            }
        });

        angry_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                angry_emoji.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimaryDark)));
                removeHighlight(feelingNow);
                feelingListener.changeFeeling(ANGRY);
                dismiss();
            }
        });

        excited_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                excited_emoji.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimaryDark)));
                removeHighlight(feelingNow);
                feelingListener.changeFeeling("excited");
                dismiss();
            }
        });


        setEmojiHighlight();
    }

    private void setEmojiHighlight() {
        switch (feelingNow)
        {
            case HAPPY:
                happy_emoji.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimaryDark)));
                happy_txt.setTextColor(getContext().getResources().getColor(R.color.white));
                break;

            case SAD:
                sad_emoji.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimaryDark)));
                sad_txt.setTextColor(getContext().getResources().getColor(R.color.white));
                break;

            case CONFUSED:
                confused_emoji.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimaryDark)));
                confused_txt.setTextColor(getContext().getResources().getColor(R.color.white));
                break;

            case EXCITED:
                excited_emoji.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimaryDark)));
                excited_txt.setTextColor(getContext().getResources().getColor(R.color.white));
                break;

            case ANGRY:
                angry_emoji.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimaryDark)));
                angry_txt.setTextColor(getContext().getResources().getColor(R.color.white));
                break;

            case BORED:
                bored_emoji.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimaryDark)));
                bored_txt.setTextColor(getContext().getResources().getColor(R.color.white));
                break;

            default:
                custom_feel_bacl.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimaryDark)));
                break;


        }

    }

    private void removeHighlight(String feelingNow) {

        switch (feelingNow)
        {
            case HAPPY: happy_emoji.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.feelingDeactiveColor )));
                        happy_txt.setTextColor(getContext().getResources().getColor(R.color.feelingDeactiveColor));
                break;

            case SAD: sad_emoji.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.feelingDeactiveColor )));
                sad_txt.setTextColor(getContext().getResources().getColor(R.color.feelingDeactiveColor));

                break;

            case CONFUSED: confused_emoji.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.feelingDeactiveColor )));
                confused_txt.setTextColor(getContext().getResources().getColor(R.color.feelingDeactiveColor));

                break;

            case EXCITED:
                excited_emoji.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.feelingDeactiveColor)));
                excited_txt.setTextColor(getContext().getResources().getColor(R.color.feelingDeactiveColor));

                break;

            case ANGRY:
                angry_emoji.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.feelingDeactiveColor)));
                angry_txt.setTextColor(getContext().getResources().getColor(R.color.feelingDeactiveColor));

                break;

            case BORED:
                bored_emoji.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.feelingDeactiveColor)));
                bored_txt.setTextColor(getContext().getResources().getColor(R.color.feelingDeactiveColor));

                break;


        }
    }
}
