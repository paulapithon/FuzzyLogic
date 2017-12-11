package br.poli.ecomp.fuzzyfca;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.Variable;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private final static int PERMISSION_STORAGE = 333;

    private TextView mSaida;
    private EditText mBuffer;
    private Spinner mSpinner;

    double problema;
    double buffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSaida = findViewById(R.id.saida);
        mBuffer = findViewById(R.id.buffer);
        mSpinner = findViewById(R.id.spinner);

        buffer = 0;
        problema = 0;
    }

    public void getFuzzyAnswer(View v) {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            loadFuzzy();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_STORAGE);
        }

    }

    private void loadFuzzy() {
        getProblema();
        if (buffer <= 32 && buffer >= 0) {
            //Carrega arquivo .flc
            try {
                InputStream inputStream = getAssets().open("fuzzy.fcl");
                FIS fis = FIS.load(inputStream, true);

                if (fis != null) {

                    // Definir inputs
                    fis.setVariable("problema", problema);
                    fis.setVariable("buffer", buffer);
                    fis.evaluate();

                    // Mostra gráfico da saída
                    Variable atividade = fis.getVariable("atividade");
                    mSaida.setText(Html.fromHtml(getResultado(atividade)));
                }

                Log.d("FUZZY", "A saída é: " + fis.getVariable("atividade").getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getProblema() {
        //Atribuir tempo aos problemas
        String item = mSpinner.getSelectedItem().toString();
        Resources resources = getResources();
        if (item.equals(resources.getString(R.string.problema_aplicacao))) {
            problema = 55.55;
        } else if (item.equals(resources.getString(R.string.problema_automatico))) {
            problema = 35.0;
        } else if (item.equals(resources.getString(R.string.problema_eletrodo))) {
            problema = 28.26;
        } else if (item.equals(resources.getString(R.string.problema_falha))) {
            problema = 29;
        } else if (item.equals(resources.getString(R.string.problema_manual))) {
            problema = 21.4;
        } else if (item.equals(resources.getString(R.string.problema_pecas))) {
            problema = 37.56;
        } else if (item.equals(resources.getString(R.string.problema_sistema))) {
            problema = 33.4;
        } else if (item.equals(resources.getString(R.string.problema_barreirafalha))) {
            problema = 54.4;
        } else if (item.equals(resources.getString(R.string.problema_trabalho))) {
            problema = 154;
        } else if (item.equals(resources.getString(R.string.problema_repouso))) {
            problema = 131;
        } else if (item.equals(resources.getString(R.string.problema_timeoutrepouso))) {
            problema = 36.74;
        } else if (item.equals(resources.getString(R.string.problema_timeouttrabalho))) {
            problema = 35.8;
        } else if (item.equals(resources.getString(R.string.problema_profinet))) {
            problema = 536.27;
        } else {
            problema = 0;
        }
        buffer = Double.parseDouble(mBuffer.getText().toString());
    }

    private String getResultado(Variable variable) {
        double resultado = variable.getValue();

        //Checar grau de certeza do resultado
        int atividade, certeza;
        double flutuante = resultado - (int) resultado;
        if (flutuante > 0.5) {
            atividade = (int) resultado + 1;
            certeza = (int) (flutuante * 100);
        } else {
            atividade = (int) resultado;
            certeza = 100 - (int) (flutuante * 100);
        }

        //Retorno final em html
        String retorno = "Realizar atividade <b>";
        switch (atividade) {
            case 0:
                retorno += "limpeza";
                break;
            case 1:
                retorno += "inspeção";
                break;
            case 2:
                retorno += "lubrificação";
                break;
            case 3:
                retorno += "reaperto";
                break;
            case 4:
                retorno += "nenhuma";
                break;
        }
        //Tempo convertido em minutos
        int tempo = (int) (problema / 60 + buffer * 0.75);
        retorno += "</b> em <b>" + tempo + " minutos</b> com <b>" + certeza + "%</b> de sucesso.";

        return retorno;

    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_STORAGE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadFuzzy();
        } else {
            finish();
        }
    }
}
