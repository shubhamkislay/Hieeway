package com.shubhamkislay.jetpacklogin;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.instachat.emojilibrary.model.layout.EmojiCompatActivity;
import br.com.instachat.emojilibrary.model.layout.EmojiEditText;
import br.com.instachat.emojilibrary.model.layout.EmojiKeyboardLayout;

public class TestJavaEmojiKeyBoard extends EmojiCompatActivity {


    EmojiEditText emojiconEditText;
    TextView emojiconTextView;
    Button sendBtn;
    Boolean emojiActive = true;
    Boolean isKeyboardOpen = false;
    ImageView emojiBtn;
    View rootView;
    EmojiKeyboardLayout emojiKeyboardLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_java_emoji_key_board);

        emojiKeyboardLayout = findViewById(R.id.emoji_keyboard_layout);


        emojiconEditText = findViewById(R.id.emojiEdittext);
        emojiconTextView = findViewById(R.id.emojitextview);
        sendBtn = findViewById(R.id.emoji_send_btn);
        rootView = findViewById(R.id.rootview);
        emojiBtn = findViewById(R.id.emoji_toggle_btn);


        emojiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(emojiActive) {

                    if (emojiconEditText.isSoftKeyboardVisible())
                        emojiconEditText.hideSoftKeyboard();

                    emojiKeyboardLayout.prepareKeyboard(TestJavaEmojiKeyBoard.this, emojiconEditText);
                    emojiKeyboardLayout.setVisibility(View.VISIBLE);

                    emojiActive = false;
                }

                else
                {


                  //  emojiKeyboardLayout.prepareKeyboard(TestJavaEmojiKeyBoard.this, emojiconEditText);
                    emojiKeyboardLayout.setVisibility(View.GONE);

                    emojiconEditText.showSoftKeyboard();

                    emojiActive = true;

                }


            }
        });


        emojiconEditText.addOnSoftKeyboardListener(new EmojiEditText.OnSoftKeyboardListener() {
            @Override
            public void onSoftKeyboardDisplay() {
                emojiKeyboardLayout.setVisibility(View.GONE);
                emojiActive = true;

            }

            @Override
            public void onSoftKeyboardHidden() {


            }
        });


    }






}
