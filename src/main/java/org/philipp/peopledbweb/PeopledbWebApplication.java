package org.philipp.peopledbweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@SpringBootApplication
public class PeopledbWebApplication implements WebMvcConfigurer {

	/*
	Erreichbar über http://127.0.0.1:8080/ bzw. localhost:8080
	Dies ist immer die Adresse des eigenen Rechners, und dort wird durch tomcat autom.
	immer index.html aufgerufen (sofern vorhanden)

	Möchte man bspw. test.html aufrufen->
	http://127.0.0.1:8080/test.html
	 */


	public static void main(String[] args) {
		SpringApplication.run(PeopledbWebApplication.class, args);
	}


	// Um verschiedene Sprachen über URL einzustellen
	// bspw: http://localhost:8080/people?locale=fr
	// http://localhost:8080/people?locale=de
	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(Locale.US);
		return slr;
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		return new LocaleChangeInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}
}
