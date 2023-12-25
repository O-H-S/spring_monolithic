plugins {
	java
	id("org.springframework.boot") version "3.1.3"
	id("io.spring.dependency-management") version "1.1.3"
	id("org.asciidoctor.jvm.convert") version "3.3.2" // docs
	id("com.palantir.docker") version "0.35.0" // deploy
	id("org.hidetake.ssh") version "2.11.2" // deploy
}


group = "com.ohs"
version = "0.0.1-SNAPSHOT"


java {
	sourceCompatibility = JavaVersion.VERSION_17
}


repositories {
	mavenCentral()
}


dependencies {

	// 템플릿 엔진
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect")

	// 폼 검증
	implementation ("org.springframework.boot:spring-boot-starter-validation")

	// 캐시
	implementation ("org.springframework.boot:spring-boot-starter-cache")

	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6:3.1.1.RELEASE")


	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-actuator")


    //runtimeOnly("mysql:mysql-connector-java") 이전 버전에서는 이렇게 명시해야했음.
	runtimeOnly("com.mysql:mysql-connector-j")


	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
}


apply(from = "./buildGradles/build-docs.gradle")
apply(from = "./buildGradles/build-deploy.gradle")
apply(from = "./buildGradles/build-querydsl.gradle")
apply(from = "./buildGradles/build-testing.gradle")

