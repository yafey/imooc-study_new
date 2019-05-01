package com.yafey.support.spring.enable.interfaces;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import com.yafey.support.spring.enable.annotation.HelloWorldConfiguration;

public class HelloWorldImportSelector implements ImportSelector {

	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{HelloWorldConfiguration.class.getName()};
	}

}
