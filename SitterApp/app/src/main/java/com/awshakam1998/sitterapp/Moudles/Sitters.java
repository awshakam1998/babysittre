package com.awshakam1998.sitterapp.Moudles;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties

public class Sitters {
    private String fname , lname , phone , email , password , skills , priceofhour;

    public Sitters() {
    }

    public Sitters(String fname, String lname, String phone, String email, String password, String skills, String priceofhour) {
        this.fname = fname;
        this.lname = lname;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.skills = skills;
        this.priceofhour = priceofhour;
    }

    public String getPriceofhour() {
        return priceofhour;
    }

    public void setPriceofhour(String priceofhour) {
        this.priceofhour = priceofhour;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }
}
