package jpabook.jpashop1;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Jpashop1Application {

	public static void main(String[] args) {
		SpringApplication.run(Jpashop1Application.class, args);
	}

	//com.fasterxml.jackson.databind.exc.InvalidDefinitionException
	// hibernate proxy 오류 : LAZY 지연로딩 관련 null 오류
	// 해결 : build.gradle에 hibernate5 모듈 추가 후 Bean 설정
	@Bean
	Hibernate5Module hibernate5Module() {
//		return new Hibernate5Module();

		// LAZY(지연로딩) 설정 시 강제 로딩 : 연관관계 데이터를 강제로 바로 가져옴
		// 엔티티 노출과, 성능 문제로 사용하지 않는 것이 좋다.
		Hibernate5Module hibernate5Module = new Hibernate5Module();
//		hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
		return hibernate5Module;
	}
}
