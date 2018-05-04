package com.paritus.reversegeo.app;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

 
public class FileTypeConverter implements IStringConverter<FileType> {
    public FileType convert(String value) {
    	FileType convertedValue = FileType.fromString(value);
 
        if(convertedValue == null) {
            throw new ParameterException("Value " + value + "can not be converted to FileType. " +
                    "Available values are: infquarter, inftown,infcity,infroad,osmnroad,osmlanduse.");
        }
        return convertedValue;
    }
}