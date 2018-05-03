package com.gobletsoft.ptouchprintercontrol;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.brother.ptouch.sdk.PrinterInfo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Activity_PrinterSettings extends BasePrinterSettingActivity {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_cutome_layout);
        addPreferencesFromResource(R.xml.printer_settings);

        Button btGetPrinterSettings = (Button) findViewById(R.id.btGetPrinterSettings);
        btGetPrinterSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPrinterSettingsButtonOnClick();

            }
        });

        Button btSetPrinterSettings = (Button) findViewById(R.id.btSetPrinterSettings);
        btSetPrinterSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPrinterSettingsButtonOnClick();

            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        updateValue();

        mList = Arrays.asList(PrinterInfo.PrinterSettingItem.PRINT_SPEED,
                PrinterInfo.PrinterSettingItem.PRINT_DENSITY,
                PrinterInfo.PrinterSettingItem.PRINT_JPEG_SCALE,
                PrinterInfo.PrinterSettingItem.PRINT_JPEG_HALFTONE,
                PrinterInfo.PrinterSettingItem.PRINTER_POWEROFFTIME_BATTERY,
                PrinterInfo.PrinterSettingItem.PRINTER_POWEROFFTIME);


    }

    private void updateValue() {

        setPreferenceValue("print_jpeg_halftone");
        String modelName = sharedPreferences.getString("printerModel", "");
        if (modelName.contains("RJ")) {
            setPreferenceValue("rj_print_density");
            ListPreference printerValuePreference = (ListPreference) getPreferenceScreen()
                    .findPreference("print_density");
            ((PreferenceGroup) findPreference("printer_setting_category")).removePreference(printerValuePreference);
        } else {
            setPreferenceValue("print_density");
            ListPreference printerValuePreference = (ListPreference) getPreferenceScreen()
                    .findPreference("rj_print_density");
            ((PreferenceGroup) findPreference("printer_setting_category")).removePreference(printerValuePreference);
        }
        setPreferenceValue("print_jpeg_scale");
        setPreferenceValue("print_speed");
        setEditValue("printer_power_off_time_battery");
        setEditValue("printer_power_off_time");
    }

    protected Map<PrinterInfo.PrinterSettingItem, String> createSettingsMap() {

        Map<PrinterInfo.PrinterSettingItem, String> settings = new HashMap<PrinterInfo.PrinterSettingItem, String>();

        settings.put(PrinterInfo.PrinterSettingItem.PRINTER_POWEROFFTIME,
                sharedPreferences.getString("printer_power_off_time", ""));
        settings.put(PrinterInfo.PrinterSettingItem.PRINTER_POWEROFFTIME_BATTERY,
                sharedPreferences.getString("printer_power_off_time_battery", ""));
        settings.put(PrinterInfo.PrinterSettingItem.PRINT_JPEG_HALFTONE,
                sharedPreferences.getString("print_jpeg_halftone", ""));
        settings.put(PrinterInfo.PrinterSettingItem.PRINT_JPEG_SCALE,
                sharedPreferences.getString("print_jpeg_scale", ""));

        String modelName = sharedPreferences.getString("printerModel", "");
        if (modelName.contains("RJ")) {
            settings.put(PrinterInfo.PrinterSettingItem.PRINT_DENSITY,
                    sharedPreferences.getString("rj_print_density", ""));
        } else {
            settings.put(PrinterInfo.PrinterSettingItem.PRINT_DENSITY,
                    sharedPreferences.getString("print_density", ""));
        }
        settings.put(PrinterInfo.PrinterSettingItem.PRINT_SPEED,
                sharedPreferences.getString("print_speed", ""));
        return settings;

    }

    protected void saveSettings(Map<PrinterInfo.PrinterSettingItem, String> settings) {

        for (PrinterInfo.PrinterSettingItem str : settings.keySet()) {
            switch (str) {

                case PRINTER_POWEROFFTIME:
                    setEditValue("printer_power_off_time", settings.get(str));
                    break;
                case PRINTER_POWEROFFTIME_BATTERY:
                    setEditValue("printer_power_off_time_battery", settings.get(str));
                    break;
                case PRINT_JPEG_HALFTONE:
                    setPreferenceValue("print_jpeg_halftone", settings.get(str));
                    break;
                case PRINT_JPEG_SCALE:
                    setPreferenceValue("print_jpeg_scale", settings.get(str));
                    break;

                case PRINT_DENSITY:
                    String modelName = sharedPreferences.getString("printerModel", "");
                    if (modelName.contains("RJ")) {
                        setPreferenceValue("rj_print_density", settings.get(str));
                    } else {
                        setPreferenceValue("print_density", settings.get(str));
                    }
                    break;
                case PRINT_SPEED:
                    setPreferenceValue("print_speed", settings.get(str));
                    break;

                default:
                    break;
            }
        }

    }
}
