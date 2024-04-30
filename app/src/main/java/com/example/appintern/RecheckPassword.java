package com.example.appintern;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class RecheckPassword extends AppCompatActivity {
    private static final String DEBUGTAG = "JWP";
    private static final int MAX_COORDINATES = 4;
    private List<Point> coordinateList2;
    private List<Point> coordinateList1 = new ArrayList<>();

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
                        goToDisplayCoordinates();
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

            // Tính khoảng cách giữa hai điểm
            double distance = distanceBetweenPoints(point1, point2);

            // Kiểm tra xem khoảng cách có nhỏ hơn bán kính cho phép không
            if (distance > radiusInPixels) {
                return false; // Nếu một điểm nằm ngoài phạm vi, trả về false ngay lập tức
            }
        }
        return true;
    }
    private double distanceBetweenPoints(Point point1, Point point2) {
        // Tính khoảng cách giữa hai điểm sử dụng công thức Euclid
        return Math.sqrt(Math.pow((point2.x - point1.x), 2) + Math.pow((point2.y - point1.y), 2));
    }
    private void goToDisplayCoordinates() {
        Intent intent = new Intent(this, DisplayCoordinate.class);
        intent.putExtra("CoordinateList", new ArrayList<>(coordinateList1));
        startActivity(intent);
        finish();
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
}