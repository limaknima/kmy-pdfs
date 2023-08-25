package com.fms.pfc.service.api.base;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.dto.LabelAndValueDto;

@Service
public class YearMonthDayItemsService {

	private final Logger logger = LoggerFactory.getLogger(YearMonthDayItemsService.class);

	public YearMonthDayItemsService() {

	}

	/**
	 * Get year select items based on current year as anchor
	 * 
	 * @param yearCount
	 * @return
	 */
	public List<LabelAndValueDto> yearDropdownItems(int yearCount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int currentYear = cal.get(Calendar.YEAR);

		LabelAndValueDto dt = new LabelAndValueDto(String.valueOf(currentYear), String.valueOf(currentYear));
		List<LabelAndValueDto> result = new ArrayList<LabelAndValueDto>();
		result.add(dt);

		for (int idx = 1; idx <= yearCount; idx++) {
			result.add(new LabelAndValueDto(String.valueOf(currentYear - idx), String.valueOf(currentYear - idx)));
		}

		return result;
	}

	/**
	 * Get Month select items
	 * 
	 * @return
	 */
	public List<LabelAndValueDto> monthDropdownItems() {
		List<LabelAndValueDto> result = new ArrayList<LabelAndValueDto>();
		result.add(new LabelAndValueDto("01-January", "1"));
		result.add(new LabelAndValueDto("02-February", "2"));
		result.add(new LabelAndValueDto("03-March", "3"));
		result.add(new LabelAndValueDto("04-April", "4"));
		result.add(new LabelAndValueDto("05-May", "5"));
		result.add(new LabelAndValueDto("06-June", "6"));
		result.add(new LabelAndValueDto("07-July", "7"));
		result.add(new LabelAndValueDto("08-August", "8"));
		result.add(new LabelAndValueDto("09-September", "9"));
		result.add(new LabelAndValueDto("10-October", "10"));
		result.add(new LabelAndValueDto("11-November", "11"));
		result.add(new LabelAndValueDto("21-December", "12"));

		return result;
	}

	/**
	 * Get Day select items
	 * 
	 * @return
	 */
	public static List<LabelAndValueDto> dayDropdownItems() {
		int maxDay = 31;
		List<LabelAndValueDto> result = new ArrayList<LabelAndValueDto>();
		for (int idx = 1; idx <= maxDay; idx++) {
			result.add(new LabelAndValueDto(String.valueOf(idx), String.valueOf(idx)));
		}

		return result;
	}
}
