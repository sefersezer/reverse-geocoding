package com.paritus.reversegeo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

public class IndexTest extends TestCase {

	public void testPoint() throws IOException {
		SpatialIndex index = new SpatialIndex("data/testindex");
		
		// point should be POINT(Lon Lat)
		List<GeoRecord> segments = new ArrayList<GeoRecord>(); 
		
		// first segment
		segments.add(new GeoRecord("223468", "Avni Dilligil Sokak", "POINT (29.00374 41.07089)"));
		segments.add(new GeoRecord("223468", "Avni Dilligil Sokak", "POINT (29.00379 41.07090)"));
		segments.add(new GeoRecord("223468", "Avni Dilligil Sokak", "POINT (29.00384 41.07091)"));

		// second segment
		segments.add(new GeoRecord("223468", "Avni Dilligil Sokak", "POINT (29.00145 41.06788)"));
		segments.add(new GeoRecord("223468", "Avni Dilligil Sokak", "POINT (29.00156 41.06819)"));
		segments.add(new GeoRecord("223468", "Avni Dilligil Sokak", "POINT (29.00170 41.06848 )"));

		index.createIndex(segments.iterator());
		
		List<GeoRecord>  res = index.searchNearest(29.50290, 40.07529, 1);
		Assert.assertEquals("should have been zero hits", 0, res.size());
		
		
		res = index.searchNearest(29.00218, 41.06952, 0);
		Assert.assertEquals("should have been zero hits", 0, res.size());

		res = index.searchNearest( 29.00218, 41.06952, 0.2);
		Assert.assertEquals("should have been 3 hits", 3, res.size());

		
		
		res = index.searchNearest( 29.00218, 41.06952, 0.3);
		Assert.assertEquals("should have been 6 hits", 6, res.size());
		
		
	}
	
}
