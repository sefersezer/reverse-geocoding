package com.paritus.reversegeo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.spatial.SpatialStrategy;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.spatial4j.core.context.SpatialContext;
import com.spatial4j.core.context.jts.JtsSpatialContext;
import com.spatial4j.core.distance.DistanceUtils;
import com.spatial4j.core.shape.Point;
import com.spatial4j.core.shape.Shape;

public class SpatialIndex {
	
	private static final String FIELDID = "id";
	private static final String FIELDNAME = "name";
	private static final String FIELDGEO = "geo";
	
	private SpatialContext spatialContext;
    private RecursivePrefixTreeStrategy spatialStrategy;

    private SpatialContext polygonCtx;
    private RecursivePrefixTreeStrategy polygonStrategy;
    
    
    private String indexPath;
    private IndexSearcher searcher;
    private int indexedCount;
    private long startTime;
    private long currentStartTime;
    private static int logEvery  = 1000;
    
    public SpatialIndex(String indexPath)  {
    	this(indexPath,10,0.000);
    }
    
    public SpatialIndex(String indexPath,int maxLevels,double distErrPct)  {
 
    	spatialContext=  SpatialContext.GEO;
        spatialStrategy= new RecursivePrefixTreeStrategy(new GeohashPrefixTree(spatialContext,maxLevels),FIELDGEO);
        spatialStrategy.setDistErrPct(distErrPct);
        
        polygonCtx =  JtsSpatialContext.GEO;
        polygonStrategy = new RecursivePrefixTreeStrategy(new GeohashPrefixTree(polygonCtx,maxLevels),FIELDGEO);
        polygonStrategy.setDistErrPct(distErrPct);
        this.indexPath = indexPath;
    }
	
    private List<String> toShortLineStrings(String wkt){

    	List<String> result = new ArrayList<String>();
    	wkt = wkt.replace("MULTILINESTRING ((", "").replace("))", "");
    	String[] lines = null;
    	if (wkt.indexOf(')') != -1){
			lines =  wkt.split("\\), \\(");
		}else{
			lines = new String[1];
			lines[0] = wkt;
		}
		for (int i = 0; i < lines.length; i++) {
			String coorda[]  = lines[i].split(", ");
			for (int j = 0; j < coorda.length-1; j++) {
				result.add("LINESTRING (" + coorda[j] + ", " + coorda[j+1] + ")"); 
			}
		}
		return result;
    }
    
	public Runnable createJob( final IndexWriter writer, final List<GeoRecord> buffer){
		return new Runnable() {
			public void run() {
				Document doc = null;
				for (GeoRecord geoRecord : buffer) {
		    		if (geoRecord.isArea()){
		        		doc = createDocument(geoRecord,polygonCtx,polygonStrategy);
		    		}else{
		    			String  wkt = geoRecord.getWkt();
//		    			if (geoRecord.isMultiLine()){
//			    			for (String shortLines: toShortLineStrings(wkt)) {
//								geoRecord.setWkt(shortLines);
//								doc = createDocument(geoRecord,spatialContext,spatialStrategy);
//							}
//		    			}else{
							doc = createDocument(geoRecord,spatialContext,spatialStrategy);
//		    			}
		    		}
		            if (doc != null){
		            	try {
							writer.addDocument(doc);
							indexedCount++;
							if (indexedCount % logEvery == 0){
								long timeElapsed = System.currentTimeMillis() - startTime;
								double averageSpeed = (double)indexedCount / timeElapsed *1000;

								double currentElapsed = System.currentTimeMillis() - currentStartTime;
								currentStartTime = System.currentTimeMillis();
								double currentSpeed = logEvery / currentElapsed  * 1000;
								System.out.println(String.format("%s indexed. Average %s/second . Current %s/second",indexedCount , (int)averageSpeed, (int)currentSpeed ));
							}
						} catch (IOException e) {
				    		System.out.println(String.format("Error indexing shape. Error = %s id = %s name = %s wkt = %s ",e.getMessage(), geoRecord.getId(), geoRecord.getName() , geoRecord.getWkt()));
				    		continue;
						}
		            }

				}			
				
			}
		};
	}
    
    
	public void createIndex( Iterator<GeoRecord> iterator) throws IOException{
		createIndex(iterator,3);
	}
	
