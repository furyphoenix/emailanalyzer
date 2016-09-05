package neos.tool.unpack;

import java.io.File;

public interface NeosUnpackTool {
	boolean isSupport(File file);
	void unpack(File source, File target, boolean iter, boolean isRemove) throws NeosUnpackWrongPasswordException;
}