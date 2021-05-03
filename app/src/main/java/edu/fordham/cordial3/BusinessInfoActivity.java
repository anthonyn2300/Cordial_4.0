package edu.fordham.cordial3;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BusinessInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_info);

        Business business = getIntent().getParcelableExtra("business");

        // TODO: do whatever you need to do with the business
        Toast.makeText(this, business.getName(), Toast.LENGTH_LONG).show();

    }
}