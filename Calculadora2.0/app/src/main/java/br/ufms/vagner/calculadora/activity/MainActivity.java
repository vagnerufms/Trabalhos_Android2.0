package br.ufms.vagner.calculadora.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.math.BigDecimal;
import java.math.RoundingMode;
import br.ufms.vagner.calculadora.R;
import br.ufms.vagner.calculadora.util.SobreUtils;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private BigDecimal num1;
    private BigDecimal num2;

    private boolean informarNum2;
    private String strNum2;
    private String operacao;

    @Bind(R.id.toolbar)
    public Toolbar toolbar;

    // EditText
    @Bind(R.id.edtxt_input)
    public EditText edTxtVisor;

    //Botões Números / Ponto
    @Bind(R.id.btn_0)
    public Button btn0;
    @Bind(R.id.btn_1)
    public Button btn1;
    @Bind(R.id.btn_2)
    public Button btn2;
    @Bind(R.id.btn_3)
    public Button btn3;
    @Bind(R.id.btn_4)
    public Button btn4;
    @Bind(R.id.btn_5)
    public Button btn5;
    @Bind(R.id.btn_6)
    public Button btn6;
    @Bind(R.id.btn_7)
    public Button btn7;
    @Bind(R.id.btn_8)
    public Button btn8;
    @Bind(R.id.btn_9)
    public Button btn9;
    @Bind(R.id.bt_ponto)
    public Button btnPonto;

    //Botões Operadores
    @Bind(R.id.btn_soma)
    public Button btnSoma;
    @Bind(R.id.btn_subtracao)
    public Button btnSubtracao;
    @Bind(R.id.btn_multiplicacao)
    public Button btnMultiplicacao;
    @Bind(R.id.btn_divisao)
    public Button btnDivisao;

    //Botões Ações
    @Bind(R.id.btn_resultado)
    public Button btnResultado;
    @Bind(R.id.btn_limpar)
    public Button btnLimpar;
    @Bind(R.id.btn_apagar_caracter)
    public Button btnApagarCaractere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        init();
    }

    private void init() {
        num1 = new BigDecimal(0);
        num2 = new BigDecimal(0);
        informarNum2 = false;
        strNum2 = new String("");
        listenersButtons();
    }

    private void listenersButtons() {
        btn0.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btnPonto.setOnClickListener(this);

        btnLimpar.setOnClickListener(this);
        btnApagarCaractere.setOnClickListener(this);
        btnResultado.setOnClickListener(this);

        btnSoma.setOnClickListener(this);
        btnSubtracao.setOnClickListener(this);
        btnMultiplicacao.setOnClickListener(this);
        btnDivisao.setOnClickListener(this);
    }

    public void addNumero(int numero) {
        if (edTxtVisor.getText().toString().trim().equals("0")) {
            clear();
            edTxtVisor.setText(String.valueOf(numero));
        } else {
            if(!informarNum2){
                edTxtVisor.setText(edTxtVisor.getText().toString() + String.valueOf(numero));
            }else{
                strNum2 += numero;
                edTxtVisor.setText(formatNumber(num1) + " " + operacao + " " + strNum2);
            }
        }
        moveCursorEditTextFinal();
    }

    public void addPonto(String ponto){
        if(!informarNum2){
            if(edTxtVisor.getText().toString().indexOf('.') < 0 && !edTxtVisor.getText().toString().equals("")){
                edTxtVisor.setText(edTxtVisor.getText().toString() + ponto);
            }
        }else{
            if(strNum2.indexOf('.') < 0 && !strNum2.equals("")){
                strNum2 += ponto;
                edTxtVisor.setText(formatNumber(num1) + " " + operacao + " " + strNum2);
            }
        }
        moveCursorEditTextFinal();
    }

    private void clear() {
        this.informarNum2 = false;
        edTxtVisor.setText("0");
    }

    public String formatNumber(BigDecimal numero){
        String str;
        if(numero.doubleValue() != 0){
            str = String.valueOf(numero);
            str = str.indexOf(".") < 0 ? str : str.replaceAll("0*$", "").replaceAll("\\.$", "");
        }else{
            str = String.valueOf(0);
        }
        return str;
    }

    public boolean isNumber(String str){
        try {
            new BigDecimal(str);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public void operacoes(String operador) {
        this.operacao = operador;
        if(!informarNum2 && isNumber(edTxtVisor.getText().toString())){
            num1 = new BigDecimal(edTxtVisor.getText().toString().trim()).setScale(15, RoundingMode.HALF_UP);
            this.informarNum2 = true;
            if (operador == "+") {
                edTxtVisor.setText(formatNumber(num1) + " + ");
            } else if (operador == "-") {
                edTxtVisor.setText(formatNumber(num1) + " - ");
            } else if (operador == "*") {
                edTxtVisor.setText(num1 + " * ");
                edTxtVisor.setText(formatNumber(num1) + " * ");
            } else if (operador == "/") {
                edTxtVisor.setText(formatNumber(num1) + " / ");
            }
        }else{
            if(isNumber(strNum2)){
                calcular();
                operacoes(this.operacao);
            }
        }
    }

    public void moveCursorEditTextFinal(){
        edTxtVisor.setSelection(edTxtVisor.getText().length());
    }

    public void moveCursorEditTextInicio(){
        edTxtVisor.setSelection(0);
    }

    public void calcular() {
        if(informarNum2 && !strNum2.equals("")){
            if(!edTxtVisor.getText().toString().trim().equals("")){
                num2 = new BigDecimal(strNum2).setScale(15, RoundingMode.HALF_UP);
                if (operacao == "+") {
                    num1 = num1.add(num2).setScale(15, RoundingMode.HALF_UP);
                } else if (operacao == "-") {
                    num1 = num1.subtract(num2).setScale(15, RoundingMode.HALF_UP);
                } else if (operacao == "*") {
                    num1 = num1.multiply(num2).setScale(15, RoundingMode.HALF_UP);
                } else if (operacao == "/") {
                    if(num2.doubleValue() == 0){
                        num1 = new BigDecimal(0);
                    } else {
                        num1 = num1.divide(num2, 15, RoundingMode.HALF_UP);
                    }
                }
            }else{
                num1 = new BigDecimal(0);
            }
            strNum2 = "";
            num2 = new BigDecimal(0);
            informarNum2 = false;
            edTxtVisor.setText(formatNumber(num1));
            moveCursorEditTextInicio();
        }
    }

    public void backspace(){
        String str = edTxtVisor.getText().toString();
        StringBuilder w = new StringBuilder(str);
        if(!informarNum2){
            if(str.length() > 1){
                str = String.valueOf( w.deleteCharAt(str.length() - 1) );
            }else{
                str = "0";
            }
            edTxtVisor.setText(str);
        }else{
            w = new StringBuilder(strNum2);
            if(strNum2.length() > 1){
                strNum2 = String.valueOf( w.deleteCharAt(strNum2.length() - 1) );
                edTxtVisor.setText(formatNumber(num1) + " " + operacao + " " + strNum2);
            }else{
                strNum2 = "";
                edTxtVisor.setText(formatNumber(num1) + " " + operacao + " " + strNum2);
            }
        }
        moveCursorEditTextFinal();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_0:
                addNumero(0);
                break;
            case R.id.btn_1:
                addNumero(1);
                break;
            case R.id.btn_2:
                addNumero(2);
                break;
            case R.id.btn_3:
                addNumero(3);
                break;
            case R.id.btn_4:
                addNumero(4);
                break;
            case R.id.btn_5:
                addNumero(5);
                break;
            case R.id.btn_6:
                addNumero(6);
                break;
            case R.id.btn_7:
                addNumero(7);
                break;
            case R.id.btn_8:
                addNumero(8);
                break;
            case R.id.btn_9:
                addNumero(9);
                break;
            case R.id.btn_limpar:
                clear();
                break;
            case R.id.btn_resultado:
                calcular();
                break;
            case R.id.bt_ponto:
                addPonto(".");
                break;
            case R.id.btn_apagar_caracter:
                backspace();
                break;
            case R.id.btn_soma:
                operacoes("+");
                break;
            case R.id.btn_subtracao:
                operacoes("-");
                break;
            case R.id.btn_multiplicacao:
                operacoes("*");
                break;
            case R.id.btn_divisao:
                operacoes("/");
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sobre:
                SobreUtils.showAbout(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}