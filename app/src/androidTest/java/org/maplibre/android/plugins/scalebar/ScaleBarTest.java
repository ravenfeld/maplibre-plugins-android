package org.maplibre.android.plugins.scalebar;


import android.app.Activity;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.plugins.BaseActivityTest;
import org.maplibre.android.plugins.annotation.MapLibreMapAction;
import org.maplibre.android.plugins.testapp.R;
import org.maplibre.android.plugins.testapp.activity.TestActivity;
import timber.log.Timber;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Basic smoke tests for ScaleBar
 */
@RunWith(AndroidJUnit4.class)
public class ScaleBarTest extends BaseActivityTest {
    private ScaleBarPlugin scaleBarPlugin;
    private Activity activity;
    private ScaleBarWidget scaleBarWidget;

    @Override
    protected Class getActivityClass() {
        return TestActivity.class;
    }

    private void setupScaleBar() {
        Timber.i("Retrieving layer");
        MapLibreMapAction.invoke(maplibreMap, (uiController, maplibreMap) -> {
            scaleBarPlugin = new ScaleBarPlugin(idlingResource.getMapView(), maplibreMap);
            activity = rule.getActivity();
            scaleBarWidget = scaleBarPlugin.create(new ScaleBarOptions(activity));
            assertNotNull(scaleBarPlugin);
            assertNotNull(scaleBarWidget);
        });
    }

    @Test
    public void testScaleBarEnable() {
        validateTestSetup();
        setupScaleBar();
        MapLibreMapAction.invoke(maplibreMap, (uiController, maplibreMap) -> {
            assertEquals(View.VISIBLE, scaleBarWidget.getVisibility());
            assertTrue(scaleBarPlugin.isEnabled());
            scaleBarPlugin.setEnabled(false);
            assertEquals(View.GONE, scaleBarWidget.getVisibility());
            assertFalse(scaleBarPlugin.isEnabled());
        });
    }

    @Test
    public void testScaleBarColor() {
        validateTestSetup();
        setupScaleBar();
        MapLibreMapAction.invoke(maplibreMap, (uiController, maplibreMap) -> {
            assertEquals(ContextCompat.getColor(activity, android.R.color.black), scaleBarWidget.getTextColor());
            assertEquals(ContextCompat.getColor(activity, android.R.color.black), scaleBarWidget.getPrimaryColor());
            assertEquals(ContextCompat.getColor(activity, android.R.color.white), scaleBarWidget.getSecondaryColor());


            int textColor = R.color.colorAccent;
            int colorPrimary = R.color.colorPrimary;
            int colorSecondary = R.color.colorPrimaryDark;

            ScaleBarOptions option = new ScaleBarOptions(activity);
            option.setTextColor(textColor);
            option.setPrimaryColor(colorPrimary);
            option.setSecondaryColor(colorSecondary);
            scaleBarWidget = scaleBarPlugin.create(option);
            assertNotNull(scaleBarWidget);
            assertEquals(ContextCompat.getColor(activity, textColor), scaleBarWidget.getTextColor());
            assertEquals(ContextCompat.getColor(activity, colorPrimary), scaleBarWidget.getPrimaryColor());
            assertEquals(ContextCompat.getColor(activity, colorSecondary), scaleBarWidget.getSecondaryColor());
        });
    }

    @Test
    public void testTextBorder() {
        validateTestSetup();
        setupScaleBar();
        MapLibreMapAction.invoke(maplibreMap, (uiController, maplibreMap) -> {
            assertEquals(activity.getResources().getDimension(R.dimen.maplibre_scale_bar_text_border_width),
                scaleBarWidget.getTextBorderWidth(), 0);
            assertTrue(scaleBarWidget.isShowTextBorder());

            ScaleBarOptions option = new ScaleBarOptions(activity);
            option.setTextBorderWidth(R.dimen.fab_margin);
            scaleBarWidget = scaleBarPlugin.create(option);
            assertNotNull(scaleBarWidget);

            assertEquals(activity.getResources().getDimension(R.dimen.fab_margin),
                scaleBarWidget.getTextBorderWidth(), 0);

            option = new ScaleBarOptions(activity);
            option.setTextBorderWidth(100f);
            scaleBarWidget = scaleBarPlugin.create(option);
            assertNotNull(scaleBarWidget);
            assertEquals(100f, scaleBarWidget.getTextBorderWidth(), 0);

            option = new ScaleBarOptions(activity);
            option.setShowTextBorder(false);
            scaleBarWidget = scaleBarPlugin.create(option);
            assertNotNull(scaleBarWidget);
            assertFalse(scaleBarWidget.isShowTextBorder());
        });
    }

