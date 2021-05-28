package com.example.capstoneproject.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneproject.FilterProductUser;
import com.example.capstoneproject.R;
import com.example.capstoneproject.models.ModelProduct;
import com.google.firebase.database.annotations.NotNull;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

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
        String productQuantity = modelProduct.getProductQuantity();
        String productId = modelProduct.getProductId();
        String timestamp = modelProduct.getTimestamp();
        String productCategory = modelProduct.getProductCategory();
        String productIcon = modelProduct.getProductIcon();

        //set data
        holder.titleTv.setText(productTitle);
        holder.quantityTv.setText("Kg: "+productQuantity);
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
                showQuantityDialog(modelProduct);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show product details
            }
        });
    }

    private double cost=0;
    private double finalCost=0;
    private int quantity=0;
    private void showQuantityDialog(ModelProduct modelProduct) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_quantity, null);
        //init layout views
        ImageView productIv;
        TextView titleTv, pQuantityTv, descriptionTv, priceTv, finalPriceTv, quantityTv;
        ImageButton decrementBtn, incrementBtn;
        Button continueBtn;

        productIv = view.findViewById(R.id.productIv);
        titleTv = view.findViewById(R.id.titleTv);
        pQuantityTv = view.findViewById(R.id.pQuantityTv);
        descriptionTv = view.findViewById(R.id.descriptionTv);
        priceTv = view.findViewById(R.id.priceTv);
        finalPriceTv = view.findViewById(R.id.finalPriceTv);
        quantityTv = view.findViewById(R.id.quantityTv);
        decrementBtn = view.findViewById(R.id.decrementBtn);
        incrementBtn = view.findViewById(R.id.incrementBtn);
        continueBtn = view.findViewById(R.id.continueBtn);

        //get data from model
        String productId = modelProduct.getProductId();
        String title = modelProduct.getProductTitle();
        String productQuantity = modelProduct.getProductQuantity();
        String description = modelProduct.getProductDescription();
        String image = modelProduct.getProductIcon();
        String price = modelProduct.getProductPrice();

        cost = Double.parseDouble(price.replaceAll("₹", ""));
        finalCost = Double.parseDouble(price.replaceAll("₹", ""));
        quantity=1;

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        //set data
        try{
            Picasso.get().load(image).placeholder(R.drawable.ic_cart_gray).into(productIv);
        }
        catch (Exception e){
            productIv.setImageResource(R.drawable.ic_cart_gray);
        }

        titleTv.setText(""+title);
        pQuantityTv.setText(""+productQuantity);
        descriptionTv.setText(""+description);
        quantityTv.setText(""+quantity);
        priceTv.setText("₹"+price);
        finalPriceTv.setText("₹"+finalCost);

        AlertDialog dialog = builder.create();
        dialog.show();
        //increment quantity of the product
        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                finalCost = finalCost+cost;
                quantity++;

                finalPriceTv.setText("₹"+finalCost);
                quantityTv.setText(""+quantity);
            }
        });

        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(quantity>1){
                    finalCost = finalCost-cost;
                    quantity--;

                    finalPriceTv.setText("₹"+finalCost);
                    quantityTv.setText(""+quantity);
                }
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleTv.getText().toString().trim();
                String priceEach = price;
                String totalPrice = finalPriceTv.getText().toString().trim().replace("₹", "");
                String quantity = quantityTv.getText().toString().trim(); 
                
                //add to db(SQLite)
                addToCart(productId, title, priceEach, totalPrice, quantity);
                dialog.dismiss();
                //Toast.makeText(context, "product added", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private int itemId = 1;
    private void addToCart(String productId, String title, String priceEach, String price, String quantity) {
        itemId++;

        EasyDB easyDB = EasyDB.init(context, "ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                .doneTableColumn();

        Boolean b = easyDB.addData("Item_Id", itemId)
                .addData("Item_PID", productId)
                .addData("Item_Name", title)
                .addData("Item_Price_Each", priceEach)
                .addData("Item_Price", price)
                .addData("Item_Quantity", quantity)
                .doneDataAdding();

        Toast.makeText(context, "Added to Cart...", Toast.LENGTH_SHORT).show();
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
        private TextView titleTv, descriptionTv, addToCartTv, productPriceTv, quantityTv;

        public HolderProductUser(@NotNull View itemView){
            super(itemView);

            productIconIv = itemView.findViewById(R.id.productIconIv);
            titleTv = itemView.findViewById(R.id.titleTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            quantityTv = itemView.findViewById(R.id.quantityTv);
            addToCartTv = itemView.findViewById(R.id.addToCartTv);
            productPriceTv = itemView.findViewById(R.id.productPriceTv);
        }
    }
}
