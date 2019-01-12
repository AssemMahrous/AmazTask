package task.com.amaz.amaztask.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import task.com.amaz.amaztask.MvpApp;
import task.com.amaz.amaztask.R;
import task.com.amaz.amaztask.data.AppDataManager;
import task.com.amaz.amaztask.data.network.model.Entry;
import task.com.amaz.amaztask.ui.base.BaseActivity;
import task.com.amaz.amaztask.util.MessageHelper;

import static task.com.amaz.amaztask.util.Constants.ALL;
import static task.com.amaz.amaztask.util.Constants.NEGATIVE;
import static task.com.amaz.amaztask.util.Constants.NEUTRAL;
import static task.com.amaz.amaztask.util.Constants.POSITIVE;

public class MainActivity extends BaseActivity implements MainMvpView, OnMapReadyCallback {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.floating_menu)
    FloatingActionMenu floating_menu;
    MainPresenter presenter;
    GoogleMap mMap;
    private List<Marker> markers = new ArrayList<>();
    private List<Entry> entries = new ArrayList<>();
    String currentShown = null;
    Snackbar snackbar;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        AppDataManager dataManager = ((MvpApp) getApplication()).getDataManager();
        presenter = new MainPresenter<>(dataManager);
        presenter.onAttach(this);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, filter);
        setViews();
    }

    private void setViews() {
        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        fm.beginTransaction().replace(R.id.map, supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);
    }

    @OnClick(R.id.menu_negative)
    public void negativeClick(View view) {
        closeMenu();
        showMarkers(NEGATIVE);
    }

    @OnClick(R.id.menu_positive)
    public void positiveClick(View view) {
        closeMenu();
        showMarkers(POSITIVE);
    }

    @OnClick(R.id.menu_neutral)
    public void neutralClick(View view) {
        closeMenu();
        showMarkers(NEUTRAL);
    }

    @OnClick(R.id.menu_all)
    public void allClick(View view) {
        closeMenu();
        showMarkers(ALL);
    }

    private void showMarkers(String type) {
        if (type.equals(currentShown))
            return;

        if (type.equals(ALL)) {
            for (Marker marker : markers) {
                marker.setVisible(true);
            }
        } else {
            for (Marker marker : markers) {
                if (marker.getSnippet().equals(type))
                    marker.setVisible(true);
                else
                    marker.setVisible(false);
            }
        }
        currentShown = type;
    }


    @Override
    public void setEntries(List<Entry> entries) {
        this.entries.addAll(entries);
        for (int i = 0; i < entries.size(); i++) {
            String message = entries.get(i).getContent().getMessage();
            String sentiment = MessageHelper.getSentiment(message);
            String readMessage = MessageHelper.getMessage(message);
            String cityName = MessageHelper.getCity(readMessage);
            analyzeCity(sentiment, readMessage, cityName);
        }

    }

    private void analyzeCity(String sentiment, String readableMessage, String cityName) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses;
            addresses = geocoder.getFromLocationName(cityName, 1);
            LatLng latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
            addMarkers(latLng, sentiment, readableMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMarkers(LatLng latLng, String sentiment, String message) {
        BitmapDescriptor bitmapDescriptor = null;
        switch (sentiment) {
            case NEUTRAL:
                bitmapDescriptor = BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                break;
            case POSITIVE:
                bitmapDescriptor = BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED);
                break;
            case NEGATIVE:
                bitmapDescriptor = BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                break;
        }

        markers.add(mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(message)
                .snippet(sentiment)
                .icon(bitmapDescriptor)));
    }

    @Override
    public void showError() {
        Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
    }


    void closeMenu() {
        floating_menu.close(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        presenter.getData();
    }

    BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            try {
                if (!noConnectivity) {
                    if (snackbar != null)
                        snackbar.dismiss();
                    if (entries.size() == 0 && count >= 1)
                        presenter.getData();
                } else
                    onConnectionLost();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    public void onConnectionLost() {
        snackbar = Snackbar.make(findViewById(android.R.id.content),
                getString(R.string.please_internet),
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(getString(R.string.dismiss), v -> snackbar.dismiss()).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkStateReceiver);
    }
}
