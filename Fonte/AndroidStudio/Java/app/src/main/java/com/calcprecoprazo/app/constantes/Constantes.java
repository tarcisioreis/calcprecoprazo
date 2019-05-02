package com.calcprecoprazo.app.constantes;

/**
 *
 * 
 * Classe usada para facilitar acesso a API
 * 
 * 
 */
public class Constantes {

    public static final String BASE_URL = "http://ws.correios.com.br/calculador";

    public static final String URL_LIST = "/CalcPrecoPrazo.asmx/ListaServicos?";
    public static final String URL_SEARCH = "/CalcPrecoPrazo.asmx/CalcPrecoFAC?";

    // NETWORK == 1 - ATIVA e NETWORK == 0 - DESATIVADA
    public static final int NETWORK = 1;
    public static final String TAG = "CalcPrecoPrazo";
}
