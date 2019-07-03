package com.moses.miiread.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.moses.miiread.MApplication;

import java.util.Set;

public class PrefUtil {

    private static PrefUtil s_prefUtil;

    public static PrefUtil getInstance() {
        if (s_prefUtil == null)
            s_prefUtil = new PrefUtil();
        return s_prefUtil;
    }

    private PrefUtil() {
        initialize();
    }

    public void initialize() {
        if (m_sp != null)
            return;
        m_sp = MApplication.getInstance().getSharedPreferences("CONFIG", Context.MODE_PRIVATE);
    }

    public void release() {
        if (m_sp == null)
            return;
        m_sp = null;
    }

    public boolean isInitialized() {
        return m_sp != null;
    }

    public void setBoolean(String strKey, boolean b) {
        SharedPreferences.Editor edit = m_sp.edit();
        edit.putBoolean(strKey, b);
        edit.apply();
    }

    public void setInt(String strKey, int n) {
        SharedPreferences.Editor edit = m_sp.edit();
        edit.putInt(strKey, n);
        edit.apply();
    }

    public void setLong(String strKey, long l) {
        SharedPreferences.Editor edit = m_sp.edit();
        edit.putLong(strKey, l);
        edit.apply();
    }

    public void setFloat(String strKey, float f) {
        SharedPreferences.Editor edit = m_sp.edit();
        edit.putFloat(strKey, f);
        edit.apply();
    }

    public void setString(String strKey, String s) {
        SharedPreferences.Editor edit = m_sp.edit();
        edit.putString(strKey, s);
        edit.apply();
    }

    public void setStringSet(String strKey, Set<String> sSet) {
        SharedPreferences.Editor edit = m_sp.edit();
        edit.putStringSet(strKey, sSet);
        edit.apply();
    }

    public boolean getBoolean(String strKey, boolean bDefault) {
        return m_sp.getBoolean(strKey, bDefault);
    }

    public int getInt(String strKey, int nDefault) {
        return m_sp.getInt(strKey, nDefault);
    }

    public long getLong(String strKey, long lDefault) {
        return m_sp.getLong(strKey, lDefault);
    }

    public float getFloat(String strKey, float fDefault) {
        return m_sp.getFloat(strKey, fDefault);
    }

    public String getString(String strKey, String strDefault) {
        return m_sp.getString(strKey, strDefault);
    }

    public Set<String> getStringSet(String strKey, Set<String> sSetDefault) {
        return m_sp.getStringSet(strKey, sSetDefault);
    }

    public void remove(String strKey) {
        SharedPreferences.Editor edit = m_sp.edit();
        edit.remove(strKey);
        edit.apply();
    }

    public SharedPreferences getPref() {
        return m_sp;
    }

    private SharedPreferences m_sp;
}