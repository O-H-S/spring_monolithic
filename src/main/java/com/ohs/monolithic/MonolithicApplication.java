package com.ohs.monolithic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication

public class MonolithicApplication {

	public static void main(String[] args) {
		//System.out.println("run start");
		SpringApplication.run(MonolithicApplication.class, args);
		//System.out.println("run finished");
	}
}
