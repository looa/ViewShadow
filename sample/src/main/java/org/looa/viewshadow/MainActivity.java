package org.looa.viewshadow;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.looa.view.ViewShadow;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvToast;
    private TextView tvToast2;
    private TextView tvToast3;
    private TextView tvSquare;
    private TextView tvCircle;

    private View vCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvToast = (TextView) findViewById(R.id.tv_toast);
        tvToast2 = (TextView) findViewById(R.id.tv_toast2);
        tvToast3 = (TextView) findViewById(R.id.tv_toast3);
        tvSquare = (TextView) findViewById(R.id.tv_square);
        tvCircle = (TextView) findViewById(R.id.tv_circle);

        vCircle = findViewById(R.id.v_circle);

        tvToast.setText("ViewCompat");
        tvToast2.setText("ViewShadow");
        tvToast3.setText("ViewShadow");
        tvSquare.setText("Square");
        tvCircle.setText("OK");

        tvToast.setOnClickListener(this);
        tvToast2.setOnClickListener(this);
        tvCircle.setOnClickListener(this);

        int elevation = 20;

        ViewCompat.setElevation(tvToast, elevation);
        ViewShadow.setElevation(tvToast2, elevation);
        ViewShadow.setElevation(tvToast3, elevation, getResources().getColor(R.color.shadow));
        ViewShadow.setElevation(tvSquare, elevation);
        ViewShadow.setElevation(tvCircle, elevation * 2);

        ViewShadow.setElevation(vCircle, 8);
    }

    @Override
    public void onClick(View v) {
        if (v == tvToast) {
            tvSquare.setText("Click 1st");
        } else if (v == tvToast2) {
            tvSquare.setText("Click 2th");
        } else if (v == tvCircle) {
            tvSquare.setText("Click Circle");
        }
    }
}
