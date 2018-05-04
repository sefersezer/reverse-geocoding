package com.paritus.reversegeo.app;

public enum FileType {
	 
    INFQUARTER,
    INFTOWN,
    INFCITY,
    INFROAD,
    OSMROAD,
    OSMLANDUSE  ;
    
    public static FileType fromString(String code) {
 
        for(FileType output : FileType.values()) {
            if(output.toString().equalsIgnoreCase(code)) {
                return output;
            }
        }
        return null;
    }
}