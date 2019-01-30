package com.social.preserve.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PreferencesHelper {

	SharedPreferences sp;
	SharedPreferences.Editor editor;
	Context context;
	private String spName;
	public PreferencesHelper(Context c, String name) {
		context = c;
		spName=name;
		sp = context.getSharedPreferences(name, 0);
		editor = sp.edit();
		videoIDs=getVideoIdList();
		Log.d("pengtao", "PreferencesHelper: videoIDs "+videoIDs.size());
	}

	public void setValue(String key, String value) {
		editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public void setValue(String key, Integer value) {
		editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public String getValue(String key, String defaultValue) {
		return sp.getString(key, defaultValue);
	}

	public String getValue(String key) {
		return sp.getString(key, null);
	}

	public Integer getIntValue(String key) {
		return sp.getInt(key, 0);
	}

	public Integer getIntValue(String key,int def) {
		return sp.getInt(key, -1);
	}

	public Integer getIntValue2(String key) {
		return sp.getInt(key, 6);
	}

	public void setBooleanValue(String key, boolean value) {
		editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public boolean getBooleanValue(String key, boolean defaultValue) {
		return sp.getBoolean(key, defaultValue);
	}


	public void setLongValue(String key, long time) {
		editor = sp.edit();
		editor.putLong(key, time);
		editor.commit();
	}

	public long getLongValue(String key, long defaultValue) {
		return sp.getLong(key, defaultValue);
	}


	public void remove(String name) {
		editor.remove(name);
	}

	public void clear() {
		editor.clear();
		editor.commit();
	}

	Set<String> videoIDs = new HashSet<>();
	public Set<String> getCacheVideoIds(){
		return videoIDs;
	}
	public void setVideoIDs(String id){
		videoIDs.add(id);
		Log.d("pengtao", "setVideoIDs: "+videoIDs.size());
		editor = sp.edit();
		editor.putStringSet(spName+"videoids",videoIDs);
		editor.commit();
	}
	Set<String> videoPaths=new HashSet<>();
	public void setVideoPaths(String id,List<String> paths){
		videoPaths.clear();
		Set<String> tmp=new HashSet<>();
		for(String path:paths){
			tmp.add(path);
		}
		videoPaths.addAll(tmp);
		editor = sp.edit();
		editor.putStringSet(id,videoPaths);
		editor.commit();
	}
	public Set<String> getVideoPaths(String id){
		Set<String> pathList = sp.getStringSet(id,new HashSet<String>());
		return pathList;
	}
	public void setVideoIDs(Set<String> ids){
		videoIDs.clear();
		videoIDs.addAll(ids);
		editor = sp.edit();
		editor.putStringSet(spName+"videoids",videoIDs);
		editor.commit();
	}
	public Set<String> getVideoIdList() {

		Set<String> idList = sp.getStringSet(spName+"videoids",new HashSet<String>());
		return idList;
	}


}
