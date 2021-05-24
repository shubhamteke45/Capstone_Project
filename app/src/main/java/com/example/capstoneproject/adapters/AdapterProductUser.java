package com.example.capstoneproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneproject.FilterProductUser;
import com.example.capstoneproject.R;
import com.example.capstoneproject.models.ModelProduct;
import com.google.firebase.database.annotations.NotNull;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProductUser extends  RecyclerView.Adapter<AdapterProductUser.HolderProductUser> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productsList, filterList;
    private FilterProductUser filterProductUser;

    public AdapterProductUser(Context context, ArrayList<ModelProduct> productsList) {
        this.context = context;
        this.productsList = productsList;
        this.filterList = productsList;
    }

    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_user, parent, false);
        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder, int position) {
        //get data
        ModelProduct modelProduct = productsList.get(position);
        String productPrice = modelProduct.getProductPrice();
        String productTitle = modelProduct.getProductTitle();
        String productDescription = modelProduct.getProductDescription();
        String productId = modelProduct.getProductId();
        String timestamp = modelProduct.getTimestamp();
        String productCategory = modelProduct.getProductCategory();
        String productIcon = modelProduct.getProductIcon();

        //set data
        holder.titleTv.setText(productTitle);
        holder.descriptionTv.setText(productDescription);
        holder.productPriceTv.setText("₹"+productPrice);
        try{
            Picasso.get().load(productIcon).placeholder(R.drawable.ic_add_shopping_cart).into(holder.productIconIv);
        }catch (Exception e){
            holder.productIconIv.setImageResource(R.drawable.ic_add_shopping_cart);
        }

        holder.addToCartTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add product to cart
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show product details
            }
        });
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        if (filterProductUser == null){
            filterProductUser = new FilterProductUser(this, filterList);
        }
        return filterProductUser;
    }

    class HolderProductUser extends RecyclerView.ViewHolder {

        //ui views
        private ImageView productIconIv;
        private TextView titleTv, descriptionTv, addToCartTv, productPriceTv;

        public HolderProductUser(@NotNull View itemView){
            super(itemView);

            productIconIv = itemView.findViewById(R.id.productIconIv);
            titleTv = itemView.findViewById(R.id.titleTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            addToCartTv = itemView.findViewById(R.id.addToCartTv);
            productPriceTv = itemView.findViewById(R.id.productPriceTv);
        }
    }
}
