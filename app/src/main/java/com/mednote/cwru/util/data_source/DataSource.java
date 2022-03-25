package com.mednote.cwru.util.data_source;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

// TODO: doesn't write or read spaces
public abstract class DataSource {
    protected File currentDir;

    public abstract void defaultDir();

    public abstract File getDefaultDir();

    public File getCurrentDir() {
        return currentDir;
    }

    public boolean moveDirectory(String dirName) {
        if (dirName.isEmpty()) {
            return false;
        }
        currentDir = new File(getCurrentDir(), dirName);
        if (!currentDir.exists()) {
            if (!currentDir.mkdirs()) {
                defaultDir();
                return false;
            }
        }
        return true;
    }

    public boolean moveDirectory(File directory) {
        if (directory == null) {
            return false;
        }
        currentDir = directory;
        if (!currentDir.exists()) {
            if (!currentDir.mkdirs()) {
                defaultDir();
                return false;
            }
        }
        return true;
    }

    public void moveFile(String fileName, String newDir) {
        File oldLocation = new File(getCurrentDir(), fileName);
        defaultDir();
        moveDirectory(newDir);

        // renaming the file and moving it to a new location
        if(oldLocation.renameTo(new File(getCurrentDir(), fileName))) {
            // if file copied successfully then delete the original file
            oldLocation.delete();
            System.out.println("File moved successfully");
        } else {
            System.out.println("Failed to move the file");
        }
    }

    public ArrayList<String> getFileNames() {
        File[] files = getCurrentDir().listFiles();
        ArrayList<String> filenames = new ArrayList<>();
        if (files != null) {
            for (File f : files) {
                filenames.add(f.getName());
            }
        }
        return filenames;
    }

    public List<String> getDirectoryNames() {
        ArrayList<String> dirNames = new ArrayList<>();
        List<File> directories = getDirectoryList();
        if (directories != null) {
            for (File d : directories) {
                dirNames.add(d.getName());
            }
        }
        return dirNames;
    }

    public List<File> getDirectoryList() {
        File[] directories = getCurrentDir().listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });
        if (directories != null) {
            return Arrays.asList(directories);
        }
        return new ArrayList<>();
    }

    public String readFile(String filename){

        // context.openFileInput(fileName, Context.MODE_PRIVATEs)
        File currentFile = new File(getCurrentDir(), filename);
        return readFile(currentFile);
    }

    public String readFile(File currentFile){
        //openFileInput reads data from internal storage.
        FileInputStream fis;
        Scanner scanner;
        String content = null;
        try {
            if (!currentFile.exists()) {
                return null;
            }
            fis = new FileInputStream(currentFile);
            scanner = new Scanner(fis);
            //A delimiter is most likely not going to be necessary.
            //scanner.useDelimiter("\\Z");
            String nextChar;
            while (scanner.hasNext()) {
                nextChar = scanner.next();
                if (content != null) {
                    content += nextChar;
                }
                else {
                    content = nextChar;
                }
            }
            scanner.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return content;
    }

    public void writeToFile(String filename, @org.jetbrains.annotations.NotNull String data) {
        this.writeToFile(new File(getCurrentDir(), filename), data);
    }

    public void writeToFile(File filename, @org.jetbrains.annotations.NotNull String data) {
        writeToFile(filename, new ArrayList<String>() {{add(data);}}, false);
    }

    public void writeToFile(File filename, @org.jetbrains.annotations.NotNull List<String> data) {
        writeToFile(filename, data, true);
    }

    public void writeToFile(File filename, @org.jetbrains.annotations.NotNull List<String> data, boolean append) {
        try {
            // fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            FileOutputStream fos = new FileOutputStream(filename, append);
            // To append with write: new FileOutputStream(filename, true);
            for (String dataPoint : data) {
                fos.write(dataPoint.getBytes());
            }
            fos.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public boolean fileExists(String filename) {
        File fileToCheck = new File(getCurrentDir(), filename);
        return fileToCheck.getAbsoluteFile().exists();
    }
}
