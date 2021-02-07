package com.example.crudmahasiswagomysql.data;

public class DataMahasiswa {
    int IDMahasiswa;
    String PhotoMahasiswa;
    String NimMahasiswa;
    String NamaMahasiswa;
    String JurusanMahasiswa;
    String HpMahasiswa;

    public DataMahasiswa(int id, String photo, String nim, String nama, String jurusan, String hp) {
        this.IDMahasiswa = id;
        this.PhotoMahasiswa = photo;
        this.NimMahasiswa = nim;
        this.NamaMahasiswa = nama;
        this.JurusanMahasiswa = jurusan;
        this.HpMahasiswa = hp;
    }

    // getter and setter
    public int getIDMahasiswa() {
        return IDMahasiswa;
    }

    public void setIDMahasiswa(int IDMahasiswa) {
        this.IDMahasiswa = IDMahasiswa;
    }

    public String getPhotoMahasiswa() {
        return PhotoMahasiswa;
    }

    public void setPhotoMahasiswa(String PhotoMahasiswa) {
        this.PhotoMahasiswa = PhotoMahasiswa;
    }

    public String getNimMahasiswa() {
        return NimMahasiswa;
    }

    public void setNimMahasiswa(String NimMahasiswa) {
        this.NimMahasiswa = NimMahasiswa;
    }

    public String getNamaMahasiswa() {
        return NamaMahasiswa;
    }

    public void setNamaMahasiswa(String NamaMahasiswa) {
        this.NamaMahasiswa = NamaMahasiswa;
    }

    public String getJurusanMahasiswa() {
        return JurusanMahasiswa;
    }

    public void setJurusanMahasiswa(String JurusanMahasiswa) {
        this.JurusanMahasiswa = JurusanMahasiswa;
    }

    public String getHpMahasiswa() {
        return HpMahasiswa;
    }

    public void setHpMahasiswa(String HpMahasiswa) {
        this.HpMahasiswa = HpMahasiswa;
    }
}
