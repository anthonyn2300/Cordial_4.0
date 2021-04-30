package edu.fordham.cordial3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class Search extends AppCompatActivity {

    RecyclerView businessList;
    SearchView searchBusiness;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        businessList = findViewById(R.id.businessList);
        searchBusiness = findViewById(R.id.searchBusiness);
    }
}

    public void doSearch(View v) {

        private DatabaseReference mDatabase;
        FirebaseRecyclerAdapter<Messages, MessageViewHolder> adapter;
        //String key = ... // get from user's input

        List<> businessList = new ArrayList<>();

        ref = FirebaseDatabase.getInstance().getReference();
        ref.child("keywords").child(key).get().
  	.addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                String listOfBusinesses = (String) dataSnapshot.getValue();
                String[] listOfBusIds = listOfBusinesses.split(",");

                for (String id : listOfBusIds) {
                    ref.child("businesses").child(id).get().addOnSuccessListener(
                            new ....
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        businessList bus = (businessList) dataSnapshot.getValue();
                        list.add(bus);
                    }
              	);
                }
            }
        });


}