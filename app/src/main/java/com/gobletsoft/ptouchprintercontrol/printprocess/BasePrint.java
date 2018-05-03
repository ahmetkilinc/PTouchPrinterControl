package com.gobletsoft.ptouchprintercontrol.printprocess;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Message;
import android.preference.PreferenceManager;

import com.brother.ptouch.sdk.LabelInfo;
import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.brother.ptouch.sdk.PrinterStatus;
import com.gobletsoft.ptouchprintercontrol.R;
import com.gobletsoft.ptouchprintercontrol.common.Common;
import com.gobletsoft.ptouchprintercontrol.common.MsgDialog;
import com.gobletsoft.ptouchprintercontrol.common.MsgHandle;

public abstract class BasePrint {

    static Printer mPrinter;
    static boolean mCancel;
    final MsgHandle mHandle;
    final MsgDialog mDialog;
    private final SharedPreferences sharedPreferences;
    private final Context mContext;
    PrinterStatus mPrintResult;
    private String customSetting;
    private PrinterInfo mPrinterInfo;

    BasePrint(Context context, MsgHandle handle, MsgDialog dialog) {

        mContext = context;
        mDialog = dialog;
        mHandle = handle;
        mDialog.setHandle(mHandle);
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        mCancel = false;
        // initialization for print
        mPrinterInfo = new PrinterInfo();
        mPrinter = new Printer();
        mPrinterInfo = mPrinter.getPrinterInfo();
        mPrinter.setMessageHandle(mHandle, Common.MSG_SDK_EVENT);
    }

    public static void cancel() {
        if (mPrinter != null)
            mPrinter.cancel();
        mCancel = true;
    }

    protected abstract void doPrint();

    /**
     * set PrinterInfo
     */
    public void setPrinterInfo() {

        getPreferences();
        setCustomPaper();
        mPrinter.setPrinterInfo(mPrinterInfo);
        if (mPrinterInfo.port == PrinterInfo.Port.USB) {
            while (true) {
                if (Common.mUsbRequest != 0)
                    break;
            }
            if (Common.mUsbRequest != 1) {
            }
        }
    }

    /**
     * get PrinterInfo
     */
    public PrinterInfo getPrinterInfo() {
        getPreferences();
        return mPrinterInfo;
    }

    /**
     * get Printer
     */
    public Printer getPrinter() {

        return mPrinter;
    }

    /**
     * get Printer
     */
    public PrinterStatus getPrintResult() {
        return mPrintResult;
    }

    /**
     * get Printer
     */
    public void setPrintResult(PrinterStatus printResult) {
        mPrintResult = printResult;
    }

    public void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {

        mPrinter.setBluetooth(bluetoothAdapter);
    }

    @TargetApi(12)
    public UsbDevice getUsbDevice(UsbManager usbManager) {
        return mPrinter.getUsbDevice(usbManager);
    }

