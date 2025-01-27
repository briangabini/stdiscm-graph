package com.brngbn;

import com.brngbn.console.MainConsole;
import com.brngbn.graph.Graph;
import com.brngbn.graph.GraphPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String fileName;
        if (args.length > 0) {
            fileName = args[0];
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the graph configuration file name: ");
            fileName = scanner.nextLine();
        }

        Graph graph = new Graph();
        try {
            graph.readGraphConfig(fileName);
            GraphPrinter.printGraph(graph);
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