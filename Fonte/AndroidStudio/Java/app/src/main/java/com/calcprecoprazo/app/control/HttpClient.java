package com.calcprecoprazo.app.control;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.calcprecoprazo.app.R;
import com.calcprecoprazo.app.constantes.Constantes;
import com.calcprecoprazo.app.utils.Utilitarios;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *
 * 
 * Classe usada para interface entre o aplicativo e a API dos Correios.
 * 
 *
 *
 */
public class HttpClient extends AsyncTask<String[], Void, String> {

    private OkHttpClient client = new OkHttpClient();

    private Context context;

    public HttpClient() {}

    public HttpClient(Context context) {
        this.context = context;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Utilitarios.showSimpleProgressDialog(context,
                                             context.getString(R.string.alerta_title),
                                             context.getString(R.string.hint_aguarde), false);
    }

    @Override
    protected String doInBackground(String[]... params) {
        String retorno = null;
        Response response = null;

        MediaType mediaType = MediaType.parse("text/xml");
        RequestBody body = RequestBody.create(mediaType, "{}");

        String parametros = params[0][0] + params[0][1];

        if (params[0].length > 2) {
            parametros += params[0][2];
            parametros += params[0][3];
            parametros += params[0][4];
        }

        Request request = new Request.Builder()
        .url(parametros)
        .addHeader("charset", "utf-8")
        .get()
        .build();

        try {
            response = client.newCall(request).execute();

            retorno = response.body().string();
        } catch (Exception e) {
            Log.e(Constantes.TAG, e.getMessage());
        }

        return retorno;
    }

    @Override
    protected void onPostExecute(String xmlParseResult) {
        super.onPostExecute(xmlParseResult);

        Utilitarios.removeSimpleProgressDialog();
    }

}