    /**
     * get the printer settings from the SharedPreferences
     */
    private void getPreferences() {
        if (mPrinterInfo == null) {
            mPrinterInfo = new PrinterInfo();
            return;
        }
        String input;
        mPrinterInfo.printerModel = PrinterInfo.Model.valueOf(sharedPreferences
                .getString("printerModel", ""));
        mPrinterInfo.port = PrinterInfo.Port.valueOf(sharedPreferences
                .getString("port", ""));
        mPrinterInfo.ipAddress = sharedPreferences.getString("address", "");
        mPrinterInfo.macAddress = sharedPreferences.getString("macAddress", "");
        if (isLabelPrinter(mPrinterInfo.printerModel)) {
            mPrinterInfo.paperSize = PrinterInfo.PaperSize.CUSTOM;
            switch (mPrinterInfo.printerModel) {
                case QL_710W:
                case QL_720NW:
                case QL_800:
                case QL_810W:
                case QL_820NWB:
                    mPrinterInfo.labelNameIndex = LabelInfo.QL700.valueOf(
                            sharedPreferences.getString("paperSize", "")).ordinal();
                    mPrinterInfo.isAutoCut = Boolean.parseBoolean(sharedPreferences
                            .getString("autoCut", ""));
                    mPrinterInfo.isCutAtEnd = Boolean
                            .parseBoolean(sharedPreferences.getString("endCut", ""));
                    break;
                case QL_1100:
                case QL_1110NWB:
                    mPrinterInfo.labelNameIndex = LabelInfo.QL1100.valueOf(
                            sharedPreferences.getString("paperSize", "")).ordinal();
                    mPrinterInfo.isAutoCut = Boolean.parseBoolean(sharedPreferences
                            .getString("autoCut", ""));
                    mPrinterInfo.isCutAtEnd = Boolean
                            .parseBoolean(sharedPreferences.getString("endCut", ""));
                    break;
                case QL_1115NWB:
                    mPrinterInfo.labelNameIndex = LabelInfo.QL1115.valueOf(
                            sharedPreferences.getString("paperSize", "")).ordinal();
                    mPrinterInfo.isAutoCut = Boolean.parseBoolean(sharedPreferences
                            .getString("autoCut", ""));
                    mPrinterInfo.isCutAtEnd = Boolean
                            .parseBoolean(sharedPreferences.getString("endCut", ""));
                    break;
                case PT_E550W:
                case PT_E500:
                case PT_P750W:
                case PT_D800W:
                case PT_E800W:
                case PT_E850TKW:
                case PT_P900W:
                case PT_P950NW:
                    String paper = sharedPreferences.getString("paperSize", "");
                    mPrinterInfo.labelNameIndex = LabelInfo.PT.valueOf(paper)
                            .ordinal();
                    mPrinterInfo.isAutoCut = Boolean.parseBoolean(sharedPreferences
                            .getString("autoCut", ""));
                    mPrinterInfo.isCutAtEnd = Boolean
                            .parseBoolean(sharedPreferences.getString("endCut", ""));
                    mPrinterInfo.isHalfCut = Boolean.parseBoolean(sharedPreferences
                            .getString("halfCut", ""));
                    mPrinterInfo.isSpecialTape = Boolean
                            .parseBoolean(sharedPreferences.getString(
                                    "specialType", ""));
                    break;
                default:
                    break;
            }
        } else {
            mPrinterInfo.paperSize = PrinterInfo.PaperSize
                    .valueOf(sharedPreferences.getString("paperSize", ""));
        }
        mPrinterInfo.orientation = PrinterInfo.Orientation
                .valueOf(sharedPreferences.getString("orientation", ""));
        input = sharedPreferences.getString("numberOfCopies", "1");
        if (input.equals(""))
            input = "1";
        mPrinterInfo.numberOfCopies = Integer.parseInt(input);
        mPrinterInfo.halftone = PrinterInfo.Halftone.valueOf(sharedPreferences
                .getString("halftone", ""));
        mPrinterInfo.printMode = PrinterInfo.PrintMode
                .valueOf(sharedPreferences.getString("printMode", ""));
        mPrinterInfo.pjCarbon = Boolean.parseBoolean(sharedPreferences
                .getString("pjCarbon", ""));
        input = sharedPreferences.getString("pjDensity", "");
        if (input.equals(""))
            input = "5";
        mPrinterInfo.pjDensity = Integer.parseInt(input);
        mPrinterInfo.pjFeedMode = PrinterInfo.PjFeedMode
                .valueOf(sharedPreferences.getString("pjFeedMode", ""));
        mPrinterInfo.align = PrinterInfo.Align.valueOf(sharedPreferences
                .getString("align", ""));
        input = sharedPreferences.getString("leftMargin", "");
        if (input.equals(""))
            input = "0";
        mPrinterInfo.margin.left = Integer.parseInt(input);
        mPrinterInfo.valign = PrinterInfo.VAlign.valueOf(sharedPreferences
                .getString("valign", ""));
        input = sharedPreferences.getString("topMargin", "");
        if (input.equals(""))
            input = "0";
        mPrinterInfo.margin.top = Integer.parseInt(input);
        input = sharedPreferences.getString("customPaperWidth", "");
        if (input.equals(""))
            input = "0";
        mPrinterInfo.customPaperWidth = Integer.parseInt(input);

        input = sharedPreferences.getString("customPaperLength", "0");
        if (input.equals(""))
            input = "0";

        mPrinterInfo.customPaperLength = Integer.parseInt(input);
        input = sharedPreferences.getString("customFeed", "");
        if (input.equals(""))
            input = "0";
        mPrinterInfo.customFeed = Integer.parseInt(input);

        customSetting = sharedPreferences.getString("customSetting", "");
        mPrinterInfo.paperPosition = PrinterInfo.Align
                .valueOf(sharedPreferences.getString("paperPosition", "LEFT"));
        mPrinterInfo.dashLine = Boolean.parseBoolean(sharedPreferences
                .getString("dashLine", "false"));

        mPrinterInfo.rjDensity = Integer.parseInt(sharedPreferences.getString(
                "rjDensity", ""));
        mPrinterInfo.rotate180 = Boolean.parseBoolean(sharedPreferences
                .getString("rotate180", ""));
        mPrinterInfo.peelMode = Boolean.parseBoolean(sharedPreferences
                .getString("peelMode", ""));

        mPrinterInfo.mode9 = Boolean.parseBoolean(sharedPreferences.getString(
                "mode9", ""));
        mPrinterInfo.dashLine = Boolean.parseBoolean(sharedPreferences
                .getString("dashLine", ""));
        input = sharedPreferences.getString("pjSpeed", "2");
        mPrinterInfo.pjSpeed = Integer.parseInt(input);

        mPrinterInfo.pjPaperKind = PrinterInfo.PjPaperKind
                .valueOf(sharedPreferences.getString("pjPaperKind",
                        "PJ_CUT_PAPER"));

        mPrinterInfo.rollPrinterCase = PrinterInfo.PjRollCase
                .valueOf(sharedPreferences.getString("printerCase",
                        "PJ_ROLLCASE_OFF"));

        mPrinterInfo.skipStatusCheck = Boolean.parseBoolean(sharedPreferences
                .getString("skipStatusCheck", "false"));

        mPrinterInfo.checkPrintEnd = PrinterInfo.CheckPrintEnd
                .valueOf(sharedPreferences.getString("checkPrintEnd", "CPE_CHECK"));
        mPrinterInfo.printQuality = PrinterInfo.PrintQuality
                .valueOf(sharedPreferences.getString("printQuality",
                        "NORMAL"));
        mPrinterInfo.overwrite = Boolean.parseBoolean(sharedPreferences
                .getString("overwrite", "true"));

        mPrinterInfo.trimTapeAfterData = Boolean.parseBoolean(sharedPreferences
                .getString("trimTapeAfterData", "false"));

        input = sharedPreferences.getString("imageThresholding", "");
        if (input.equals(""))
            input = "127";
        mPrinterInfo.thresholdingValue = Integer.parseInt(input);

        input = sharedPreferences.getString("scaleValue", "");
        if (input.equals(""))
            input = "0";
        try {
            mPrinterInfo.scaleValue = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            mPrinterInfo.scaleValue = 1.0;
        }

        if (mPrinterInfo.printerModel == PrinterInfo.Model.TD_4000
                || mPrinterInfo.printerModel == PrinterInfo.Model.TD_4100N) {
            mPrinterInfo.isAutoCut = Boolean.parseBoolean(sharedPreferences
                    .getString("autoCut", ""));
            mPrinterInfo.isCutAtEnd = Boolean.parseBoolean(sharedPreferences
                    .getString("endCut", ""));
        }

        input = sharedPreferences.getString("savePrnPath", "");
        mPrinterInfo.savePrnPath = input;


        mPrinterInfo.workPath = sharedPreferences.getString("workPath", "");
        mPrinterInfo.softFocusing = Boolean.parseBoolean(sharedPreferences
                .getString("softFocusing", "false"));
        mPrinterInfo.enabledTethering = Boolean.parseBoolean(sharedPreferences
                .getString("enabledTethering", "false"));
        mPrinterInfo.rawMode = Boolean.parseBoolean(sharedPreferences
                .getString("rawMode", "false"));


        input = sharedPreferences.getString("processTimeout", "");
        if (input.equals(""))
            input = "0";
        mPrinterInfo.timeout.processTimeoutSec = Integer.parseInt(input);

        input = sharedPreferences.getString("sendTimeout", "");
        if (input.equals(""))
            input = "60";
        mPrinterInfo.timeout.sendTimeoutSec = Integer.parseInt(input);

        input = sharedPreferences.getString("receiveTimeout", "");
        if (input.equals(""))
            input = "180";
        mPrinterInfo.timeout.receiveTimeoutSec = Integer.parseInt(input);

        input = sharedPreferences.getString("connectionTimeout", "");
        if (input.equals(""))
            input = "0";
        mPrinterInfo.timeout.connectionWaitMSec = Integer.parseInt(input);

        input = sharedPreferences.getString("closeWaitTime", "");
        if (input.equals(""))
            input = "3";
        mPrinterInfo.timeout.closeWaitDisusingStatusCheckSec = Integer.parseInt(input);

    }

