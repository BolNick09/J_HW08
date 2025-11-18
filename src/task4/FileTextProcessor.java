package task4;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FileTextProcessor
{
    private final String directory;
    private final String searchWord;
    private final char[] specialChars = {'!', '@', '#', '$', '%', '^', '&', '*', '(', ')',  //Вместо файла с запрещёнными словами
            '-', '_', '=', '+', '[', ']', '{', '}', ';', ':',
            '"', '\'', '<', '>', ',', '.', '?', '/', '|', '\\'};

    public FileTextProcessor(String directory, String searchWord)
    {
        this.directory = directory;
        this.searchWord = searchWord;
    }

    public void process()
    {
        List<Path> foundFiles = new ArrayList<>();
        final String mergedFileName = "merged_files.txt";
        final String cleanedFileName = "cleaned_files";

        // AtomicInteger для счетчика в лямбда-выражении
        AtomicInteger cleanedFilesCount = new AtomicInteger(0);

        // Первый поток: поиск файлов и объединение содержимого
        Thread searchAndMergeThread = new Thread(() ->
        {
            try
            {
                System.out.println("Поиск файлов содержащих слово: " + searchWord);

                Files.walk(Paths.get(directory))
                        .filter(Files::isRegularFile)
                        .forEach(file ->
                        {
                            try
                            {
                                String content = new String(Files.readAllBytes(file));
                                if (content.contains(searchWord))
                                {
                                    foundFiles.add(file);
                                    System.out.println("Найден файл: " + file.getFileName());
                                }
                            }
                            catch (IOException e)
                            {
                                System.err.println("Ошибка при чтении файла " + file + ": " + e.getMessage());
                            }
                        });


                if (!foundFiles.isEmpty())
                {
                    try (PrintWriter writer = new PrintWriter(new FileWriter(mergedFileName)))
                    {
                        for (Path file : foundFiles)
                        {
                            writer.println("=== Файл: " + file.getFileName() + " ===");
                            String content = new String(Files.readAllBytes(file));
                            writer.println(content);
                            writer.println();
                        }
                        System.out.println("Содержимое объединено в файл: " + mergedFileName);
                    }
                }
                else
                    System.out.println("Файлы содержащие слово '" + searchWord + "' не найдены");


            }
            catch (IOException e)
            {
                System.err.println("Ошибка при поиске файлов: " + e.getMessage());
            }
        });

        // Второй поток: очистка спецсимволов
        Thread cleanSpecialCharsThread = new Thread(() ->
        {
            try
            {
                searchAndMergeThread.join();

                System.out.println("Начало очистки спецсимволов из файлов...");

                Files.walk(Paths.get(directory))
                        .filter(Files::isRegularFile)
                        .forEach(file ->
                        {
                            try
                            {
                                String originalContent = new String(Files.readAllBytes(file));
                                String cleanedContent = removeSpecialChars(originalContent);


                                String cleanedFilePath = cleanedFileName + "_" + file.getFileName();
                                try (PrintWriter writer = new PrintWriter(new FileWriter(cleanedFilePath)))
                                {
                                    writer.print(cleanedContent);
                                }
                                int currentCount = cleanedFilesCount.incrementAndGet();
                                System.out.println("Очищен файл: " + file.getFileName() + " -> " + cleanedFilePath + " (" + currentCount + ")");

                            }
                            catch (IOException e)
                            {
                                System.err.println("Ошибка при очистке файла " + file + ": " + e.getMessage());
                            }
                        });

                System.out.println("Очищено файлов: " + cleanedFilesCount.get());

            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
            catch (IOException e)
            {
                System.err.println("Ошибка при очистке спецсимволов: " + e.getMessage());
            }
        });


        searchAndMergeThread.start();
        cleanSpecialCharsThread.start();


        try
        {
            searchAndMergeThread.join();
            cleanSpecialCharsThread.join();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }

        System.out.println("\n=== Статистика задания 4 ===");
        System.out.println("Найдено файлов с словом '" + searchWord + "': " + foundFiles.size());
        System.out.println("Очищено файлов от спецсимволов: " + cleanedFilesCount.get());
        System.out.println("Объединенный файл: " + mergedFileName);
        System.out.println("Задание 4 завершено!");
    }

    private String removeSpecialChars(String text)
    {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray())
        {
            if (!isSpecialChar(c))
                result.append(c);

        }
        return result.toString();
    }

    private boolean isSpecialChar(char c)
    {
        for (char special : specialChars)
        {
            if (c == special)
                return true;
        }
        return false;
    }
}