    @Test
    public void testScaleBarWidth() {
        validateTestSetup();
        setupScaleBar();
        MapLibreMapAction.invoke(maplibreMap, (uiController, maplibreMap) -> {
            assertEquals(MapView.class, scaleBarWidget.getParent().getClass());
            MapView parent = (MapView) scaleBarWidget.getParent();
            assertEquals(parent.getWidth(), scaleBarWidget.getMapViewWidth());
        });
    }

    @Test
    public void testMargin() {
        validateTestSetup();
        setupScaleBar();
        MapLibreMapAction.invoke(maplibreMap, (uiController, maplibreMap) -> {
            assertEquals(activity.getResources().getDimension(R.dimen.maplibre_scale_bar_margin_left),
                scaleBarWidget.getMarginLeft(), 0);
            assertEquals(activity.getResources().getDimension(R.dimen.maplibre_scale_bar_margin_top),
                scaleBarWidget.getMarginTop(), 0);
            assertEquals(activity.getResources().getDimension(R.dimen.maplibre_scale_bar_text_margin),
                scaleBarWidget.getTextBarMargin(), 0);

            ScaleBarOptions option = new ScaleBarOptions(activity);
            option.setMarginLeft(R.dimen.fab_margin);
            option.setMarginTop(R.dimen.fab_margin);
            option.setTextBarMargin(R.dimen.fab_margin);
            scaleBarWidget = scaleBarPlugin.create(option);
            assertNotNull(scaleBarWidget);
            assertEquals(activity.getResources().getDimension(R.dimen.fab_margin),
                scaleBarWidget.getMarginLeft(), 0);
            assertEquals(activity.getResources().getDimension(R.dimen.fab_margin),
                scaleBarWidget.getMarginTop(), 0);
            assertEquals(activity.getResources().getDimension(R.dimen.fab_margin),
                scaleBarWidget.getTextBarMargin(), 0);

            option = new ScaleBarOptions(activity);
            option.setMarginLeft(100f);
            option.setMarginTop(50f);
            option.setTextBarMargin(30f);
            scaleBarWidget = scaleBarPlugin.create(option);
            assertNotNull(scaleBarWidget);
            assertEquals(100f, scaleBarWidget.getMarginLeft(), 0);
            assertEquals(50f, scaleBarWidget.getMarginTop(), 0);
            assertEquals(30f, scaleBarWidget.getTextBarMargin(), 0);

        });
    }

    @Test
    public void testBarHeight() {
        validateTestSetup();
        setupScaleBar();
        MapLibreMapAction.invoke(maplibreMap, (uiController, maplibreMap) -> {
            assertEquals(activity.getResources().getDimension(R.dimen.maplibre_scale_bar_height),
                scaleBarWidget.getBarHeight(), 0);

            ScaleBarOptions option = new ScaleBarOptions(activity);
            option.setBarHeight(R.dimen.fab_margin);
            scaleBarWidget = scaleBarPlugin.create(option);
            assertNotNull(scaleBarWidget);
            assertEquals(activity.getResources().getDimension(R.dimen.fab_margin),
                scaleBarWidget.getBarHeight(), 0);

            option = new ScaleBarOptions(activity);
            option.setBarHeight(100f);
            scaleBarWidget = scaleBarPlugin.create(option);
            assertNotNull(scaleBarWidget);
            assertEquals(100f, scaleBarWidget.getBarHeight(), 0);

        });
    }