    /**
     * Launch the thread to print
     */
    public void print() {
        mCancel = false;
        PrinterThread printTread = new PrinterThread();
        printTread.start();
    }

    /**
     * Launch the thread to get the printer's status
     */
    public void getPrinterStatus() {
        mCancel = false;
        getStatusThread getTread = new getStatusThread();
        getTread.start();
    }

    /**
     * Launch the thread to print
     */
    public void sendFile() {


        SendFileThread getTread = new SendFileThread();
        getTread.start();
    }

    /**
     * set custom paper for RJ and TD
     */
    private void setCustomPaper() {

        switch (mPrinterInfo.printerModel) {
            case RJ_4030:
            case RJ_4030Ai:
            case RJ_4040:
            case RJ_3050:
            case RJ_3150:
            case TD_2020:
            case TD_2120N:
            case TD_2130N:
            case TD_4100N:
            case TD_4000:
            case RJ_2030:
            case RJ_2140:
            case RJ_2150:
            case RJ_2050:
            case RJ_3050Ai:
            case RJ_3150Ai:
                mPrinterInfo.customPaper = Common.CUSTOM_PAPER_FOLDER + customSetting;
                break;
            default:
                break;
        }
    }

    /**
     * get the end message of print
     */
    @SuppressWarnings("UnusedAssignment")
    public String showResult() {

        String result;
        if (mPrintResult.errorCode == PrinterInfo.ErrorCode.ERROR_NONE) {
            result = mContext.getString(R.string.error_message_none);
        } else {
            result = mPrintResult.errorCode.toString();
        }

        return result;
    }

