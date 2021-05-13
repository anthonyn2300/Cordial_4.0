package edu.fordham.cordial3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {

    RecyclerView businessList;
    SearchView searchBusiness;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        businessList = findViewById(R.id.businessList);
        businessList.setLayoutManager(new LinearLayoutManager(this));

        searchBusiness = findViewById(R.id.searchBusiness);
        searchBusiness.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
                doSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public void doSearch(String key) {
        // for testing
        //List<Business> searchResults = new ArrayList<>();
        //searchResults.add(new Business("Tom's Pizza", ""));
        //searchResults.add(new Business("Pizza Hut", ""));
        //SearchResultAdapter adapter = new SearchResultAdapter(searchResults);
        //businessList.setAdapter(adapter);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("keywords").child(key).get().
                addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        String listOfBusinesses = dataSnapshot.getValue(String.class);
                        String[] listOfBusIds = listOfBusinesses.split(",");

                        List<Business> searchResults = new ArrayList<>();
                        for (String id : listOfBusIds) {
                            ref.child("businesses").child(id).get().addOnSuccessListener(
                                    new OnSuccessListener<DataSnapshot>() {
                                        @Override
                                        public void onSuccess(DataSnapshot dataSnapshot) {
                                            Business bus = dataSnapshot.getValue(Business.class);
                                            searchResults.add(bus);
                                        }
                                    });
                        }
                        SearchResultAdapter adapter = new SearchResultAdapter(searchResults);
                        businessList.setAdapter(adapter);
                    }
                });
    }

    public static class BusinessViewHolder extends RecyclerView.ViewHolder {
        // TODO: add more stuff here; anything you want to display; look at the weather app
        //  for example:
        TextView businessNameTextView;
        Business business;
        TextView phoneTextView;
        TextView urlTextView;
        TextView locationTextView;


        public BusinessViewHolder(@NonNull View itemView) {
            super(itemView);
            businessNameTextView = itemView.findViewById(R.id.businessNameTextView);
            phoneTextView = itemView.findViewById(R.id.phoneTextView);
            urlTextView = itemView.findViewById(R.id.urlTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            businessNameTextView = itemView.findViewById(R.id.businessNameTextView);
            businessNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: open another activity for displaying the info of business
                    Toast.makeText(v.getContext(), business.getName(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(v.getContext(), BusinessInfoActivity.class);
                    intent.putExtra("business", business);
                    v.getContext().startActivity(intent);
                }
            });
        }
                public void updateBusiness(Business business) {
                    // TODO: update the view
                    Log.i("mobdev", "update business: " + business.getName());
                    businessNameTextView.setText(business.getName());
                    phoneTextView.setText(business.getPhone());
                    urlTextView.setText(business.getWebsite());
                    locationTextView.setText(business.getAddress());
                    this.business = business;
                }
            }


    static class SearchResultAdapter extends RecyclerView.Adapter<BusinessViewHolder> {
        List<Business> data;

        public SearchResultAdapter(List<Business> businesses) {
            data = businesses;
        }


        @NonNull
        @Override
        public BusinessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_business, parent, false);
            return new BusinessViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull BusinessViewHolder holder, int position) {
            holder.updateBusiness(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
}