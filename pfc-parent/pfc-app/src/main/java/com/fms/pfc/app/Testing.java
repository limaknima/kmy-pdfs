package com.fms.pfc.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.filechooser.FileSystemView;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.fms.pfc.common.CommonUtil;
import com.fms.pfc.domain.dto.LabelAndValueDto;

import net.samuelcampos.usbdrivedetector.USBDeviceDetectorManager;

public class Testing {

	private static JavaMailSender javaMailSender;
	public static void main(String[] arg) throws Exception {
		/*String str = "a,b,c,d,e,f";
		List<String> li = Stream.of(str.split(",", -1)).collect(Collectors.toList());
//		li.forEach(arg0->System.out.println("li="+arg0));

		try {
//			zipFolder(Paths.get("C:/pfc/temp"), Paths.get("C:/pfc/temp.zip"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<String> str2 = Arrays.asList(new String[] { "a", "b", "c", "d", "e", "f" });

		compareList(li, str2);
		
		int i=0;
		
		System.out.println(">> null?"+Objects.isNull(i));
		*/
		
		//sendEmail();
		
		/*String pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		System.out.println(">> date="+date);
		
		String strDate = "07/11/2022 03:43:29 PM";
		pattern = "dd/MM/yyyy hh:mm:ss a";
		simpleDateFormat = new SimpleDateFormat(pattern);
		Date dt = simpleDateFormat.parse(strDate);
		System.out.println(">> dt="+dt);
		
		pattern = "yyyy-MM-dd HH:mm:ss";
		simpleDateFormat = new SimpleDateFormat(pattern);
		date = simpleDateFormat.format(dt);
		System.out.println(">> date dt="+date);*/
		
		/*StringBuffer tt = new StringBuffer();
		
		if(Objects.isNull(tt)) System.out.println("is null");
		if(StringUtils.isEmpty(tt.toString())) System.out.println("is empty");
		
		List<String> arr = Arrays.asList("a","b","c");
		String ss = String.join("\n\r", arr);
		System.out.println("ss="+ss);
		
		Integer int1 = null;
		Integer int2 = 2;
		
		if(Objects.equals(int1, int2)) {
			System.out.println("EQ");
			
		}
		
		List<String> reglInfoList = new ArrayList<String>();
		reglInfoList.add("arr");
		int reDoRowNo = 0;
		for (int row = 0; row < reglInfoList.size(); row++) {
			System.out.println("reDoRowNo start row="+row);
			reglInfoList.get(row);
			reDoRowNo++;
			System.out.println("reDoRowNo="+reDoRowNo);
		}
		
		for(int i = 3; i > 0; i--) {
			System.out.println(" i >>>> "+i);
		}
		
		testDate();*/
		
		//testGetUuid();
		//testUsb();
		//HDD_SerialNo();
		//yearDropdownItems();
		//String filePath = "F:/220517_KX225HD.csv";
		//isPathValid(filePath);
		//testValidateFileNames();
		//System.out.println("isValidFilePath?" + isValidFilePath(filePath));
		
		//usbDetector();
		//detectUsb1();
		//fileSizeConvert();
		
		//localDate();
		String s = "KMY210104504";
		System.out.println("----> pattern " + Pattern.compile("^[0-9_]").matcher(s).find());
		String year = StringUtils.substring(s, 3, 5);
		year = year.length() == 2? "20"+year : year;
		System.out.println("----> year " + year);
		
		segregateFileName();
		//createDirectories();
		
		int cnt=0;
		String[] abs = new String[] {"1","2","3","4","5"};
		for (String string : Arrays.asList(abs)) {
			if(string!="1")
			cnt++;
		}
		System.out.println("cnt="+cnt);
		
		if(isEmpty(Paths.get("D:\\TEMP\\LIV\\LIV_652_N_002486_HOOISONG38"))) {			
			System.out.println("Empty folder?yes");
		} else 
			System.out.println("Empty folder?no");
		
		if(fileExists()) {
			System.out.println("fileExists?yes");
		} else {
			System.out.println("fileExists?no");
		}
		
		System.out.println("prdln="+CommonUtil.prodLnConversionFromFileName("A", "IF"));
		
	}

