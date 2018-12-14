package com.team1389.watch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Supplier;

import com.team1389.util.Timer;

/**
 * handles logging to a specific given file
 * 
 * @author amind
 */
public class LogFile {
	private final String filename;
	private Writer writer;
	private boolean inited;
	private Timer timer;
	private LogType type;

	/**
	 * @param type the log file type
	 * @param filename the file to write to
	 */
	public LogFile(String filename, LogType type) {
		timer = new Timer();
		inited = false;
		this.type = type;
		this.filename = filename + getFileEnding();
	}

	/**
	 * uses the default log format, CSV
	 * 
	 * @param filename the file to write to
	 */
	public LogFile(String filename) {
		this(filename, LogType.CSV);
	}

	/**
	 * initializes the file
	 */
	public void init() {
		clearFile(filename);
		open();
		inited = true;
		timer.zero();
	}

	/**
	 * @return whether this file has been initialized
	 */
	public boolean isInited() {
		return inited;
	}

	/**
	 * LogFile must be initialized before calling this
	 * 
	 * @return the writer for this log file
	 */
	public Writer getWriter() {
		if (!inited) {
			init();
		}
		return writer;
	}

	/**
	 * @return the time since this file was initialized
	 */
	public double getTimeStamp() {
		return timer.get();
	}

	void close() {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void open() {
		try {
			System.out.println("initializing logger for file " + filename);
			writer = new BufferedWriter(new FileWriter(filename, true), 32768);
		} catch (IOException e) {
			System.out.println("logger faild to initialize");
			e.printStackTrace();
		}
	}

	/**
	 * writes the given list of column headings to the file <br>
	 * assumes the file is blank
	 * 
	 * @param values the list of headings to fill the row with
	 * @throws IOException if the writer fails to append
	 */
	public void writeHeadings(List<String> values) throws IOException {
		writer.append("Time" + type.seperator);
		for (String heading : values) {
			writer.append(heading + type.seperator);
		}
		writer.flush();
	}

	/**
	 * writes the given list of double values as a row in the log file <br>
	 * assumes writer is still on the previous lines
	 * 
	 * @param values the list of values to fill the row with
	 * @throws IOException if writer fails to append
	 */
	public void writeRow(List<Double> values) throws IOException {
		writer.append("\n");
		writer.append(getTimeStamp() + type.seperator);
		for (Double val : values) {
			writer.append(val + type.seperator);
		}
		writer.flush();
	}

	/**
	 * deletes all existing text in the file
	 * 
	 * @param filename the file to clear
	 */
	public static void clearFile(String filename) {
		if (new File(filename).exists()) {
			PrintWriter w;
			try {
				w = new PrintWriter(filename);
				w.print("");
				w.close();
			} catch (FileNotFoundException e) {
			}
		}
	}
	// Log File Naming util

	/**
	 * generates a LogFile with the given naming convention and folder path
	 * 
	 * @param format the naming convention, a String supplier
	 * @param path the string path to the log folder - should end with a '/'
	 * @param type the format of the log file
	 * @see FileNameSupplier
	 * @return a fresh LogFile
	 * @see LogFile#dateTimeFormatter
	 * @see LogFile#getFormatter(DateTimeFormatter)
	 */
	public static LogFile make(FileNameSupplier format, String path, LogType type) {
		new File(path).mkdirs();
		return new LogFile(path + format.get(), type);
	}

	/**
	 * generates a LogFile with the given naming convention <br>
	 * uses the default folder path
	 * 
	 * @param format the naming convetion, a String supplier
	 * @param type the format of the logfile
	 * @return a fresh LogFile
	 * @see LogFile#dateTimeFormatter
	 * @see LogFile#getFormatter(DateTimeFormatter)
	 */
	public static LogFile make(FileNameSupplier format, LogType type) {
		return make(format, "", type);
	}

	/**
	 * generates the default LogFile
	 * 
	 * @return a fresh LogFile
	 */
	public static LogFile make() {
		return make(LogFile::dateTime, LogType.CSV);
	}

	/**
	 * @return a {@link FileNameSupplier} that supplies the a string representation of the date and
	 *         time in the default format
	 */
	public static FileNameSupplier dateTimeFormatter() {
		return LogFile::dateTime;
	}

	/**
	 * 
	 * @param format the date/time format to use
	 * @return a {@link FileNameSupplier} that supplies the a string representation of the date and
	 *         time in the given format
	 */
	public static FileNameSupplier getFormatter(DateTimeFormatter format) {
		return () -> dateTime(format);
	}

	private static String dateTime() {
		return dateTime(DateTimeFormatter.ofPattern("MM-dd-yy-HH-mm-s"));
	}

	private static String dateTime(DateTimeFormatter format) {
		return LocalDateTime.now().format(format);
	}

	@Override
	public String toString() {
		return filename;
	}

	/**
	 * a wrapper around the {@link java.util.function.Supplier} for Strings specifically
	 * 
	 * @author amind
	 */
	public interface FileNameSupplier extends Supplier<String> {

		/**
		 * generates a new supplier with the string concatenated to the result of the old supplier
		 * <br>
		 * <em>NOTE</em>: no spaces or seperators are added between the {@code add} and the result
		 * by default
		 * 
		 * @param add the String
		 * @return a new FileNameSupplier with the modified get method
		 */
		public default FileNameSupplier concat(String add) {
			return () -> {
				return add + get();
			};
		}
	}

	/**
	 * sets the log format of this file
	 * 
	 * @param type the log format to use
	 * @throws IOException if the file has already been initialized
	 */
	public void setLogType(LogType type) throws IOException {
		if (inited) {
			throw new IOException("Too late to set LogType, file already initialized");
		} else {
			this.type = type;
		}
	}

	private String getFileEnding() {
		return type.fileEnding;
	}

	/**
	 * represents a log format
	 * 
	 * @author amind
	 *
	 */
	public enum LogType {
		/**
		 * the TSV (Tab seperated values) file format
		 */
		TSV(".tsv", "\t"),
		/**
		 * the CSV (Comma seperated values) file format
		 */
		CSV(".csv", ",");
		private final String fileEnding;
		private final String seperator;

		LogType(String fileEnding, String seperator) {
			this.fileEnding = fileEnding;
			this.seperator = seperator;
		}
	}
}
