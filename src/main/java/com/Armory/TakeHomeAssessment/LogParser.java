package com.Armory.TakeHomeAssessment;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.stream.Stream;

public class LogParser {

    /*
    * My Approach:
    * As we have only 16 GB ram available
    * first I tried to read a line form every log file and put it in a maxHeap.
    * and get the largest timestamp
    * and collecting logs from each file which has timestamp less than largest timestamp to list and sorting and printing that list.
    * I am also storing the last read line number of each file in an array so that I makes me easy to get the next largest timestamp.(Yes, you are correct with
    *  log file with hundred's of Gb, line numbers will exceed the range of Integer which will result in failure)
    *
    *  */
    private static final String pathTologfiles = "/temp";
    private int[] lastReadLine;

    public void collectLogs(String pathTologfiles) throws Exception {

        File[] files = new File(pathTologfiles).listFiles();
        lastReadLine = new int[files.length];

        String timeStamp = getMaxTimeStamp(files);

        while(timeStamp.isEmpty()) {
            getLogs(files,timeStamp);
            timeStamp = getMaxTimeStamp(files);
        }
        return;
    }

    private String getMaxTimeStamp(File[] files) throws Exception {

        PriorityQueue<String> queue = new PriorityQueue<String>((a,b)-> b.compareTo(a));
        int fileNumber=0;
        for(File file : files) {
            Stream<String> lines = Files.lines(file.toPath());
            String nextLine = lines.skip(lastReadLine[fileNumber]).findFirst().get();
            if(!nextLine.isEmpty())
                queue.offer(nextLine.split(",")[0]);
        }
        return queue.isEmpty() ? "" : queue.poll();
    }

    private void getLogs(File[] files, String timeStamp) throws Exception {

        List<String> logsToPrint = new ArrayList<>();
        int fileNumber=0;
        for(File file : files) {
            int lineNumber =lastReadLine[fileNumber];
            Scanner scanner = new Scanner(file);
            while(scanner.hasNext()) {
                String line = scanner.nextLine();
                if (line.split(",")[0].compareTo(timeStamp) > 0)
                    break;
                logsToPrint.add(scanner.nextLine());
                lineNumber++;
            }
            lastReadLine[fileNumber++] = lineNumber;
        }
        logsToPrint.stream().sorted((a,b)-> a.split(",")[0].compareTo(b.split(",")[0])).forEach(line -> System.out.println(line));
    }


}