    @Test
    public void testTextSize() {
        validateTestSetup();
        setupScaleBar();
        MapLibreMapAction.invoke(maplibreMap, (uiController, maplibreMap) -> {
            assertEquals(activity.getResources().getDimension(R.dimen.maplibre_scale_bar_text_border_width),
                scaleBarWidget.getTextSize(), 0);

            ScaleBarOptions option = new ScaleBarOptions(activity);
            option.setTextSize(R.dimen.fab_margin);
            scaleBarWidget = scaleBarPlugin.create(option);
            assertNotNull(scaleBarWidget);
            assertEquals(activity.getResources().getDimension(R.dimen.fab_margin),
                scaleBarWidget.getTextSize(), 0);

            option = new ScaleBarOptions(activity);
            option.setTextSize(100f);
            scaleBarWidget = scaleBarPlugin.create(option);
            assertNotNull(scaleBarWidget);
            assertEquals(100f, scaleBarWidget.getTextSize(), 0);

        });
    }

    @Test
    public void testBorderWidth() {
        validateTestSetup();
        setupScaleBar();
        MapLibreMapAction.invoke(maplibreMap, (uiController, maplibreMap) -> {
            assertEquals(activity.getResources().getDimension(R.dimen.maplibre_scale_bar_border_width),
                scaleBarWidget.getBorderWidth(), 0);

            ScaleBarOptions option = new ScaleBarOptions(activity);
            option.setBorderWidth(R.dimen.fab_margin);
            scaleBarWidget = scaleBarPlugin.create(option);
            assertNotNull(scaleBarWidget);
            assertEquals(activity.getResources().getDimension(R.dimen.fab_margin),
                scaleBarWidget.getBorderWidth(), 0);

            option = new ScaleBarOptions(activity);
            option.setBorderWidth(100f);
            scaleBarWidget = scaleBarPlugin.create(option);
            assertNotNull(scaleBarWidget);
            assertEquals(100f, scaleBarWidget.getBorderWidth(), 0);

        });
    }


    @Test
    public void testRefreshInterval() {
        validateTestSetup();
        setupScaleBar();
        MapLibreMapAction.invoke(maplibreMap, (uiController, maplibreMap) -> {
            assertEquals(ScaleBarOptions.REFRESH_INTERVAL_DEFAULT, scaleBarWidget.getRefreshInterval(), 0);

            ScaleBarOptions option = new ScaleBarOptions(activity);
            option.setRefreshInterval(1000);
            scaleBarWidget = scaleBarPlugin.create(option);
            assertNotNull(scaleBarWidget);
            assertEquals(1000, scaleBarWidget.getRefreshInterval(), 0);

        });
    }

    @Test
    public void testMetrics() {
        validateTestSetup();
        setupScaleBar();
        MapLibreMapAction.invoke(maplibreMap, (uiController, maplibreMap) -> {
            assertEquals(ScaleBarOptions.LocaleUnitResolver.isMetricSystem(), scaleBarWidget.isMetricUnit());

            ScaleBarOptions option = new ScaleBarOptions(activity);
            option.setMetricUnit(true);
            scaleBarWidget = scaleBarPlugin.create(option);
            assertNotNull(scaleBarWidget);
            assertTrue(scaleBarWidget.isMetricUnit());

            option = new ScaleBarOptions(activity);
            option.setMetricUnit(false);
            scaleBarWidget = scaleBarPlugin.create(option);
            assertNotNull(scaleBarWidget);
            assertFalse(scaleBarWidget.isMetricUnit());
        });
    }

    @Test
    public void testRatio() {
        validateTestSetup();
        setupScaleBar();
        assertEquals(0.5f, scaleBarWidget.getRatio(), 0f);
        MapLibreMapAction.invoke(maplibreMap, (uiController, maplibreMap) -> {
            ScaleBarOptions option = new ScaleBarOptions(activity);
            option.setMaxWidthRatio(0.1f);
            scaleBarWidget = scaleBarPlugin.create(option);
            assertNotNull(scaleBarWidget);
            assertEquals(0.1f, scaleBarWidget.getRatio(), 0f);
        });
        MapLibreMapAction.invoke(maplibreMap, (uiController, maplibreMap) -> {
            ScaleBarOptions option = new ScaleBarOptions(activity);
            option.setMaxWidthRatio(1.0f);
            scaleBarWidget = scaleBarPlugin.create(option);
            assertNotNull(scaleBarWidget);
            assertEquals(1.0f, scaleBarWidget.getRatio(), 0f);
        });
    }
}
