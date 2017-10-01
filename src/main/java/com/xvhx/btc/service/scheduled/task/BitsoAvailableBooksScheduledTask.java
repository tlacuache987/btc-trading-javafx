package com.xvhx.btc.service.scheduled.task;

import java.net.URI;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.xvhx.btc.api.rest.domain.BitsoAvailableBooksResponse;

import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Scope("prototype")
public class BitsoAvailableBooksScheduledTask extends Task<BitsoAvailableBooksResponse> {

	private static final String URL_BITSO_AVAILABLE_BOOKS = "https://api.bitso.com/v3/available_books/";

	@Autowired
	private RestTemplate restTemplate;

	@Override
	protected BitsoAvailableBooksResponse call() throws Exception {
		HttpHeaders headers = new HttpHeaders();

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(URL_BITSO_AVAILABLE_BOOKS);

		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("user-agent", "REST-TEMPLATE");

		HttpEntity<?> entity = new HttpEntity<>(headers);

		ResponseEntity<BitsoAvailableBooksResponse> response = null;

		URI uri = builder.build().encode().toUri();

		log.debug("Requesting {} ", uri);

		try {
			response = restTemplate.exchange(uri, HttpMethod.GET, entity, BitsoAvailableBooksResponse.class);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}

		if (response != null)
			return response.getBody();
		return null;
	}

}
