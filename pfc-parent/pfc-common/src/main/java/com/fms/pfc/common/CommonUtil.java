package com.fms.pfc.common;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.fms.pfc.domain.dto.LabelAndValueDto;

public class CommonUtil implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4428086390273048922L;

	private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);

	private static long BYTE = 1L;
	private static long KiB = BYTE << 10;
	private static long MiB = KiB << 10;
	private static long GiB = MiB << 10;
	private static long TiB = GiB << 10;
	private static long PiB = TiB << 10;
	private static long EiB = PiB << 10;

	private static long KB = BYTE * 1000;
	private static long MB = KB * 1000;
	private static long GB = MB * 1000;
	private static long TB = GB * 1000;
	private static long PB = TB * 1000;
	private static long EB = PB * 1000;

	private static DecimalFormat DEC_FORMAT = new DecimalFormat("#.##");

	public static <T> List<T> castList(Class<? extends T> clazz, Collection<?> rawCollection)
			throws ClassCastException {
		List<T> result = new ArrayList<>(rawCollection.size());
		for (Object o : rawCollection) {
			result.add(clazz.cast(o));
		}
		return result;
	}

	/**
	 * Display human readable file size - Binary
	 * 
	 * @param size
	 * @return
	 */
	public static String toHumanReadableBinaryPrefixes(long size) {
		if (size < 0)
			throw new IllegalArgumentException("Invalid file size: " + size);
		if (size >= EiB)
			return formatSize(size, EiB, "EiB");
		if (size >= PiB)
			return formatSize(size, PiB, "PiB");
		if (size >= TiB)
			return formatSize(size, TiB, "TiB");
		if (size >= GiB)
			return formatSize(size, GiB, "GiB");
		if (size >= MiB)
			return formatSize(size, MiB, "MiB");
		if (size >= KiB)
			return formatSize(size, KiB, "KiB");
		return formatSize(size, BYTE, "Bytes");
	}

	/**
	 * Display human readable file size - SI
	 * 
	 * @param size
	 * @return
	 */
	public static String toHumanReadableSIPrefixes(long size) {
		if (size < 0)
			throw new IllegalArgumentException("Invalid file size: " + size);
		if (size >= EB)
			return formatSize(size, EB, "EB");
		if (size >= PB)
			return formatSize(size, PB, "PB");
		if (size >= TB)
			return formatSize(size, TB, "TB");
		if (size >= GB)
			return formatSize(size, GB, "GB");
		if (size >= MB)
			return formatSize(size, MB, "MB");
		if (size >= KB)
			return formatSize(size, KB, "KB");
		return formatSize(size, BYTE, "Bytes");
	}

	private static String formatSize(long size, long divider, String unitName) {
		return DEC_FORMAT.format((double) size / divider) + " " + unitName;
	}

	/**
	 * Convert MultipartFile to File object - save in a directory
	 * 
	 * @param multipart
	 * @param dir
	 * @return
	 * @throws IOException
	 */
	public static Path multipartFileToFile(MultipartFile multipart, Path dir) throws IOException {
		Path filepath = Paths.get(dir.toString(), multipart.getOriginalFilename());
		multipart.transferTo(filepath);
		return filepath;
	}

	/**
	 * Get Year select items based on current year as anchor
	 * 
	 * @param yearCount
	 * @return
	 */
	public static List<LabelAndValueDto> yearDropdownItems(int yearCount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int currentYear = cal.get(Calendar.YEAR);

		LabelAndValueDto dt = new LabelAndValueDto(String.valueOf(currentYear), String.valueOf(currentYear));
		List<LabelAndValueDto> result = new ArrayList<LabelAndValueDto>();
		result.add(dt);

		for (int idx = 1; idx <= yearCount; idx++) {
			LabelAndValueDto d = new LabelAndValueDto(String.valueOf(currentYear - idx),
					String.valueOf(currentYear - idx));
			result.add(d);
		}

		return result;
	}

	/**
	 * Get Month select items
	 * 
	 * @return
	 */
	public static List<LabelAndValueDto> monthDropdownItems() {
		List<LabelAndValueDto> result = new ArrayList<LabelAndValueDto>();
		/*result.add(new LabelAndValueDto("January", "01"));
		result.add(new LabelAndValueDto("February", "02"));
		result.add(new LabelAndValueDto("March", "03"));
		result.add(new LabelAndValueDto("April", "04"));
		result.add(new LabelAndValueDto("May", "05"));
		result.add(new LabelAndValueDto("June", "06"));
		result.add(new LabelAndValueDto("July", "07"));
		result.add(new LabelAndValueDto("August", "08"));
		result.add(new LabelAndValueDto("September", "09"));
		result.add(new LabelAndValueDto("October", "10"));
		result.add(new LabelAndValueDto("November", "11"));
		result.add(new LabelAndValueDto("December", "12"));*/
		result.add(new LabelAndValueDto("01", "01"));
		result.add(new LabelAndValueDto("02", "02"));
		result.add(new LabelAndValueDto("03", "03"));
		result.add(new LabelAndValueDto("04", "04"));
		result.add(new LabelAndValueDto("05", "05"));
		result.add(new LabelAndValueDto("06", "06"));
		result.add(new LabelAndValueDto("07", "07"));
		result.add(new LabelAndValueDto("08", "08"));
		result.add(new LabelAndValueDto("09", "09"));
		result.add(new LabelAndValueDto("10", "10"));
		result.add(new LabelAndValueDto("11", "11"));
		result.add(new LabelAndValueDto("12", "12"));

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
			result.add(new LabelAndValueDto(String.valueOf(idx >= 10 ? idx : "0" + idx),
					String.valueOf(idx >= 10 ? idx : "0" + idx)));
		}

		return result;
	}
	
	public static List<LabelAndValueDto> hplProcTypeDropdownItems() {
		List<LabelAndValueDto> result = new ArrayList<LabelAndValueDto>();
		result.add(new LabelAndValueDto("Mikron", CommonConstants.PROCESS_TYPE_HPL_MIKRON));
		result.add(new LabelAndValueDto("Back End", CommonConstants.PROCESS_TYPE_HPL_BACKEND));
		return result;		
	}

	public static boolean isPathValid(String path) {

		if (isNullOrEmpty(path)) {
			logger.error("isPathValid() Path is empty!");
			return false;
		}

		// check 1st position - must be drive name e.g. C, D, E etc
		String first = StringUtils.substring(path, 0, path.indexOf(":"));

		// check 2nd position - must be character ':'
		int second = path.indexOf(":");

		// check if 1st and 2nd is valid
		{
			String strPattern = "^[a-zA-Z]$";
			boolean alphabetMatch = first.matches(strPattern);
			if (!alphabetMatch || second != 1) {
				System.err.println("2. invalid path " + path);
				logger.error("isPathValid() alphabet match?={}, ':' position={}", alphabetMatch, second);
				return false;
			}
		}

		try {
			Paths.get(path);
		} catch (InvalidPathException ex) {
			logger.error("isPathValid() Invalid path {} ", path);
			return false;
		}

		return true;
	}

	public static boolean isValidFilePath(String filePath) {
		if (isNullOrEmpty(filePath)) {
			return false;
		}

		String path = filePath.trim();

		// Validate publish settings path
		File file = new File(path);
		if ((!file.exists()) || file.isDirectory()) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean isNullOrEmpty(String value) {
		return (value == null) || (value.trim().length() == 0);

	}

	public static String dayConversionFromFileName(String day, String hplName) {
		String result = "";

		try {
			int d = Integer.valueOf(day);
			if (d > 0 && d < 10) {
				result = "0" + d;
			} else {
				result = day;
			}

		} catch (Exception ex) {
			if (StringUtils.equalsIgnoreCase(hplName, CommonConstants.RECORD_TYPE_ID_HPL_IF)) {
				switch (day) {
				case "A":
					result = "10";
					break;
				case "B":
					result = "11";
					break;
				case "C":
					result = "12";
					break;
				case "D":
					result = "13";
					break;
				case "E":
					result = "14";
					break;
				case "F":
					result = "15";
					break;
				case "G":
					result = "16";
					break;
				case "H":
					result = "17";
					break;
				case "J":
					result = "18";
					break;
				case "K":
					result = "19";
					break;
				case "L":
					result = "20";
					break;
				case "M":
					result = "21";
					break;
				case "N":
					result = "22";
					break;
				case "P":
					result = "23";
					break;
				case "Q":
					result = "24";
					break;
				case "R":
					result = "25";
					break;
				case "S":
					result = "26";
					break;
				case "T":
					result = "27";
					break;
				case "U":
					result = "28";
					break;
				case "V":
					result = "29";
					break;
				case "W":
					result = "30";
					break;
				case "X":
					result = "31";
					break;
				default:
					result = "";
				}
			}
		}

		return result;
	}

	public static String monthConversionFromFileName(String month, String hplName) {
		String result = "";

		try {
			int d = Integer.valueOf(month);
			if (d > 0 && d < 10) {
				result = "0" + d;
			} else {
				result = month;
			}

		} catch (Exception ex) {
			if (StringUtils.equalsIgnoreCase(hplName, CommonConstants.RECORD_TYPE_ID_HPL_IF)) {
				switch (month) {
				case "X":
					result = "10";
					break;
				case "Y":
					result = "11";
					break;
				case "Z":
					result = "12";
					break;
				default:
					result = "";
				}
			}
		}

		return result;
	}

	public static String prodLnConversionFromFileName(String prodLn, String hplName) {
		String result = "";

		if (StringUtils.equalsIgnoreCase(hplName, CommonConstants.RECORD_TYPE_ID_HPL_IF)) {
			switch (prodLn) {
			case "A":
				result = "1";
				break;
			case "B":
				result = "2";
				break;
			case "C":
				result = "3";
				break;
			case "D":
				result = "4";
				break;
			case "E":
				result = "5";
				break;
			default:
				result = "0";
			}
		} else {
			result = prodLn;
		}

		return result;
	}
	
	public static boolean isEmptyDir(Path path) throws IOException {
		if (Files.isDirectory(path)) {
			try (Stream<Path> entries = Files.list(path)) {
				return !entries.findFirst().isPresent();
			}
		}

		return false;
	}
	
	public static String randomHexColor() {
		// create random object - reuse this as often as possible
		Random random = new Random();

		// create a big random number - maximum is ffffff (hex) = 16777215 (dez)
		int nextInt = random.nextInt(0xffffff + 1);

		// format it as hexadecimal string (with hashtag and leading zeros)
		String colorCode = String.format("#%06x", nextInt);

		return colorCode;
	}

}
