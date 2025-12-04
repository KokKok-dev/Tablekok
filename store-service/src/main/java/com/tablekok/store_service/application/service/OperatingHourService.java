package com.tablekok.store_service.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.store_service.application.dto.param.CreateOperatingHourParam;
import com.tablekok.store_service.domain.entity.OperatingHour;
import com.tablekok.store_service.domain.entity.Store;
import com.tablekok.store_service.domain.repository.OperatingHourRepository;
import com.tablekok.store_service.domain.vo.OperatingHourData;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OperatingHourService {
	private final OperatingHourRepository operatingHourRepository;

	@Transactional
	public List<OperatingHour> saveOperatingHours(Store store, List<CreateOperatingHourParam> requests) {

		// Param을 VO로 변환하고, Store와 연결하여 Entity 생성
		List<OperatingHour> hoursToSave = requests.stream()
			.map(param -> {
				OperatingHourData vo = param.toVo();
				return OperatingHour.of(store, vo);
			})
			.toList();

		return operatingHourRepository.saveAll(hoursToSave);
	}
}
