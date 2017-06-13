package com.Yourstylist;

import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Pattern;

@SuppressWarnings("ALL")
public class Validation {

    // Regular Expression
    // you can change the expression based on your need
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    // --Commented out by Inspection (1/2/2017 5:13 PM):private static final String PHONE_REGEX = "^[789]\\d{9}$";
    private static final String NAME_REGEX = "^[\\p{L} .'-]+$";
    private static final String DATE_REGEX=".*";

    // Error Messages
    private static final String REQUIRED_MSG = "required";
    private static final String EMAIL_MSG = "invalid email";
    // --Commented out by Inspection (1/2/2017 5:13 PM):private static final String PHONE_MSG = "invalid mobile number";
    private static final String NAME_MSG="invalid name";
    private static final String DATE_MSG="invalid date";

    // call this method when you need to check email validation
    public static boolean isEmailAddress(EditText editText, boolean required) {
        return isValid(editText, EMAIL_REGEX, EMAIL_MSG, required);
    }

// --Commented out by Inspection START (1/2/2017 5:13 PM):
//    // call this method when you need to check phone number validation
//    public static boolean isPhoneNumber(EditText editText, boolean required) {
//        return isValid(editText, PHONE_REGEX, PHONE_MSG, required);
//    }
// --Commented out by Inspection STOP (1/2/2017 5:13 PM)

    public static boolean isDateSet(Button button,boolean required){
        return isValid(button, required);
    }

    public static boolean isName(EditText editText,boolean required){
        return isValid(editText,NAME_REGEX,NAME_MSG,required);
    }

    private static boolean isValid(Button button, boolean required){
        String text = button.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        button.setError(null);

        // text required and editText is blank, so return false
        if ( required && button.getText().toString().isEmpty()) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(Validation.DATE_REGEX, text)) {
            button.setError(Validation.DATE_MSG);
            return false;
        }

        return true;
    }

    // return true if the input field is valid, based on the parameter passed
    private static boolean isValid(EditText editText, String regex, String errMsg, boolean required) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if ( required && !hasText(editText) ) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
            return false;
        }

        return true;
    }

    // check the input field has any text or not
    // return true if it contains text otherwise false
    private static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(REQUIRED_MSG);
            return false;
        }

        return true;
    }
}