package com.paritus.reversegeo;
import java.util.Comparator;

public class GeoRecord{

	private String id;
	private String name;
	private String wkt;
	private boolean area = false;
	private boolean point = false;
	private boolean multiPoint = false;
	private boolean multiLine = false;
	private boolean line = false;
	private double distance;
	
	/*public static Comparator<GeoRecord> GeoRecordDistanceComparator = new Comparator<GeoRecord>() {
		public int compare(GeoRecord gr1, GeoRecord gr2) {
			return Double.compare( gr1.getDistance(), gr2.getDistance());
		}
	};
	
	public static Comparator<GeoRecord> GeoRecordNameComparator = new Comparator<GeoRecord>() {
		public int compare(GeoRecord gr1, GeoRecord gr2) {
			return gr1.getName().compareTo(gr2.getName());
		}
	};

	public static Comparator<GeoRecord> GeoRecordIdComparator = new Comparator<GeoRecord>() {
		public int compare(GeoRecord gr1, GeoRecord gr2) {
			return gr1.getId().compareTo(gr2.getId());
		}
	};
	*/
	
	public static enum GeoRecordComparator implements Comparator<GeoRecord> {
	    DISTANCE_SORT {
	        public int compare(GeoRecord o1, GeoRecord o2) {
	            return Double.compare(o1.getDistance(),o2.getDistance());
	        }},
	    ID_SORT {
	        public int compare(GeoRecord o1, GeoRecord o2) {
	            return o1.getId().compareTo(o2.getId());
	        }};

	    public static Comparator<GeoRecord> ascending(final Comparator<GeoRecord> other) {
	        return new Comparator<GeoRecord>() {
	            public int compare(GeoRecord o1, GeoRecord o2) {
	                return other.compare(o1, o2);
	            }
	        };
	    }

	    public static Comparator<GeoRecord> getComparator(final GeoRecordComparator... multipleOptions) {
	        return new Comparator<GeoRecord>() {
	            public int compare(GeoRecord o1, GeoRecord o2) {
	                for (GeoRecordComparator option : multipleOptions) {
	                    int result = option.compare(o1, o2);
	                    if (result != 0) {
	                        return result;
	                    }
	                }
	                return 0;
	            }
	        };
	    }
	}
	
	
	public GeoRecord(String id, String name, String wkt) {
		super();
		this.id = id;
		this.name = name;
		this.wkt = wkt;
		if (wkt != null ){
			if (wkt.startsWith("MULTIPOLYGON") || wkt.startsWith("POLYGON")){
				area = true;
			}else if (wkt.startsWith("LINE") ){
				line = true;
			}else if (wkt.startsWith("MULTILINE") ){
				multiLine = true;
			}else if (wkt.startsWith("POINT") ){
				point = true;
			}else if (wkt.startsWith("MULTIPOINT") ){
				multiPoint = true;
			}
		}
	}
	
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getWkt() {
		return wkt;
	}

	public boolean isArea() {
		return area;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public void setWkt(String wkt) {
		this.wkt = wkt;
	}

	public boolean isPoint() {
		return point;
	}

	public boolean isMultiPoint() {
		return multiPoint;
	}

	public boolean isMultiLine() {
		return multiLine;
	}

	public boolean isLine() {
		return line;
	}
}
