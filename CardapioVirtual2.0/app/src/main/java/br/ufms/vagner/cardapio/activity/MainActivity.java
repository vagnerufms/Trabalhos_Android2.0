package br.ufms.vagner.cardapio.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import br.ufms.vagner.cardapio.R;
import br.ufms.vagner.cardapio.adapter.ListViewAdapter;
import br.ufms.vagner.cardapio.model.Lanche;
import br.ufms.vagner.cardapio.util.AlertDialogUtil;
import br.ufms.vagner.cardapio.util.FireBaseUtil;
import br.ufms.vagner.cardapio.util.SobreUtils;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private Firebase firebase;
    @Bind(R.id.toolbar)
    public Toolbar mToolbar;
    ListViewAdapter listViewAdapter;
    List<Lanche> lancheList;
    Lanche lanche = new Lanche();
    @Bind(R.id.listview)
    ListView listView;
    Boolean updateListView = false;
    String mensagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        firebase = FireBaseUtil.getFirebase();
        setSupportActionBar(mToolbar);
        lancheList = new ArrayList<Lanche>();
        mensagem = new String();
        // Toolbar Title
        getSupportActionBar().setTitle(getString(R.string.lanches));
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        listViewAdapter = new ListViewAdapter(this, R.layout.listview_item, lancheList);
        listView.setAdapter(listViewAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int checkedCount = listView.getCheckedItemCount();
                if (checkedCount < 2) {
                    mode.setTitle(checkedCount + getString(R.string.text_item_selecionado));
                } else {
                    mode.setTitle(checkedCount + getString(R.string.text_itens_selecionados));
                }
                listViewAdapter.toggleSelection(position);
            }

            public void selectAllContatos() {
                deSelectAllContatos();
                for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                    listView.setItemChecked(i, true);
                }
            }

            public void deSelectAllContatos() {
                listView.clearChoices();
                for (int i = 0; i < listView.getCount(); i++) {
                    listView.setItemChecked(i, false);
                }
                listViewAdapter.notifyDataSetChanged();
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        if (new AlertDialogUtil().getYesNoWithExecutionStop("Exclusão", "Deseja realmente Excluir o(s) Lanche(s)?", MainActivity.this)) {
                            SparseBooleanArray selected = listViewAdapter.getSelectedIds();
                            for (int i = (selected.size()); i >= 0; i--) {
                                if (selected.valueAt(i)) {
                                    Lanche selecteditem = listViewAdapter.getItem(selected.keyAt(i));
                                    excluirContatoFirebase(selecteditem);
                                    listViewAdapter.remove(selecteditem);
                                }
                            }
                            int qtdContatos = selected.size();
                            String mengagemExclusao = "";
                            if (qtdContatos < 2) {
                                mengagemExclusao = qtdContatos + getString(R.string.msg_lanche_excluido_sucesso);
                            } else {
                                mengagemExclusao = qtdContatos + getString(R.string.msg_lanches_excluidos_sucesso);
                            }
                            Toast.makeText(getApplicationContext(), mengagemExclusao, Toast.LENGTH_SHORT).show();
                        }
                        deSelectAllContatos();
                        return true;
                    case R.id.selectAll:
                        selectAllContatos();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_main_select, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                listViewAdapter.removeSelection();
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }
        });

        listenerListViewItensContato();
        getAllContatosFirebase();
    }

    public void getAllContatosFirebase() {
        firebase.child("cardapio").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                lancheList.clear();
                for (DataSnapshot contatoSnapshot : snapshot.getChildren()) {
                    Lanche lanche = contatoSnapshot.getValue(Lanche.class);
                    lanche.getPreco().setScale(2, RoundingMode.DOWN);
                    System.out.println("ID: " + lanche.getId());
                    System.out.println("Nome: " + lanche.getNome());
                    System.out.println("Descrição: " + lanche.getDescricao());
                    System.out.println("Preço: " + lanche.getPreco());
                    lancheList.add(lanche);
                }
                listViewAdapter.setListItem(lancheList);
                listViewAdapter.notifyDataSetInvalidated();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("Falha no Carregamento dos Contatos pelo Firebase. " + firebaseError.getMessage());
            }
        });
    }

    public void listenerListViewItensContato() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int posicao, long id) {
                loadViewContato(adapter, posicao);
            }
        });
    }

    public void loadViewContato(AdapterView<?> adapter, int posicao) {
        // Carrega a Tela do Edicao
        lanche = (Lanche) adapter.getAdapter().getItem(posicao);
        Intent intent = new Intent(getBaseContext(), LancheViewActivity.class);
        intent.putExtra("lanche", lanche);
        startActivityForResult(intent, 1);
    }


    public void excluirContatoFirebase(Lanche lanche) {
        Firebase mFirebase = firebase.child("cardapio").child(lanche.getId());
        mFirebase.removeValue();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                listViewAdapter.getFilter().filter(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                listViewAdapter.getFilter().filter(query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.add:
                loadContatoCadastroActivity();
                return true;
            case R.id.action_refresh:
                atualizaListaContatos();
                return true;
            case R.id.action_sobre:
                SobreUtils.showAbout(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void atualizaListaContatos() {
        getAllContatosFirebase();
        Toast.makeText(getApplicationContext(), getString(R.string.message_success_atualiza_lista), Toast.LENGTH_SHORT).show();
    }

    public void loadContatoCadastroActivity() {
        // Carrega a Tela de Cadastro
        Intent intent = new Intent(getBaseContext(), LancheCadastroActivity.class);
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == 1) {
                updateListView = data.getBooleanExtra("updateListView", true);
                if (resultCode == RESULT_OK) {
                    mensagem = data.getStringExtra("mensagem");
                    Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_SHORT).show();
                    //preencheListViewCardapio();
                }
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "Erro. Operação Cancelada!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}