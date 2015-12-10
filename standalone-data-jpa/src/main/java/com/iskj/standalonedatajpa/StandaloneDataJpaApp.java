package com.iskj.standalonedatajpa;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class StandaloneDataJpaApp {

	private static final String CONFIG_PACKAGE = "com.iskj.standalonedatajpa.config";

	public static void main(String[] args) {
		
		try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext()) {
			
			ctx.scan(CONFIG_PACKAGE);
			ctx.refresh();
			
			MainBean bean = ctx.getBean(MainBean.class);
			bean.start();
		}
	}
}
