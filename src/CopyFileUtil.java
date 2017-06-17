/**
 * @author Runtian Zhai
 * @license MIT
 * 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * A class that supports file or folder copying operation. The source code
 * comes from the Internet.
 */
class CopyFileUtil {

	/** 
	 * 复制单个文件 
	 *  
	 * @param srcFileName 
	 *            待复制的文件名 
	 * @param descFileName 
	 *            目标文件名 
	 * @param overlay 
	 *            如果目标文件存在，是否覆盖 
	 * @return 如果复制成功返回true，否则返回false 
	 */  
	public static boolean copyFile(String srcFileName, String destFileName,  
			boolean overlay) {  
		File srcFile = new File(srcFileName);  

		// 判断源文件是否存在  
		if (!srcFile.exists()) {  
			return false;  
		} else if (!srcFile.isFile()) {  
			return false;  
		}  

		// 判断目标文件是否存在  
		File destFile = new File(destFileName);  
		if (destFile.exists()) {  
			// 如果目标文件存在并允许覆盖  
			if (overlay) {  
				// 删除已经存在的目标文件，无论目标文件是目录还是单个文件  
				new File(destFileName).delete();  
			}  
		} else {  
			// 如果目标文件所在目录不存在，则创建目录  
			if (!destFile.getParentFile().exists()) {  
				// 目标文件所在目录不存在  
				if (!destFile.getParentFile().mkdirs()) {  
					// 复制文件失败：创建目标文件所在目录失败  
					return false;  
				}  
			}  
		}  

		// 复制文件  
		int byteread = 0; // 读取的字节数  
		InputStream in = null;  
		OutputStream out = null;  

		try {  
			in = new FileInputStream(srcFile);  
			out = new FileOutputStream(destFile);  
			byte[] buffer = new byte[1024];  

			while ((byteread = in.read(buffer)) != -1) {  
				out.write(buffer, 0, byteread);  
			}  
			return true;  
		} catch (FileNotFoundException e) {  
			return false;  
		} catch (IOException e) {  
			return false;  
		} finally {  
			try {  
				if (out != null)  
					out.close();  
				if (in != null)  
					in.close();  
			} catch (IOException e) {  
				e.printStackTrace();  
			}  
		}  
	}  

	/** 
	 * 复制整个目录的内容 
	 *  
	 * @param srcDirName 
	 *            待复制目录的目录名 
	 * @param destDirName 
	 *            目标目录名 
	 * @param overlay 
	 *            如果目标目录存在，是否覆盖 
	 * @return 如果复制成功返回true，否则返回false 
	 */  
	public static boolean copyDirectory(String srcDirName, String destDirName,  
			boolean overlay) {  
		// 判断源目录是否存在  
		File srcDir = new File(srcDirName);  
		if (!srcDir.exists()) {  
			return false;  
		} else if (!srcDir.isDirectory()) {  
			return false;  
		}  

		// 如果目标目录名不是以文件分隔符结尾，则加上文件分隔符  
		if (!destDirName.endsWith(File.separator)) {  
			destDirName = destDirName + File.separator;  
		}  
		File destDir = new File(destDirName);  
		// 如果目标文件夹存在  
		if (destDir.exists()) {  
			// 如果允许覆盖则删除已存在的目标目录  
			if (overlay) {  
				new File(destDirName).delete();  
			} else {  
				return false;  
			}  
		} else {  
			// 创建目的目录  
			System.out.println("目的目录不存在，准备创建。。。");  
			if (!destDir.mkdirs()) {  
				System.out.println("复制目录失败：创建目的目录失败！");  
				return false;  
			}  
		}  

		boolean flag = true;  
		File[] files = srcDir.listFiles();  
		for (int i = 0; i < files.length; i++) {  
			// 复制文件  
			if (files[i].isFile()) {  
				flag = CopyFileUtil.copyFile(files[i].getAbsolutePath(),  
						destDirName + files[i].getName(), overlay);  
				if (!flag)  
					break;  
			} else if (files[i].isDirectory()) {  
				flag = CopyFileUtil.copyDirectory(files[i].getAbsolutePath(),  
						destDirName + files[i].getName(), overlay);  
				if (!flag)  
					break;  
			}  
		}  
		if (!flag) {  
			return false;  
		} else {  
			return true;  
		}  
	} 

	/**
	 * Delete a file.
	 * @param sPath The path of the file.
	 * @return Whether the file is successfully deleted.
	 */
	public static boolean deleteFile(String sPath) {  
		boolean flag = false;  
		File file = new File(sPath);  
		// 路径为文件且不为空则进行删除  
		if (file.isFile() && file.exists()) {  
			file.delete();  
			flag = true;  
		}  
		return flag;  
	} 
	
	/**
	 * Rename a file.
	 * @param path The path of the old file.
	 * @param newpath The new path of the file.
	 * @param _replace Whether it is allowed to replace a file that already exists.
	 * @return Whether the file is successfully renamed.
	 */
	public static boolean renameFile(String path,String newpath, boolean _replace) { 
		if(!path.equals(newpath)){//新的文件名和以前文件名不同时,才有必要进行重命名 
			File oldfile=new File(path); 
			File newfile=new File(newpath); 
			if(!oldfile.exists()){
				return false;//重命名文件不存在
			}
			if(newfile.exists()) {
				if(_replace) {
					newfile.delete();
					oldfile.renameTo(newfile);
					return true;
				}
				else
					return false;
			}
			else { 
				oldfile.renameTo(newfile); 
				return true;
			} 
		}
		return false;
	}
}
