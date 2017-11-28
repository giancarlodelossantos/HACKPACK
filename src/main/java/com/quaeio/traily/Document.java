package com.quaeio.traily;

/**
 * Created by simeon.garcia on 11/28/2017.
 */

public class Document {

    private String _mDocumentNo;
    private String _mDate;
    private String _mTime;

    public String getDocumentNo()
    {
        //getting the userID variable instance
        return _mDocumentNo;
    }
    public void setDocumentNo(String value)
    {
        //setting the userID variable value
        this._mDocumentNo = value;
    }

    public String getDate()
    {
        //getting the userID variable instance
        return _mDate;
    }
    public void setDate(String value)
    {
        //setting the userID variable value
        this._mDate = value;
    }

    public String getTime()
    {
        //getting the userID variable instance
        return _mTime;
    }
    public void setTime(String value)
    {
        //setting the userID variable value
        this._mTime = value;
    }
}
