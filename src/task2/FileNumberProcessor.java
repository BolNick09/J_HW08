package task2;

import java.io.*;
import java.util.Random;

public class FileNumberProcessor
{
    private final String filePath;
    private final int numberCount;
    private volatile boolean isFileFilled = false;

    public FileNumberProcessor(String filePath, int numberCount)
    {
        this.filePath = filePath;
        this.numberCount = numberCount;
    }

    public void process()
    {

        Thread fillerThread = new Thread(() ->
        {
            try (PrintWriter writer = new PrintWriter(new FileWriter(filePath)))
            {
                Random random = new Random();
                for (int i = 0; i < numberCount; i++)
                {
                    int number = random.nextInt(10) + 1;
                    writer.println(number);
                }
                isFileFilled = true;
                System.out.println("Файл " + filePath + " заполнен числами");
                synchronized (this)
                {
                    this.notifyAll();
                }
            }
            catch (IOException e)
            {
                System.err.println("Ошибка при записи в файл: " + e.getMessage());
            }
        });

        // Поток для поиска простых чисел
        Thread primeThread = new Thread(() ->
        {
            synchronized (this)
            {
                while (!isFileFilled)
                {
                    try
                    {
                        this.wait();
                    }
                    catch (InterruptedException e)
                    {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }


            try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
                 PrintWriter writer = new PrintWriter(new FileWriter(filePath + ".primes.txt")))
            {

                String line;
                while ((line = reader.readLine()) != null)
                {
                    try
                    {
                        int number = Integer.parseInt(line.trim());
                        if (isPrime(number))
                        {
                            writer.println(number);
                            System.out.println("Найдено простое число: " + number);
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        // Пропуск некорректных строки
                    }
                }
                System.out.println("Простые числа записаны в файл: " + filePath + ".primes.txt");

            }
            catch (IOException e)
            {
                System.err.println("Ошибка при обработке простых чисел: " + e.getMessage());
            }
        });

        // Поток для вычисления факториалов
        Thread factorialThread = new Thread(() ->
        {
            synchronized (this) {
                while (!isFileFilled)
                {
                    try
                    {
                        this.wait();
                    }
                    catch (InterruptedException e)
                    {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
                 PrintWriter writer = new PrintWriter(new FileWriter(filePath + ".factorials.txt")))
            {

                String line;
                while ((line = reader.readLine()) != null)
                {
                    try
                    {
                        int number = Integer.parseInt(line.trim());
                        long factorial = calculateFactorial(number);
                        writer.println(number + "! = " + factorial);
                        System.out.println("Вычислен факториал: " + number + "! = " + factorial);
                    }
                    catch (NumberFormatException e)
                    {
                        // Пропуск некорректных строк
                    }
                }
                System.out.println("Факториалы записаны в файл: " + filePath + ".factorials.txt");

            }
            catch (IOException e)
            {
                System.err.println("Ошибка при вычислении факториалов: " + e.getMessage());
            }
        });


        fillerThread.start();
        primeThread.start();
        factorialThread.start();


        try
        {
            fillerThread.join();
            primeThread.join();
            factorialThread.join();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }

        System.out.println("Задание 2 завершено!");
    }

    private boolean isPrime(int number)
    {
        if (number <= 1)
            return false;
        else if (number == 2)
            return true;
        else if (number % 2 == 0)
            return false;

        for (int i = 3; i <= Math.sqrt(number); i += 2)
        {
            if (number % i == 0)
                return false;
        }
        return true;
    }

    private long calculateFactorial(int n)
    {
        if (n == 0 || n == 1)
            return 1;
        long result = 1;
        for (int i = 2; i <= n; i++)
            result *= i;

        return result;
    }
}