package br.ufms.vagner.cardapio.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import br.ufms.vagner.cardapio.R;
import butterknife.Bind;
import butterknife.ButterKnife;

public final class SobreUtils {
    private static final String ABOUT_DIALOG_TAG = "sobre_dialog";


    public static void showAbout(Activity activity) {
        FragmentManager fm = activity.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(ABOUT_DIALOG_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        new AboutDialog().show(ft, "sobre_dialog");
    }

    public static class AboutDialog extends DialogFragment {
        private static final String VERSION_UNAVAILABLE = "N/A";

        @Bind(R.id.tvSobre)
        public TextView tvAbout;

        public AboutDialog() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get app version
            PackageManager pm = getActivity().getPackageManager();
            String packageName = getActivity().getPackageName();
            String versionName;
            try {
                PackageInfo info = pm.getPackageInfo(packageName, 0);
                versionName = info.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                versionName = VERSION_UNAVAILABLE;
            }

            SpannableStringBuilder aboutBody = new SpannableStringBuilder();
            String appNome = getString(R.string.app_name);
            aboutBody.append(Html.fromHtml(getString(R.string.sobre_texto, appNome, versionName)));

            LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = li.inflate(R.layout.sobre_dialog, null);

            ButterKnife.bind(this, v);

            tvAbout.setText(aboutBody);
            tvAbout.setMovementMethod(new LinkMovementMethod());

            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.sobre)
                    .setView(v)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            }
                    ).create();
        }
    }
}