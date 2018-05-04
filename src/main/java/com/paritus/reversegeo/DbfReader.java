package com.paritus.reversegeo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nl.knaw.dans.common.dbflib.CorruptedTableException;
import nl.knaw.dans.common.dbflib.IfNonExistent;
import nl.knaw.dans.common.dbflib.Record;
import nl.knaw.dans.common.dbflib.Table;

public class DbfReader {

	private Map<String, String> nameMap = new HashMap<String, String>();
	
	public void load(String fileName) throws CorruptedTableException, IOException {
		final Table table = new Table(new File(fileName));
		try {
			table.open(IfNonExistent.ERROR);

			final Iterator<Record> recordIterator = table.recordIterator();
			int count = 0;
			while (recordIterator.hasNext()) {
				count++;
				final Record record = recordIterator.next();
				String id  = Integer.toString(count);
				String name  = record.getStringValue("name");
				nameMap.put(id, name);
			}
		} finally {
			table.close();
		}
	}
	
	public String getName(String id){
		return nameMap.get(id);
	}
}