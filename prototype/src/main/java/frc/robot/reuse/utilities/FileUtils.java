package frc.robot.reuse.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class FileUtils
{
    /**
     * Read all files from the file system, parse as CSV, and merge together if they're the same size
     * @param filenamesWithPath
     * @return
     */
    public static ArrayList<String[]> mergeCsvFiles(ArrayList<String> file1, ArrayList<String> file2)
    {
        ArrayList<String[]> lines = new ArrayList<String[]>();

        int size = file1.size();
        if(size != file2.size())
        {
            for (int i = 0; i < size; i++)
            {
                lines.add(splitCsv(file1.get(i) + "," + file2.get(i)));
            }
        }

        return lines;
    }

    public static ArrayList<String[]> splitCsvFile(ArrayList<String> file)
    {
        ArrayList<String[]> lines = new ArrayList<String[]>();

        int size = file.size();
        for (int i = 0; i < size; i++)
        {
            lines.add(splitCsv(file.get(i)));
        }

        return lines;
    }

    private static String[] splitCsv(String line)
    {
        return line.split(",");
    }

    /**
     * Read a full file from the file system
     * @param filenameWithPath
     * @return
     */
    public static ArrayList<String> readFile(String filenameWithPath)
    {
        ArrayList<String> lines = new ArrayList<String>();

        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(filenameWithPath));
            String line;
            while((line = reader.readLine()) != null)
            {
                lines.add(line);
            }
            reader.close();
            return lines;
        }
        catch (Exception e)
        {
            ConsoleLogger.error("Exception occurred trying to read" + filenameWithPath);
            return lines;
        }
    }

    /**
     * Read just the first line of a file from the file system
     * @param filenameWithPath full path to the file
     * @return
     */
    public static String readLine(String filenameWithPath)
    {
        String singleLine = "";
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(filenameWithPath));
            String line;
            if ((line = reader.readLine()) != null)
            {
                singleLine = line;
            }
            reader.close();
            return singleLine;
        }
        catch (Exception e)
        {
            ConsoleLogger.error("Exception occurred trying to read " + filenameWithPath);
            return singleLine;
        }
    }

    /**
     * Make the file path use the current "user.home" path (on the Rio, this is /home/lvuser)
     * @param filePath
     * @return
     */
    public static String joinUserFolder(String filePath)
    {
        if(!filePath.startsWith("~/"))
        {
            filePath = "~/" + filePath;
        }

        filePath = filePath.replaceFirst("^~", System.getProperty("user.home"));
        return filePath;
    }

    public static boolean exists(String filenameWithPath)
    {
        return new File(filenameWithPath).exists();
    }
}