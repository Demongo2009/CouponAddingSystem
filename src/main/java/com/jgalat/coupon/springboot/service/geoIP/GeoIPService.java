package com.jgalat.coupon.springboot.service.geoIP;

import java.util.Optional;


public interface GeoIPService {

	Optional<String> getCountryByIpAddress(String ipAddress);

}
