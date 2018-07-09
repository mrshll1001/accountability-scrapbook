package uk.mrshll.matt.accountabilityscrapbook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Overlay;

public class OSMPickerActivity extends AppCompatActivity implements MapEventsReceiver
{
    MapView map;
    private double START_LAT = 54.9738301;
    private double START_LONG = -1.6131525;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /*TODO some persmissions stuff?*/

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_osmpicker);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(15);
        GeoPoint startPoint = new GeoPoint(START_LAT, START_LONG);
        mapController.setCenter(startPoint);

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this, this);
        map.getOverlays().add(0, mapEventsOverlay);




    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        Intent data = new Intent();
        data.putExtra("latitude", p.getLatitude());
        data.putExtra("longitude", p.getLongitude());

        setResult(RESULT_OK, data);
        finish();
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        return false;
    }
}
