package br.ufms.vagner.cardapio.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import br.ufms.vagner.cardapio.R;
import br.ufms.vagner.cardapio.model.Lanche;
import br.ufms.vagner.cardapio.util.BitmapUtils;
import br.ufms.vagner.cardapio.util.ImageConverter;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ListViewAdapter extends ArrayAdapter<Lanche> {
    Context context;
    LayoutInflater inflater;
    List<Lanche> lancheList;
    private SparseBooleanArray mSelectedItemsIds;
    private final Context mContext;
    private Filter filter;

    public ListViewAdapter(Context context, int resourceId, List<Lanche> lancheList) {
        super(context, resourceId, lancheList);
        this.mContext = context;

        mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;
        this.lancheList = lancheList;
        inflater = LayoutInflater.from(context);
    }

    public class ViewHolder {
        @Bind(R.id.nome)
        TextView nome;
        @Bind(R.id.descricao)
        TextView descricao;
        @Bind(R.id.preco)
        TextView preco;
        @Bind(R.id.imagem)
        ImageView imagem;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.listview_item, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.nome.setText(lancheList.get(position).getNome());
        holder.descricao.setText(lancheList.get(position).getDescricao());
        holder.preco.setText("R$ "+lancheList.get(position).getPreco().setScale(2, RoundingMode.DOWN).toString());
        //Lanches com Preço maior que R$ 10,00
        if(lancheList.get(position).getPreco().doubleValue() > 10){
            holder.preco.setTextColor(Color.RED);
        }
        String S = lancheList.get(position).getImagem();

        Bitmap bitmap = BitmapUtils.getBitmapFromImgString(lancheList.get(position).getImagem(), mContext);
        Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 100);
        holder.imagem.setImageBitmap(circularBitmap);
        return view;
    }

    public void setListItem(List<Lanche> listItem) {
        this.lancheList = listItem;
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new AppFilter<Lanche>(lancheList);
        return filter;
    }

    @Override
    public void remove(Lanche object) {
        lancheList.remove(object);
        notifyDataSetChanged();
    }

    public List<Lanche> getLancheList() {
        return lancheList;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value) {
            mSelectedItemsIds.put(position, value);
        } else {
            mSelectedItemsIds.delete(position);
        }
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    private class AppFilter<T> extends Filter {

        private ArrayList<T> sourceObjects;

        public AppFilter(List<T> objects) {
            sourceObjects = new ArrayList<T>();
            synchronized (this) {
                sourceObjects.addAll(objects);
            }
        }

        @Override
        protected FilterResults performFiltering(CharSequence chars) {
            String filterSeq = chars.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (filterSeq != null && filterSeq.length() > 0) {
                ArrayList<T> filter = new ArrayList<T>();

                for (T object : sourceObjects) {
                    // the filtering itself:
                    if (object.toString().toLowerCase().contains(filterSeq))
                        filter.add(object);
                }
                result.count = filter.size();
                result.values = filter;
            } else {
                // add all objects
                synchronized (this) {
                    result.values = sourceObjects;
                    result.count = sourceObjects.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // NOTE: this function is *always* called from the UI thread.
            ArrayList<T> filtered = (ArrayList<T>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = filtered.size(); i < l; i++)
                add((Lanche) filtered.get(i));
            notifyDataSetInvalidated();
        }
    }
}