package com.example.capstoneproject;

import android.widget.Filter;

import com.example.capstoneproject.adapters.AdapterProductSeller;
import com.example.capstoneproject.adapters.AdapterProductUser;
import com.example.capstoneproject.models.ModelProduct;

import java.util.ArrayList;

public class FilterProductUser extends Filter {

    private AdapterProductUser adapter;
    private ArrayList<ModelProduct> filterList;

    public FilterProductUser(AdapterProductUser adapter, ArrayList<ModelProduct> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        //validate data for search query
        ArrayList<ModelProduct> filterModels = new ArrayList<>();
        if(charSequence!=null && charSequence.length()>0){
            //search filed not empty, searching something, perform search

            //change to upper case to make case insensitive
            charSequence = charSequence.toString().toUpperCase();
            //store our filtered list
            for(int i=0; i<filterList.size(); i++){
                //check, search by title and category
                if(filterList.get(i).getProductTitle().toUpperCase().contains(charSequence) ||
                      filterList.get(i).getProductCategory().toUpperCase().contains(charSequence)){
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
        adapter.productsList = (ArrayList<ModelProduct>) filterResults.values;
        //refresh adapter
        adapter.notifyDataSetChanged();
    }
}
