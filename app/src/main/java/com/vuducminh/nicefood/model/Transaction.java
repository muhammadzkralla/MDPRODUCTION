package com.vuducminh.nicefood.model;

public class Transaction {

    private String id,status,type,currencyIsoCode,amount,merchanAccountId,subMerchanAccountId;
    private String masterMerchantAccountId,orderId,craeteAt,updateAt;

    public Transaction() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCurrencyIsoCode() {
        return currencyIsoCode;
    }

    public void setCurrencyIsoCode(String currencyIsoCode) {
        this.currencyIsoCode = currencyIsoCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMerchanAccountId() {
        return merchanAccountId;
    }

    public void setMerchanAccountId(String merchanAccountId) {
        this.merchanAccountId = merchanAccountId;
    }

    public String getSubMerchanAccountId() {
        return subMerchanAccountId;
    }

    public void setSubMerchanAccountId(String subMerchanAccountId) {
        this.subMerchanAccountId = subMerchanAccountId;
    }

    public String getMasterMerchantAccountId() {
        return masterMerchantAccountId;
    }

    public void setMasterMerchantAccountId(String masterMerchantAccountId) {
        this.masterMerchantAccountId = masterMerchantAccountId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCraeteAt() {
        return craeteAt;
    }

    public void setCraeteAt(String craeteAt) {
        this.craeteAt = craeteAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }
}
