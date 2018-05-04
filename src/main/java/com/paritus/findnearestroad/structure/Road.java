package com.paritus.findnearestroad.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import com.paritus.reversegeo.GeoRecord;
import com.paritus.reversegeo.GeoRecord.GeoRecordComparator;

public class Road {
	private String id;
	private String name;
	private List<Segment> segments = new ArrayList<Segment>();
	private List<GeoRecord> nearestGeoRecords = new ArrayList<GeoRecord>();
	private List<GeoRecord> nearestNormalizeRoads = new ArrayList<GeoRecord>();
    final Logger logger = Logger.getLogger(Road.class);
    private String lastUsedId = "";
    /**
     * @param id : segmentin idsini alıyorum. o da point1den alıyor.
     * id değerini output verirken kullanacağım.
     * bir de checkSelfId() için kullanıyorum.
     */
    
    public Road(){}
    
    public Road(String id){
    	this.id = id;
    }
    
    public void setId(String id) {
		this.id = id;
	}
    
    /**
     * Road id şimdilik hiçbiyerde kullanılmadı. Fakat output için kullanılacaktır.
     * @return
     */
    public String getId(){
		return this.id;
	}
	
    public String getName(){
    	return name;
    }
	
    /**
     * @param segment : gelen segment değerini boş olarak tanımlanmış listeme ekliyorum.
     */
    public void addSegment(Segment segment) {
    	if(this.name==null){
    		this.name = segment.getName();
    	}
		segments.add(segment);
	}
	
    /**
     * TODO tüm segmentlerin nearest pointlerini indexten çek ve nearestPoints'e at
     * sıralama yapmak gerektiği için direkt olarak segmentten okumadım; ayrı bir listeye
     * atıp ordan sonra sıralayacağım.
     */
    public void setNearestGeoRecords() {
    	
		for(Segment currentSegment : segments){ // n adet segment
			nearestGeoRecords.addAll(currentSegment.getGeoRecords());
		}
		segments= new ArrayList<Segment>();
		sortNearestGeoRecords();
    }

    /**
     * bir yol için tüm normalize olmayan geoRecordlarını
     * self id hariç tutularak, ilk nearest'i alıp diğer büyük nearestleri almayarak
     * normalize edilmiş en yakın georecordları getiriyoruz. 
     * @return
     */
	public List<GeoRecord> getNearestNormalizeRoads() {
		return nearestNormalizeRoads;
	}
	
	public void setNearestNormalizeRoads(){
		setNearestGeoRecords();
		for(int i=0;i<nearestGeoRecords.size();i++){
			if(checkSelfId(nearestGeoRecords.get(i).getId())){
				nearestGeoRecords.set(i,null);
				continue;
			}
			
			if(nearestGeoRecords.get(i).getId().equals(lastUsedId)){
				nearestGeoRecords.set(i,null);
				continue;
			}
			else{
				nearestNormalizeRoads.add(nearestGeoRecords.get(i));
				lastUsedId=nearestGeoRecords.get(i).getId();
			}
		}
		nearestGeoRecords.clear();
	}

	private boolean checkSelfId(String id) {
		return this.id.equals(id);
	}
	
	/**
	 * geoRecordları, en yakını en üstte olmak üzere id ve uzaklığa göre sıralıyoruz.
	 * ilk farklılar nearest geoRecordlardır.
	 */
	private void sortNearestGeoRecords() {
		Collections.sort(this.nearestGeoRecords,
				GeoRecordComparator.ascending(GeoRecordComparator.getComparator(GeoRecordComparator.ID_SORT,GeoRecordComparator.DISTANCE_SORT)));
	}

}
