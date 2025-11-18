package task3;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class DirectoryCopier
{
    private final String sourceDir;
    private final String targetDir;
    private int filesCopied = 0;
    private int directoriesCopied = 0;

    public DirectoryCopier(String sourceDir, String targetDir)
    {
        this.sourceDir = sourceDir;
        this.targetDir = targetDir;
    }

    public void copyDirectory()
    {
        Thread copyThread = new Thread(() ->
        {
            try
            {
                copyDirectoryRecursive(Paths.get(sourceDir), Paths.get(targetDir));
                System.out.println("\n=== Статистика копирования ===");
                System.out.println("Скопировано файлов: " + filesCopied);
                System.out.println("Скопировано директорий: " + directoriesCopied);
                System.out.println("Исходная директория: " + sourceDir);
                System.out.println("Целевая директория: " + targetDir);
            }
            catch (IOException e)
            {
                System.err.println("Ошибка при копировании: " + e.getMessage());
            }
        });

        copyThread.start();

        try
        {
            copyThread.join();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }

        System.out.println("Задание 3 завершено!");
    }

    private void copyDirectoryRecursive(Path source, Path target) throws IOException
    {
        if (!Files.exists(source))
        {
            throw new IOException("Исходная директория не существует: " + source);
        }

        if (!Files.exists(target))
        {
            Files.createDirectories(target);
            directoriesCopied++;
            System.out.println("Создана директория: " + target);
        }


        Files.list(source).forEach(path ->
        {
            try
            {
                Path targetPath = target.resolve(path.getFileName());
                if (Files.isDirectory(path))
                    copyDirectoryRecursive(path, targetPath);
                else
                {
                    Files.copy(path, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    filesCopied++;
                    System.out.println("Скопирован файл: " + path.getFileName());
                }
            }
            catch (IOException e)
            {
                System.err.println("Ошибка при копировании " + path + ": " + e.getMessage());
            }
        });
    }
}