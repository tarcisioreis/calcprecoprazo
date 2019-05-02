package com.calcprecoprazo.app.common;

/**
 *
 * 
 * Classe Abstrata para instancia classe Activity com metodos abstratos e concreto para faciliar a implementacao
 * 
 * 
 */
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

abstract public class CommonActivity extends AppCompatActivity {

    protected ProgressBar progressBar;

    protected void showToast( String message ){
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG)
                .show();
    }

    protected void openProgressBar(){
        if (progressBar == null) {
            progressBar = new ProgressBar(this);
        }

        progressBar.setVisibility( View.VISIBLE );
    }

    protected void closeProgressBar(){
        if (progressBar == null) {
            progressBar = new ProgressBar(this);
        }

        progressBar.setVisibility( View.GONE );
    }

    abstract protected void initViews();

    abstract protected void initUser();

    protected abstract boolean isValidate();

    abstract protected void errorAlert(String message);
    abstract protected void messageAlert(String message);

}