package com.paritus.findnearestroad;

import static org.junit.Assert.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;
import com.paritus.findnearestroad.dbtool.MysqlInstance;
import com.paritus.findnearestroad.structure.Segment;
import com.paritus.findnearestroad.structure.Town;
import com.paritus.reversegeo.SpatialIndex;
import com.vividsolutions.jts.util.Assert;

public class TestClass {
	static final Logger logger = Logger.getLogger(TestClass.class);
	static SpatialIndex index = new SpatialIndex("data/Kadıköy");	
	
	/**
	 * release 2
	 * bir noktaya en yakın noktaları alabilirsin ama yapma.
	 * bir segment için 3 noktasının da yakın noktalarını al, bir listeye at.
	 * bunlar yakın roadların farklı/aynı noktalarını ifade edebilirler.
	 * normalize için bu aşamada işlem yapma
	 * sen bu aşamada -önce- mesafeye göre Collections.sort() kullanıp ilkleri alacaksın.
	 * name='' veya id değişiyorsa al, aynıysa continue yap. böylelikle normalize etmiş olacaksın.
	 * ilk release ile aynı sonuçları vermesi lazım.
	 * @throws Exception
	 */
	//@Test
	public void test_normalizeRoad_release2() throws Exception {
	/*	setLog4j();
		List<GeoRecord> roadList = new ArrayList<GeoRecord>();
		Point point1 = new Point(index,  29.10184, 40.96541);
		Point point2 = new Point(index,  29.10235, 40.96518);
		Point point3 = new Point(index,  29.10286, 40.96496);
		Point point4 = new Point(index, 29.10370, 40.96464);
		Point point5 = new Point(index, 29.10375, 40.96469);
		Point point6 = new Point(index,  29.10380, 40.96474);
		Point point7 = new Point(index,  29.10360, 40.96463);
		Point point8 = new Point(index,  29.10365, 40.96463);
		Point point9 = new Point(index,  29.10370, 40.96464);					
		Point otherpoint = new Point(index,  29.06518,40.98148);
		Segment segment1 = new Segment("196325","Şebnem Sokak",point1,point2,point3);
		Segment segment2 = new Segment("196325","Şebnem Sokak",point4,point5,point6);
		Segment segment3 = new Segment("196325","Şebnem Sokak",point7,point8,point9);
		Road roadSebnem = new Road("196325");
		roadSebnem.addSegment(segment1);
		roadSebnem.addSegment(segment2);
		roadSebnem.addSegment(segment3);
		roadSebnem.setNearestGeoRecords();
		roadList= roadSebnem.getNearestNormalizeRoads();
		//self id filtered geo records list size :  1577
		assertEquals(1658, segment1.getGeoRecords().size() +segment2.getGeoRecords().size()+ segment3.getGeoRecords().size() );
		assertEquals(27, roadList.size());*/
	}
	
	private static void setLog4j() {
		PropertyConfigurator.configure("log4j.properties");
	}
	
	
	@Test
	public void test_townRoads() throws Exception {
		Town myTown = new Town("Kadıköy");
		System.out.println("işlemler başlıyor");
		List<Segment> allmytownSegments = getSegmentsFromMysql(myTown.getName());
		System.out.println("Veritabanından konak mahallesi çekildi. segmetler objeleri oluşturuldu."
				+ "segmentlerin pointleri oluşturuldu. pointlerin indexten nearestRoad'ları dolduruldu.");
//		assertNotNull(allKadikoySegments.get(0));
		myTown.fillSegments(allmytownSegments);
//		assertEquals(6078,townKadikoy.getSegments().size());
//		System.out.println("kadıkoyün ilk segmenti : " + townKadikoy.getSegments().get(0).getId());
		myTown.setAllRoadsNearestRoads();
//		assertNotNull(townKadikoy.getAllRoads().get(0));
//		for(Road normalizeRoad : townKadikoy.getAllRoads()){
//			System.out.println(normalizeRoad.getId() + " :  " + normalizeRoad.getNearestNormalizeRoads().size());
//		}
		
	}
	
