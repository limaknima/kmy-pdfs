package com.fms.pfc.domain.dto.main;

import java.util.Date;

import javax.persistence.Column;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProdFileDto {
	private Integer pkPfileId;
	private Integer fkHplId;
	private String fkHplName;
	private Integer fkHplModelId;
	private String fkHplModelName;
	private String g2Lot;
	private String year;
	private String mth;
	private String prodLn;
	private String fileName;
	private String filePath;
	private String fileType;
	private Long fileSize;
	private Long crcValue;
	private String creatorId;
	private Date createdDatetime;
	private String modifierId;
	private Date modifiedDatetime;
	private MultipartFile fileContent;
	private String hpl;
	private String hModel;
	private String day;
	private String seq;
	private byte[] contentObject;
}
