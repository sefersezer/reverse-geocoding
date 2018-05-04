package com.paritus.reversegeo.app;

import java.io.File;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class FileNameValidator implements IParameterValidator  {

	public void validate(String arg0, String arg1) throws ParameterException {
		if (!new File(arg1).exists()){
			throw new ParameterException("File not exist");
		}
	}

}
