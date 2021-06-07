package com.example.capstoneproject.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneproject.FilterOrderFarmer;
import com.example.capstoneproject.R;
import com.example.capstoneproject.models.ModelOrderFarmer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderFarmer extends RecyclerView.Adapter<AdapterOrderFarmer.HolderOrderFarmer> implements Filterable {

    private Context context;
    public ArrayList<ModelOrderFarmer> orderFarmerArrayList, filterList;
    private FilterOrderFarmer filter;

    public AdapterOrderFarmer(Context context, ArrayList<ModelOrderFarmer> orderFarmerArrayList) {
        this.context = context;
        this.orderFarmerArrayList = orderFarmerArrayList;
        this.filterList = orderFarmerArrayList;
    }

    @NonNull
    @Override
    public HolderOrderFarmer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_seller, parent, false);
        return new HolderOrderFarmer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderFarmer holder, int position) {
        ModelOrderFarmer modelOrderFarmer = orderFarmerArrayList.get(position);
        String orderId = modelOrderFarmer.getOrderId();
        String orderBy = modelOrderFarmer.getOrderBy();
        String orderCost = modelOrderFarmer.getOrderCost();
        String orderStatus = modelOrderFarmer.getOrderStatus();
        String orderTime = modelOrderFarmer.getOrderTime();
        String orderTo = modelOrderFarmer.getOrderTo();

        //load buyer info
        loadUserInfo(modelOrderFarmer, holder);

        //set data
        holder.amountTv.setText("Amount: ₹"+orderCost);
        holder.statusTv.setText(orderStatus);
        holder.orderIdTv.setText("Order ID: "+orderId);

        if (orderStatus.equals("In Progress")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.colorYellow));
        } else if (orderStatus.equals("Completed")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.colorGreen));
        } else if (orderStatus.equals("Cancelled")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.colorRed));
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTime));
        String formatedDate = DateFormat.format("dd/MM/yyyy", calendar).toString();

        holder.orderDateTv.setText(formatedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void loadUserInfo(ModelOrderFarmer modelOrderFarmer, HolderOrderFarmer holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(modelOrderFarmer.getOrderBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String email = ""+snapshot.child("email").getValue();
                        holder.emailTv.setText(email);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return orderFarmerArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter==null){
            filter = new FilterOrderFarmer(this, filterList);
        }
        return filter;
    }

    class HolderOrderFarmer extends RecyclerView.ViewHolder{

        private TextView orderIdTv, orderDateTv, emailTv, amountTv, statusTv;
        private ImageView nextIv;
        public HolderOrderFarmer(@NonNull View itemView) {
            super(itemView);

            orderDateTv = itemView.findViewById(R.id.orderDateTv);
            emailTv = itemView.findViewById(R.id.emailTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            statusTv = itemView.findViewById(R.id.statusTv);
            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            nextIv = itemView.findViewById(R.id.nextIv);
        }
    }
}
