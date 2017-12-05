package br.poli.ecomp.fuzzyfca;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Variable;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private TextView mSaida;
    private EditText mProblema;
    private EditText mBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSaida = findViewById(R.id.saida);
        mProblema = findViewById(R.id.problema);
        mBuffer = findViewById(R.id.buffer);
    }

    public void getFuzzyAnswer (View v) throws Exception {

        //Carrega arquivo .flc
        InputStream inputStream = getAssets().open("fuzzy.fcl");
        FIS fis = FIS.load(inputStream, true);

        if (fis != null) {
            JFuzzyChart.get().chart(fis);

            // Definir inputs
            fis.setVariable("problema", Double.parseDouble(mProblema.getText().toString()));
            fis.setVariable("buffer", Double.parseDouble(mBuffer.getText().toString()));
            fis.evaluate();

            // Mostra gráfico da saída
            Variable tempo = fis.getVariable("tempo");
            mSaida.setText(String.valueOf(tempo.getValue()));
        }

        Log.d("FUZZY", "A saída é: " + fis.getVariable("tempo").getValue());

    }

}
