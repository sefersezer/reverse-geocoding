package com.paritus.reversegeo.app;


import com.beust.jcommander.Parameter;

public class Settings {

	@Parameter(names = "-shapefile", description = "full path of the shape file.", required = true, validateWith = FileNameValidator.class)
	private String file;
	
	@Parameter(names = "-filetype", description = "Type of the shape file. ", required = true, converter = FileTypeConverter.class)
 	private FileType type;

	@Parameter(names = "-indexpath", description = "full path of index folder. Contents will be overwritten", required = true)
	private String indexPath;
	
	@Parameter(names = "-maxLevels", description = "Grid level. Use 10 for 1meter sensitivity. ", required = false)
	private int maxLevels = 10;

	@Parameter(names = "-numthreads", description = "Number of parallel executin threads. #of cores suggested ", required = false)
	private int numthreads = 2;
	
	@Parameter(names = "-disterrpct", description = "Distance error percantage. Must be between 0 and 0.5. ", required = false)
	private double distErrPct = 0.000;

	
	public String getFile() {
		return file;
	}

	public FileType getType() {
		return type;
	}

	public int getMaxLevels() {
		return maxLevels;
	}

	public double getDistErrPct() {
		return distErrPct;
	}

	public String getIndexPath() {
		return indexPath;
	}

	public int getNumthreads() {
		return numthreads;
	}

	
	
}
