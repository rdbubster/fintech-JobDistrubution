package com.jobengine.job_engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.TimeZone;

@SpringBootApplication
public class JobEngineApplication {

	public static void main(String[] args) {
		// Force the JVM to completely forget 'Asia/Calcutta' at the absolute entry point
		System.setProperty("user.timezone", "UTC");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		SpringApplication.run(JobEngineApplication.class, args);
	}
}

//package com.jobengine.job_engine;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication
//public class JobEngineApplication {
//
//	public static void main(String[] args) {
//		SpringApplication.run(JobEngineApplication.class, args);
//	}
//
//}