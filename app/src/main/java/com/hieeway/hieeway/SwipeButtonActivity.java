package com.hieeway.hieeway;

import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.ebanx.swipebtn.OnStateChangeListener;
import com.ebanx.swipebtn.SwipeButton;


public class SwipeButtonActivity extends AppCompatActivity {

    SwipeButton swipeButton;
    Boolean swipeState = false;
    Switch aSwitch;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_button);





        swipeButton = findViewById(R.id.swipeBtn);

        aSwitch = findViewById(R.id.switch_btn);


        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    Toast.makeText(SwipeButtonActivity.this,"Checked!",Toast.LENGTH_SHORT).show();

                else
                    Toast.makeText(SwipeButtonActivity.this,"Unchecked",Toast.LENGTH_SHORT).show();
            }
        });



        swipeButton.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean active) {
                if(active) {
                    swipeState = active;
                    Toast.makeText(SwipeButtonActivity.this, "Message swiped!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(SwipeButtonActivity.this, "Message replying!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        /*swipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SwipeButtonActivity.this,"Message Clicked!",Toast.LENGTH_SHORT).show();
            }
        });
*/
        /*swipeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {



                    Toast.makeText(SwipeButtonActivity.this,"Message Clicked!",Toast.LENGTH_SHORT).show();

                return false;
            }
        });*/
    }
}
