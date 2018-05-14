package com.gobletsoft.ptouchprintercontrol.printprocess;

import com.gobletsoft.ptouchprintercontrol.common.Common;

import java.util.Locale;

public class PrinterModelInfo {

    /**
     * Arrays of paper sizes.
     * Element 0 is default.
     * The PocketJet variant for Locale.US is the same as non-US
     * except the order changes to set a different default.
     */

    private static final String[] PS_PT = {"W3_5", "W6", "W9", "W12", "W18", "W24", "HS_W6", "HS_W9", "HS_W12", "HS_W18", "HS_W24"};

    /**
     * Arrays of printer ports.
     * Element 0 is default.
     */

    private static final String[] PORTS_NET_USB = {Common.NET, Common.USB};

    /**
     * String array containing the names of all printer models.
     */
    private static String[] model = null;

    /**
     * Get an array of the names of all printer models.
     *
     * @return array of names of all printer models
     */
    public static String[] getModelNames() {
        if (model != null) {
            return model;
        }
        // Lazy initialization
        Model[] models = Model.values();
        int count = models.length;
        String[] m = new String[count];
        for (int i = 0; i < count; ++i) {
            m[i] = models[i].name();
        }
        model = m;
        return model;
    }

    /**
     * Get the port or paper size information for a specific model.
     *
     * @param model printer model name
     * @param value type of information, either Common.SETTINGS_PORT or Common.SETTINGS_PAPERSIZE
     * @return the requested information, if input parameters are valid, otherwise return null
     */
    public static String[] getPortOrPaperSizeInfo(String model, String value) {
        String name = model.trim();
        Model m;
        String[] result = null;

        try {
            m = Model.valueOf(name);
            if (value.equalsIgnoreCase(Common.SETTINGS_PORT)) {
                result = m.getPorts();
            } else if (value.equalsIgnoreCase(Common.SETTINGS_PAPERSIZE)) {
                result = m.getPaperSizes();
            }
        } catch (IllegalArgumentException ignored) {
        }
        return result;
    }


    /**
     * All printer models.
     * Each model holds a list of supported communication ports and paper sizes.
     */
    public enum Model {

        PT_E550W(PORTS_NET_USB, PS_PT);

        private final String[] mPorts;
        private final String[] mPaperSizes;
        private final String[] mPaperSizesUS;

        Model(String[] ports, String[] paperSizes, String[] paperSizesUS) {
            mPorts = ports;
            mPaperSizes = paperSizes;
            mPaperSizesUS = paperSizesUS;
        }

        Model(String[] ports, String[] paperSizes) {
            this(ports, paperSizes, null);
        }

        Model(Model alias) {
            this(alias.mPorts, alias.mPaperSizes, alias.mPaperSizesUS);
        }

        public String[] getPorts() {
            return mPorts;
        }

        public String[] getPaperSizes() {
            return (Locale.getDefault().equals(Locale.US) && (mPaperSizesUS != null))
                    ? mPaperSizesUS : mPaperSizes;
        }

        public String getDefaultPaperSize() {
            String[] paperSizes = getPaperSizes();
            String paperSize = (paperSizes != null) ? paperSizes[0] : "";
            return (paperSize != null) ? paperSize : "";
        }
    } // enum Model
}
