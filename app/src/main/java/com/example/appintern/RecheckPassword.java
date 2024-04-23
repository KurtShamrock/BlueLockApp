package com.example.appintern;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class RecheckPassword extends AppCompatActivity {
    static {
        System.loadLibrary("keys");
    }
    public static native String getKeys();
    private static final String INPUT_PASSWORD = getKeys();
    private static final String DEBUGTAG = "JWP";
    private static final int MAX_COORDINATES = 4;

    String AES = "AES";
    private List<Point> coordinateList2;
    private List<Point> coordinateList1 = new ArrayList<>();
    String outputString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recheck_password);
        coordinateList2 = new ArrayList<>();
        addTouchListener();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addTouchListener() {
        ImageView image = (ImageView)findViewById((R.id.touch_img));
        image.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int)event.getX();
                int y = (int)event.getY();
                Point point = new Point(x,y);
                coordinateList2.add(point);
                drawCircleAroundPoint(point);
                coordinateList1 = (List<Point>) getIntent().getSerializableExtra("CoordinateList");
                if (coordinateList2.size() == MAX_COORDINATES) {
                    boolean withinRadius = isPointsWithinRadius(coordinateList1, coordinateList2, 100);
                    if(withinRadius) {
                        try {
                            saveNewPassword();
                            Intent intent = new Intent();
                            RecheckPassword.this.setResult(RESULT_OK, intent);
                            finish();
                        } catch (Exception e) {
                            Intent intent = new Intent();
                            RecheckPassword.this.setResult(RESULT_OK, intent);
                            finish();
                            e.printStackTrace();
                        }
                    }
                    else {
                        resetActivity();
                    }
                }
                @SuppressLint("DefaultLocale") String message = String.format("Coordinates: (%d, %d)", x, y);
                Log.d(RecheckPassword.DEBUGTAG, message);
                return false;
            }
        });
    }

    private boolean isPointsWithinRadius(List<Point> coordinateList1, List<Point> coordinateList2, int radiusInDp) {
        float scale = getResources().getDisplayMetrics().density;
        int radiusInPixels = (int) (radiusInDp * scale + 0.5f);
        for (int i = 0; i < coordinateList1.size(); i++) {
            Point point1 = coordinateList1.get(i);
            Point point2 = coordinateList2.get(i);

            double distance = distanceBetweenPoints(point1, point2);

            if (distance > radiusInPixels) {
                return false;
            }
        }
        return true;
    }
    private double distanceBetweenPoints(Point point1, Point point2) {
        // Tính khoảng cách giữa hai điểm sử dụng công thức Euclid
        return Math.sqrt(Math.pow((point2.x - point1.x), 2) + Math.pow((point2.y - point1.y), 2));
    }
    private void saveNewPassword() throws Exception {
        SharedPreferences sharedPreferences = getSharedPreferences("password", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        outputString = encryptCoordinateList(coordinateList1, INPUT_PASSWORD);
        String textToSave = outputString;
        editor.putString("password", textToSave);
        editor.apply();
        Toast.makeText(this, "Set password successfully", Toast.LENGTH_LONG).show();
    }
    private void drawCircleAroundPoint(Point point) {
        ImageView imageView = findViewById(R.id.touch_img);
        Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        imageView.draw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        // Vẽ vòng tròn với bán kính là 50px xung quanh điểm
        canvas.drawCircle(point.x, point.y, 75, paint);
        imageView.setImageBitmap(bitmap);
    }
    private void resetActivity() {
//        coordinateList2.clear();
//        ImageView imageView = findViewById(R.id.touch_img);
//        imageView.setImageBitmap(null);
        Toast.makeText(this, "Incorrect", Toast.LENGTH_LONG).show();
        recreate();
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