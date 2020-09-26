package com.nicktackes;

import java.io.*;
import java.util.Scanner;

public class Utilities {

    public static File timelinePath = new File(System.getProperty("user.home") + "/.timelineFX");

    public static void initTimelineDir() {
        if (!timelinePath.exists()) {
            if (timelinePath.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Failed to create directory!");
            }
        }
    }

    public static void mozoomdar() throws IOException {
        File mozFile = new File(timelinePath + "/Sample Timeline.txt");
        if (!mozFile.exists()) {
            mozFile.createNewFile();

            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(mozFile, true)
            );

            writer.write("Independence Day%%1776-07-04%%1776-07-04%%The United States of America was freed from British Rule.%%false");
            writer.newLine();

            writer.write("The Emancipation Proclamation%%1863-01-01%%1863-01-01%%Abraham Lincoln nominally abolished slavery in the United States of America.%%false");
            writer.newLine();

            writer.write("Barack Obama's Presidency%%2009-01-20%%2017-01-20%%Barack Hussein Obama served as the 44th president of the United States of America.%%false");
            writer.newLine();

            writer.close();
        }

    }

    public static boolean isParsable(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    public static boolean isYear(String input) {
        return input.matches("^-?\\d+$");
    }

    public static boolean isMonth(String input) {
        return input.matches("([1-9]|0[1-9]|1[012])");
    }

    public static boolean isDay(String input) {
        return input.matches("([1-9]|0[1-9]|[12][0-9]|3[01])");
    }

    public static boolean validDate(String input) {
        return input.matches("(-?\\d+)-([1-9]|0[1-9]|1[012])-([1-9]|0[1-9]|[12][0-9]|3[01])");
    }


}
