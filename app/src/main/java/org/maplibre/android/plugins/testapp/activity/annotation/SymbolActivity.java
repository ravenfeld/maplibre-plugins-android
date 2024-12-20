package org.maplibre.android.plugins.testapp.activity.annotation;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.maplibre.android.camera.CameraUpdateFactory;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.Style;
import org.maplibre.android.plugins.annotation.OnSymbolDragListener;
import org.maplibre.android.plugins.annotation.Symbol;
import org.maplibre.android.plugins.annotation.SymbolManager;
import org.maplibre.android.plugins.annotation.SymbolOptions;
import org.maplibre.android.plugins.testapp.TestStyles;
import org.maplibre.android.plugins.testapp.R;
import org.maplibre.android.plugins.testapp.Utils;
import org.maplibre.android.style.expressions.Expression;
import org.maplibre.android.style.layers.Property;
import org.maplibre.android.style.sources.GeoJsonOptions;
import org.maplibre.android.utils.BitmapUtils;
import org.maplibre.android.utils.ColorUtils;
import org.maplibre.geojson.FeatureCollection;
import org.maplibre.geojson.Point;
import timber.log.Timber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static org.maplibre.android.style.expressions.Expression.*;

/**
 * Activity showcasing adding symbols using the annotation plugin
 */
public class SymbolActivity extends AppCompatActivity {

    private static final String ID_ICON_AIRPORT = "airport";
    private static final String MAKI_ICON_CAR = "car-15";
    private static final String MAKI_ICON_CAFE = "cafe-15";
    private static final String MAKI_ICON_CIRCLE = "fire-station-15";

    private final Random random = new Random();
    private final List<ValueAnimator> animators = new ArrayList<>();

