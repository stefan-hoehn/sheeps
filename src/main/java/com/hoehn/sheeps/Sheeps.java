package com.hoehn.sheeps;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Sheeps {
    public static void main(String[] args) throws IOException {
        String zwillinggemPath = "ALLE";
        String abgangTotPath = "TOT";
        String abgangVKPath = "VERKAUFT";
        String bestandPath = "BESTAND";
        int sheetIndex = 0;  // Index of the sheet you want to read (zero-based)
        int startRow = 1;    // Starting row index (zero-based)

        try {
            copyFiles();

            Map<String, Sheep> sheeps = new HashMap<>();
            try {
                File allSheeps = queryFirstFile(zwillinggemPath);
                if (allSheeps==null) {
                    System.out.println("Keine Datei mit "+ zwillinggemPath + " im Namen gefunden.");
                    return;
                } else {
                    System.out.println("Verarbeite "+ allSheeps.getName());
                }
                FileInputStream file = new FileInputStream(allSheeps);
                Workbook workbook = new HSSFWorkbook(file);

                Sheet sheet = workbook.getSheetAt(sheetIndex);
                for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        Sheep sheep = new Sheep();
                        sheep.eid = getCell(row, 0).trim();
                        sheep.vid = getCell(row, 1);
                        sheep.datum = getCell(row, 2);
                        sheep.zeit = getCell(row, 3);
                        sheep.anmerkung = getCell(row, 4);
                        sheeps.put(sheep.eid, sheep);
                    }
                    workbook.close();

                    file.close();
                }
                System.out.println("Size in "+allSheeps.getName()+": " + sheeps.size());
                int allSheepsSize = sheeps.size();

                final File abgaengeTod = queryFirstFile(abgangTotPath);
                if (abgaengeTod==null) {
                    System.out.println("Keine Datei mit "+ abgangTotPath + " im Namen gefunden.");
                    return;
                }
                file = new FileInputStream(abgaengeTod);
                workbook = new HSSFWorkbook(file);

                System.out.println("\nVerarbeite "+(sheet.getLastRowNum()-1) + " TOTE Schafe");
                sheet = workbook.getSheetAt(sheetIndex);
                int nichtGefunden = 0;
                int gefunden = 0;
                for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        Sheep sheep = new Sheep();
                        String eid = getCell(row, 0).trim();
                        if (sheeps.containsKey(eid)) {
                            sheeps.remove(eid);
                            gefunden++;
                        } else {
                            System.out.println("Schaf "+eid+" nicht in " + zwillinggemPath + " gefunden - ");
                            nichtGefunden++;
                        }
                    }
                }
                System.out.println("gelistete tote Schafe = "+ (sheet.getLastRowNum()));
                System.out.println("gefundene tote Schafe: " + gefunden);
                System.out.println("nicht gefundene tote Schafe: " + nichtGefunden +"\n");
                System.out.println("Size after removing TOD: " + sheeps.size());
                System.out.println("Size = "+ sheeps.size() +" = " +allSheepsSize+ " - " + gefunden + " tote Schafe" );

                allSheepsSize = sheeps.size();
                nichtGefunden = 0;
                gefunden = 0;
                final File abgaengeVK = queryFirstFile(abgangVKPath);
                if (file==null) {
                    System.out.println("Keine Datei mit "+ abgangVKPath + " im Namen gefunden.");
                    return;
                }

                file = new FileInputStream(abgaengeVK);
                workbook = new HSSFWorkbook(file);

                sheet = workbook.getSheetAt(sheetIndex);

                System.out.println("\nVerarbeite "+(sheet.getLastRowNum()-1) + " VERKAUFTE Schafe");
                for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        String eid = getCell(row, 0).trim();
                        if (sheeps.containsKey(eid)) {
                            sheeps.remove(eid);
                            gefunden++;
                        } else {
                            System.out.println("Schaf "+eid+" nicht in " + zwillinggemPath + " gefunden");
                            nichtGefunden++;
                        }
                    }
                }
                System.out.println("gelistete verkaufte Schafe = "+ (sheet.getLastRowNum()));
                System.out.println("gefundene verkaufte Schafe: " + gefunden);
                System.out.println("nicht gefundene verkaufte Schafe: " + nichtGefunden +"\n");
                System.out.println("Size after removing ABGANG: " + sheeps.size());
                System.out.println("Size = "+ sheeps.size() +" = " +allSheepsSize+ " - " + gefunden + " tote Schafe" );

            } catch (IOException e) {
                e.printStackTrace();
            }
         // Create a new workbook
            Workbook workbook = new HSSFWorkbook();

            // Create a new sheet
            Sheet sheet = workbook.createSheet("Sheet1");


            AtomicInteger rowNum = new AtomicInteger();
            Row row = sheet.createRow(rowNum.getAndIncrement());
            int col = 0;
            Cell cellheader = row.createCell(col++);
            cellheader.setCellValue("EID");
            cellheader = row.createCell(col++);
            cellheader.setCellValue("VID");
            cellheader = row.createCell(col++);
            cellheader.setCellValue("Datum");
            cellheader = row.createCell(col++);
            cellheader.setCellValue("Zeit");
            cellheader = row.createCell(col++);
            cellheader.setCellValue("ANMERKUNG");


            sheeps.forEach( (key, sheep) -> {
                    Row sheeprow = sheet.createRow(rowNum.getAndIncrement());
                    int colNum = 0;
                    Cell cell = sheeprow.createCell(colNum++);
                    cell.setCellValue((String) sheep.eid);
                    cell = sheeprow.createCell(colNum++);
                    cell.setCellValue((String) sheep.vid);
                    cell = sheeprow.createCell(colNum++);
                    cell.setCellValue((String) sheep.datum);
                    cell = sheeprow.createCell(colNum++);
                    cell.setCellValue((String) sheep.zeit);
                    cell = sheeprow.createCell(colNum++);
                    cell.setCellValue((String) sheep.anmerkung);
                }
            );

            // Write the workbook to a file
            String filePath = bestandPath+".xls";
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
                System.out.println("Aktualisierte Schaf-Datei wurde erzeugt:"+filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Close the workbook
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ich konnte leider keine Sicherheitskopie der Original-Dateien anlegen");
        }
    }


    private static String getCell(Row row, int i) {
        Cell cell = row.getCell(i);  // Assuming the data is in the first column
        if (cell != null) {
            String cellValue = cell.getStringCellValue();
            return cellValue;
        } else {
            return "";
        }
    }

    private static void copyFiles() throws IOException {

        LocalDate today = LocalDate.now();
        // Specify the destination directory path
        Path sourceDirectory = Paths.get("./");
        Path destinationDirectory = Path.of("./backup-"+today);

        String wildcardPattern = "*.*";
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(sourceDirectory, wildcardPattern)) {
            // Create the destination directory if it doesn't exist
            if (!Files.exists(destinationDirectory)) {
                Files.createDirectories(destinationDirectory);
            }

            // Iterate over the files in the source directory matching the wildcard pattern
            for (Path file : directoryStream) {
                // Calculate the destination file path
                Path destinationFile = destinationDirectory.resolve(file.getFileName());

                // Copy the file to the destination directory
                Files.copy(file, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

        }
        System.out.println("File copied successfully.");

    }

    public static File queryFirstFile(String filePattern) {
        File currentDir = new File(".");
        File[] files = currentDir.listFiles();

        // Filter the list of files to only include the files that end with the specified file pattern.
        List<File> filteredFiles = new ArrayList<>();
        for (File file : files) {
            if (file.getName().contains(filePattern)) {
                return file;
            }
        }
        return null;
    }
}
