package com.hieeway.hieeway;

import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class EncryptionActivity extends AppCompatActivity {

    EditText messageBox;
    Button encryptBtn, decryptBtn, generateKeysBtn;
    TextView encryptedText, decrpytedText, publicKeyTxtView, privateKeyTxtView;
    String publicKeyText, privateKeyText, encryptedMessage, originalMessage, decryptedMessage;
    String prevPublicKey;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String PUBLIC_KEY = "publicKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encryption);

        messageBox = findViewById(R.id.edit_text_message);


        encryptBtn = findViewById(R.id.encryptBtn);
        decryptBtn = findViewById(R.id.decryptedBtn);

        decrpytedText = findViewById(R.id.decryptedText);
        encryptedText =findViewById(R.id.encryptedText);

        generateKeysBtn = findViewById(R.id.generatekeysBtn);
        publicKeyTxtView = findViewById(R.id.publickey);
        privateKeyTxtView = findViewById(R.id.privatekey);


        publicKeyText = null;
        privateKeyText =  null;
        encryptedMessage = null;
        originalMessage = null;
        decryptedMessage = null;
        prevPublicKey = null;


        generateKeysBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                KeyPair kp = getKeyPair();
                PublicKey publicKey = kp.getPublic();
                byte[] publicKeyBytes = publicKey.getEncoded();
                String publicKeyBytesBase64 = new String(Base64.encode(publicKeyBytes, Base64.DEFAULT));

                publicKeyText = publicKeyBytesBase64;

                if(prevPublicKey!=null)
                {
                    if(privateKeyText.equals(prevPublicKey))
                        Toast.makeText(EncryptionActivity.this,"Same public keys",Toast.LENGTH_SHORT).show();

                    prevPublicKey = publicKeyText;
                }
                else
                    prevPublicKey = publicKeyText;

                publicKeyTxtView.setText(publicKeyText);


                PrivateKey privateKey = kp.getPrivate();
                byte[] privateKeyBytes = privateKey.getEncoded();
                String privateKeyBytesBase64 = new String(Base64.encode(privateKeyBytes, Base64.DEFAULT));

                privateKeyText = privateKeyBytesBase64;

                privateKeyTxtView.setText(privateKeyText);

                Toast.makeText(EncryptionActivity.this,
                        "Lengths--- public key: "
                                +publicKeyText.length()+"--- private key: "+privateKeyText.length(),Toast.LENGTH_SHORT).show();

                saveKeys(publicKeyText,privateKeyText);


            }
        });

        encryptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(publicKeyText!=null&&privateKeyText!=null)
                {
                    originalMessage = messageBox.getText().toString();

                    encryptedMessage = encryptRSAToString(originalMessage,publicKeyText);
                    encryptedText.setText(encryptedMessage);

                    Toast.makeText(EncryptionActivity.this,
                            "Length--- Encrypted Message: "
                                    +encryptedMessage.length(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        decryptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(encryptedText!=null)
                {
                    decryptedMessage = decryptRSAToString(encryptedMessage,privateKeyText);

                    decrpytedText.setText(decryptedMessage);
                }
            }
        });




        LoadKeys();

    }

    private void saveKeys(String publicKey,String privateKey) {


        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(PRIVATE_KEY,privateKey);
        editor.putString(PUBLIC_KEY,publicKey);

        editor.apply();

        Toast.makeText(EncryptionActivity.this,"Keys saved",Toast.LENGTH_SHORT).show();


    }

    private void LoadKeys()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

        privateKeyText = sharedPreferences.getString(PRIVATE_KEY,null);
        if(privateKeyText!=null)
            privateKeyTxtView.setText(privateKeyText);

        publicKeyText = sharedPreferences.getString(PUBLIC_KEY,null);
        if(publicKeyText!=null)
            publicKeyTxtView.setText(publicKeyText);


    }


/*    public void TestEncryptData(String dataToEncrypt) {
        // generate a new public/private key pair to test with (note. you should only do this once and keep them!)
        KeyPair kp = getKeyPair();

        PublicKey publicKey = kp.getPublic();
        byte[] publicKeyBytes = publicKey.getEncoded();
        String publicKeyBytesBase64 = new String(Base64.encode(publicKeyBytes, Base64.DEFAULT));

        PrivateKey privateKey = kp.getPrivate();
        byte[] privateKeyBytes = privateKey.getEncoded();
        String privateKeyBytesBase64 = new String(Base64.encode(privateKeyBytes, Base64.DEFAULT));

        // test encryption
        String encrypted = encryptRSAToString(dataToEncrypt, publicKeyBytesBase64);

        // test decryption
        String decrypted = decryptRSAToString(encrypted, privateKeyBytesBase64);
    }*/

    public  KeyPair getKeyPair() {
        KeyPair kp = null;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            kp = kpg.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return kp;
    }

    public  String encryptRSAToString(String clearText, String publicKey) {
        String encryptedBase64 = "";
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            KeySpec keySpec = new X509EncodedKeySpec(Base64.decode(publicKey.trim().getBytes(), Base64.DEFAULT));
            Key key = keyFac.generatePublic(keySpec);

            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encryptedBytes = cipher.doFinal(clearText.getBytes("UTF-8"));
            encryptedBase64 = new String(Base64.encode(encryptedBytes, Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encryptedBase64.replaceAll("(\\r|\\n)", "");
    }

    public  String decryptRSAToString(String encryptedBase64, String privateKey) {

        String decryptedString = "";
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            KeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decode(privateKey.trim().getBytes(), Base64.DEFAULT));
            Key key = keyFac.generatePrivate(keySpec);

            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            // encrypt the plain text using the public key
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] encryptedBytes = Base64.decode(encryptedBase64, Base64.DEFAULT);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            decryptedString = new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decryptedString;
    }
}