	private static void zipFolder(Path sourceFolderPath, Path zipPath) throws Exception {
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()));
		Files.walkFileTree(sourceFolderPath, new SimpleFileVisitor<Path>() {
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				zos.putNextEntry(new ZipEntry(sourceFolderPath.relativize(file).toString()));
				Files.copy(file, zos);
				zos.closeEntry();
				return FileVisitResult.CONTINUE;
			}
		});
		zos.close();
	}

	private static void compareList(List<String> db, List<String> ui) {
		List<String> del = db.stream().filter(arg0 -> !ui.contains(arg0)).collect(Collectors.toList());
		del.forEach(System.out::println);

		System.out.println("<>");

		List<String> add = ui.stream().filter(arg0 -> !db.contains(arg0)).collect(Collectors.toList());
		add.forEach(System.out::println);
	}
	
	private static void sendEmail() throws Exception {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo("amin.kamil@gmail.com");
		msg.setSubject("Boot test");
		msg.setText("Boot test");
		
		javaMailSender.send(msg);
	}
	
	private static void testDate() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");		
		String vipdDate = "21/01/2018";
		Date vipd = new Date();
		try {
			vipd = simpleDateFormat.parse(vipdDate);
			System.out.println(">> dt="+vipd);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LocalDate crrDate = LocalDate.now().plusDays(1);
		
		for(int i = 3; i >= 0;i --) {
			LocalDate expiredDate = vipd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
					.plusYears(5);
			LocalDate remindDate = expiredDate.minusDays(i);
			int diff = crrDate.compareTo(remindDate);
			
			
			if(diff == 0) {
				System.out.println("testDate() ... vipdDate="+vipdDate+"; expired = "+expiredDate+"; rem="+remindDate+"; diff = "+diff+"; today = "+crrDate+" >>> Send reminder ... ");
			} else {
				System.out.println("testDate() ... vipdDate="+vipdDate+"; expired = "+expiredDate+"; rem="+remindDate+"; diff = "+diff+"; today = "+crrDate);
			}
		}
		
		
	}
	
	private static void testGetUuid() throws IOException {
		for (final FileStore store: FileSystems.getDefault().getFileStores()) {
			System.out.println(">> 1. store name: "+store.name());
			System.out.println(">> 1. store type: "+store.type());
			System.out.println(">> 1. store toString: "+store.toString());
			System.out.println(">> 1. store getTotalSpace: "+store.getTotalSpace()); 
			System.out.println(">> store getAttribute: "+store.getAttribute("Class GUID")); //Class GUID
		}
	}
	
	private static void testUsb() {
		System.out.println("File system roots returned by   FileSystemView.getFileSystemView():");
	      FileSystemView fsv = FileSystemView.getFileSystemView();
	      File[] roots = fsv.getRoots();
	      for (int i = 0; i < roots.length; i++)
	      {
	        System.out.println("2. Root: " + roots[i]);
	      }

	      System.out.println("2. Home directory: " + fsv.getHomeDirectory());

	      System.out.println("2. File system roots returned by File.listRoots():");

	      File[] f = File.listRoots();
	      for (int i = 0; i < f.length; i++)
	      {
	        System.out.println("2. Drive: " + f[i]);
	        System.out.println("2. Display name: " + fsv.getSystemDisplayName(f[i]));
	        System.out.println("2. Is drive: " + fsv.isDrive(f[i]));
	        System.out.println("2. Is floppy: " + fsv.isFloppyDrive(f[i]));
	        System.out.println("2. Readable: " + f[i].canRead());
	        System.out.println("2. Writable: " + f[i].canWrite());
	      }
	}
	
	private static void HDD_SerialNo(){
	    StringBuffer HDD_Serial=new StringBuffer();
	    String HDD_Number;
	    Runtime rt = Runtime.getRuntime();
	    try {
	        Process process=rt.exec(new String[]{"CMD", "/C", "WMIC diskdrive get Model, serialnumber"});
	        String s = null;
	        //Reading sucessful output of the command
	        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        while ((s = reader.readLine()) != null) {
	            HDD_Serial.append(s);
	            System.out.println("3. HDD_Serial >> "+HDD_Serial.toString());
	        }
	        // Reading error if any
	        reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
	        while ((s = reader.readLine()) != null) {
	         System.out.print("3. >> err "+s);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	    HDD_Number = HDD_Serial.toString();
	    
	    System.out.println("3. >> HDD_Number >> "+HDD_Number);
	}
	
	private static List<LabelAndValueDto> yearDropdownItems() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int currentYear = cal.get(Calendar.YEAR);
		int yearCount = 20;

		LabelAndValueDto dt = new LabelAndValueDto(String.valueOf(currentYear), String.valueOf(currentYear));
		List<LabelAndValueDto> result = new ArrayList<LabelAndValueDto>();
		result.add(dt);

		for (int idx = 1; idx <= yearCount; idx++) {
			LabelAndValueDto d = new LabelAndValueDto(String.valueOf(currentYear - idx),
					String.valueOf(currentYear - idx));
			result.add(d);
			
			System.out.println("adding year >>> "+d.getStrValue());
		}

		return result;
	}
	
	private static boolean isPathValid(String path) {
		
		if (isNullOrEmpty(path)) {
			System.err.println("1. invalid path " + path);
			return false;
		}
		
		// check 1st position - must be drive name e.g. C, D, E etc
		String first = StringUtils.substring(path, 0, path.indexOf(":"));
		System.out.println("First = "+ first);
		// check 2nd position - must be character ':'
		//String second = StringUtils.substring(path, path.indexOf(":"), path.length());
		int second = path.indexOf(":");
		System.out.println("Second = "+ second);
		
		// check if 1st and 2nd is valid
		{
			String strPattern = "^[a-zA-Z]$";
			System.out.println("first matched pattern? = "+ first.matches(strPattern));
			
			if(!first.matches(strPattern) || second != 1) {
				System.err.println("2. invalid path " + path);
				return false;
			}
		}

		try {

			Path p = Paths.get(path);
			
			if(Files.exists(p)) {
				System.out.println("p="+p.toAbsolutePath());
				try {
					convertFileToMultipartFile(path);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("file not exists");
			}

		} catch (InvalidPathException ex) {
			System.err.println("3. invalid path " + path);
			return false;
		}
		
		System.out.println("4. valid path " + path);

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
	
	public static void testValidateFileNames() {
		 String[] strFileNames = {
	                "file!1.doc",
	                "file1.txt",
	                "file1",
	                ".doc",
	                "file1.doc",
	                "file 1.pdf",
	                "123_abc-AbZ copy.csv"
	        };
	        
	        String strPattern = "^[a-zA-Z0-9._ -]+\\.(doc|pdf|csv|xls)$";
	        
	        for(String strFileName : strFileNames) {            
	            System.out.println( strFileName + " => " + strFileName.matches(strPattern) );
	        }
	        
	}
	
	public static void usbDetector () throws IOException {
		USBDeviceDetectorManager det = new USBDeviceDetectorManager();
		// Display all the USB storage devices currently connected
		det.getRemovableDevices().forEach(System.out::println);
		// Add an event listener to be notified when an USB storage device is connected or removed
		det.addDriveListener(System.out::println);
		// Unmount a device
		//det.unmountStorageDevice(det.getRemovableDevices().get(0));
		
		/*
		 * Once you invoke addDriveListener, your application keep running because it will internally create an ScheduledExecutorService. 
		 * To finish your application, just invoke the close method;
		 */
		 // Shutdown an initialized USBDeviceDetectorManager
		det.close();
	}
	
	public static void detectUsb1() {
		// here the usb drive names are named as E,F,G,H in the system
		String[] drive_name = new String[] { "E", "F", "G", "H", "I", "J", "K", "L", "M", "N" };
		// here we initialize an array for the usb that is to be inserted
		File[] usb = new File[drive_name.length];
		// if the usb is detected then it is assigned True else False
		boolean[] usb_detected = new boolean[drive_name.length];

		// in below loop we append :/ to the drive names because E:/ D:/
		// and then the corresponding drive is searched with the help of canRead()
		// method

		for (int i = 0; i < drive_name.length; ++i) {
			usb[i] = new File(drive_name[i] + ":/");
			// This method determines whether the program can read the file denoted by the
			// mentioned path name
			usb_detected[i] = usb[i].canRead();
		}

		System.out.println("Insert USB");
		detect(usb, drive_name, usb_detected);
	}

	public static void detect(File[] usb, String[] drive_name, boolean[] usb_detected) {
		while (true) {
			// the following loop is iterated to find the usb inserted
			for (int i = 0; i < drive_name.length; ++i) {
				boolean if_detected;
				if_detected = usb[i].canRead();
				if (if_detected != usb_detected[i]) {
					if (if_detected)
						System.out.println("USB " + drive_name[i] + " detected ");

					usb_detected[i] = if_detected;
				}
			}
		}
	}
	
	public static void fileSizeConvert () {
		double size_bytes=7028067;
		String cnt_size;
		
		double size_kb = size_bytes /1024;
		double size_mb = size_kb / 1024;
		double size_gb = size_mb / 1024 ;
		
		System.out.println("KB = " + size_kb);
 
		 if (size_gb > 0){
			    cnt_size = size_gb + " GB";
	        }else if(size_mb > 0){
			    cnt_size = size_mb + " MB";
	        }else{
	    	           cnt_size = size_kb + " KB";
	        }	     
		System.out.println("Converted Size: " + cnt_size);		
	}
	
	public static MultipartFile convertFileToMultipartFile(String fileName) throws IOException {
		System.out.println("fileName="+fileName);
		File file = new File(fileName);
		FileItem fileItem = new DiskFileItem("mainFile", Files.probeContentType(file.toPath()), false, file.getName(),
				(int) file.length(), file.getParentFile());

		try {
			InputStream input = new FileInputStream(file);
			OutputStream os = fileItem.getOutputStream();
			IOUtils.copy(input, os);
			// Or faster..
			// IOUtils.copy(new FileInputStream(file), fileItem.getOutputStream());
		} catch (IOException ex) {
			// do something.
		}

		MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
		
		System.out.println("multipartFile="+multipartFile.getOriginalFilename()+" size="+multipartFile.getSize());
		
		return multipartFile;
	}
	
	public static void localDate() {
		LocalDate crrDate = LocalDate.now();
		LocalDate backDate = crrDate.minusMonths(2);
		LocalDate backDate2 = crrDate.minusDays(20);
		long daysBetween = ChronoUnit.DAYS.between(backDate2, crrDate);
		
		LocalDateTime curTime = LocalDateTime.now();
		
		String toDate = curTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss"));
		
		System.out.println("Backdate="+backDate2+"; days between="+daysBetween+"; toDate="+toDate);
		
		System.out.println("Year="+crrDate.getYear()+"; Month value="+crrDate.getMonthValue()+"; Day of month="+crrDate.getDayOfMonth());
		System.out.println("Year="+crrDate.getYear()+"; Month getMonth="+crrDate.getMonth()+"; Day of getDayOfYear="+crrDate.getDayOfYear());
		System.out.println("Year="+crrDate.getYear()+"; Month getMonth="+crrDate.getMonth()+"; Day of getDayOfWeek="+crrDate.getDayOfWeek());
	}
	
	public static void segregateFileName () {
		
		//IF 
		//>> file -> 220517_KX225HD.csv -> lot KX225HD -> model=KX, y=2022, m=5, d=H, prdLn=D
		
		//MGG 
		//>> file -> 569_2021073107480.csv -> lot KMY569210731M1 -> model=569, y=2021, m=7, d=31, prdLn=M1
		
		//GTMS 
		//>> file -> Mikron cell 1,2.1,3 KMY210104504.txt -> lot 500421403104 -> model=5004, y=2021, m=4, d=0, prdLn=3, seq=104
		
		//GTMS 
		//>> file -> Back end FET2,3 5004_500421403104.csv -> lot 500421403104  -> model=5004, y=2021, m=4, d=0, prdLn=3, seq=104
		
		//GTMS 
		//>> file -> Back end FET1 500421403104.csv -> lot 500421403104  -> model=5004, y=2021, m=4, d=0, prdLn=3, seq=104
		
		
//		String originalFileName = "220517_KX225HD.csv";// IF
//		String originalFileName = "569_2021073107480.csv";// MGG
		String originalFileName = "KMY210104504.txt";// GTMS mikron ??
//		String originalFileName = "5004_500421403104.csv";// GTMS Back end FET2,3
//		String originalFileName = "500421403104.csv";// GTMS Back end FET1
		
//		String format = "model=8,2|year=1,2|month=3,2|day=5,2|prodLn=14,1|seq=NA|lot=8,7"; //IF
//		String format = "model=1,3|year=5,4|month=9,2|day=11,2|prodLn=NA|seq=NA|lot=NA"; //MGG
		String format = "model=NA|year=4,2|month=NA|day=NA|prodLn=NA|seq=7,3|lot=NA"; //GTMS mikron ??
//		String format = "model=6,4|year=10,2|month=12,1|day=NA|prodLn=13,2|seq=15,3|lot=6,12"; //GTMS Back end FET2,3
//		String format = "model=1,4|year=5,2|month=7,1|day=NA|prodLn=8,2|seq=10,3|lot=1,12"; //GTMS Back end FET1
		
		System.out.println("name ok? "+Pattern.compile("^[A-Z]").matcher(originalFileName).find());
		// remove file extension
		originalFileName = StringUtils.substring(originalFileName, 0, originalFileName.lastIndexOf("."));
		int nameLength = originalFileName.length();
		System.out.println("current file name = "+originalFileName+"; length="+nameLength);
		
		StringTokenizer items = new StringTokenizer(format, "|");
		while (items.hasMoreElements()) {
			String item = (String) items.nextElement();
			String key = item.split("=")[0];
			String val = item.split("=")[1];
			int post = 0; int len = 0;
			String model = ""; String year = ""; String month = ""; String day = ""; String prodLn = ""; String seq = ""; String lot = "";
			
			if (StringUtils.isEmpty(key))
				continue;

			if (StringUtils.isEmpty(val) || StringUtils.equalsIgnoreCase(val, "na"))
				continue;

			if (val.split(",").length < 2)
				continue;
			
			post = Integer.valueOf(val.split(",")[0]);
			len = Integer.valueOf(val.split(",")[1]);
			
			String result = StringUtils.substring(originalFileName, post-1, post-1+len);
			
			if(key.equalsIgnoreCase("model")) {
				model = result;
				System.out.println("file="+originalFileName+"; post="+post+" len="+len+"; model => "+model);
			} else if(key.equalsIgnoreCase("year")) {
				year = result;
				if (StringUtils.isNotEmpty(year) && year.length() == 2) {
					year = "20" + year;
				}
				System.out.println("file="+originalFileName+"; post="+post+" len="+len+"; year => "+year);
			} else if (key.equalsIgnoreCase("month")) {
				month = result;
				if (StringUtils.isNotEmpty(month) && month.length() == 1) {
					month = "0" + month;
				}
				System.out.println("file="+originalFileName+"; post="+post+" len="+len+"; month => "+month);
			} else if (key.equalsIgnoreCase("day")) {
				day = result;
				if (StringUtils.isNotEmpty(day) && day.length() == 1) {
					day = "0" + day;
				}
				System.out.println("file="+originalFileName+"; post="+post+" len="+len+"; day => "+day);
			} else if (key.equalsIgnoreCase("prodLn")) {
				prodLn = result;
				System.out.println("file="+originalFileName+"; post="+post+" len="+len+"; prodLn => "+prodLn);
			} else if (key.equalsIgnoreCase("seq")) {
				seq = result;
				System.out.println("file="+originalFileName+"; post="+post+" len="+len+"; seq => "+seq);
			} else if (key.equalsIgnoreCase("lot")) {
				lot = result;
				System.out.println("file="+originalFileName+"; post="+post+" len="+len+"; lot => "+lot);
			}
			
		}
	}
	
	public static void createDirectories() {
		try {

			Path path = Paths.get("D:/PFC/DATA/GTMS/01.Mikron/Mikron#3/2023/Cell 1/prod/");

			// java.nio.file.Files;
			Files.createDirectories(path);

			System.out.println("Directory is created!");

		} catch (IOException e) {

			System.err.println("Failed to create directory!" + e.getMessage());

		}
	}
	
	public static boolean isEmpty(Path path) throws IOException {
		if (Files.isDirectory(path)) {
			try (Stream<Path> entries = Files.list(path)) {
				return !entries.findFirst().isPresent();
			}
		}

		return false;
	}
	
	public static boolean fileExists() throws IOException {
		Path path = Paths.get("C:\\Users\\aminkamilm\\Downloads\\amin-mc-20230704a.pdf");
		boolean exists = Files.exists(path);     //true
		return exists;
	}
}
