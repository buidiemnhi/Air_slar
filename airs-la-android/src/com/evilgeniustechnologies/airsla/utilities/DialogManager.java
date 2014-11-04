package com.evilgeniustechnologies.airsla.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import com.evilgeniustechnologies.airsla.LoginActivity;
import com.evilgeniustechnologies.airsla.MainMenuActivity;
import com.evilgeniustechnologies.airsla.R;

import java.util.Calendar;

/**
 * Created by benjamin on 3/10/14.
 */
public class DialogManager {
    public static final String ALERT = "ALERT";
    public static Alerts alert;
    private static ProgressDialog progressDialog;
    private static ErrorDialog errorDialog;

    public static enum Alerts {
        NULL,
        LOGIN_NO_INTERNET,
        LOGIN_INCORRECT_EMAIL,
        LOGIN_NO_PASSWORD,
        LOGIN_FAIL_AUTHENTICATION,
        REGISTER_NO_INTERNET,
        REGISTER_BLANK_INPUTS,
        REGISTER_INVALID_EMAIL,
        REGISTER_NO_VERIFY_EMAIL,
        REGISTER_EMPTY_INTERESTS,
        REGISTER_NO_CERTIFY,
        REGISTER_WRONG_YEAR_BIRTH_AFTER,
        REGISTER_WRONG_YEAR_BIRTH_BEFORE,
        REGISTER_ALREADY_MEMBER,
        REGISTER_SUCCESS,
        REGISTER_UNKNOWN_ERROR,
        FORGOT_NO_INTERNET,
        FORGOT_INCORRECT_EMAIL,
        FORGOT_EMAIL_NOT_FOUND,
        FORGOT_SUCCESS,
        CHANGE_NO_INTERNET,
        CHANGE_INCORRECT_EMAIL,
        CHANGE_NO_PASSWORD,
        CHANGE_NO_NEW_PASSWORD,
        CHANGE_NO_CONFIRM_NEW_PASSWORD,
        CHANGE_NO_MATCH_NEW_PASSWORD,
        CHANGE_FAIL_AUTHENTICATION,
        CHANGE_SUCCESS,
        UNREGISTER_NO_INTERNET,
        UNREGISTER_INCORRECT_EMAIL,
        UNREGISTER_NO_PASSWORD,
        UNREGISTER_FAIL_AUTHENTICATION,
        UNREGISTER_UNABLE_CANCEL,
        UNREGISTER_CANNOT_CANCEL,
        UNREGISTER_SUCCESS,
        GENERAL_NO_INTERNET,
        GENERAL_TIMEOUT,
        BROADCAST_EXIST,
        IN_PROGRESS
    }

    public static boolean showDialog(Activity activity, Alerts item, String newPass) {
        alert = item;
        return buildDialog(activity, newPass);
    }

    public static boolean showDialog(Activity activity, Alerts item) {
        alert = item;
        return buildDialog(activity, null);
    }

    public static boolean showDialog(Activity activity) {
        return buildDialog(activity, null);
    }

