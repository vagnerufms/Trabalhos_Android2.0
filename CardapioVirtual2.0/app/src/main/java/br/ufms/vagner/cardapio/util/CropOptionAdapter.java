package br.ufms.vagner.cardapio.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import br.ufms.vagner.cardapio.R;
import butterknife.Bind;
import butterknife.ButterKnife;

public class CropOptionAdapter extends ArrayAdapter<CropOption> {
    private ArrayList<CropOption> mOptions;
    private LayoutInflater mInflater;
    @Bind(R.id.iv_icon)
    public ImageView icon;
    @Bind(R.id.tv_name)
    public TextView txtName;

    public CropOptionAdapter(Context context, ArrayList<CropOption> options) {
        super(context, R.layout.crop_selector, options);
        mOptions = options;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup group) {
        if (convertView == null)
            convertView = mInflater.inflate(R.layout.crop_selector, null);

        ButterKnife.bind(convertView);

        CropOption item = mOptions.get(position);

        if (item != null) {
            icon.setImageDrawable(item.icon);
            txtName.setText(item.title);

            return convertView;
        }

        return null;
    }
}