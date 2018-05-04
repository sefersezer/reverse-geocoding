package com.paritus.reversegeo.inf;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import com.paritus.reversegeo.GeoRecord;
import com.paritus.reversegeo.ShapeFileReader;
import com.vividsolutions.jts.geom.MultiLineString;

public class InfotechStreetShapeReader extends ShapeFileReader {

	@Override
	protected GeoRecord featureToGeoRecord(Feature feature) {
		
		String id = null;
		String name = null;
		String wkt = null;
		Property property =  feature.getProperty("MAHALLE_ID");
		if (property != null){
			id = property.getValue().toString();
		}
		property =  feature.getProperty("MAHALLE_ADI");
		if (property != null){
			name = property.getValue().toString();
		}
		property =  feature.getProperty("the_geom");
		if (property != null){
        	wkt =  ((MultiLineString)property.getValue()).toText();
		}
		return new GeoRecord(id, name, wkt);
	}

}