    /**
     * show information of battery
     */
    public String getBattery() {

        String battery = "";
        if (mPrinterInfo.printerModel == PrinterInfo.Model.MW_260
                || mPrinterInfo.printerModel == PrinterInfo.Model.MW_260MFi) {
            if (mPrintResult.batteryLevel > 80) {
                battery = mContext.getString(R.string.battery_full);
            } else if (30 <= mPrintResult.batteryLevel
                    && mPrintResult.batteryLevel <= 80) {
                battery = mContext.getString(R.string.battery_middle);
            } else if (0 <= mPrintResult.batteryLevel
                    && mPrintResult.batteryLevel < 30) {
                battery = mContext.getString(R.string.battery_weak);
            }
        } else if (mPrinterInfo.printerModel == PrinterInfo.Model.RJ_4030
                || mPrinterInfo.printerModel == PrinterInfo.Model.RJ_4030Ai
                || mPrinterInfo.printerModel == PrinterInfo.Model.RJ_4040
                || mPrinterInfo.printerModel == PrinterInfo.Model.RJ_3050
                || mPrinterInfo.printerModel == PrinterInfo.Model.RJ_3150
                || mPrinterInfo.printerModel == PrinterInfo.Model.PT_E550W
                || mPrinterInfo.printerModel == PrinterInfo.Model.PT_E500
                || mPrinterInfo.printerModel == PrinterInfo.Model.PT_P750W
                || mPrinterInfo.printerModel == PrinterInfo.Model.TD_2020
                || mPrinterInfo.printerModel == PrinterInfo.Model.TD_2120N
                || mPrinterInfo.printerModel == PrinterInfo.Model.TD_2130N
                || mPrinterInfo.printerModel == PrinterInfo.Model.PJ_722
                || mPrinterInfo.printerModel == PrinterInfo.Model.PJ_723
                || mPrinterInfo.printerModel == PrinterInfo.Model.PJ_762
                || mPrinterInfo.printerModel == PrinterInfo.Model.PJ_763
                || mPrinterInfo.printerModel == PrinterInfo.Model.PJ_763MFi
                || mPrinterInfo.printerModel == PrinterInfo.Model.PJ_773
                || mPrinterInfo.printerModel == PrinterInfo.Model.PT_P900W
                || mPrinterInfo.printerModel == PrinterInfo.Model.PT_P950NW
                || mPrinterInfo.printerModel == PrinterInfo.Model.PT_E850TKW
                || mPrinterInfo.printerModel == PrinterInfo.Model.PT_E800W
                || mPrinterInfo.printerModel == PrinterInfo.Model.PT_D800W
                || mPrinterInfo.printerModel == PrinterInfo.Model.QL_800
                || mPrinterInfo.printerModel == PrinterInfo.Model.QL_810W
                || mPrinterInfo.printerModel == PrinterInfo.Model.QL_820NWB
                || mPrinterInfo.printerModel == PrinterInfo.Model.RJ_2030
                || mPrinterInfo.printerModel == PrinterInfo.Model.RJ_2050
                || mPrinterInfo.printerModel == PrinterInfo.Model.RJ_2140
                || mPrinterInfo.printerModel == PrinterInfo.Model.RJ_2150
                || mPrinterInfo.printerModel == PrinterInfo.Model.RJ_3050Ai
                || mPrinterInfo.printerModel == PrinterInfo.Model.RJ_3150Ai
                ) {
            switch (mPrintResult.batteryLevel) {
                case 0:
                    battery = mContext.getString(R.string.battery_full);
                    break;
                case 1:
                    battery = mContext.getString(R.string.battery_middle);
                    break;
                case 2:
                    battery = mContext.getString(R.string.battery_weak);
                    break;
                case 3:
                    battery = mContext.getString(R.string.battery_charge);
                    break;
                case 4:
                    battery = mContext.getString(R.string.ac_adapter);
                    break;
                default:
                    break;
            }
        } else {
            switch (mPrintResult.batteryLevel) {
                case 0:
                    battery = mContext.getString(R.string.ac_adapter);
                    break;
                case 1:
                    battery = mContext.getString(R.string.battery_weak);
                    break;
                case 2:
                    battery = mContext.getString(R.string.battery_middle);
                    break;
                case 3:
                    battery = mContext.getString(R.string.battery_full);
                    break;
                default:
                    break;
            }
        }
        if (mPrintResult.errorCode != PrinterInfo.ErrorCode.ERROR_NONE)
            battery = "";
        return battery;
    }

