plugins {
	java
	id("org.springframework.boot") version "3.1.3"
	id("io.spring.dependency-management") version "1.1.3"
}

group = "com.ohs"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
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

	// security와 thymeleaf 연동을 위해 필요함.
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6:3.1.1.RELEASE")

	// 인증/인가
	implementation("org.springframework.boot:spring-boot-starter-security")
		// JWT
		implementation("io.jsonwebtoken:jjwt-api:0.11.5")
		runtimeOnly ("io.jsonwebtoken:jjwt-impl:0.11.5")
		runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5") // Jackson이나 Gson을 사용하는 경우

		//oAuth2
		implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")






	//runtimeOnly("mysql:mysql-connector-java") 이전 버전에서는 이렇게 명시해야했음.
	runtimeOnly("com.mysql:mysql-connector-j")


	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// 이 스타터는 JUnit, AssertJ, Hamcrest, Mockito와 같은 주요 테스트 라이브러리를 포함하
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("com.h2database:h2")
}

// 이 코드는 단지 프로젝트 내의 모든 test 태스크가 JUnit 5를 사용하도록 설정만 할 뿐
tasks.withType<Test> {
	useJUnitPlatform()
}
