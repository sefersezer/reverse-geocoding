package com.paritus.findnearestroad.structure;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.plaf.ListUI;

import org.opengis.metadata.spatial.Georectified;

import com.paritus.reversegeo.GeoRecord;
import com.paritus.reversegeo.GeoRecord.GeoRecordComparator;
import com.paritus.reversegeo.SpatialIndex;

public class Segment{
	private String id;
	private String name;
	
	private List<GeoRecord> geoRecords= new ArrayList<GeoRecord>();
	/**
	 * her segmentte 3 point var.
	 * bunların start,center ve end pointlerinin idleri aynı. 
	 * Segmentin de id değerini aynı yapıyoruz.
	 * @param id : uavtStreetCode
	 * @param p1 : start
	 * @param p2 : center
	 * @param p3 : end
	 */

	public Segment(ResultSet resultSet,SpatialIndex index) throws NumberFormatException, SQLException, IOException {
		id=resultSet.getString("uavtStreetCode");
		name = resultSet.getString("name");
		geoRecords.addAll(toPoint(resultSet, index, "startLongitude","startLatitute"));
		geoRecords.addAll(toPoint(resultSet, index,"centerLongitude","centerLatitute"));
		geoRecords.addAll(toPoint(resultSet, index, "endLongitude","endLatitute"));
	}

	private List<GeoRecord> toPoint(ResultSet resultSet, SpatialIndex index,String lon, String lat)
			throws IOException, SQLException {
		return index.searchNearest(Double.parseDouble(resultSet.getString(lon)), Double.parseDouble(resultSet.getString(lat)), 0.3);
	}

	/**
	 * Segmentin id değeri sıralama için ve yolların normalize nearest
	 * yollarını çekmek için kullanılıyor
	 * @return
	 */
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * Segmentin tüm non-normalize olan nearest geoRecordlarını getir
	 * @return
	 */
	public List<GeoRecord> getGeoRecords() {
		return geoRecords;
	}
	
	public static Comparator<Segment> SegmentIdComparator = new Comparator<Segment>() {
		public int compare(Segment s1, Segment s2) {
			return s1.getId().compareTo(s2.getId());
		}
	};
}