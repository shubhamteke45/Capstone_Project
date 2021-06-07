package com.example.capstoneproject;

import android.widget.Filter;

import com.example.capstoneproject.adapters.AdapterOrderFarmer;
import com.example.capstoneproject.adapters.AdapterProductSeller;
import com.example.capstoneproject.models.ModelOrderFarmer;
import com.example.capstoneproject.models.ModelProduct;

import java.util.ArrayList;

public class FilterOrderFarmer extends Filter {

    private AdapterOrderFarmer adapter;
    private ArrayList<ModelOrderFarmer> filterList;

    public FilterOrderFarmer(AdapterOrderFarmer adapter, ArrayList<ModelOrderFarmer> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        //validate data for search query
        ArrayList<ModelOrderFarmer> filterModels = new ArrayList<>();
        if(charSequence!=null && charSequence.length()>0){
            //search filed not empty, searching something, perform search

            //change to upper case to make case insensitive
            charSequence = charSequence.toString().toUpperCase();
            //store our filtered list
            for(int i=0; i<filterList.size(); i++){
                //check, search by title and category
                if(filterList.get(i).getOrderStatus().toUpperCase().contains(charSequence)){
                    //add filtered data to list
                    filterModels.add(filterList.get(i));
                }
            }

            results.count = filterModels.size();
            results.values = filterModels;
        }
        else{
            //search filed empty, not searching, return original/all/complete list

            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapter.orderFarmerArrayList = (ArrayList<ModelOrderFarmer>) filterResults.values;
        //refresh adapter
        adapter.notifyDataSetChanged();
    }
}
