package com.example.capstoneproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.InetAddresses;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstoneproject.Constants;
import com.example.capstoneproject.R;
import com.example.capstoneproject.adapters.AdapterCartItem;
import com.example.capstoneproject.adapters.AdapterProductUser;
import com.example.capstoneproject.models.ModelCartItem;
import com.example.capstoneproject.models.ModelProduct;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

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
    public String deliveryFee;
    private FirebaseAuth firebaseAuth;
    private AdapterProductUser adapterProductUser;

    private ArrayList<ModelProduct> productList;
    private ArrayList<ModelCartItem> cartItemList;
    private AdapterCartItem adapterCartItem;

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

        deleteCartData();

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
                //cart dialog
                showCartDialog();
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

    private void deleteCartData() {

        EasyDB easyDB = EasyDB.init(this, "ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                .doneTableColumn();

        easyDB.deleteAllDataFromTable();
    }

    public double allTotalPrice = 0.00;
    public TextView sTotalTv, dFeeTv, allTotalPriceTv;
    private void showCartDialog() {

        cartItemList = new ArrayList<>();

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cart, null);

        TextView farmerNameTv = view.findViewById(R.id.farmerNameTv);
        RecyclerView cartItemsRv = view.findViewById(R.id.cartItemsRv);
        sTotalTv = view.findViewById(R.id.sTotalTv);
        dFeeTv = view.findViewById(R.id.dFeeTv);
        allTotalPriceTv = view.findViewById(R.id.totalTv);
        Button checkoutBtn = view.findViewById(R.id.checkOutBtn);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        farmerNameTv.setText(farmerName);

        EasyDB easyDB = EasyDB.init(this, "ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                .doneTableColumn();

        Cursor res = easyDB.getAllData();
        while (res.moveToNext()){
            String id = res.getString(1);
            String pId = res.getString(2);
            String name = res.getString(3);
            String price = res.getString(4);
            String cost = res.getString(5);
            String quantity = res.getString(6);

            allTotalPrice = allTotalPrice + Double.parseDouble(cost);

            ModelCartItem modelCartItem = new ModelCartItem(
                    ""+id,
                    ""+pId,
                    ""+name,
                    ""+price,
                    ""+cost,
                    ""+quantity
            );

            cartItemList.add(modelCartItem);
        }

        //setup adapter
        adapterCartItem = new AdapterCartItem(this, cartItemList);
        cartItemsRv.setAdapter(adapterCartItem);

        dFeeTv.setText("₹"+deliveryFee);
        sTotalTv.setText("₹"+String.format("%2f", allTotalPrice));
        allTotalPriceTv.setText("₹"+(allTotalPrice + Double.parseDouble(deliveryFee.replaceAll("₹", ""))));

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                allTotalPrice = 0.00;
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
                deliveryFee = ""+snapshot.child("deliveryFee").getValue();
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