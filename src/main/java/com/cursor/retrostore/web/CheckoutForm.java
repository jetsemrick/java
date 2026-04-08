package com.cursor.retrostore.web;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;

public class CheckoutForm {

    @Size(max = 320)
    private String email = "";

    @AssertTrue(message = "Please confirm the demo terms (mock checkout)")
    private boolean agreeTerms;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAgreeTerms() {
        return agreeTerms;
    }

    public void setAgreeTerms(boolean agreeTerms) {
        this.agreeTerms = agreeTerms;
    }
}
