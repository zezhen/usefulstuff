package me.lcode.usecase.util;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CmdLineParser {

	
	@SuppressWarnings("static-access")
	public static void usecase1(String[] args) {

		// TODO 2. how to make much complex usage, now it's quite simple.
		Options options = new Options();
		options.addOption("h", "help", false, "print the help info");
		
		OptionGroup og = new OptionGroup();
		// TODO 1. can these required field be ignor when I just want to know the usage?
		og.addOption(OptionBuilder.hasArg().withArgName("start").withDescription("query start time").isRequired().create('s'));
		og.addOption(OptionBuilder.hasArg().withArgName("end").withDescription("query end time").isRequired().create('e'));
		
		options.addOption("i", "id", true,
				"query object id, if not set, return the all querable object ids in time range");
		options.addOption("t", "type", true,
				"query object type, set with object id");
		options.addOptionGroup(og);
		
		BasicParser parser = new BasicParser();
		CommandLine cl;

		try {
			cl = parser.parse(options, args);

			if (cl.hasOption('h')) {
				HelpFormatter hf = new HelpFormatter();
				hf.printHelp("Options", options);
			} else {
				String startTime = cl.getOptionValue('s');
				String endTime = cl.getOptionValue('e');

				if (cl.hasOption('i') && cl.hasOption('t')) {
					String id = cl.getOptionValue('i');
					String type = cl.getOptionValue('t');
					String ret = String
							.format("query start time: %s, end time: %s, object id: %s, object type: %s",
									startTime, endTime, id, type);
					System.out.println(ret);
				} else {
					String ret = String
							.format("query start time: %s, end time: %s",
									startTime, endTime);
					System.out.println(ret);
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		args = new String[]{"-h"};
		usecase1(args);
		args = new String[]{ "-e", "201311082334"};
		usecase1(args);
	}

}
