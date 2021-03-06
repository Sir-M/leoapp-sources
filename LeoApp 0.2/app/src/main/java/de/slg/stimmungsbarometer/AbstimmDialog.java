package de.slg.stimmungsbarometer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import de.slg.leoapp.R;
import de.slg.leoapp.Start;
import de.slg.leoapp.Utils;

public class AbstimmDialog extends AlertDialog {
    private final String[] gruende = {"Wetter", "Fächer", "Lehrer", "Freunde/Bekannte", "Arbeiten/Klausuren", "besonderer Anlass", "Sonstiges"};
    int userid;
    private int voteid = 0;
    private String ausgewählterGrund = "";
    private View confirm;
    private ImageButton very_satisfied;
    private ImageButton satisfied;
    private ImageButton neutral;
    private ImageButton dissatisfied;
    private ImageButton bad_mood;
    private ListView listView;

    public AbstimmDialog(@NonNull Context context) {
        super(context);
        userid = Utils.getUserID();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_abstimmen);

        initListView();
        initSmileys();
        initSendButton();
    }

    private void initSmileys() {
        very_satisfied = (ImageButton) findViewById(R.id.imageButtonVS);
        satisfied = (ImageButton) findViewById(R.id.imageButtonS);
        neutral = (ImageButton) findViewById(R.id.imageButtonN);
        dissatisfied = (ImageButton) findViewById(R.id.imageButtonD);
        bad_mood = (ImageButton) findViewById(R.id.imageButtonB);

        very_satisfied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm.setEnabled(true);
                listView.setClickable(true);
                refreshButtons();
                very_satisfied.setEnabled(false);
                voteid = 1;
            }
        });

        satisfied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm.setEnabled(true);
                listView.setClickable(true);
                refreshButtons();
                satisfied.setEnabled(false);
                voteid = 2;
            }
        });

        neutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm.setEnabled(true);
                listView.setClickable(true);
                refreshButtons();
                neutral.setEnabled(false);
                voteid = 3;
            }
        });

        dissatisfied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm.setEnabled(true);
                listView.setClickable(true);
                refreshButtons();
                dissatisfied.setEnabled(false);
                voteid = 4;
            }
        });

        bad_mood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm.setEnabled(true);
                listView.setClickable(true);
                refreshButtons();
                bad_mood.setEnabled(false);
                voteid = 5;
            }
        });
    }

    private void initSendButton() {
        confirm = findViewById(R.id.buttonDialog2);
        confirm.setEnabled(false);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (confirm.isEnabled()) {
                    new SendeDaten().execute(new Wahl(voteid, userid, ausgewählterGrund));
                    Utils.setLastVote(voteid);
                    dismiss();
                }
            }
        });

        View cancel = findViewById(R.id.buttonDialog1);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void initListView() {
        listView = (ListView) findViewById(R.id.listView);
        if (Start.pref.getBoolean("pref_key_show_reasons_survey", false)) {
            listView.setClickable(false);
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(new ListAdapterGrund(getContext(), gruende));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (listView.isClickable()) {
                        if (ausgewählterGrund.equals(gruende[i])) {
                            view.setSelected(false);
                            ausgewählterGrund = "";
                            view.findViewById(R.id.textViewGrund).setSelected(false);
                        } else {
                            view.setSelected(true);
                            ausgewählterGrund = gruende[i];
                            view.findViewById(R.id.textViewGrund).setSelected(true);
                        }
                    }
                }
            });
        } else {
            findViewById(R.id.relativeLayout).setVisibility(View.GONE);
        }
    }

    private void refreshButtons() {
        very_satisfied.setEnabled(true);
        satisfied.setEnabled(true);
        neutral.setEnabled(true);
        dissatisfied.setEnabled(true);
        bad_mood.setEnabled(true);
    }

    class Wahl {
        final int voteid;
        final int userid;
        final String grund;

        Wahl(int voteid, int userid, String grund) {
            this.voteid = voteid;
            this.userid = userid;
            this.grund = grund;
        }
    }

    private class ListAdapterGrund extends ArrayAdapter<String> {
        private final Context context;
        private final String[] gruende;

        ListAdapterGrund(Context context, String[] gruende) {
            super(context, R.layout.list_item_grund, gruende);
            this.gruende = gruende;
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, View v, @NonNull ViewGroup group) {
            if (v == null)
                v = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_grund, null);
            final TextView grund = (TextView) v.findViewById(R.id.textViewGrund);
            grund.setText(gruende[position]);
            return v;
        }
    }

    private class SendeDaten extends AsyncTask<Wahl, Void, Void> {
        @Override
        protected Void doInBackground(AbstimmDialog.Wahl... wahls) {
            if (wahls[0] != null) {
                try {
                    AbstimmDialog.Wahl w = wahls[0];
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            new URL("http://moritz.liegmanns.de/stimmungsbarometer/vote.php?key=5453&voteid=" + w.voteid + "&userid=" + w.userid + "&grund=" + w.grund.replace(" ", "%20"))
                                                    .openConnection()
                                                    .getInputStream(), "UTF-8"));
                    while (reader.readLine() != null) ;
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}