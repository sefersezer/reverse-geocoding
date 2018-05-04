package com.paritus.reversegeo.app;

import java.io.IOException;

import com.beust.jcommander.JCommander;
import com.paritus.reversegeo.ShapeFileReader;
import com.paritus.reversegeo.SpatialIndex;
import com.paritus.reversegeo.inf.InfotechQuarterShapeReader;
import com.paritus.reversegeo.inf.InfotechStreetShapeReader;
import com.paritus.reversegeo.osm.OsmShapeReader;

public class Indexer {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		Settings cmd = new Settings();
		try {
			new JCommander(cmd, args);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return;
		}
		
		
		ShapeFileReader reader = null;
		switch (cmd.getType()) {
		case INFQUARTER:
			reader = new InfotechQuarterShapeReader(); 
			break;
		case INFROAD:
			reader = new InfotechStreetShapeReader(); 
			break;
		case OSMROAD:
		case OSMLANDUSE:
			reader = new OsmShapeReader(); 
			break;
		default:
			throw new RuntimeException(cmd.getType().name() + " reader not implemented.");
		}
	 
		reader.open(cmd.getFile());
		SpatialIndex index = new SpatialIndex(cmd.getIndexPath() ,cmd.getMaxLevels(), cmd.getDistErrPct());
		index.createIndex(reader,cmd.getNumthreads());
	}

}
