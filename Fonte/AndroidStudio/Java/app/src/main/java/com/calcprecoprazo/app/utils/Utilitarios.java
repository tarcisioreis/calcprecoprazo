package com.calcprecoprazo.app.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.calcprecoprazo.app.constantes.Constantes;
import com.calcprecoprazo.app.interfaces.OnProgressCancelListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * 
 * Classe com rotina de uso comum no aplicativo para data, teste de conexao e assim por diante
 * 
 * 
 */
public class Utilitarios {

    private static Dialog mDialog;
    private static OnProgressCancelListener progressCancelListener;
    private static ProgressDialog mProgressDialog;

    public Utilitarios() { super(); }

    /*	ConnectivityManager
    *.TYPE_MOBILE	0
    *.TYPE_WIFI	    1
    *.TYPE_WIMAX	6
    *.TYPE_ETHERNET 9
    */

    public static boolean isConnected(Context c) {

        if (Constantes.NETWORK > 0) {
            try {
                ConnectivityManager cm = (ConnectivityManager)
                        c.getSystemService(Context.CONNECTIVITY_SERVICE);
                int[] p = {0, 1, 6, 9};
                for (int i : p) {
                    if (cm.getNetworkInfo(i).isConnected()) return true;
                }
            } catch (Exception e) {
                Log.e("NETWORK", e.getMessage());
            }
        }

        return false;
    }

    public static String StringToDateFormat(String receivedDate) {
        String strDate = null;

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = formatter.parse(receivedDate);

            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            strDate = df.format(date).toString();
        }catch (Exception e) {
            return null;
        }

        return strDate;
    }

    public static boolean ValidateDate(String receivedDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date date = formatter.parse(receivedDate);

            if (date != null) {
                return true;
            } else return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static void showSimpleProgressDialog(Context context,
                                                String title,
                                                String msg,
                                                boolean isCancelable) {

        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(context, title, msg);
                mProgressDialog.setCancelable(isCancelable);
            }

            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }

        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void removeSimpleProgressDialog() {

        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();

        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
