package com.gobletsoft.ptouchprintercontrol.printprocess;

import android.content.Context;

import com.gobletsoft.ptouchprintercontrol.common.MsgDialog;
import com.gobletsoft.ptouchprintercontrol.common.MsgHandle;

import java.util.ArrayList;

public class MultiImagePrint extends BasePrint {

    private ArrayList<String> mImageFiles;

    public MultiImagePrint(Context context, MsgHandle mHandle, MsgDialog mDialog) {
        super(context, mHandle, mDialog);
    }

    /**
     * set print data
     */
    public ArrayList<String> getFiles() {
        return mImageFiles;
    }

    /**
     * set print data
     */
    public void setFiles(ArrayList<String> files) {
        mImageFiles = files;
    }

    /**
     * do the particular print
     */
    @Override
    protected void doPrint() {
        mPrintResult = mPrinter.printFileList(mImageFiles);

    }
}
