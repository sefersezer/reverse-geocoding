package com.paritus.findnearestroad;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.ao.etl.ExcelFileWriter;
import com.ao.etl.FileWriter;
import com.paritus.reversegeo.GeoRecord;
import com.paritus.reversegeo.SpatialIndex;
import com.paritus.findnearestroad.dbtool.MysqlInstance;
import com.paritus.findnearestroad.structure.Road;
import com.paritus.findnearestroad.structure.Segment;
import com.paritus.findnearestroad.structure.Town;

public class MyApp {
	
	static SpatialIndex index = new SpatialIndex("data/kadıköy");//testindexsefer
	static final Logger logger = Logger.getLogger(MyApp.class);
	
	private static FileWriter fw;
	private static final int staticColumn = 3;
	private static int iteration=0;
	
	public static void main(String[] args) throws IOException, NumberFormatException, SQLException {
		setLog4j();
		logger.info("işlem başlıyor");
		Town loopTown;
		List<Segment> allTownSegments;
		for(String city : getCityList()){
			for(String town : getTownList(city)){
				loopTown = new Town(town);
				indexData(town);
				allTownSegments = getSegmentsFromMysql(town);
				loopTown.fillSegments(allTownSegments);
				loopTown.setAllRoadsNearestRoads();
				System.out.println("Town Completed: " + town);
				allTownSegments.clear();
				writeAsFile(loopTown);
			}
		}
		
	}

	private static void indexData(String town) throws SQLException, IOException {
		logger.info("veritabanından veriler çekildi.");
		index= new SpatialIndex("data/"+town);
		index.createIndex(getCoordinatesFromMysql(town).iterator());
		logger.info("index oluşturuldu");
	}
	
/*
	private static void indexallData() throws SQLException, IOException {
		System.out.println("işlem başlıyor");
		singleTownPointList = getPointsFromMysql("");
		System.out.println("veritabanından veriler çekildi.");
		System.out.println("indexleme başlıyor : data/allindex");
		index.createIndex(points.iterator());
		System.out.println("indexleme tamamlandı: data/allindex");
	}
	*/

	private static List<GeoRecord> getCoordinatesFromMysql(String town) throws SQLException {
		logger.info(town + " için veritabanından resultset alınıyor.");
		List<GeoRecord> returnList = new ArrayList<GeoRecord>();
		MysqlInstance mi = new  MysqlInstance("select uavtStreetCode,name,startLongitude,startLatitute,centerLongitude,centerLatitute,endLongitude,endLatitute from includesuavt where town ='"+town+"'");
		ResultSet rs = mi.getResultSet();
		while(rs.next()){
			returnList.add(toGeoRecord(rs, "startLongitude", "startLatitute"));
			returnList.add(toGeoRecord(rs, "centerLongitude", "centerLatitute"));
			returnList.add(toGeoRecord(rs, "endLongitude", "endLatitute"));
		}
		logger.info(town + " için veritabanından resultset alındı.");
		return returnList;
	}
	
	private static GeoRecord toGeoRecord(ResultSet rs,String lon,String lat) throws SQLException{
		return new GeoRecord(rs.getString("uavtStreetCode"), rs.getString("name"), "POINT ("+rs.getString(lon)+" "+rs.getString(lat)+")");
	}

	/***
	 * 
	 * @return
	 * @throws SQLException
	 */
	private static List<String> getTownList(String city) throws SQLException {
		MysqlInstance mysqlInstance = new MysqlInstance("select town from includesuavt where city='"+city+"' group by town;");//  
		ResultSet resultSet= mysqlInstance.getResultSet();
		List<String> returnList = new ArrayList<String>();
		while(resultSet.next()){
			returnList.add(resultSet.getString("town"));
		}
		resultSet.close();
		return returnList;
	}
	
	private static List<String> getCityList() throws SQLException {
		MysqlInstance mysqlInstance = new MysqlInstance("select city from includesuavt group by city;");//  
		ResultSet resultSet= mysqlInstance.getResultSet();
		List<String> returnList = new ArrayList<String>();
		while(resultSet.next()){
			returnList.add(resultSet.getString("city"));
		}
		resultSet.close();
		return returnList;
	}

	private static void writeAsFile(Town town){
		logger.info(town.getName() + " için normalize roadlar dosyaya yazılıyor.");
		fw = new ExcelFileWriter("outputs\\"+ town.getName() + ".xlsx");
		List<Road> roads = town.getAllRoads();
		List<GeoRecord> nearestNormalizeRoads;
		Object[] excelRow;
		int i=0;
		for(Road road : roads){
			nearestNormalizeRoads = road.getNearestNormalizeRoads();
			excelRow = new Object[nearestNormalizeRoads.size()+staticColumn];
			excelRow[0] = town.getName();
			excelRow[1] = road.getId();
			excelRow[2] =road.getName();
			for(i=0;i<nearestNormalizeRoads.size();i++){
				excelRow[iteration + staticColumn] = nearestNormalizeRoads.get(i).getId() + ":" + nearestNormalizeRoads.get(i).getName() + ":" + nearestNormalizeRoads.get(i).getDistance();
				iteration++;
			}
			iteration=0;
			fw.writeLine(excelRow);
			excelRow=null;
		}
		fw.close();
		logger.info("normalize roadlar dosyaya yazıldı.");
	}
	
	private static void setLog4j() {
		PropertyConfigurator.configure("log4j.properties");
	}
	
	public static List<Segment> getSegmentsFromMysql(String townName) throws SQLException, NumberFormatException, IOException{
		MysqlInstance mysqlInstance = new MysqlInstance("select uavtStreetCode,name,startLongitude,startLatitute,centerLongitude,centerLatitute,endLongitude,endLatitute from includesuavt where town ='"+townName+"'");
		ResultSet resultSet= mysqlInstance.getResultSet();
		List<Segment> returnList = new ArrayList<Segment>();
		logger.info("segmentler ve pointler oluşturuluyor.");
		while(resultSet.next()){
			returnList.add(new Segment(resultSet,index));//,"uavtStreetCode","name","startLongitude","startLatitute","centerLongitude","centerLatitute","endLongitude","endLatitute"
		}
		logger.info("segmentler ve pointler oluşturuldu.");
		resultSet.close();
		return returnList;
	}
	
		
}
