package com.example.capstoneproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.InetAddresses;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstoneproject.Constants;
import com.example.capstoneproject.R;
import com.example.capstoneproject.adapters.AdapterProductUser;
import com.example.capstoneproject.models.ModelProduct;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FarmerDetailsActivity extends AppCompatActivity {

    private ImageView farmerIv;
    private TextView farmerNameTv, phoneTv, emailTv, sellingTv, deliveryFeeTv, addressTv, filteredProductsTv;
    private ImageButton callBtn, mapBtn, cartBtn, backBtn, filterProductBtn;
    private RelativeLayout toolbarRl, productsRl;
    private EditText searchProductEt;
    private RecyclerView productsRv;

    private String farmerUid;
    private String myLatitude, myLongitude;
    private String farmerName, farmerEmail, farmerPhone, farmerAddress, farmerLatitude, farmerLongitude;
    private FirebaseAuth firebaseAuth;
    private AdapterProductUser adapterProductUser;

    private ArrayList<ModelProduct> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_details);

        farmerIv = findViewById(R.id.farmerIv);
        farmerNameTv = findViewById(R.id.farmerNameTv);
        phoneTv = findViewById(R.id.phoneTv);
        emailTv = findViewById(R.id.emailTv);
        sellingTv = findViewById(R.id.sellingTv);
        deliveryFeeTv = findViewById(R.id.deliveryFeeTv);
        addressTv = findViewById(R.id.addressTv);
        filteredProductsTv = findViewById(R.id.filteredProductsTv);
        callBtn = findViewById(R.id.callBtn);
        mapBtn = findViewById(R.id.mapBtn);
        cartBtn = findViewById(R.id.cartBtn);
        backBtn = findViewById(R.id.backBtn);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        searchProductEt = findViewById(R.id.searchProductEt);
        productsRl = findViewById(R.id.productsRl);
        productsRv = findViewById(R.id.productsRv);

        farmerUid = getIntent().getStringExtra("farmerUid");
        firebaseAuth = FirebaseAuth.getInstance();
        loadMyInfo();
        loadFarmerDetails();
        loadFarmerProducts();

        //search
        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    adapterProductUser.getFilter().filter(charSequence);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialPhone();
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMap();
            }
        });

        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FarmerDetailsActivity.this);
                builder.setTitle("Choose Category:")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //get selected Item
                                String selected = Constants.productCategories1[i];
                                filteredProductsTv.setText(selected);
                                if(selected.equals("All")){
                                    //load All
                                    loadFarmerProducts();
                                }
                                else{
                                    //load filtered
                                    adapterProductUser.getFilter().filter(selected);
                                }
                            }
                        }).show();
            }
        });
    }

    private void openMap() {
        //saddr means source address
        //daddr means destination address
        String address = "https://maps.google.com/maps?saddr=" + myLatitude +","+ myLongitude + "&daddr=" + farmerLatitude + "," + farmerLongitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        startActivity(intent);
    }

    private void dialPhone() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(farmerPhone))));
        Toast.makeText(this, ""+farmerPhone, Toast.LENGTH_SHORT).show();
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String name = ""+ds.child("name").getValue();
                            String email = ""+ds.child("email").getValue();
                            String phone = ""+ds.child("phone").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();
                            String accountType = ""+ds.child("accountType").getValue();
                            myLatitude = ""+ds.child("latitude").getValue();
                            myLongitude = ""+ds.child("longitude").getValue();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadFarmerDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(farmerUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get shop data
                farmerName = ""+snapshot.child("name").getValue();
                farmerEmail = ""+snapshot.child("email").getValue();
                farmerPhone = ""+snapshot.child("phone").getValue();
                farmerLatitude = ""+snapshot.child("latitude").getValue();
                farmerLongitude = ""+snapshot.child("longitude").getValue();
                farmerAddress = ""+snapshot.child("address").getValue();
                String deliveryFee = ""+snapshot.child("deliveryFee").getValue();
                String farmerSelling = ""+snapshot.child("selling").getValue();

                //set data
                farmerNameTv.setText(farmerName);
                emailTv.setText(farmerEmail);
                phoneTv.setText(farmerPhone);
                addressTv.setText(farmerAddress);
                deliveryFeeTv.setText("Delivery Fee: ₹"+deliveryFee);

                if(farmerSelling.equals("true")){
                    sellingTv.setText("Selling");
                }
                else{
                    sellingTv.setText("Not Selling");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadFarmerProducts() {
        productList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(farmerUid).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear list before adding items
                        for(DataSnapshot ds:snapshot.getChildren()){
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }
                        //set up adapter
                        adapterProductUser = new AdapterProductUser(FarmerDetailsActivity.this, productList);
                        //set adapter
                        productsRv.setAdapter(adapterProductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }




}