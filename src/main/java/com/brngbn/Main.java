package com.brngbn;

import com.brngbn.console.MainConsole;
import com.brngbn.graph.GraphImpl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Set logger level
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "Debug");

        String fileName;
        if (args.length > 0) {
            fileName = args[0];
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the graph configuration file name: ");
            fileName = scanner.nextLine();
        }

        GraphImpl graph = new GraphImpl();
        try {
//            graph.readGraphConfig(fileName);
            graph.readGraphConfig("ps2/weighted_custom_tc1.txt");
            MainConsole.handleUserQueries(graph);
        } catch (IOException e) {
            e.printStackTrace();
            try (FileWriter writer = new FileWriter("error_log.txt")) {
                writer.write("Failed to find file: " + fileName);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}