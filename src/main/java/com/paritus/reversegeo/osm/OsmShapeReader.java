package com.paritus.reversegeo.osm;

import java.io.IOException;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;

import com.paritus.reversegeo.DbfReader;
import com.paritus.reversegeo.GeoRecord;
import com.paritus.reversegeo.ShapeFileReader;

public class OsmShapeReader extends ShapeFileReader {

	private DbfReader dbfReader;
 
	
	public void open(String inputFile) throws IOException{
		super.open(inputFile);
		dbfReader = new DbfReader();
		String dbfFile = inputFile.substring(0,inputFile.lastIndexOf('.')) + ".dbf";
		try {
			dbfReader.load(dbfFile);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected GeoRecord featureToGeoRecord(Feature feature) {
		
		String id = feature.getIdentifier().getID().replace(feature.getDescriptor().getLocalName() + ".", "");
		String name = dbfReader.getName(id);
		String wkt = null;
		Property property =  feature.getProperty("the_geom");
		if (property != null){
        	wkt =  property.getValue().toString();
		}
		return new GeoRecord(id, name, wkt);
	}

}