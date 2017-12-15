package br.poli.ecomp.fuzzyfca;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import net.sourceforge.jFuzzyLogic.rule.RuleBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;

import java.io.IOException;
import java.io.InputStream;
import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static int PERMISSION_STORAGE = 333;

    private TextView mSaida;
    private EditText mBuffer;
    private Spinner mSpinner;

    double problema;
    double buffer;
    double recente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSaida = findViewById(R.id.saida);
        mBuffer = findViewById(R.id.buffer);
        mSpinner = findViewById(R.id.spinner);

        buffer = 0;
        problema = 0;
        recente = 4;
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
                    fis.setVariable("recente", 4);
                    fis.setVariable("buffer", buffer);
                    fis.evaluate();

                    List<Double> rules = new ArrayList<>();
                    for(Rule rule : fis.getFunctionBlock("tipper").getFuzzyRuleBlock("rules").getRules()) {
                        rules.add(rule.getDegreeOfSupport());
                    }

                    mSaida.setText(Html.fromHtml(getResultado(rules)));
                }

                Log.d("FUZZY", "A saída é: " + fis.getVariable("atividade").getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Condições do buffer inválidas. Favor inserir um valor entre 0 e 32.")
                    .setTitle("Erro!");
            final AlertDialog dialog = builder.create();
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            builder.show();
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

    private List<String> getCiclo () {
        List<String> ciclos = new ArrayList<>();
        ciclos.add("inspeção");
        ciclos.add("limpeza");
        ciclos.add("reaperto");
        ciclos.add("lubrificação");
        ciclos.add("nenhuma");

        return ciclos;
    }

    private String getResultado(List<Double> values) {
        //Ordenar lista do maior tempo pro menor
        List<Double> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        Collections.reverse(sorted);

        //Pegar ciclo de maior valor
        int index = values.indexOf(sorted.get(0));

        //Se tiver side realizada agora, não repetir
        if (recente == index) {
            index = values.indexOf(sorted.get(1));
        }
        recente = index;

        int certeza = (int) (values.get(index) * 100);

        //Tempo convertido em minutos
        int tempo = (int) (problema / 60 + buffer * 0.75);

        //Retorno final em html
        return "Realizar atividade <b>" + getCiclo().get(index) + "</b> em <b>" + tempo + " minutos</b> com <b>" + certeza + "%</b> de sucesso.";

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
