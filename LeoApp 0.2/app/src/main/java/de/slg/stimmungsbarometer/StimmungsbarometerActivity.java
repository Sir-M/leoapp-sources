package de.slg.stimmungsbarometer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.GregorianCalendar;

import de.slg.essensqr.WrapperQRActivity;
import de.slg.klausurplan.KlausurplanActivity;
import de.slg.leoapp.PreferenceActivity;
import de.slg.leoapp.R;
import de.slg.leoapp.Utils;
import de.slg.messenger.OverviewWrapper;
import de.slg.schwarzes_brett.SchwarzesBrettActivity;
import de.slg.startseite.MainActivity;
import de.slg.stundenplan.WrapperStundenplanActivity;

public class StimmungsbarometerActivity extends AppCompatActivity {
    static boolean drawI;
    static boolean drawS;
    static boolean drawL;
    static boolean drawA;
    private static Ergebnis[][] daten;
    private DrawerLayout drawerLayout;
    private ZeitraumFragment[] fragments;

    public static Ergebnis[][] getData() {
        return daten;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrapper_stimmungsbarometer);

        drawI = true;
        drawS = true;
        drawL = true;
        drawA = true;

        new StartTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        if (mi.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }

    private void initLayouts() {
        final View lI = findViewById(R.id.layoutIch);
        final View lS = findViewById(R.id.layoutSchueler);
        final View lL = findViewById(R.id.layoutLehrer);
        final View lA = findViewById(R.id.layoutAlle);

        lI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawI = !drawI;
                v.findViewById(R.id.textViewIch).setEnabled(drawI);
                v.findViewById(R.id.imageViewIch).setEnabled(drawI);
                updateFragments(false);
            }
        });
        lS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawS = !drawS;
                v.findViewById(R.id.textViewSchueler).setEnabled(drawS);
                v.findViewById(R.id.imageViewSchueler).setEnabled(drawS);
                updateFragments(false);
            }
        });
        lL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawL = !drawL;
                v.findViewById(R.id.textViewLehrer).setEnabled(drawL);
                v.findViewById(R.id.imageViewLehrer).setEnabled(drawL);
                updateFragments(false);
            }
        });
        lA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawA = !drawA;
                v.findViewById(R.id.textViewAlle).setEnabled(drawA);
                v.findViewById(R.id.imageViewAlle).setEnabled(drawA);
                updateFragments(false);
            }
        });
    }

    private void updateFragments(boolean recreateCharts) {
        for (ZeitraumFragment fragment : fragments)
            fragment.update(recreateCharts);
    }

    private void initTabs() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        fragments = new ZeitraumFragment[4];
        for (int i = 0; i < fragments.length; i++) {
            fragments[i] = new ZeitraumFragment();
            fragments[i].zeitraum = i;
        }
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        });
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Letzte Woche");
        tabLayout.getTabAt(1).setText("Letzter Monat");
        tabLayout.getTabAt(2).setText("Letztes Jahr");
        tabLayout.getTabAt(3).setText("Gesamt");
    }

    private void initToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.actionBarStatistik);
        myToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getString(R.string.title_survey));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        navigationView.getMenu().findItem(R.id.barometer).setChecked(true);

