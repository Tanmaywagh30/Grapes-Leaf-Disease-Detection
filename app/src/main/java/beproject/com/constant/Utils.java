package beproject.com.constant;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.Inflater;

import beproject.com.grapesdiseasedetection.R;


public class Utils {


    public static void saveToSharedPreferences(Context context,String sharedPref,String sharedPrefKey,String sharedPrefValue)
    {
        SharedPreferences preferences = context.getSharedPreferences(sharedPref,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(sharedPrefKey,sharedPrefValue);
        editor.commit();

    }

    public static String getSharedPreferences(Context context , String sharedPref , String sharedPrefKey)
    {
        SharedPreferences preferences = context.getSharedPreferences(sharedPref,Context.MODE_PRIVATE);
        return preferences.getString(sharedPrefKey,"").toString();

    }


    public static String getCurrentDate()
    {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm-dd/MMM/yyyy");
        String formattedDate = sdf.format(date);
        return formattedDate;
    }


    public static String[] splitDate(String DateTime)
    {
        String arr[] = DateTime.split("-");
        return arr;
    }

    public static Dialog getDialogBox(Context context, int layout)
    {
        Dialog dialog;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);
        dialog.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.setContentView(layout);
        dialog.getWindow().setAttributes(lp);


        return dialog;
    }

}