	public static List<Segment> getSegmentsFromMysql(String townName) throws SQLException, NumberFormatException, IOException{
		MysqlInstance mysqlInstance = new MysqlInstance("select uavtStreetCode,name,startLongitude,startLatitute,centerLongitude,centerLatitute,endLongitude,endLatitute from includesuavt where town ='"+townName+"'");
		ResultSet resultSet= mysqlInstance.getResultSet();
		List<Segment> returnList = new ArrayList<Segment>();
		while(resultSet.next()){
	//		returnList.add(toSegment(resultSet,"uavtStreetCode","name","startLongitude","startLatitute","centerLongitude","centerLatitute","endLongitude","endLatitute"));
		}
		return returnList;
	}

//	private static Segment toSegment(ResultSet resultSet, String uavtStreetCode,
//			String name, String startLongitude, String startLatitute, String centerLongitude,
//			String centerLatitute, String endLongitude, String endLatitute) throws NumberFormatException, SQLException, IOException {
//		Point p1 = toPoint(index, startLongitude, startLatitute);
//		Point p2 = toPoint(resultSet, centerLongitude, centerLatitute);
//		Point p3 = toPoint(resultSet, endLongitude,endLatitute);
//		return new Segment(resultSet.getString(uavtStreetCode),resultSet.getString(name), p1,p2,p3);
//	}
	
	
	
