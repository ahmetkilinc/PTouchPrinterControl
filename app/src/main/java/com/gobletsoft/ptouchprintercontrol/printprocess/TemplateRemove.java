package com.gobletsoft.ptouchprintercontrol.printprocess;

import android.content.Context;
import android.os.Message;

import com.brother.ptouch.sdk.PrinterInfo;
import com.brother.ptouch.sdk.PrinterStatus;
import com.brother.ptouch.sdk.TemplateInfo;
import com.gobletsoft.ptouchprintercontrol.common.Common;
import com.gobletsoft.ptouchprintercontrol.common.MsgDialog;
import com.gobletsoft.ptouchprintercontrol.common.MsgHandle;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class TemplateRemove extends BasePrint {

    private final static int COMMAND_REMOVE = 0;
    private final static int COMMAND_GET_LIST = 1;

    private TemplateRemoveListener mListener = null;

    public TemplateRemove(Context context, MsgHandle mHandle, MsgDialog mDialog) {
        super(context, mHandle, mDialog);
    }

    /**
     * Template list are acquired by the printer
     *
     * @param listener The results are reported in listener
     */
    public void getTemplateList(TemplateRemoveListener listener) {

        this.mListener = listener;
        mCancel = false;
        RemoveThread pref = new RemoveThread();
        pref.getTemplateList();
    }

    /**
     * Template list are removed by the printer
     *
     * @param key      key of removed tempaltes
     * @param listener The results are reported in listener
     */
    public void removeTemplate(List<Integer> key,
                               TemplateRemoveListener listener) {
        this.mListener = listener;
        mCancel = false;
        RemoveThread pref = new RemoveThread();
        pref.removeTemplate(key);
    }

    @Override
    protected void doPrint() {
    }

    public interface TemplateRemoveListener extends EventListener {
        void finish(PrinterStatus status, List<TemplateInfo> tempList);
    }

    private class RemoveThread extends Thread {
        private List<TemplateInfo> mTempList;
        private int mCommandType = -1;
        private List<Integer> mKey;

        public void getTemplateList() {
            this.mCommandType = COMMAND_GET_LIST;
            mTempList = new ArrayList<TemplateInfo>();
            start();
        }

        public void removeTemplate(List<Integer> keys) {
            this.mCommandType = COMMAND_REMOVE;
            mTempList = new ArrayList<TemplateInfo>();
            this.mKey = keys;
            start();
        }

        @Override
        public void run() {

            setPrinterInfo();

            Message msg = mHandle.obtainMessage(Common.MSG_TRANSFER_START);
            mHandle.sendMessage(msg);
            mHandle.setFunction(MsgHandle.FUNC_TRANSFER);
            mPrintResult = new PrinterStatus();
            if (!mCancel) {
                if (mCommandType == COMMAND_GET_LIST) {
                    mPrintResult = mPrinter.getTemplateList(mTempList);

                } else if (mCommandType == COMMAND_REMOVE) {
                    mPrinter.startCommunication();
                    if (!mCancel) {
                        mPrintResult = mPrinter.removeTemplate(mKey);
                    } else {
                        mPrintResult.errorCode = PrinterInfo.ErrorCode.ERROR_CANCEL;
                    }
                    if (!mCancel && mPrintResult.errorCode == PrinterInfo.ErrorCode.ERROR_NONE) {
                        mPrintResult = mPrinter.getTemplateList(mTempList);
                    } else if (mCancel && mPrintResult.errorCode == PrinterInfo.ErrorCode.ERROR_NONE) {
                        mPrintResult.errorCode = PrinterInfo.ErrorCode.ERROR_CANCEL;
                    }
                    mPrinter.endCommunication();
                }
            } else {
                mPrintResult.errorCode = PrinterInfo.ErrorCode.ERROR_CANCEL;
            }

            if (mListener != null) {
                mListener.finish(mPrintResult, mTempList);
            }

            // end message
            mHandle.setResult(showResult());
            mHandle.setBattery(getBattery());

            msg = mHandle.obtainMessage(Common.MSG_DATA_SEND_END);
            mHandle.sendMessage(msg);
        }
    }
}
