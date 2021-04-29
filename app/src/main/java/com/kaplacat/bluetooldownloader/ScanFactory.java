package com.kaplacat.bluetooldownloader;

import android.bluetooth.le.ScanResult;

import com.kaplacat.bluetooldownloader.Models.TagBase;
import com.kaplacat.bluetooldownloader.Models.TagTemperature;

import java.util.HashMap;

public class ScanFactory
{
    private static ScanFactory instance = null;
    private HashMap<String, TagBase> TagList = new HashMap<>();

    /**
     * Instance of the scanner Factory
     * @return instance
     */
    public static ScanFactory getInstance()
    {
        if(null == instance)
            instance = new ScanFactory();
        return instance;
    }

    /**
     * Create tags objects from the scan result
     * @param scanResult Scan result given by the Android library
     */
    void buildScan(ScanResult scanResult)
    {
        if(scanResult == null) {return;}

        TagBase tag = null;
        if(ValidLe.isElaLe(scanResult.getScanRecord()).equals(eTagType.TEMPERATURE))
        {
            tag = new TagTemperature(scanResult);
        }

        if(null == tag) {return;}
        this.TagList.put(tag.getName(),tag);
    }


    /**
     * Clear tag list and tag name list
     */
    public void clearList()
    {
        this.TagList.clear();
    }

    public HashMap<String,TagBase> getTagNameList()
    {
        return this.TagList;
    }
}
