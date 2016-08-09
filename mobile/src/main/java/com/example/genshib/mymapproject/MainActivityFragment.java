package com.example.genshib.mymapproject;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.LayerManager;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private MapView mapView;
    private TileCache tileCache;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Log.d("MainActivityFragment", "onCreateView");

        this.mapView = (MapView) rootView.findViewById(R.id.mapView);

        this.mapView.setClickable(true);
        this.mapView.getMapScaleBar().setVisible(true);
        this.mapView.setBuiltInZoomControls(true);
        this.mapView.setZoomLevelMin((byte) 10);
        this.mapView.setZoomLevelMax((byte) 20);

        createLayers();

        return rootView;
    }

    private final byte PERMISSIONS_REQUEST_READ_STORAGE = 122;


    protected void createLayers() {
        if (AndroidSupportUtil.runtimePermissionRequiredForReadExternalStorage(this.getActivity(), getMapFileDirectory())) {
            // note that this the Fragment method, not compat lib
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_STORAGE);
        } else {
            LayerManager layerManager = this.mapView.getLayerManager();
            Layers layers = layerManager.getLayers();

            MapViewPosition mapViewPosition = this.mapView.getModel().mapViewPosition;
            mapViewPosition.setZoomLevel((byte) 16);
            this.tileCache = AndroidUtil.createTileCache(
                this.getActivity(),
                "fragments",
                this.mapView.getModel().displayModel.getTileSize(),
                1.0f,
                1.5);

            mapViewPosition.setCenter(new LatLong(39.5296, -119.8138));
            layers.add(
                AndroidUtil.createTileRendererLayer(
                    this.tileCache,
                    mapViewPosition,
                    getMapFile(),
                    InternalRenderTheme.OSMARENDER, false, true, false
                )
            );
        }

    }

    protected MapFile getMapFile() {
        return new MapFile(new File(getMapFileDirectory(),
            this.getMapFileName()));
    }

    protected File getMapFileDirectory() {
        return Environment.getExternalStorageDirectory();
    }

    protected String getMapFileName() {
        return "nevada.map";
    }

    @Override
    public void onDestroy() {
        this.mapView.destroyAll();
        AndroidGraphicFactory.clearResourceMemoryCache();
        super.onDestroy();
    }
}
