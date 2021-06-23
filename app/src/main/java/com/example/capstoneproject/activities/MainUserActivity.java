package com.example.capstoneproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstoneproject.R;
import com.example.capstoneproject.adapters.AdapterFarmer;
import com.example.capstoneproject.adapters.AdapterOrderUser;
import com.example.capstoneproject.models.ModelFarmer;
import com.example.capstoneproject.models.ModelOrderUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MainUserActivity extends AppCompatActivity {

    private TextView nameTv, emailTv, phoneTv, tapFarmersTv, tapOrdersTv;
    private ImageButton logoutBtn, editProfileBtn, settingsBtn;
    private ImageView profileIv;
    private RelativeLayout farmerRl,  ordersRl;
    private RecyclerView farmersRv, orderRv;
    private ArrayList<ModelFarmer> farmerList;
    private AdapterFarmer adapterFarmer;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelOrderUser> orderList;
    private AdapterOrderUser adapterOrderUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        nameTv = findViewById(R.id.nameTv);
        emailTv = findViewById(R.id.emailTv);
        phoneTv = findViewById(R.id.phoneTv);
        logoutBtn = findViewById(R.id.logoutBtn);
        tapFarmersTv = findViewById(R.id.tapFarmersTv);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        tapOrdersTv = findViewById(R.id.tapOrdersTv);
        profileIv = findViewById(R.id.profileIv);
        farmerRl = findViewById(R.id.shopsRl);
        ordersRl = findViewById(R.id.ordersRl);
        farmersRv = findViewById(R.id.farmersRv);
        orderRv = findViewById(R.id.orderRv);
        settingsBtn = findViewById(R.id.settingsBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        checkUser();

        //at starts show Farmers ui
        showFarmersUI();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //make offline
                //sign out
                //go to login activity
                makeMeOffline();
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open edit profile activity
                startActivity(new Intent(MainUserActivity.this, ProfileEditUserActivity.class));
            }
        });

        tapFarmersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show start farmers UI
                showFarmersUI();
            }
        });

        tapOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrdersUI();
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainUserActivity.this, SettingsActivity.class));
            }
        });
    }

    private void showFarmersUI() {
        //show shops ui only
        farmerRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);

        tapFarmersTv.setTextColor(getResources().getColor(R.color.colorBlack));
        tapFarmersTv.setBackgroundResource(R.drawable.shape_rect04);

        tapOrdersTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tapOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showOrdersUI() {
        //show orders only
        farmerRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);

        tapFarmersTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tapFarmersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tapOrdersTv.setTextColor(getResources().getColor(R.color.colorBlack));
        tapOrdersTv.setBackgroundResource(R.drawable.shape_rect04);


    }

    private void makeMeOffline(){
        //after logging in make user online
        progressDialog.setMessage("Logging out...");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online","false");

        //update value to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //update Successfully
                        firebaseAuth.signOut();
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed Updating
                        progressDialog.dismiss();
                        Toast.makeText(MainUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(MainUserActivity.this, LoginActivity.class));
            finish();
        }
        else{
            loadMyInfo();
        }
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

                            nameTv.setText(name);
                            emailTv.setText(email);
                            phoneTv.setText(phone);
                            try{
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(profileIv);
                            }
                            catch(Exception e){
                                profileIv.setImageResource(R.drawable.ic_person_gray);
                            }

                            loadFarmers();
                            loadOrders();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadOrders() {

        orderList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    String uid = ""+ds.getRef().getKey();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Orders");
                    ref.orderByChild("orderBy").equalTo(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if(snapshot.exists()){
                                        orderList.clear();
                                        for(DataSnapshot ds:snapshot.getChildren()){
                                            ModelOrderUser modelOrderUser = ds.getValue(ModelOrderUser.class);
                                            orderList.add(modelOrderUser);
                                        }
                                        //set up adapter
                                        adapterOrderUser = new AdapterOrderUser(MainUserActivity.this, orderList);
                                        //set to recyclerview
                                        orderRv.setAdapter(adapterOrderUser);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadFarmers() {

        farmerList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("accountType").equalTo("Seller")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear list before adding
                        farmerList.clear();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            ModelFarmer modelFarmer = ds.getValue(ModelFarmer.class);
                            farmerList.add(modelFarmer);
                        }

                        adapterFarmer = new AdapterFarmer(MainUserActivity.this, farmerList);
                        farmersRv.setAdapter(adapterFarmer);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}