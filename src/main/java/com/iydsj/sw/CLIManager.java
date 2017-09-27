package com.iydsj.sw;

import org.apache.commons.cli.*;

import java.util.Arrays;

/**
 * Created by xiang on 17/9/20.
 */
public class CLIManager {

    Options options;

    public static final String HELP = "l";

    public CLIManager(){
        options = new Options();

        options.addOption(HELP,false,"display options");
        options.addOption("s","sw",true, "display current time");

    }

    public CommandLine parse(String[] args ) throws ParseException
    {
        // We need to eat any quotes surrounding arguments...
        String[] cleanArgs = CleanArgument.cleanArgs( args );
        CommandLineParser parser = new DefaultParser();
        return parser.parse( options, cleanArgs );
    }


//    public static void main(String[] s) throws Exception{
//        Options options = new Options();
//        options.addOption("s","sw",true, "display current time");
//        CommandLineParser parser = new DefaultParser();
//        String[] args = new String[]{"--sw=185"};
//        CommandLine cmd = parser.parse( options, args);
//
//
//        System.out.println(cmd.getOptionValue("sw"));
//    }
}
