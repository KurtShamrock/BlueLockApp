package com.example.appintern;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class NewPassword extends AppCompatActivity {
    private static final String DEBUGTAG = "JWP";
    private static final int MAX_COORDINATES = 4;
    private List<Point> coordinateList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_password);
        coordinateList = new ArrayList<>();
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
                coordinateList.add(point);
                drawCircleAroundPoint(point);
                if (coordinateList.size() == MAX_COORDINATES) {
                    Button nextBtn = findViewById(R.id.nextBtn);
                    Button cancelBtn = findViewById(R.id.cancelBtn);
                    nextBtn.setVisibility(View.VISIBLE);
                    cancelBtn.setVisibility(View.VISIBLE);
                    nextBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            goToRecheckCoordinates();
                        }
                    });
                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            resetActivity();
                        }
                    });
                }
                if (coordinateList.size() > MAX_COORDINATES) {
                    resetActivity();
                }
                @SuppressLint("DefaultLocale") String message = String.format("Coordinates: (%d, %d)", x, y);
                Log.d(NewPassword.DEBUGTAG, message);
                return false;
            }
        });
    }

    private void resetActivity() {
        recreate();
    }
    private void goToRecheckCoordinates() {
        Intent intent = new Intent(this, RecheckPassword.class);
        intent.putExtra("CoordinateList", new ArrayList<>(coordinateList));
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

}