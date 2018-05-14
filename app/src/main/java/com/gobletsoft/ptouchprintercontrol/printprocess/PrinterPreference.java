package com.gobletsoft.ptouchprintercontrol.printprocess;

import android.content.Context;
import android.os.Message;

import com.brother.ptouch.sdk.PrinterInfo;
import com.brother.ptouch.sdk.PrinterStatus;
import com.gobletsoft.ptouchprintercontrol.R;
import com.gobletsoft.ptouchprintercontrol.common.Common;
import com.gobletsoft.ptouchprintercontrol.common.MsgDialog;
import com.gobletsoft.ptouchprintercontrol.common.MsgHandle;

import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrinterPreference extends BasePrint {

    private final static int COMMAND_SEND = 0;
    private final static int COMMAND_RECEIVE = 1;
    private final Context context;
    private List<PrinterInfo.PrinterSettingItem> mKey;
    private Map<PrinterInfo.PrinterSettingItem, String> mSettings;
    private int commandType = 0;
    private PrinterPreListener mListener = null;

    public PrinterPreference(Context context, MsgHandle mHandle,
                             MsgDialog mDialog) {
        super(context, mHandle, mDialog);
        this.context = context;
    }


    public void updatePrinterSetting(Map<PrinterInfo.PrinterSettingItem, String> settings) {
        mCancel = false;
        this.commandType = COMMAND_SEND;
        this.mSettings = settings;
        PrinterPrefThread pref = new PrinterPrefThread();
        pref.start();
    }

    /**
     * Getting the printer settings
     *
     * @param listener
     */
    public void getPrinterSetting(List<PrinterInfo.PrinterSettingItem> keys,
                                  PrinterPreListener listener) {
        if (listener == null) {
            mDialog.showAlertDialog(
                    context.getString(R.string.msg_title_warning),
                    context.getString(R.string.error_input));
            return;
        }
        mCancel = false;

        mListener = listener;
        this.mKey = keys;
        this.commandType = COMMAND_RECEIVE;
        PrinterPrefThread pref = new PrinterPrefThread();
        pref.start();
    }

    @Override
    protected void doPrint() {
    }

    public interface PrinterPreListener extends EventListener {

        void finish(PrinterStatus status,
                    Map<PrinterInfo.PrinterSettingItem, String> settings);
    }

    private class PrinterPrefThread extends Thread {

        @Override
        public void run() {

            // set info. for printing
            setPrinterInfo();

            // start message
            Message msg = mHandle.obtainMessage(Common.MSG_TRANSFER_START);
            mHandle.sendMessage(msg);
            mHandle.setFunction(MsgHandle.FUNC_SETTING);

            mPrintResult = new PrinterStatus();

            if (!mCancel) {

                if (commandType == COMMAND_SEND) {

                    mPrintResult = mPrinter.updatePrinterSettings(mSettings);

                } else if (commandType == COMMAND_RECEIVE) {

                    mSettings = new HashMap<PrinterInfo.PrinterSettingItem, String>();
                    mPrintResult = mPrinter.getPrinterSettings(mKey, mSettings);
                }
            } else {

                mPrintResult.errorCode = PrinterInfo.ErrorCode.ERROR_CANCEL;
            }

            if (mListener != null) {

                mListener.finish(mPrintResult, mSettings);
            }

            // end message
            mHandle.setResult(showResult());
            mHandle.setBattery(getBattery());

            msg = mHandle.obtainMessage(Common.MSG_DATA_SEND_END);
            mHandle.sendMessage(msg);
        }
    }
}
