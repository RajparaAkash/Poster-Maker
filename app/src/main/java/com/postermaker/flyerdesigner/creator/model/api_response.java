package com.postermaker.flyerdesigner.creator.model;

public class api_response {

    private int server_id, type;
    private String Country_name, ik_Ip, ik_usr, ik_ps, ik_cert, Re_IP, ovp_cert;

    public void setServer_id(int server_id) {
        this.server_id = server_id;
    }

    public int getServer_id() {
        return server_id;
    }

    public void setCountry_name(String Country_name) {

        this.Country_name = Country_name;
    }

    public String getCountry_name() {

        return Country_name;
    }

    public void setIk_Ip(String ik_Ip) {
        this.ik_Ip = ik_Ip;
    }

    public String getIk_Ip() {
        return ik_Ip;
    }

    public void setIk_usr(String ik_usr) {
        this.ik_usr = ik_usr;
    }

    public String getIk_usr() {
        return ik_usr;
    }

    public void setIk_ps(String ik_ps) {
        this.ik_ps = ik_ps;
    }

    public String getIk_ps() {
        return ik_ps;
    }


    public void setIk_cert(String ik_cert) {
        this.ik_cert = ik_cert;
    }

    public String getIk_cert() {
        return ik_cert;
    }

    public void setRe_IP(String Re_IP) {
        this.Re_IP = Re_IP;
    }

    public String getRe_IP() {
        return Re_IP;
    }
}
