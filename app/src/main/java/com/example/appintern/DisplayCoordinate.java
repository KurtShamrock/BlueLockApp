package com.example.appintern;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.WindowDecorActionBar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.graphics.Point;

public class DisplayCoordinate  extends AppCompatActivity {
    private static final String INPUT_PASSWORD = "not a password";
    TextView outputText;
    Button encBtn, decBtn;
    String outputString;
    String AES = "AES";
    boolean setPassword = false;
    private List<Point> coordinates = new ArrayList<>();
    TextView coordinateTextView;
    private List<Point> coordinates2 = new ArrayList<>();
    private static final String DEBUGTAG = "JWP";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_coordinate);
        coordinateTextView = findViewById(R.id.coordinateTextView);
        ArrayList<Point> CoordinatesList = (ArrayList<Point>) getIntent().getSerializableExtra("CoordinateList");
        coordinateTextView.setText(String.valueOf(CoordinatesList));

        outputText = (TextView) findViewById(R.id.outputText);
        encBtn = (Button) findViewById(R.id.encBtn);
        decBtn = (Button) findViewById(R.id.decBtn);
        outputText.setTextIsSelectable(true);
        SharedPreferences sharedPreferences = getSharedPreferences("password", Context.MODE_PRIVATE);

        encBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    outputString = encryptCoordinateList(CoordinatesList, INPUT_PASSWORD);
                    outputText.setText(outputString);

                    if (setPassword == false) {
                        setPassword = true;
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String textToSave = outputString;
                        editor.putString("password", textToSave);
                        editor.apply();
                    }
                    else {
                        String savedText = sharedPreferences.getString("password", "");
                        String newText = outputString;
                        if (newText.equals(savedText)) {
                            Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        decBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    coordinates2 = decryptCoordinateString(outputString, INPUT_PASSWORD);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                outputText.setText(String.valueOf(coordinates2));
            }
        });

    }

    private List<Point> decryptCoordinateString(String encryptedString, String password) throws Exception {
        // Giải mã chuỗi bằng AES
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodeValue = Base64.decode(encryptedString, Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodeValue);
        String decryptedCoordinateString = new String(decValue);

        // Tạo danh sách để lưu các điểm
        List<Point> coordinateList = new ArrayList<>();

        // Sử dụng biểu thức chính quy để trích xuất các tọa độ của từng điểm
        Pattern pattern = Pattern.compile("\\((\\d+),\\s*(\\d+)\\)");
        Matcher matcher = pattern.matcher(decryptedCoordinateString);

        // Duyệt qua từng kết quả phù hợp và thêm các điểm vào danh sách
        while (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            coordinateList.add(new Point(x, y));
        }
        @SuppressLint("DefaultLocale") String message1 = String.format(coordinateList.toString());
        Log.d(DisplayCoordinate.DEBUGTAG, message1);
        // Trả về danh sách các điểm đã giải mã
        return coordinateList;
    }
    private String encryptCoordinateList(List<Point> coordinateList, String password) throws Exception {
        // Chuyển đổi danh sách điểm thành chuỗi
        String coordinateString = coordinateList.toString();
        // Mã hóa chuỗi bằng AES9
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(coordinateString.getBytes());
        return Base64.encodeToString(encVal, Base64.DEFAULT);
    }
    private SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }
}
