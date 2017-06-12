/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image.explorer.importer;

import java.util.ArrayList;

/**
 *
 * @author short
 */
public class DialogResult {
    private ArrayList<String> tags;
    private double lat, lon;
    private String collectionName;
    
    
    public DialogResult()
    {
	tags = new ArrayList<>();
	lat = 9999;
	lon = 9999;
	collectionName = "";
    }

    public ArrayList<String> getTags() {
	return tags;
    }

    public double getLat() {
	return lat;
    }

    public double getLon() {
	return lon;
    }

    public void setTags(ArrayList<String> tags) {
	this.tags = tags;
    }

    public void setLat(double lat) {
	this.lat = lat;
    }

    public void setLon(double lon) {
	this.lon = lon;
    }

    public String getCollectionName() {
	return collectionName;
    }

    public void setCollectionName(String collectionName) {
	this.collectionName = collectionName;
    }
    
    
}
