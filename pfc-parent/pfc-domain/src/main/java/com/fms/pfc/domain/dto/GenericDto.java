package com.fms.pfc.domain.dto;

import lombok.Data;

@Data
public class GenericDto<T> {

	private T obj;
}
