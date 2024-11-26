package com.example.seg2105_project_grp_33;

import android.text.TextUtils;
import android.webkit.URLUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

//A helper class used for validation of input fields in project
public class InputValidationHelper {
    //Validates whether a given string is Alphanumeric
    public boolean ValidPassword(String string) {
        return string.matches("^[a-zA-Z0-9_!]+$");
    }

    //Validates whether a given string contains digits only
    public boolean ValidNumber(String string) {
        return TextUtils.isDigitsOnly(string);
    }

    //Validates whether a given string is a valid url
    public boolean ValidSocialMedia(String string) {
        return URLUtil.isValidUrl(string);
    }

    //Validates whether a given string is a date of the right format
    public boolean ValidDate(String string) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        formatter.setLenient(false);
        try {
            Date date = formatter.parse(string);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    //Checks if a given string is empty
    public boolean NullOrEmptyField(String string) {
        return TextUtils.isEmpty(string);
    }

    //Checks if string is an integer
    public boolean ValidInteger(String string) {return string.matches("\\d+");}
}


