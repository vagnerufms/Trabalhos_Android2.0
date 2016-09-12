package br.ufms.vagner.cardapio.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.math.RoundingMode;

import br.ufms.vagner.cardapio.R;
import br.ufms.vagner.cardapio.model.Lanche;
import br.ufms.vagner.cardapio.util.Alert;
import br.ufms.vagner.cardapio.util.AlertDialogUtil;
import br.ufms.vagner.cardapio.util.BitmapUtils;
import br.ufms.vagner.cardapio.util.FireBaseUtil;
import br.ufms.vagner.cardapio.util.ImageConverter;
import butterknife.Bind;
import butterknife.ButterKnife;

public class LancheViewActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolbar;
    @Bind(R.id.nome)
    public TextView mNome;
    @Bind(R.id.descricao)
    public TextView mDescricao;
    @Bind(R.id.preco)
    public TextView mPreco;
    @Bind(R.id.image_preview)
    public ImageView mImageView;
    private Lanche lanche;
    private Bitmap photo;
    private Uri outputFileUri;
    private int posicaoLista;
    private Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lanche_view);
        ButterKnife.bind(this);
        firebase = FireBaseUtil.getFirebase();
        setSupportActionBar(mToolbar);
        // Toolbar Title
        getSupportActionBar().setTitle(getString(R.string.detalhes));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
        Intent i = getIntent();
        lanche = (Lanche) i.getSerializableExtra("lanche");
        setValuesElements();
    }

    public void setValuesElements() {
        if (lanche != null) {
            mNome.setText(lanche.getNome());
            mDescricao.setText(lanche.getDescricao());
            mPreco.setText(lanche.getPreco().setScale(2, RoundingMode.DOWN).toString());
            mImageView.setImageBitmap(ImageConverter.getRoundedCornerBitmap(BitmapUtils.getBitmapFromImgString(lanche.getImagem(), getApplicationContext()), 100));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contato_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                if (new AlertDialogUtil().getYesNoWithExecutionStop("Exclusão", "Deseja realmente Excluir o Lanche?", LancheViewActivity.this)) {
                    excluirContatoFirebase(lanche);
                    Toast.makeText(getApplicationContext(), getString(R.string.msg_lanche_excluido_sucesso), Toast.LENGTH_SHORT).show();
                    finish();
                }
                return true;
            case R.id.action_edit:
                loadEditContato(lanche);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadEditContato(Lanche lanche) {
        // Carrega a Tela de Edicao
        Intent intent = new Intent(getBaseContext(), LancheEditActivity.class);
        intent.putExtra("lanche", lanche);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Bitmap image = savedInstanceState.getParcelable("BitmapImageContato");
        Uri outputFileUri = savedInstanceState.getParcelable("outputFileUri");
        if (image != null) {
            photo = image;
            mImageView.setImageBitmap(image);
        }
        if (outputFileUri != null) {
            this.outputFileUri = outputFileUri;
        }
    }

    public void excluirContatoFirebase(Lanche lanche) {
        Firebase mFirebase = firebase.child("cardapio").child(lanche.getId());
        mFirebase.removeValue();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (photo != null) {
            savedInstanceState.putParcelable("BitmapImageContato", photo);
        }
        if (outputFileUri != null) {
            savedInstanceState.putParcelable("outputFileUri", outputFileUri);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        lanche = (Lanche) data.getSerializableExtra("lanche");
                        setValuesElements();
                    }
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            Alert.information(getApplicationContext(), "Edição Cancelada.");
        } else {
            Alert.information(getApplicationContext(), "Erro Editar Lanche.");
        }
    }
}