	//@Test
	public void test_normalizeRoad_release2_ornek2() throws Exception {
		/*setLog4j();
		List<GeoRecord> roadList = new ArrayList<GeoRecord>();
		Point point1 = new Point(index, 29.03102, 40.98149);
		Point point2 = new Point(index, 29.03099, 40.98125);
		Point point3 = new Point(index, 29.03097, 40.98101);
		Point point4 = new Point(index, 29.03109, 40.98221);
		Point point5 = new Point(index, 29.03107, 40.98197);
		Point point6 = new Point(index, 29.03104, 40.98173);
		Point point7 = new Point(index, 29.03120, 40.98308);
		Point point8 = new Point(index, 29.03114, 40.98265);
		Point point9 = new Point(index, 29.03109, 40.98221);
		Point point10 = new Point(index, 29.03109, 40.98221);
		Point point11 = new Point(index, 29.03164, 40.98213);
		Point point12 = new Point(index, 29.03219, 40.98206);
		Point point13 = new Point(index, 29.03104, 40.981739);
		Point point14 = new Point(index, 29.03103, 40.981619);
		Point point15 = new Point(index, 29.03102, 40.98149);
		Segment segment1 = new Segment("210121","Şifa Sokak",point1,point2,point3);
		Segment segment2 = new Segment("210121","Şifa Sokak",point4,point5,point6);
		Segment segment3 = new Segment("210121","Şifa Sokak",point7,point8,point9);
		Segment segment4 = new Segment("210121","Şifa Sokak",point7,point8,point9);
		Segment segment5 = new Segment("210121","Şifa Sokak",point10,point11,point12);
		Segment segment6 = new Segment("210121","Şifa Sokak",point13,point14,point15);
		
		Road roadSifa = new Road("210121");
		roadSifa.addSegment(segment1);
		roadSifa.addSegment(segment2);
		roadSifa.addSegment(segment3);
		roadSifa.addSegment(segment4);
		roadSifa.addSegment(segment5);
		roadSifa.addSegment(segment6);
		roadSifa.setNearestGeoRecords();
		roadList= roadSifa.getNearestNormalizeRoads();
		for(GeoRecord georecord : roadList){
			logger.info(String.format("Normalize GeoRecord: %s %30s %s", georecord.getId(),georecord.getName(),georecord.getDistance()));
		}
		
		for(GeoRecord georecord : segment1.getGeoRecords()){
			logger.info(String.format("1Not-Normalized GeoRecord:%s:%30s:%s", georecord.getId(),georecord.getName(),georecord.getDistance()));
		}
		for(GeoRecord georecord : segment2.getGeoRecords()){
			logger.info(String.format("2Not-Normalized GeoRecord:%s:%30s:%s", georecord.getId(),georecord.getName(),georecord.getDistance()));
		}
		for(GeoRecord georecord : segment3.getGeoRecords()){
			logger.info(String.format("3Not-Normalized GeoRecord:%s:%30s:%s", georecord.getId(),georecord.getName(),georecord.getDistance()));
		}
		for(GeoRecord georecord : segment4.getGeoRecords()){
			logger.info(String.format("4Not-Normalized GeoRecord:%s:%30s:%s", georecord.getId(),georecord.getName(),georecord.getDistance()));
		}
		for(GeoRecord georecord : segment5.getGeoRecords()){
			logger.info(String.format("5Not-Normalized GeoRecord:%s:%30s:%s", georecord.getId(),georecord.getName(),georecord.getDistance()));
		}
		for(GeoRecord georecord : segment6.getGeoRecords()){
			logger.info(String.format("6Not-Normalized GeoRecord:%s:%30s:%s", georecord.getId(),georecord.getName(),georecord.getDistance()));
		}
		
		assertEquals(1912, segment1.getGeoRecords().size() +
				segment2.getGeoRecords().size() +
				segment3.getGeoRecords().size() +
				segment4.getGeoRecords().size() +
				segment5.getGeoRecords().size() +
				segment6.getGeoRecords().size() );
		assertEquals(26, roadList.size());
		*/
		/*
		 * excelden incelenen sonuçlar (27 - kendisi dahil)
208898	Doktor Esat Işık Caddesi	0.462502957969252
208901	Hasırcıbaşı Caddesi	220.9179658728838
209247	Soner Sokak	254.56540720615385
209260	Emin Onat Sokak	149.52819758384229
209267	İleri Sokak	259.2537675764561
209268	Hacı Ahmet Bey Sokak	295.6084569350958
209271	Gülşen Sokak	244.68503091034887
209276	Nezihe Gürbüz Sokak	149.52819758384229
210117	Şehit Cem Nuri Başgil Sokak	157.4170427204609
210120	Şevki Bey Sokak	206.37140563966034
210121	Şifa Sokak	0.15697315616458654
210143	Küçükmoda Burnu Sokak	272.87855436897274
210144	Profesör Vehbi Sarıdal Sokak	128.0513098698616
210149	Keresteci Aziz Sokak	117.8420648951292
210151	Safa Sokak	48.827806139189
210152	Şair Latifi Sokak	73.18838569092155
210157	Güneş Sokak	150.67899936128853
210158	Ahter Sokak	212.15130249665975
210164	Yenikapı Çıkmazı	0.2861312481974019
210605	Yoğurtçu Parkı Caddesi	107.87986909425854
210610	Profesör Vehbi Sarıdal Sokak	215.70348189374207
210620	Rahmeti Arat Sokak	253.9194937443733
862885	Cem Karaca Sokak	299.38497222585676
922834	Şair Latifi Sokak	103.61722200458645
938671	Saadettin Çıkmazı	0.44877291223923677
938690	Ulubatlı Sokak	247.71971602184772
938801	Yoğurtçu Parkı Caddesi	103.61722200458645
**************************************************
programdan gelen sonuçlar: aynı
 208898       Doktor Esat Işık Caddesi 0.462502957969252
 208901            Hasırcıbaşı Caddesi 220.9179658728838
 209247                    Soner Sokak 254.56540720615385
 209260                Emin Onat Sokak 149.52819758384229
 209267                    İleri Sokak 259.2537675764561
 209268           Hacı Ahmet Bey Sokak 295.6084569350958
 209271                   Gülşen Sokak 244.68503091034887
 209276            Nezihe Gürbüz Sokak 149.52819758384229
 210117    Şehit Cem Nuri Başgil Sokak 157.4170427204609
 210120                Şevki Bey Sokak 206.37140563966034
 210143          Küçükmoda Burnu Sokak 272.87855436897274
 210144   Profesör Vehbi Sarıdal Sokak 84.25168181371093
 210149           Keresteci Aziz Sokak 117.8420648951292
 210151                     Safa Sokak 48.827806139189
 210152              Şair Latifi Sokak 73.18838569092155
 210157                    Güneş Sokak 150.67899936128853
 210158                    Ahter Sokak 212.15130249665975
 210164               Yenikapı Çıkmazı 0.2861312481974019
 210605         Yoğurtçu Parkı Caddesi 107.87986909425854
 210610   Profesör Vehbi Sarıdal Sokak 215.70348189374207
 210620             Rahmeti Arat Sokak 253.9194937443733
 862885               Cem Karaca Sokak 299.38497222585676
 922834              Şair Latifi Sokak 103.61722200458645
 938671              Saadettin Çıkmazı 0.44877291223923677
 938690                 Ulubatlı Sokak 247.71971602184772
 938801         Yoğurtçu Parkı Caddesi 103.61722200458645

		 * */
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