//        navigationView.getMenu().findItem(R.id.nachhilfe).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.messenger).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.klausurplan).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.stundenplan).setEnabled(Utils.isVerified());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();
                Intent i;
                switch (menuItem.getItemId()) {
                    case R.id.foodmarks:
                        i = new Intent(getApplicationContext(), WrapperQRActivity.class);
                        break;
                    case R.id.messenger:
                        i = new Intent(getApplicationContext(), OverviewWrapper.class);
                        break;
                    case R.id.newsboard:
                        i = new Intent(getApplicationContext(), SchwarzesBrettActivity.class);
                        break;
//                    case R.id.nachhilfe:
//                        i = new Intent(getApplicationContext(), NachhilfeboerseActivity.class);
//                        break;
                    case R.id.stundenplan:
                        i = new Intent(getApplicationContext(), WrapperStundenplanActivity.class);
                        break;
                    case R.id.barometer:
                        return true;
                    case R.id.klausurplan:
                        i = new Intent(getApplicationContext(), KlausurplanActivity.class);
                        break;
                    case R.id.startseite:
                        i = null;
                        break;
//                    case R.id.vertretung:
//                        i = new Intent(getApplicationContext(), WrapperSubstitutionActivity.class);
//                        break;
                    case R.id.settings:
                        i = new Intent(getApplicationContext(), PreferenceActivity.class);
                        break;
                    default:
                        i = new Intent(getApplicationContext(), MainActivity.class);
                        Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
                if (i != null)
                    startActivity(i);
                finish();
                return true;
            }
        });
        TextView username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username);
        username.setText(Utils.getUserName());

        TextView grade = (TextView) navigationView.getHeaderView(0).findViewById(R.id.grade);
        if (Utils.getUserPermission() == 2)
            grade.setText(Utils.getLehrerKuerzel());
        else
            grade.setText(Utils.getUserStufe());

        ImageView mood = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        mood.setImageResource(Utils.getCurrentMoodRessource());
    }

    private class StartTask extends AsyncTask<Void, Void, Void> {
        private String[] splitI, splitS, splitL, splitA;

        StartTask() {
            splitI = new String[0];
            splitS = new String[0];
            splitL = new String[0];
            splitA = new String[0];
        }

        @Override
        protected void onPreExecute() {
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            initTabs();
            initToolbar();
            initNavigationView();
            initLayouts();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (daten == null) {
                try {
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            new URL("http://moritz.liegmanns.de/stimmungsbarometer/ergebnisse.php?key=5453&userid=" + Utils.getUserID())
                                                    .openConnection()
                                                    .getInputStream(), "UTF-8"));
                    String line;
                    StringBuilder builder = new StringBuilder();
                    while ((line = reader.readLine()) != null)
                        builder.append(line);
                    String[] e = builder.toString().split("_abschnitt_");
                    reader.close();
                    if (!e[0].equals("."))
                        splitI = e[0].split("_next_");
                    if (!e[1].equals("."))
                        splitS = e[1].split("_next_");
                    if (!e[2].equals("."))
                        splitL = e[2].split("_next_");
                    if (!e[3].equals("."))
                        splitA = e[3].split("_next_");
                    Ergebnis[][] ergebnisse = new Ergebnis[4][];
                    ergebnisse[0] = new Ergebnis[splitI.length];
                    ergebnisse[1] = new Ergebnis[splitS.length];
                    ergebnisse[2] = new Ergebnis[splitL.length];
                    ergebnisse[3] = new Ergebnis[splitA.length];
                    for (int i = 0; i < ergebnisse[0].length; i++) {
                        String[] current = splitI[i].split(";");
                        if (current.length == 2) {
                            String[] date = current[1].replace('.', '_').split("_");
                            ergebnisse[0][i] = new Ergebnis(new GregorianCalendar(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0])).getTime(), Double.parseDouble(current[0]), true, false, false, false);
                        }
                    }
                    for (int i = 0; i < ergebnisse[1].length; i++) {
                        String[] current = splitS[i].split(";");
                        if (current.length == 2) {
                            String[] date = current[1].replace('.', '_').split("_");
                            ergebnisse[1][i] = new Ergebnis(new GregorianCalendar(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0])).getTime(), Double.parseDouble(current[0]), false, true, false, false);
                        }
                    }
                    for (int i = 0; i < ergebnisse[2].length; i++) {
                        String[] current = splitL[i].split(";");
                        if (current.length == 2) {
                            String[] date = current[1].replace('.', '_').split("_");
                            ergebnisse[2][i] = new Ergebnis(new GregorianCalendar(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0])).getTime(), Double.parseDouble(current[0]), false, false, true, false);
                        }
                    }
                    for (int i = 0; i < ergebnisse[3].length; i++) {
                        String[] current = splitA[i].split(";");
                        if (current.length == 2) {
                            String[] date = current[1].replace('.', '_').split("_");
                            ergebnisse[3][i] = new Ergebnis(new GregorianCalendar(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0])).getTime(), Double.parseDouble(current[0]), false, false, false, true);
                        }
                    }
                    daten = ergebnisse;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            for (ZeitraumFragment fragment : fragments) {
                fragment.fillData();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            updateFragments(true);
            findViewById(R.id.progressBar).setVisibility(View.GONE);
        }
    }
}