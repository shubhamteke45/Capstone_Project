package com.example.capstoneproject.models;

public class ModelOrderFarmer {

    private String orderId, orderTime, orderStatus, orderCost, deliveryFee, orderBy, orderTo, latitude, longitude;

    public ModelOrderFarmer() {
    }

    public ModelOrderFarmer(String orderId, String orderTime, String orderStatus,
                            String orderCost, String deliveryFee, String orderBy, String orderTo, String latitude, String longitude) {
        this.orderId = orderId;
        this.orderTime = orderTime;
        this.orderStatus = orderStatus;
        this.orderCost = orderCost;
        this.deliveryFee = deliveryFee;
        this.orderBy = orderBy;
        this.orderTo = orderTo;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderCost() {
        return orderCost;
    }

    public void setOrderCost(String orderCost) {
        this.orderCost = orderCost;
    }

    public String getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(String deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderTo() {
        return orderTo;
    }

    public void setOrderTo(String orderTo) {
        this.orderTo = orderTo;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
