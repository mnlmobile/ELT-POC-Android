package org.cambridge.eltpoc.util;

import android.content.Context;
import android.content.SharedPreferences;

import org.cambridge.eltpoc.Constants;
import org.cambridge.eltpoc.model.CLMSUser;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by etorres on 6/18/15.
 */

public class SharedPreferencesUtils {
    private static final String USER_PREFERENCES = "USER_PREFERENCES";
    private static final String USER_CONTENTS = "USER_CONTENTS";
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD_HASH = "PASSWORD_HASH";
    private static final String DISPLAY_NAME = "DISPLAY_NAME";
    private static final String USER_ID = "USER_ID";
    private static final String SYNC = "SYNC";

    public static CLMSUser getLoggedInUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        CLMSUser user = new CLMSUser();
        user.setUsername(sharedPreferences.getString(USERNAME, ""));
        String decrypted = "";
        try {
            decrypted = PasswordHash.decrypt(Constants.SECRET_KEY,
                    sharedPreferences.getString(PASSWORD_HASH, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        user.setPassword(decrypted);
        user.setId(sharedPreferences.getLong(USER_ID, 0));
        user.setDisplayName(sharedPreferences.getString(DISPLAY_NAME, ""));
        return user;
    }

    public static void updateLoggedInUser(Context context, String username, String password, String displayName,
                                          long userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME, username);
        String encrypted = "";
        try {
            encrypted = PasswordHash.encrypt(Constants.SECRET_KEY, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.putString(PASSWORD_HASH, encrypted);
        editor.putString(DISPLAY_NAME, displayName);
        editor.putLong(USER_ID, userId);
        editor.commit();
    }

    public static void addContentSync(Context context, String uniqueId) {
        CLMSUser user = getLoggedInUser(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                USER_CONTENTS+"_"+user.getUsername(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ArrayList<String> uniqueIds = getContentSync(context);
        JSONArray a = new JSONArray();
        boolean check = true;
        for (int i = 0; i < uniqueIds.size(); i++) {
            a.put(uniqueIds.get(i));
            if(uniqueIds.get(i).equalsIgnoreCase(uniqueId))
                check = false;
        }
        if(check)
            a.put(uniqueId);
        editor.putString(SYNC, a.toString());
        editor.commit();
    }

    public static ArrayList<String> getContentSync(Context context) {
        CLMSUser user = getLoggedInUser(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                USER_CONTENTS+"_"+user.getUsername(), Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(SYNC, null);
        ArrayList<String> uniqueIds = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String uniqueId = a.optString(i);
                    uniqueIds.add(uniqueId);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return uniqueIds;
    }

    public static void clearContentSync(Context context) {
        CLMSUser user = getLoggedInUser(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                USER_CONTENTS+"_"+user.getUsername(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SYNC, null);
        editor.commit();
    }
}
