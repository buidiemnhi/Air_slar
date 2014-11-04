package com.evilgeniustechnologies.airsla.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Patterns;
import com.evilgeniustechnologies.airsla.MainMenuActivity;
import com.evilgeniustechnologies.airsla.R;

import java.util.List;

/**
 * Created by benjamin on 3/11/14.
 */
public class DataValidator {
    private static final String LOGIN_ERROR = "Your EMail Address or Password are incorrect";
    private static final String[] REGISTER_ERRORS = {
            "Year of Birth Cannot Occur After",
            "Year of Birth Makes You Older than 110",
            "You Are Already a Member."
    };
    private static final String REGISTER_SUCCESS = "Thank You";
    private static final String FORGOT_ERROR = "Sorry, That EMail Address Isn't in Our System!";
    private static final String CHANGE_ERROR = "Your EMail Address or Old Password are incorrect";
    private static final String[] UNREGISTER_ERRORS = {
            "Your EMail Address or Password are incorrect",
            "Sorry, I Wasn't Able to Cancel Your Membership",
            "Sorry, We Can't Cancel THAT Membership"
    };

    public static boolean validateLogin(Activity activity, String email, String password) {
        if (!isConnected(activity)) {
            DialogManager.showDialog(activity, DialogManager.Alerts.LOGIN_NO_INTERNET);
        } else if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            DialogManager.showDialog(activity, DialogManager.Alerts.LOGIN_INCORRECT_EMAIL);
        } else if (TextUtils.isEmpty(password)) {
            DialogManager.showDialog(activity, DialogManager.Alerts.LOGIN_NO_PASSWORD);
        } else {
            // Show progress
            DialogManager.showProgress(activity);
            return true;
        }
        return false;
    }

    public static void processLoginResponse(Activity activity, String response,
                                            String email, String password) {
        // Dismiss progress
        DialogManager.closeProgress();
        // Process response
        if (response.contains(LOGIN_ERROR)) {
            DialogManager.showDialog(activity, DialogManager.Alerts.LOGIN_FAIL_AUTHENTICATION);
        } else {
            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                // Save preferences
                SharedPreferences settings = activity.getSharedPreferences(UserPreferences.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putString(UserPreferences.EMAIL, email);
                editor.putString(UserPreferences.PASSWORD, password);

                // Commit the edits
                editor.commit();
            }
            Intent extra = new Intent(activity, MainMenuActivity.class);
            activity.startActivity(extra);
            activity.finish();
        }
    }

    public static boolean validateRegister(Activity activity, String firstName, String lastName,
                                           String email, String verifyEmail, String gender,
                                           String birth, String career, String address,
                                           String visionImpairment, String whoAffected,
                                           List<String> interests, boolean certify) {
        if (!isConnected(activity)) {
            DialogManager.showDialog(activity, DialogManager.Alerts.REGISTER_NO_INTERNET);
        } else if (TextUtils.isEmpty(firstName) ||
                TextUtils.isEmpty(lastName) ||
                TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(verifyEmail) ||
                TextUtils.isEmpty(gender) ||
                TextUtils.isEmpty(birth) ||
                TextUtils.isEmpty(career) ||
                TextUtils.isEmpty(address) ||
                TextUtils.isEmpty(visionImpairment) ||
                TextUtils.isEmpty(whoAffected)) {
            DialogManager.showDialog(activity, DialogManager.Alerts.REGISTER_BLANK_INPUTS);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            DialogManager.showDialog(activity, DialogManager.Alerts.REGISTER_INVALID_EMAIL);
        } else if (!email.equals(verifyEmail)) {
            DialogManager.showDialog(activity, DialogManager.Alerts.REGISTER_NO_VERIFY_EMAIL);
        } else if (interests.isEmpty()) {
            DialogManager.showDialog(activity, DialogManager.Alerts.REGISTER_EMPTY_INTERESTS);
        } else if (!certify) {
            DialogManager.showDialog(activity, DialogManager.Alerts.REGISTER_NO_CERTIFY);
        } else {
            // Show progress
            DialogManager.showProgress(activity);
            return true;
        }
        return false;
    }

    public static void processRegisterResponse(Activity activity, String response) {
        // Dismiss progress
        DialogManager.closeProgress();
        // Process response
        if (response.contains(REGISTER_ERRORS[0])) {
            DialogManager.showDialog(activity, DialogManager.Alerts.REGISTER_WRONG_YEAR_BIRTH_AFTER);
        } else if (response.contains(REGISTER_ERRORS[1])) {
            DialogManager.showDialog(activity, DialogManager.Alerts.REGISTER_WRONG_YEAR_BIRTH_BEFORE);
        } else if (response.contains(REGISTER_ERRORS[2])) {
            DialogManager.showDialog(activity, DialogManager.Alerts.REGISTER_ALREADY_MEMBER);
        } else if (response.contains(REGISTER_SUCCESS)) {
            DialogManager.showDialog(activity, DialogManager.Alerts.REGISTER_SUCCESS);
        } else {
            DialogManager.showDialog(activity, DialogManager.Alerts.REGISTER_UNKNOWN_ERROR);
        }
    }

    public static boolean validateForgotPassword(Activity activity, String email) {
        if (!isConnected(activity)) {
            DialogManager.showDialog(activity, DialogManager.Alerts.FORGOT_NO_INTERNET);
        } else if (TextUtils.isEmpty(email) ||
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            DialogManager.showDialog(activity, DialogManager.Alerts.FORGOT_INCORRECT_EMAIL);
        } else {
            // Show progress
            DialogManager.showProgress(activity);
            return true;
        }
        return false;
    }

    public static void processForgotPasswordResponse(Activity activity, String response) {
        // Dismiss progress
        DialogManager.closeProgress();
        // Process response
        if (response.contains(FORGOT_ERROR)) {
            DialogManager.showDialog(activity, DialogManager.Alerts.FORGOT_EMAIL_NOT_FOUND);
        } else {
            DialogManager.showDialog(activity, DialogManager.Alerts.FORGOT_SUCCESS);
        }
    }

    public static boolean validateChangePassword(Activity activity, String email,
                                                 String oldPass, String newPass,
                                                 String newPassConfirm) {
        if (!isConnected(activity)) {
            DialogManager.showDialog(activity, DialogManager.Alerts.CHANGE_NO_INTERNET);
        } else if (TextUtils.isEmpty(email) ||
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            DialogManager.showDialog(activity, DialogManager.Alerts.CHANGE_INCORRECT_EMAIL);
        } else if (TextUtils.isEmpty(oldPass)) {
            DialogManager.showDialog(activity, DialogManager.Alerts.CHANGE_NO_PASSWORD);
        } else if (TextUtils.isEmpty(newPass)) {
            DialogManager.showDialog(activity, DialogManager.Alerts.CHANGE_NO_NEW_PASSWORD);
        } else if (TextUtils.isEmpty(newPassConfirm)) {
            DialogManager.showDialog(activity, DialogManager.Alerts.CHANGE_NO_CONFIRM_NEW_PASSWORD);
        } else if (!newPass.equals(newPassConfirm)) {
            DialogManager.showDialog(activity, DialogManager.Alerts.CHANGE_NO_MATCH_NEW_PASSWORD);
        } else {
            // Show progress
            DialogManager.showProgress(activity);
            return true;
        }
        return false;
    }

    public static void processChangePasswordResponse(Activity activity,
                                                     String response, String newPass) {
        // Dismiss progress
        DialogManager.closeProgress();
        // Process response
        if (response.contains(CHANGE_ERROR)) {
            DialogManager.showDialog(activity, DialogManager.Alerts.CHANGE_FAIL_AUTHENTICATION);
        } else {
            DialogManager.showDialog(activity, DialogManager.Alerts.CHANGE_SUCCESS, newPass);
        }
    }

    public static boolean validateUnregister(Activity activity,
                                             String email, String password) {
        if (!isConnected(activity)) {
            DialogManager.showDialog(activity, DialogManager.Alerts.UNREGISTER_NO_INTERNET);
        } else if (TextUtils.isEmpty(email) ||
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            DialogManager.showDialog(activity, DialogManager.Alerts.UNREGISTER_INCORRECT_EMAIL);
        } else if (TextUtils.isEmpty(password)) {
            DialogManager.showDialog(activity, DialogManager.Alerts.UNREGISTER_NO_PASSWORD);
        } else {
            // Show progress
            DialogManager.showProgress(activity);
            return true;
        }
        return false;
    }

    public static void processUnregisterResponse(Activity activity, String response) {
        // Dismiss progress
        DialogManager.closeProgress();
        // Process response
        if (response.contains(UNREGISTER_ERRORS[0])) {
            DialogManager.showDialog(activity, DialogManager.Alerts.UNREGISTER_FAIL_AUTHENTICATION);
        } else if (response.contains(UNREGISTER_ERRORS[1])) {
            DialogManager.showDialog(activity, DialogManager.Alerts.UNREGISTER_UNABLE_CANCEL);
        } else if (response.contains(UNREGISTER_ERRORS[2])) {
            DialogManager.showDialog(activity, DialogManager.Alerts.UNREGISTER_CANNOT_CANCEL);
        } else {
            DialogManager.showDialog(activity, DialogManager.Alerts.UNREGISTER_SUCCESS);
        }
    }

    public static boolean checkInternetConnection(Activity activity) {
        // Check internet connection
        if (!isConnected(activity)) {
            DialogManager.showDialog(activity, DialogManager.Alerts.GENERAL_NO_INTERNET);
            return false;
        }
        // Show progress
        DialogManager.showProgress(activity);
        return true;
    }

    public static boolean checkTimeout(Activity activity, List list) {
        // Dismiss progress
        DialogManager.closeProgress();
        // Check timeout
        if (list == null || list.isEmpty()) {
            DialogManager.showDialog(activity, DialogManager.Alerts.GENERAL_TIMEOUT);
            return false;
        }
        return true;
    }

    public static boolean checkEmpty(final Activity activity, List contents) {
        if (contents.isEmpty()) {
            new ErrorDialog(activity.getResources().getString(R.string.error_message_empty),
                    activity.getResources().getString(R.string.message_title)) {
                @Override
                public void onClicked() {
                    activity.finish();
                }
            }.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                    activity.getResources().getString(R.string.error_message_empty));
            return false;
        }
        return true;
    }

    public static boolean isConnected(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }

        return false;
    }
}
