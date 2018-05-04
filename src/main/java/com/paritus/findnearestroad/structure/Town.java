package com.paritus.findnearestroad.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.opengis.metadata.spatial.Georectified;

import com.ao.etl.ExcelFileWriter;
import com.ao.etl.FileWriter;
import com.paritus.findnearestroad.MyApp;
import com.paritus.reversegeo.GeoRecord;

public class Town {
	private String name;
	private List<Segment> segmentList = new ArrayList<Segment>();
	private List<Road> roads = new ArrayList<Road>();
	private String tmp_loopId;
	private Road tmpRoad = new Road();
	static final Logger logger = Logger.getLogger(Town.class);
	/**
	 * yeni bir town yaratırken adına göre yaratma seçeneğini kullandık
	 * eğer istenirse town id sine göre, eğer istenirse
	 * uavttowncode a göre de key olarak kullanılabilir
	 * @param townName
	 */
	public Town(String townName){
		name=townName;
	}

	public String getName() {
		return name;
	}

	/***
	 * ham olarak veritabanından alınan (her satır segment olarak adlandırıldı) satırlar
	 * buraya sırasız olarak yükleniyor.
	 * sortSegments(); ile uavtStreetCode'una göre ascending sıralanıyor.
	 * @param segmentList
	 */
	public void fillSegments(List<Segment> segmentList) {
		this.segmentList =segmentList;
		Collections.sort(this.segmentList,Segment.SegmentIdComparator);
	}

		
	/***
	 * buradaki segmentlist, bu fonksiyon kullanılmadan önce sıralanmış olmalı.
	 * sıra ile her bir uavtStreetCode değişiminde yeni bir Road nesnesi yaratılıyor.
	 * Aynı koda sahip Road'lar için segmentleri yükleniyor.
	 * 
	 * loop sonunda her bir Road'ın nearestRoad'ları normalize olarak set ediliyor.
	 */
	public void setAllRoadsNearestRoads() {
		logger.info("segmentlerden yollar oluşturuluyor.");
		tmp_loopId ="nothing";
		for(int i=0;i< segmentList.size();i++){
			if(!segmentList.get(i).getId().equals(tmp_loopId)){
				tmp_loopId=segmentList.get(i).getId();
				tmpRoad= new Road(tmp_loopId);
				tmpRoad.addSegment(segmentList.get(i));
				roads.add(tmpRoad);
			}
			else{
				roads.get(roads.size()-1).addSegment(segmentList.get(i));
			}
		}
		segmentList.clear();
		logger.info("segmentlerden yollar oluşturuldu.");
		normalizeAllRoadsNearests();
	}
			
	/***
	 * Bu towndaki her bir road için
	 * road'ların içindeki her bir segmentin
	 * öncelikle en yakın georecordlarını bul
	 * sonra nearestRoads olarak, en yakın georecord'ların normalizeHalini {tekrarsız, en yakın, kendini barındırmayan}
	 * georecordlarını al => road olarak ekle.
	 */
	private void normalizeAllRoadsNearests(){
		logger.info("segmentleri doldurulmuş yollar normalize ediliyor.");
		for(Road road : roads){
			road.setNearestNormalizeRoads();
		}
		logger.info("segmentleri doldurulmuş yollar normalize edildi.");
	}
	
	public List<Road> getAllRoads(){
		return roads;
	}
} 
