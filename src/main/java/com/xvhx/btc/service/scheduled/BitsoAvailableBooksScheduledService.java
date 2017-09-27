package com.xvhx.btc.service.scheduled;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xvhx.btc.api.rest.domain.BitsoAvailableBooksResponse;
import com.xvhx.btc.service.scheduled.task.BitsoAvailableBooksScheduledTask;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

@Component
public class BitsoAvailableBooksScheduledService extends ScheduledService<BitsoAvailableBooksResponse> {

	@Autowired
	private ObjectFactory<BitsoAvailableBooksScheduledTask> bitsoAvailableBooksScheduledTaskBuilder;

	@Override
	protected Task<BitsoAvailableBooksResponse> createTask() {
		return bitsoAvailableBooksScheduledTaskBuilder.getObject();
	}

}
