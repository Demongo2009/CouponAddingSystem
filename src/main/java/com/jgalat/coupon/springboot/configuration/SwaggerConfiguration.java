package com.jgalat.coupon.springboot.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "swagger")
public class SwaggerConfiguration {

	private String appName;

	private String appDescription;

	private String appVersion;

	private String appLicense;

	private String appLicenseUrl;

	private String contactName;

	private String contactUrl;

	private String contactMail;

	@Bean
	public OpenAPI openAPI() {

		final Info apiInformation = getApiInformation();
		
		final OpenAPI openAPI = new OpenAPI();
		openAPI.setInfo(apiInformation);

		return openAPI;
	}

	private Info getApiInformation() {

		final License license = new License();
		license.setName(appLicense);
		license.setUrl(appLicenseUrl);

		final Contact contact = new Contact();
		contact.setName(contactName);
		contact.setUrl(contactUrl);
		contact.setEmail(contactMail);


		final Info info = new Info();
		info.setTitle(appName);
		info.setVersion(appVersion);
		info.setDescription(appDescription);
		info.setLicense(license);
		info.setContact(contact);

		return info;
	}

}