	public void createIndex( Iterator<GeoRecord> iterator, int threadCount) throws IOException{
		IndexWriterConfig cfg = new IndexWriterConfig( new StandardAnalyzer());
        cfg.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        cfg.setRAMBufferSizeMB(256);
        Directory dir = FSDirectory.open(Paths.get(indexPath));
        final IndexWriter writer = new IndexWriter(dir,cfg);
        
        List<GeoRecord> buffer = new ArrayList<GeoRecord>();
        while (iterator.hasNext()) {
        	buffer.add(iterator.next());
        }
        JobController<GeoRecord> controller = new JobController<GeoRecord>() {
			
			@Override
			public Runnable createThread(List<GeoRecord> lines) {
    			return createJob(writer,lines);
			}
		};
		indexedCount = 0;
		startTime = System.currentTimeMillis();
		
		controller.process(buffer, threadCount);
		writer.commit();
		writer.close();
	}
	
	private static Document createDocument(GeoRecord geoRecord, SpatialContext indexingContext,SpatialStrategy indexingStrategy ){
		Document doc = new Document();
		String value = geoRecord.getWkt();

		if (value != null && value.length() > 0){
			Shape shp = null; 
			try {
				shp = indexingContext.readShapeFromWkt(value);
                for(IndexableField f : indexingStrategy.createIndexableFields(shp )){
                    doc.add(f);
                }
			} catch (ParseException e) {

				throw new RuntimeException(e);
			}
		}else{
            return null;
		}

		value = geoRecord.getId();
		if (value != null && value.length() > 0){
			doc.add(new StringField(FIELDID, value, Store.YES));
		}else{
            return null;
		}
		value = geoRecord.getName();
		if (value != null && value.length() > 0){
			doc.add(new StringField(FIELDNAME, value, Store.YES));
		}
		return doc;
	} 
	
	private void createSearcher() throws IOException{
//		RAMDirectory   d = new RAMDirectory(FSDirectory.open( new File(indexPath)), IOContext.READONCE);
//		searcher = new IndexSearcher(DirectoryReader.open(d));

		
		searcher = new IndexSearcher(DirectoryReader.open(FSDirectory .open(Paths.get(indexPath))));
	}
	
    public List<GeoRecord>  searchNearest(double lng,double lat,double radiusInKM) throws IOException{
    	return search(lng,lat,radiusInKM,false);
    	
    }
	
    public List<GeoRecord>  searchWithin(double lng,double lat) throws IOException{
    	return search(lng,lat,0,true);
    }
	
    private List<GeoRecord> search(double lng,double lat,double radiusInKM, boolean searchWithin) throws IOException{
        if (searcher == null){
        	createSearcher();
        }
    	
    	Point point = spatialContext.makePoint(lng,lat);

        ValueSource valueSource = spatialStrategy.makeDistanceValueSource(point);
        
        
        List<GeoRecord> result = new ArrayList<GeoRecord>();
        
        SpatialArgs args = null;
        if (searchWithin){
        	args = new SpatialArgs(SpatialOperation.Contains,spatialContext.makePoint(lng,lat));
        }else{
        	args = new  SpatialArgs(SpatialOperation.Intersects,spatialContext.makeCircle(lng, lat,DistanceUtils.dist2Degrees(radiusInKM, DistanceUtils.EARTH_MEAN_RADIUS_KM)));
        }
               
        FunctionQuery functionQuery = new FunctionQuery(valueSource);


        Filter filter = spatialStrategy.makeFilter(args);
        Query q = new FilteredQuery(functionQuery, filter);
        //Query q = new FilteredQuery(new MatchAllDocsQuery(), filter);
        
        TopDocs topDocs = searcher.search(q, 1000);
        
        
        for(ScoreDoc docScore: topDocs.scoreDocs){
            Document mostRelevantDoc = searcher.doc(docScore.doc);
            GeoRecord geoRecord = new GeoRecord(mostRelevantDoc.get(FIELDID), mostRelevantDoc.get(FIELDNAME), null);
            if (docScore.score >= 170){
            	geoRecord.setDistance(-1);	
            }else{
                geoRecord.setDistance(docScore.score * 1000 *  111.1951);
            }
            result.add(geoRecord);
        }
        return result;
    }
	
}

