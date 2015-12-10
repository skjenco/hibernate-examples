package com.iskj;

import java.io.File;
import java.io.IOException;

public class GenerateDdlApp {
	
	public static void main(String[] args) throws IOException {

//		HibernateExporter exporter = new HibernateExporter("org.hibernate.dialect.HSQLDialect", "com.iskj.model");
		HibernateExporter exporter = new HibernateExporter("org.hibernate.dialect.MySQL5Dialect", "com.iskj.model");
		exporter.setGenerateDropQueries(true);
		exporter.exportToConsole();
		exporter.export(new File("schema.sql"));
	}
}
