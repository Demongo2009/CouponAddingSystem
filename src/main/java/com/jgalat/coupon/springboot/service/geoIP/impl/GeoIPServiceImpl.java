package com.jgalat.coupon.springboot.service.geoIP.impl;

import com.jgalat.coupon.springboot.service.geoIP.GeoIPService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class GeoIPServiceImpl implements GeoIPService {

	private static final String IP_API_URL_TEMPLATE = "http://ip-api.com/json/%s?fields=country";
	private static final String COUNTRY_KEY = "country";

	private final WebClient webClient;

	@Override
	public Optional<String> getCountryByIpAddress(String ipAddress) {
		try {
			log.info("Getting country by ip address: {}", ipAddress);
			Mono<Map> mapOfIpInformationMono = webClient.get()
				.uri(String.format(IP_API_URL_TEMPLATE, ipAddress))
				.retrieve()
				.bodyToMono(Map.class);

			Map<String, String> mapOfIpInformation = mapOfIpInformationMono.block();

			if (mapOfIpInformation == null) {
				log.error("Error waiting for ip information by ip address: {}", ipAddress);
				return Optional.empty();
			}

			final String country = mapOfIpInformation.get(COUNTRY_KEY);
			log.info("Country by ip address: {}", country);
            return Optional.ofNullable(country);
        } catch (Exception e) {
			log.error("Error getting country by ip address: {}", ipAddress, e);
			return Optional.empty();
        }
	}
}
