package neos.tool.unpack;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


public class Neos7ZipTool implements NeosUnpackTool{
	private String path;
	private final static String defaultPath=".\\tool\\7z.exe";
	private final static String[] exts={"rar","zip","7z","tar","tgz","gz","bzip2"};
	private final static String wrongPasswordMsg="Wrong password?";
	
	public Neos7ZipTool(){
		this(defaultPath);
	}
	
	public Neos7ZipTool(String path){
		this.path=path;
	}

	@Override
	public boolean isSupport(File file) {
		if(!file.isFile()){
			return false;
		}
		
		String fileName=file.getAbsolutePath().toLowerCase();
		for(int i=0; i<exts.length; i++){
			if(fileName.endsWith("."+exts[i])){
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void unpack(File source, File target, boolean iter,
			boolean isRemove) throws NeosUnpackWrongPasswordException{
		
		if(!target.exists()){
			target.mkdirs();
		}
		
		if(!target.isDirectory()){
			return;
		}
		
		if(!iter){
			if(isSupport(source)){
				unpack(source, target.getAbsolutePath());
				if(isRemove){
					source.delete();
				}
			}
		}else{
			if(source.isDirectory()){
				String destPath;
				if(!source.equals(target)){
					String sourceFileName=source.getName();
					destPath=target.getAbsolutePath()+"\\"+sourceFileName;
				}else{
					destPath=target.getAbsolutePath();
				}
				
				File dest=new File(destPath);
				File[] children=source.listFiles();
				for(int i=0; i<children.length; i++){					
					unpack(children[i],dest,true,isRemove);
				}
			}else{
				if(isSupport(source)){
					unpack(source, target.getAbsolutePath());
					if(isRemove){
						source.delete();
					}
					unpack(target, target, true, isRemove);
				}
			}
		}
	}
	
	private void unpack(File file, String dest) throws NeosUnpackWrongPasswordException{
		String cmd=path+" x \""+file.getAbsolutePath()+"\" -o\""+dest+"\" -p12345 -y";
		Runtime rt = Runtime.getRuntime();
		try {
			Process p = rt.exec(cmd);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s;
			boolean isContainEncryptFile=false;
			while((s=bufferedReader.readLine()) != null){
				if(s.contains(wrongPasswordMsg)){
					isContainEncryptFile=true;
					continue;
				}
			}
			p.waitFor();
			p.destroy();
			if(isContainEncryptFile){
				throw new NeosUnpackWrongPasswordException(file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	
	
}