    private static boolean buildDialog(final Activity activity, final String newPass) {
        // Dismiss any previous showing dialog
//        destroyDialog();
        Log.e("current alert", alert.toString());
        switch (alert) {
            case LOGIN_NO_INTERNET:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_internet),
                        activity.getResources().getString(R.string.error_title_login));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_internet));
                return true;
            case LOGIN_INCORRECT_EMAIL:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_email_login),
                        activity.getResources().getString(R.string.error_title_login));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_email_login));
                return true;
            case LOGIN_NO_PASSWORD:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_password_login),
                        activity.getResources().getString(R.string.error_title_login));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_password_login));
                return true;
            case LOGIN_FAIL_AUTHENTICATION:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_login),
                        activity.getResources().getString(R.string.error_title_login));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_login));
                return true;
            case REGISTER_NO_INTERNET:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_internet),
                        activity.getResources().getString(R.string.error_title_register));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_internet));
                return true;
            case REGISTER_BLANK_INPUTS:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.general_error_message),
                        activity.getResources().getString(R.string.error_title_register));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.general_error_message));
                return true;
            case REGISTER_INVALID_EMAIL:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_email),
                        activity.getResources().getString(R.string.error_title_register));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_email));
                return true;
            case REGISTER_NO_VERIFY_EMAIL:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_verify),
                        activity.getResources().getString(R.string.error_title_register));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_verify));
                return true;
            case REGISTER_EMPTY_INTERESTS:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_interests),
                        activity.getResources().getString(R.string.error_title_register));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_interests));
                return true;
            case REGISTER_NO_CERTIFY:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_certify),
                        activity.getResources().getString(R.string.error_title_register));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_certify));
                return true;
            case REGISTER_WRONG_YEAR_BIRTH_AFTER:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_birth_after) +
                        " " + Calendar.getInstance().get(Calendar.YEAR),
                        activity.getResources().getString(R.string.error_title_register));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_birth_after));
                return true;
            case REGISTER_WRONG_YEAR_BIRTH_BEFORE:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_birth_before),
                        activity.getResources().getString(R.string.error_title_register));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_birth_before));
                return true;
            case REGISTER_ALREADY_MEMBER:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_already_member),
                        activity.getResources().getString(R.string.error_title_register));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_already_member));
                return true;
            case REGISTER_SUCCESS:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.success_message_register),
                        activity.getResources().getString(R.string.title_register)) {
                    @Override
                    public void onClicked() {
                        Intent intent = new Intent(activity, LoginActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                };
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.success_message_register));
                return true;
            case REGISTER_UNKNOWN_ERROR:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_unknown),
                        activity.getResources().getString(R.string.error_title_register));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_unknown));
                return true;
            case FORGOT_NO_INTERNET:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_internet),
                        activity.getResources().getString(R.string.error_title_forgot));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_internet));
                return true;
            case FORGOT_INCORRECT_EMAIL:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_forgot_email),
                        activity.getResources().getString(R.string.error_title_forgot));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_forgot_email));
                return true;
            case FORGOT_EMAIL_NOT_FOUND:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_forgot),
                        activity.getResources().getString(R.string.error_title_forgot));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_forgot));
                return true;
            case FORGOT_SUCCESS:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.message_forgot),
                        activity.getResources().getString(R.string.title_forgot)) {
                    @Override
                    public void onClicked() {
                        Intent intent = new Intent(activity, MainMenuActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                };
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.message_forgot));
                return true;
            case CHANGE_NO_INTERNET:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_internet),
                        activity.getResources().getString(R.string.error_title_change));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_internet));
                return true;
            case CHANGE_INCORRECT_EMAIL:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_change_email),
                        activity.getResources().getString(R.string.error_title_change));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_change_email));
                return true;
            case CHANGE_NO_PASSWORD:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_change_old),
                        activity.getResources().getString(R.string.error_title_change));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_change_old));
                return true;
            case CHANGE_NO_NEW_PASSWORD:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_change_new),
                        activity.getResources().getString(R.string.error_title_change));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_change_new));
                return true;
            case CHANGE_NO_CONFIRM_NEW_PASSWORD:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_change_confirm),
                        activity.getResources().getString(R.string.error_title_change));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_change_confirm));
                return true;
            case CHANGE_NO_MATCH_NEW_PASSWORD:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_change_disagree),
                        activity.getResources().getString(R.string.error_title_change));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_change_disagree));
                return true;
            case CHANGE_FAIL_AUTHENTICATION:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_change),
                        activity.getResources().getString(R.string.error_title_forgot));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_change));
                return true;
            case CHANGE_SUCCESS:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.message_change),
                        activity.getResources().getString(R.string.title_change)) {
                    @Override
                    public void onClicked() {
                        // Save new password
                        SharedPreferences settings = activity.getSharedPreferences(UserPreferences.PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(UserPreferences.PASSWORD, newPass);
                        editor.commit();
                        // Returns to main menu
                        Intent intent = new Intent(activity, MainMenuActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                };
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.message_change));
                return true;
            case UNREGISTER_NO_INTERNET:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_internet),
                        activity.getResources().getString(R.string.error_title_unregister));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_internet));
                return true;
            case UNREGISTER_INCORRECT_EMAIL:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_unregister_email),
                        activity.getResources().getString(R.string.error_title_unregister));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_unregister_email));
                return true;
            case UNREGISTER_NO_PASSWORD:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_unregister_password),
                        activity.getResources().getString(R.string.error_title_unregister));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_unregister_password));
                return true;
            case UNREGISTER_FAIL_AUTHENTICATION:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_unregister_1),
                        activity.getResources().getString(R.string.error_title_unregister));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_unregister_1));
                return true;
            case UNREGISTER_UNABLE_CANCEL:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_unregister_2),
                        activity.getResources().getString(R.string.error_title_unregister));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_unregister_2));
                return true;
            case UNREGISTER_CANNOT_CANCEL:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_unregister_3),
                        activity.getResources().getString(R.string.error_title_unregister));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_unregister_3));
                return true;
            case UNREGISTER_SUCCESS:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.message_unregister),
                        activity.getResources().getString(R.string.title_unregister)) {
                    @Override
                    public void onClicked() {
                        // Remove all preferences
                        SharedPreferences settings = activity.getSharedPreferences(UserPreferences.PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.clear();
                        editor.commit();
                        // Return to login screen
                        Intent intent = new Intent(activity, LoginActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                };
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.message_unregister));
                return true;
            case GENERAL_NO_INTERNET:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_internet),
                        activity.getResources().getString(R.string.error_title_message)) {
                    @Override
                    public void onClicked() {
                        activity.finish();
                    }
                };
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_internet));
                return true;
            case GENERAL_TIMEOUT:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_timeout),
                        activity.getResources().getString(R.string.error_title_message)) {
                    @Override
                    public void onClicked() {
                        activity.finish();
                    }
                };
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_timeout));
                return true;
            case BROADCAST_EXIST:
                errorDialog = new ErrorDialog(activity.getResources().getString(R.string.error_message_already_exist),
                        activity.getResources().getString(R.string.message_title));
                errorDialog.show(((ActionBarActivity) activity).getSupportFragmentManager(),
                        activity.getResources().getString(R.string.error_message_already_exist));
                return true;
            case IN_PROGRESS:
                showProgress(activity);
                return true;
            case NULL:
                destroyProgress();
                return false;
            default:
                return false;
        }
    }

    public static void destroyDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            Log.e("destroy", "executed");
        }
        progressDialog = null;
    }

    public static void setAlertFromString(String string) {
        for (Alerts item : Alerts.values()) {
            if (item.toString().equals(string)) {
                alert = item;
                break;
            }
        }
    }

    public static void reset() {
        alert = Alerts.NULL;
        Log.e("reset", "executed");
    }

    public static void prepareProgress() {
        alert = Alerts.IN_PROGRESS;
        Log.e("prepare", "executed");
    }

    public static void restoreProgress(Activity activity) {
        Log.e("restores", "executed");
        if (alert.equals(Alerts.IN_PROGRESS)) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage(activity.getResources().getString(R.string.progress_message));
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
            }
            progressDialog.show();
        }
    }

    public static void showProgress(Activity activity) {
        closeProgress();
        alert = Alerts.IN_PROGRESS;
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage(activity.getResources().getString(R.string.progress_message));
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    public static void destroyProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public static void closeProgress() {
        Log.e("close", "executed");
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            reset();
        }
        progressDialog = null;
    }
}