    private boolean isLabelPrinter(PrinterInfo.Model model) {
        switch (model) {
            case QL_710W:
            case QL_720NW:
            case PT_E550W:
            case PT_E500:
            case PT_P750W:
            case PT_D800W:
            case PT_E800W:
            case PT_E850TKW:
            case PT_P900W:
            case PT_P950NW:
            case QL_810W:
            case QL_800:
            case QL_820NWB:
            case QL_1100:
            case QL_1110NWB:
            case QL_1115NWB:
                return true;
            default:
                return false;
        }
    }

    /**
     * Thread for printing
     */
    private class PrinterThread extends Thread {
        @Override
        public void run() {

            // set info. for printing
            setPrinterInfo();

            // start message
            Message msg = mHandle.obtainMessage(Common.MSG_PRINT_START);
            mHandle.sendMessage(msg);

            mPrintResult = new PrinterStatus();

            mPrinter.startCommunication();
            if (!mCancel) {
                doPrint();
            } else {
                mPrintResult.errorCode = PrinterInfo.ErrorCode.ERROR_CANCEL;
            }
            mPrinter.endCommunication();

            // end message
            mHandle.setResult(showResult());
            mHandle.setBattery(getBattery());
            msg = mHandle.obtainMessage(Common.MSG_PRINT_END);
            mHandle.sendMessage(msg);
        }
    }

    /**
     * Thread for getting the printer's status
     */
    private class getStatusThread extends Thread {
        @Override
        public void run() {

            // set info. for printing
            setPrinterInfo();

            // start message
            Message msg = mHandle.obtainMessage(Common.MSG_PRINT_START);
            mHandle.sendMessage(msg);

            mPrintResult = new PrinterStatus();
            if (!mCancel) {
                mPrintResult = mPrinter.getPrinterStatus();
            } else {
                mPrintResult.errorCode = PrinterInfo.ErrorCode.ERROR_CANCEL;
            }
            // end message
            mHandle.setResult(showResult());
            mHandle.setBattery(getBattery());
            msg = mHandle.obtainMessage(Common.MSG_PRINT_END);
            mHandle.sendMessage(msg);

        }
    }

    /**
     * Thread for getting the printer's status
     */
    private class SendFileThread extends Thread {
        @Override
        public void run() {

            // set info. for printing
            setPrinterInfo();

            // start message
            Message msg = mHandle.obtainMessage(Common.MSG_PRINT_START);
            mHandle.sendMessage(msg);

            mPrintResult = new PrinterStatus();

            mPrinter.startCommunication();

            doPrint();

            mPrinter.endCommunication();
            // end message
            mHandle.setResult(showResult());
            mHandle.setBattery(getBattery());
            msg = mHandle.obtainMessage(Common.MSG_PRINT_END);
            mHandle.sendMessage(msg);

        }
    }
}
