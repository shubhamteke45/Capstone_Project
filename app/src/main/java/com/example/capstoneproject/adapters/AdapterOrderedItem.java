package com.example.capstoneproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneproject.R;
import com.example.capstoneproject.models.ModelCartItem;
import com.example.capstoneproject.models.ModelOrderedItem;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class AdapterOrderedItem extends RecyclerView.Adapter<AdapterOrderedItem.HolderOrderedItem>{

    private Context context;
    private ArrayList<ModelOrderedItem> modelOrderedItemArrayList;

    public AdapterOrderedItem(Context context, ArrayList<ModelOrderedItem> modelOrderedItemArrayList) {
        this.context = context;
        this.modelOrderedItemArrayList = modelOrderedItemArrayList;
    }

    @NonNull
    @Override
    public HolderOrderedItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_ordereditem, parent, false);
        return new HolderOrderedItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderedItem holder, int position) {

        ModelOrderedItem modelOrderedItem = modelOrderedItemArrayList.get(position);
        String getpId = modelOrderedItem.getpId();
        String name = modelOrderedItem.getName();
        String cost = modelOrderedItem.getCost();
        String price = modelOrderedItem.getPrice();
        String quantity = modelOrderedItem.getQuantity();

        //set data
        holder.itemTitleTv.setText(name);
        holder.itemPriceEachTv.setText("₹"+price);
        holder.itemPriceTv.setText("₹"+cost);
        holder.itemQuantityTv.setText("["+ quantity +"]");
    }

    @Override
    public int getItemCount() {
        return modelOrderedItemArrayList.size();
    }

    //view holder class
    class HolderOrderedItem extends RecyclerView.ViewHolder{

        //views
        private TextView itemTitleTv, itemPriceTv, itemPriceEachTv, itemQuantityTv;
        public HolderOrderedItem(@NonNull View itemView) {
            super(itemView);
            itemTitleTv = itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv = itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);
        }
    }
}
