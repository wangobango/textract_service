package com.example.textract.entity;

public class Result {
    private final String vat_id;
    private final String address;
    private final Float total;

    public Result(String nip, String company, Float brutto) {
        this.vat_id = nip;
        this.address = company;
        this.total = brutto;
    }

    public String getVat_id() {
        return vat_id;
    }

    public String getAddress() {
        return address;
    }

    public Float getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return "Result{" +
                "nip='" + vat_id + '\'' +
                ", company='" + address + '\'' +
                ", brutto='" + total.toString() + '\'' +
                '}';
    }
}
