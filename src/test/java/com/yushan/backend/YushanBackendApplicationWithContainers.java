package com.yushan.backend;

import org.springframework.boot.SpringApplication;

public class YushanBackendApplicationWithContainers {

	public static void main(String[] args) {
		SpringApplication.from(YushanBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
