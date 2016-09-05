package neos.app.email.gui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.verhas.licensor.HardwareBinder;
import com.verhas.licensor.License;

public class LicenseChecker {
	private final static String sha256="F3773F58EF3D52E5FBAD69034DC728F035D6679AC8113C7294CBF38C7C6C2B6C";
	private final static byte [] digest = trans(sha256);
	private final static SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd");
	
	private static byte[] trans(String cs){
		byte[] bytes=new byte[cs.length()/2];
		
		for(int i=0; i<cs.length()/2; i++){
			String str=cs.substring(i*2,i*2+2);
			int v=Integer.parseInt(str, 16);
			bytes[i]=(byte)v;
		}
		
		return bytes;
	}
	
	public static boolean checkLicense(){
		try{
			String fileName="lic.lic";
			File licFile=new File(fileName);
			License lic=new License();
			lic.loadKeyRing(".\\data\\pubring.gpg", digest);
			//byte[] sha=lic.calculatePublicKeyRingDigest();
			lic.setLicenseEncoded(licFile);
			String licMachineId=lic.getFeature("MachineID");
			HardwareBinder binder=new HardwareBinder();
			String machineId=binder.getMachineIdString();
			Date start=fmt.parse(lic.getFeature("StartTime"));
			Date end=fmt.parse(lic.getFeature("EndTime"));
			
			Date now=new Date();
			if((licMachineId.equals(machineId))&&(now.after(start))&&(now.before(end))){
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static String getMachineId(){
		HardwareBinder binder=new HardwareBinder();
		return binder.getMachineIdString();
	}
	
	public static String getUserName(){
		try{
			String fileName="lic.lic";
			License lic=new License();
			lic.loadKeyRing(".\\data\\pubring.gpg", digest);
			lic.setLicenseEncodedFromFile(fileName);
			String licMachineId=lic.getFeature("MachineID");
			HardwareBinder binder=new HardwareBinder();
			String machineId=binder.getMachineIdString();
			if(licMachineId.equals(machineId)){
				String licUserName=lic.getFeature("UserName");
				return licUserName;
			}else{
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static Date getStartTime(){
		try{
			String fileName="lic.lic";
			License lic=new License();
			lic.loadKeyRing(".\\data\\pubring.gpg", digest);
			lic.setLicenseEncodedFromFile(fileName);
			String licMachineId=lic.getFeature("MachineID");
			HardwareBinder binder=new HardwareBinder();
			String machineId=binder.getMachineIdString();
			if(licMachineId.equals(machineId)){
				String start=lic.getFeature("StartTime");
				return fmt.parse(start);
			}else{
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static Date getEndTime(){
		try{
			String fileName="lic.lic";
			License lic=new License();
			lic.loadKeyRing(".\\data\\pubring.gpg", digest);
			lic.setLicenseEncodedFromFile(fileName);
			String licMachineId=lic.getFeature("MachineID");
			HardwareBinder binder=new HardwareBinder();
			String machineId=binder.getMachineIdString();
			if(licMachineId.equals(machineId)){
				String end=lic.getFeature("EndTime");
				return fmt.parse(end);
			}else{
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
}
