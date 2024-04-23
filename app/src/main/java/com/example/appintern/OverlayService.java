package com.example.appintern;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class OverlayService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private WindowManager windowManager;
    private View overlayView;
    private static final String DEBUGTAG = "JWP OverLay";
    static {
        System.loadLibrary("keys");
    }
    public static native String getKeys();
    private static final String INPUT_PASSWORD = getKeys();
    String AES = "AES";
    private static final int MAX_COORDINATES = 4;
    private List<Point> coordinateList;
    private List<Point> coordinatePassword = new ArrayList<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showOverlay();
        Log.i("Check","start");
        return START_STICKY;
    }

    @SuppressLint("InflateParams")
    private void showOverlay() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                PixelFormat.OPAQUE);
        params.x=0;
        params.y=0;
        LayoutInflater inflater = LayoutInflater.from(this);
        overlayView = inflater.inflate(R.layout.overlay_layout, null);
        coordinateList = new ArrayList<>();
        addTouchListener();
        // Button to dismiss the overlay view
//        Button dismissButton = overlayView.findViewById(R.id.dismissButton);
//        dismissButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Dismiss the overlay view
//                dismissOverlay();
//            }
//        });


        windowManager.addView(overlayView, params);
    }
    @SuppressLint("ClickableViewAccessibility")
    private void addTouchListener() {
        ImageView image = (ImageView)overlayView.findViewById((R.id.touch_img));
        image.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int)event.getX();
                int y = (int)event.getY();
                Point point = new Point(x,y);
                coordinateList.add(point);
                drawCircleAroundPoint(point);
                if (coordinateList.size() == MAX_COORDINATES) {
                    try {
                        boolean isPass = CheckPassword();
                        if(isPass) {
                            Toast.makeText(getApplicationContext(), "Password correct", Toast.LENGTH_SHORT).show();
                            dismissOverlay();

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
    }
    private boolean CheckPassword() throws Exception {
        SharedPreferences sharedPreferences = getSharedPreferences("password", Context.MODE_PRIVATE);
        String savedText = sharedPreferences.getString("password", "");
        @SuppressLint("DefaultLocale") String message1 = String.format(coordinatePassword.toString());
        Log.d(OverlayService.DEBUGTAG, message1);
        coordinatePassword = decryptCoordinateString(savedText, INPUT_PASSWORD);
        return isPointsWithinRadius(coordinateList,coordinatePassword,100);
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
        Log.d(OverlayService.DEBUGTAG, message1);
        // Trả về danh sách các điểm đã giải mã
        return coordinateList;
    }
    private SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }
    private void drawCircleAroundPoint(Point point) {
        ImageView imageView = overlayView.findViewById(R.id.touch_img);
        Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        imageView.draw(canvas);
        Log.i("touch","click");
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        // Vẽ vòng tròn với bán kính là 50px xung quanh điểm
        canvas.drawCircle(point.x, point.y, 75, paint);
        imageView.setImageBitmap(bitmap);
    }
    private void dismissOverlay() {
        if (windowManager != null && overlayView != null) {
            windowManager.removeView(overlayView);
            stopSelf();
        }
    }
}