    private MapView mapView;
    private SymbolManager symbolManager;
    private Symbol symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation);

        TextView draggableInfoTv = findViewById(R.id.draggable_position_tv);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(maplibreMap -> maplibreMap.setStyle(TestStyles.BRIGHT.getUrl(), style -> {
            findViewById(R.id.fabStyles).setOnClickListener(v -> {
                maplibreMap.setStyle(Utils.INSTANCE.getNextStyle());
                maplibreMap.getStyle(this::addAirplaneImageToStyle);
            });

            maplibreMap.moveCamera(CameraUpdateFactory.zoomTo(2));

            addAirplaneImageToStyle(style);

            // create symbol manager
            GeoJsonOptions geoJsonOptions = new GeoJsonOptions().withTolerance(0.4f);
            symbolManager = new SymbolManager(mapView, maplibreMap, style, null, null, geoJsonOptions);
            symbolManager.addClickListener(symbol -> {
                Toast.makeText(SymbolActivity.this,
                    String.format("Symbol clicked %s", symbol.getId()),
                    Toast.LENGTH_SHORT
                ).show();
                return false;
            });
            symbolManager.addLongClickListener(symbol -> {
                Toast.makeText(SymbolActivity.this,
                    String.format("Symbol long clicked %s", symbol.getId()),
                    Toast.LENGTH_SHORT
                ).show();
                return false;
            });

            // set non data driven properties
            symbolManager.setIconAllowOverlap(true);
            symbolManager.setTextAllowOverlap(true);

            // create a symbol
            SymbolOptions symbolOptions = new SymbolOptions()
                .withLatLng(new LatLng(6.687337, 0.381457))
                .withIconImage(ID_ICON_AIRPORT)
                .withIconSize(1.3f)
                .withSymbolSortKey(10.0f)
                .withDraggable(true);
            symbol = symbolManager.create(symbolOptions);
            Timber.e(symbol.toString());

            // create nearby symbols
            SymbolOptions nearbyOptions = new SymbolOptions()
                .withLatLng(new LatLng(6.626384, 0.367099))
                .withIconImage(MAKI_ICON_CIRCLE)
                .withIconColor(ColorUtils.colorToRgbaString(Color.YELLOW))
                .withIconSize(2.5f)
                .withSymbolSortKey(5.0f)
                .withDraggable(true);
            symbolManager.create(nearbyOptions);

            // random add symbols across the globe
            List<SymbolOptions> symbolOptionsList = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                symbolOptionsList.add(new SymbolOptions().withLatLng(createRandomLatLng()).withIconImage(MAKI_ICON_CAR)
                    .withDraggable(true));
            }
            symbolManager.create(symbolOptionsList);

            try {
                symbolManager.create(FeatureCollection.fromJson(Utils.INSTANCE.loadStringFromAssets(this, "annotations.json")));
            } catch (IOException e) {
                throw new RuntimeException("Unable to parse annotations.json");
            }

            symbolManager.addDragListener(new OnSymbolDragListener() {
                @Override
                public void onAnnotationDragStarted(Symbol annotation) {
                    draggableInfoTv.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnnotationDrag(Symbol annotation) {
                    draggableInfoTv.setText(String.format(
                        Locale.US,
                        "ID: %s\nLatLng:%f, %f",
                        annotation.getId(),
                        annotation.getLatLng().getLatitude(), annotation.getLatLng().getLongitude()));
                }

                @Override
                public void onAnnotationDragFinished(Symbol annotation) {
                    draggableInfoTv.setVisibility(View.GONE);
                }

            });
        }));
    }

    private LatLng createRandomLatLng() {
        return new LatLng((random.nextDouble() * -180.0) + 90.0,
            (random.nextDouble() * -360.0) + 180.0);
    }

    private void addAirplaneImageToStyle(Style style) {
        style.addImage(ID_ICON_AIRPORT,
            BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.ic_airplanemode_active_black_24dp)),
            true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_symbol, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_action_draggable) {
            for (int i = 0; i < symbolManager.getAnnotations().size(); i++) {
                Symbol symbol = symbolManager.getAnnotations().get(i);
                symbol.setDraggable(!symbol.isDraggable());
            }
        } else if (item.getItemId() == R.id.menu_action_filter) {
            Expression expression = eq(toNumber(get("id")), symbol.getId());
            Expression filter = symbolManager.getFilter();
            if (filter != null && filter.equals(expression)) {
                symbolManager.setFilter(not(eq(toNumber(get("id")), -1)));
            } else {
                symbolManager.setFilter(expression);
            }
        } else if (item.getItemId() == R.id.menu_action_icon) {
            symbol.setIconImage(MAKI_ICON_CAFE);
        } else if (item.getItemId() == R.id.menu_action_rotation) {
            symbol.setIconRotate(45.0f);
        } else if (item.getItemId() == R.id.menu_action_text) {
            symbol.setTextField("Hello world!");
        } else if (item.getItemId() == R.id.menu_action_anchor) {
            symbol.setIconAnchor(Property.ICON_ANCHOR_BOTTOM);
        } else if (item.getItemId() == R.id.menu_action_opacity) {
            symbol.setIconOpacity(0.5f);
        } else if (item.getItemId() == R.id.menu_action_offset) {
            symbol.setIconOffset(new PointF(10.0f, 20.0f));
        } else if (item.getItemId() == R.id.menu_action_text_anchor) {
            symbol.setTextAnchor(Property.TEXT_ANCHOR_TOP);
        } else if (item.getItemId() == R.id.menu_action_text_color) {
            symbol.setTextColor(Color.WHITE);
        } else if (item.getItemId() == R.id.menu_action_text_size) {
            symbol.setTextSize(22f);
        } else if (item.getItemId() == R.id.menu_action_z_index) {
            symbol.setSymbolSortKey(0.0f);
        } else if (item.getItemId() == R.id.menu_action_halo) {
            symbol.setIconHaloWidth(5.0f);
            symbol.setIconHaloColor(Color.RED);
            symbol.setIconHaloBlur(1.0f);
        } else if (item.getItemId() == R.id.menu_action_animate) {
            resetSymbol();
            easeSymbol(symbol, new LatLng(6.687337, 0.381457), 180);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

        symbolManager.update(symbol);
        return true;
    }

    private void resetSymbol() {
        symbol.setIconRotate(0.0f);
        symbol.setGeometry(Point.fromLngLat(6.687337, 0.381457));
        symbolManager.update(symbol);
    }

    private void easeSymbol(Symbol symbol, final LatLng location, final float rotation) {
        final LatLng originalPosition = symbol.getLatLng();
        final float originalRotation = symbol.getIconRotate();
        final boolean changeLocation = originalPosition.distanceTo(location) > 0;
        final boolean changeRotation = originalRotation != rotation;
        if (!changeLocation && !changeRotation) {
            return;
        }

        ValueAnimator moveSymbol = ValueAnimator.ofFloat(0, 1).setDuration(5000);
        moveSymbol.setInterpolator(new LinearInterpolator());
        moveSymbol.addUpdateListener(animation -> {
            if (symbolManager == null || symbolManager.getAnnotations().indexOfValue(symbol) < 0) {
                return;
            }
            float fraction = (float) animation.getAnimatedValue();

            if (changeLocation) {
                double lat = ((location.getLatitude() - originalPosition.getLatitude()) * fraction) + originalPosition.getLatitude();
                double lng = ((location.getLongitude() - originalPosition.getLongitude()) * fraction) + originalPosition.getLongitude();
                symbol.setGeometry(Point.fromLngLat(lng, lat));
            }

            if (changeRotation) {
                symbol.setIconRotate((rotation - originalRotation) * fraction + originalRotation);
            }

            symbolManager.update(symbol);
        });

        moveSymbol.start();
        animators.add(moveSymbol);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        for (ValueAnimator animator : animators) {
            animator.cancel();
        }
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (symbolManager != null) {
            symbolManager.onDestroy();
        }

        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}