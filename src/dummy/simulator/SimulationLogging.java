package dummy.simulator;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Used to configure logging.
 * 
 * @author khoa_nguyen
 *
 */
abstract class SimulationLogging {
	// Make sure this is only initialised once
	private static boolean initialised = false;

	// Creates a file handler that outputs everything and add's it to the root
	// logger.
	public static void init() {
		if (SimulationLogging.initialised) {
			return;
		} else {
			SimulationLogging.initialised = true;
		}

		try {

			// Get the root logger
			Logger logger = Logger.getLogger("");
			// Logger logger = Logger.getLogger("beddem_simulation");
			// logger.setLevel(Level.FINER);

			// Create handlers
			File logFile = new File("model_log.txt");
			if (logFile.exists())
				logFile.delete(); // Delete an old log file
			FileHandler fileHandler = new FileHandler(logFile.getAbsolutePath());
			fileHandler.setLevel(Level.ALL); // Write everything to the file

			// Create a formatters.
			Formatter fileFormatter = new SimpleFormatter();

			// Add the formatters to the handlers
			fileHandler.setFormatter(fileFormatter);

			// Add the handlers to the logger
			logger.addHandler(fileHandler);

		} catch (Exception e) {
			System.err.println("Problem creating loggers, cannot continue (exit with -1).");
			System.exit(-1);
		}

	}
}
