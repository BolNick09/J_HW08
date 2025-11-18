import java.util.Scanner;
import task1.ArrayProcessor;
import task2.FileNumberProcessor;
import task3.DirectoryCopier;
import task4.FileTextProcessor;

public class Main
{
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Проект по работе с файлами ===");
        System.out.println("1. Обработка массива в потоках");
        System.out.println("2. Обработка чисел в файле");
        System.out.println("3. Копирование директории");
        System.out.println("4. Поиск и обработка текста в файлах");
        System.out.print("Выберите задание (1-4): ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice)
        {
            case 1:
                runTask1();
                break;
            case 2:
                runTask2(scanner);
                break;
            case 3:
                runTask3(scanner);
                break;
            case 4:
                runTask4(scanner);
                break;
            default:
                System.out.println("Неверный выбор!");
        }

        scanner.close();
    }

    private static void runTask1()
    {
        System.out.println("\n=== Задание 1: Обработка массива ===");
        ArrayProcessor processor = new ArrayProcessor(20);
        processor.process();
    }

    private static void runTask2(Scanner scanner)
    {
        System.out.println("\n=== Задание 2: Обработка чисел в файле ===");
        System.out.print("Введите путь к файлу: ");
        String filePath = scanner.nextLine();

        FileNumberProcessor processor = new FileNumberProcessor(filePath, 10);
        processor.process();
    }

    private static void runTask3(Scanner scanner)
    {
        System.out.println("\n=== Задание 3: Копирование директории ===");
        System.out.print("Введите исходную директорию: ");
        String sourceDir = scanner.nextLine();
        System.out.print("Введите целевую директорию: ");
        String targetDir = scanner.nextLine();

        DirectoryCopier copier = new DirectoryCopier(sourceDir, targetDir);
        copier.copyDirectory();
    }

    private static void runTask4(Scanner scanner)
    {
        System.out.println("\n=== Задание 4: Поиск и обработка текста ===");
        System.out.print("Введите директорию для поиска: ");
        String directory = scanner.nextLine();
        System.out.print("Введите слово для поиска: ");
        String searchWord = scanner.nextLine();

        FileTextProcessor processor = new FileTextProcessor(directory, searchWord);
        processor.process();
    }
}