package swayvil.utils;
import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {
	public static void main(String args[]) {
		String srcPathDir = "";
		String destPathDir = "";
		Options options;

		CommandLineParser parser = new DefaultParser();
		try {
			options = createOptions();
			CommandLine cmd = parser.parse(options, args);
			
			if (cmd.hasOption("h")) {
				// automatically generate the help statement
				HelpFormatter helpFormatter = new HelpFormatter();
				helpFormatter.printHelp("Files replace pattern", options);
			}
			else
			{
				if (cmd.hasOption("s"))
				{
					srcPathDir = cmd.getOptionValue("s");
					String lastChar = srcPathDir.substring(srcPathDir.length() - 1); 
					if (lastChar.compareTo("/") != 0 && lastChar.compareTo("\\") != 0)
						srcPathDir += "/";
				}
				else
				{
					srcPathDir = getCurrentPath();
					System.out.println("[Info] Get input files in current directory");
				}
				if (cmd.hasOption("d"))
				{
					destPathDir = cmd.getOptionValue("d");
					String lastChar = destPathDir.substring(destPathDir.length() - 1); 
					if (lastChar.compareTo("/") != 0 && lastChar.compareTo("\\") != 0)
						destPathDir += "/";
				}
				Converter converter = new Converter();
				converter.convert(srcPathDir, destPathDir);
			}
		} catch (ParseException e1) {
			System.err.println("Unexpected exception: " + e1.getMessage() );
		}
	}

	private static String getCurrentPath() {
		String currentDirectory = "";
		File file = new File(".");
		currentDirectory = file.getAbsolutePath();
		return currentDirectory.substring(0, currentDirectory.length()- 1);
	}

	private static Options createOptions() {
		Options options = new Options();
		options.addOption("s", true, "Path of the source directory");
		options.addOption("d", true, "Path of the destination directory");
		options.addOption("h", false, "Usage");
		return options;
	}
}
