package swayvil.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

public class Converter {
	private final String targetFolderName = "target";
	private File srcDirectory = null;
	private File destDirectory = null;
	private String srcDirPath = null;
	private File srcTargetDirectory = null;
	
	public void convert(String srcPath, String destPath) {
		initRootDir(srcPath, destPath);
		if (srcDirectory != null && srcDirPath != null) {
			replacePatterns("ABC", "abc");
			moveTargetContentToDestFolder();
		}
	}

	private void initRootDir(String srcPath, String destPath) {
		if (srcPath == null || destPath == null) {
			System.err.println("[ERROR] Incorrect directories.");
			return;
		}
		srcDirectory = new File(srcPath);
		srcDirPath = srcDirectory.getAbsolutePath();
		System.out.println("[INFO] Directory: " + srcDirPath);
		
		destDirectory = new File(destPath);

		try {
			// Delete old target directory if exists
			srcTargetDirectory = new File(srcDirPath + File.separator
					+ targetFolderName);
			if (srcTargetDirectory != null && srcTargetDirectory.exists())
				FileUtils.deleteDirectory(srcTargetDirectory);
			srcTargetDirectory.mkdir();
		} catch (IOException e) {
			System.err.println("[ERROR] Fail to delete old target directory.");
			e.printStackTrace();
		}
	}

	final IOFileFilter fileFilter = new IOFileFilter() {
		@Override
		public boolean accept(File file, String s) {
			return file.isFile();
		}

		@Override
		public boolean accept(File file) {
			return !file.getPath().endsWith(".jar");
		}
	};

	final IOFileFilter dirFilter = new IOFileFilter() {
		@Override
		public boolean accept(File file, String s) {
			return file.isDirectory();
		}

		@Override
		public boolean accept(File file) {
			return !file.getName().equals("target");
		}
	};

	private void replacePatterns(String patternSrc, String patternDest) {
		try {
			// Iterate over the files in the given directory and its
			// subdirectories
			Iterator<File> it = FileUtils.iterateFilesAndDirs(srcDirectory,
					fileFilter, dirFilter);
			Charset charset = StandardCharsets.UTF_8;

			System.out.println("[INFO] Replacing " + patternSrc + " characters by " + patternDest);
			while (it.hasNext()) {
				File file = (File) it.next();
				if (file.isFile()) {
					String content;
					content = IOUtils.toString(new FileInputStream(file),
							charset);
					content = content.replaceAll(patternSrc,
							patternDest);

					String targetDirPath = file.getAbsolutePath().replace(
							srcDirPath,
							srcDirPath + File.separator + targetFolderName
									+ File.separator);
					IOUtils.write(content, new FileOutputStream(targetDirPath),
							charset);
				} else {
					String targetDirPath = file.getAbsolutePath().replace(
							srcDirPath,
							srcTargetDirectory.getAbsolutePath());
					File newDir = new File(targetDirPath);
					newDir.mkdirs();
				}
			}
		} catch (FileNotFoundException e) {
			System.err
					.println("[ERROR] While replacing caracters, file not found.");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("[ERROR] While replacing caracters.");
			e.printStackTrace();
		}
	}
	
	private void deleteAllFolders(File folder) {
		File[] files = folder.listFiles();
		if (files != null) { // some JVMs return null for empty dirs
			for (File f : files) {
				if (f.isDirectory()) {
					deleteFolder(f);
				}
			}
		}
	}

	private void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) { // some JVMs return null for empty dirs
			for (File f : files) {
				if (f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}
	
	private void moveTargetContentToDestFolder() {
		if (destDirectory != null && destDirectory.isDirectory()) {
			System.out.println("[INFO] Deleting folders in: " + destDirectory.getAbsolutePath());
			deleteAllFolders(destDirectory);
			System.out.println("[INFO] Copiing updated source folders to destination folder");
			try {
				FileUtils.copyDirectory(srcTargetDirectory, destDirectory);
			} catch (IOException e) {
				System.err.println("[ERROR] During copiing folders to destination folder");
				e.printStackTrace();
			}
		}
	}
}