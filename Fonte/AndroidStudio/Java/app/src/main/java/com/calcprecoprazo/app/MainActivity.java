package com.calcprecoprazo.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.calcprecoprazo.app.common.CommonActivity;
import com.calcprecoprazo.app.constantes.Constantes;
import com.calcprecoprazo.app.control.HttpClient;
import com.calcprecoprazo.app.utils.Utilitarios;

import org.w3c.dom.Document;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.calcprecoprazo.app.utils.Utilitarios.isConnected;

public class MainActivity extends CommonActivity implements View.OnClickListener {

    /* Variaveis usadas para mostrar alertas de saida do aplicativo */
    private Context context = this;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;

    private Spinner spServico;
    private EditText edPeso;
    private EditText edData;

    private Button btAcao;

    private int codServico;
    private int peso;
    private String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
    }

    protected void init() {
        Utilitarios.showSimpleProgressDialog(context,
                getString(R.string.alerta_title),
                getString(R.string.hint_aguarde), true);

        if (isConnected(context)) {
            initViews();
            initUser();
        } else {
            messageAlert(getString(R.string.error_network));
            return;
        }
    }


    @Override
    protected void initViews() {
        spServico = findViewById(R.id.spServico);
        edPeso = findViewById(R.id.edPeso);
        edData = findViewById(R.id.edData);

        btAcao = findViewById(R.id.btAcao);

        btAcao.setOnClickListener(this);
    }

    @Override
    protected void initUser() {
        String[] params = new String[2];

        params[0] = Constantes.BASE_URL;
        params[1] = Constantes.URL_LIST;

        getListServicos(params);
    }

    @Override
    protected boolean isValidate() {
        boolean cancel = false;
        View focusView = null;

        int peso = 0;
        String data = null;

        try {
            peso = Integer.parseInt(edPeso.getText().toString());

            if (peso <= 0) {
                edPeso.setError(getString(R.string.error_peso));
                focusView = edPeso;
                cancel = true;
            }
        } catch (Exception e) {
            edPeso.setError(getString(R.string.error_peso));
            focusView = edPeso;
            cancel = true;
        }

        if (!cancel) {
            try {
                if (!Utilitarios.ValidateDate(edData.getText().toString())){
                    edData.setError(getString(R.string.error_data));
                    focusView = edPeso;
                    cancel = true;
                }

            } catch (Exception e) {
                edData.setError(getString(R.string.error_data));
                focusView = edPeso;
                cancel = true;
            }
        }

        if (cancel) {
            if (focusView != null) {
                focusView.requestFocus();
            }
        }

        return cancel;
    }

    @Override
    protected void errorAlert(String message) {
        alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.StyleDialogs));

        alertDialogBuilder.setTitle(getString(R.string.alerta_title));
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(getString(R.string.hint_fechar), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void messageAlert(String message) {
        alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.StyleDialogs));

        alertDialogBuilder.setTitle(getString(R.string.alerta_title));

        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton(getString(R.string.hint_sim), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.hint_nao), new DialogInterface.OnClickListener() {
            @SuppressLint("NewApi")
            public void onClick(DialogInterface dialog, int id) {
                finishAndRemoveTask();
                dialog.dismiss();
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btAcao :

                if (isConnected(context)) {
                    if (!isValidate()) {
                        getResult();
                    }
                } else {
                    errorAlert(getString(R.string.error_network));
                }

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sair) {
            sair();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
        Metodo atribuido ao botão do celular Voltar, ao acionar irá solicitar confirmação ou
        não do aplicativo
    */
    public void onBackPressed() {
        sair();
    }

    /*
        Metodo usado para sair e encerrar o aplicativo
    */
    private void sair() {
        alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.StyleDialogs));

        alertDialogBuilder.setTitle(getString(R.string.alerta_title));

        alertDialogBuilder.setMessage(getString(R.string.alerta_sair));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(getString(R.string.hint_sim), new DialogInterface.OnClickListener() {
            @SuppressLint("NewApi")
            public void onClick(DialogInterface dialog, int id) {
                finishAndRemoveTask();
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.hint_nao), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void getListServicos(String[] params) {
        List<String> list = new ArrayList<String>();

        try {
            HttpClient httpClient = new HttpClient(context);

            String xmlRetorno = httpClient.execute(params).get();

            if (xmlRetorno != null) {
                parseXmlParserServico(xmlRetorno, list);
            }
        } catch (ExecutionException e) {
            errorAlert(e.getMessage());
        } catch (InterruptedException e) {
            errorAlert(e.getMessage());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spServico.setAdapter(dataAdapter);
    }

    private void getResult() {
        List<String> list = new ArrayList<String>();

        try {
            String[] params = new String[5];

            params[0] = Constantes.BASE_URL;
            params[1] = Constantes.URL_SEARCH;
            params[2] = "nCdServico=" + spServico.getSelectedItem().toString();
            params[3] = "&nVlPeso=" + edPeso.getText().toString();
            params[4] = "&strDataCalculo=" + edData.getText().toString();

            HttpClient httpClient = new HttpClient(context);

            String xmlRetorno = httpClient.execute(params).get();

            if (xmlRetorno != null) {
                parseXmlParserEnvio(xmlRetorno, list);

                if (list.size() > 0) {
                    String dados = "";

                    for(int i = 0; i < list.size(); i++) {
                        dados += list.get(i) + "\n";
                    }

                    messageResultado(dados);
                }
            }
        } catch (ExecutionException e) {
            errorAlert(e.getMessage());
        } catch (InterruptedException e) {
            errorAlert(e.getMessage());
        }
    }

    private void parseXmlParserServico(String xmlString, List<String> list) {

        try {
            // Create xml pull parser factory.
            XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();

            // Create XmlPullParser.
            XmlPullParser xmlPullParser = parserFactory.newPullParser();

            // Create a new StringReader.
            StringReader xmlStringReader = new StringReader(xmlString);

            // Set the string reader as XmlPullParser input.
            xmlPullParser.setInput(xmlStringReader);

            // Get event type during xml parse.
            int eventType = xmlPullParser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                // Get xml element node name.
                String nodeName = xmlPullParser.getName();

                if (!TextUtils.isEmpty(nodeName)) {

                    if (nodeName.toLowerCase().equals("codigo")) {
                        // Get xml element text value.
                        String value = xmlPullParser.nextText();

                        Log.d(Constantes.TAG, "element text : " + value);

                        list.add(value);
                    }
                }

                eventType = xmlPullParser.next();
            }
        }catch(XmlPullParserException ex) {
            errorAlert(ex.getMessage());
        } catch (IOException ioex) {
            errorAlert(ioex.getMessage());
        }

    }

    private void parseXmlParserEnvio(String xmlString, List<String> list) {

        try {
            // Create xml pull parser factory.
            XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();

            // Create XmlPullParser.
            XmlPullParser xmlPullParser = parserFactory.newPullParser();

            // Create a new StringReader.
            StringReader xmlStringReader = new StringReader(xmlString);

            // Set the string reader as XmlPullParser input.
            xmlPullParser.setInput(xmlStringReader);

            // Get event type during xml parse.
            int eventType = xmlPullParser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                // Get xml element node name.
                String nodeName = xmlPullParser.getName();

                if (!TextUtils.isEmpty(nodeName)) {

                    if (nodeName.toLowerCase().equals("codigo")) {
                        // Get xml element text value.
                        String value = xmlPullParser.nextText();

                        Log.d(Constantes.TAG, "element text : " + value);

                        list.add(nodeName + ": " + value);
                    }

                    if (nodeName.toLowerCase().equals("valor")) {
                        // Get xml element text value.
                        String value = xmlPullParser.nextText();

                        Log.d(Constantes.TAG, "element text : " + value);

                        list.add(nodeName + ": " + value);
                    }

                    if (nodeName.toLowerCase().equals("erro")) {
                        // Get xml element text value.
                        String value = xmlPullParser.nextText();

                        Log.d(Constantes.TAG, "element text : " + value);

                        list.add(nodeName + ": " + value);
                    }

                    if (nodeName.toLowerCase().equals("msgerro")) {
                        // Get xml element text value.
                        String value = xmlPullParser.nextText();

                        Log.d(Constantes.TAG, "element text : " + value);

                        list.add(nodeName + ": " + value);
                    }

                }

                eventType = xmlPullParser.next();
            }
        }catch(XmlPullParserException ex) {
            errorAlert(ex.getMessage());
        } catch (IOException ioex) {
            errorAlert(ioex.getMessage());
        }

    }

    protected void messageResultado(String message) {
        alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.StyleDialogs));

        alertDialogBuilder.setTitle(getString(R.string.alerta_title));

        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton(getString(R.string.hint_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
