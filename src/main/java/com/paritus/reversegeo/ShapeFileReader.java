package com.paritus.reversegeo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;

public abstract class ShapeFileReader implements Iterator<GeoRecord>{

	@SuppressWarnings("rawtypes")
	private FeatureIterator iterator;
	
	
	@SuppressWarnings("deprecation")
	public void open(String inputFile) throws IOException{
        File shp = new File(inputFile);
        Map<String, URL> map = new HashMap<String, URL>();
        map.put("url",shp.toURL());
        DataStore dataStore = DataStoreFinder.getDataStore(map);
        String type = dataStore.getTypeNames()[0];
        @SuppressWarnings("rawtypes")
		FeatureSource source = dataStore.getFeatureSource( type );
        @SuppressWarnings("rawtypes")
		FeatureCollection collection = source.getFeatures();
        iterator = collection.features();
	}

	protected abstract GeoRecord featureToGeoRecord(Feature feature);

	
	public boolean hasNext() {
		if (iterator == null){
			throw new RuntimeException("File Closed!"); 
		}
		return iterator.hasNext();
	}

	public GeoRecord next() {
        if (iterator.hasNext()){
        	return featureToGeoRecord(iterator.next());
        }
		return null;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}