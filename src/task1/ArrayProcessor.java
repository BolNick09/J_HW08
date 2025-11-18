package task1;

import java.util.Arrays;
import java.util.Random;

public class ArrayProcessor
{
    private final int size;
    private int[] array;
    private volatile boolean isFilled = false;

    public ArrayProcessor(int size)
    {
        this.size = size;
        this.array = new int[size];
    }

    public void process()
    {

        Thread fillerThread = new Thread(() ->
        {
            Random random = new Random();
            for (int i = 0; i < size; i++)
                array[i] = random.nextInt(100) + 1;
            isFilled = true;
            System.out.println("Массив заполнен: " + Arrays.toString(array));
            synchronized (this)
            {
                this.notifyAll();
            }
        });


        Thread sumThread = new Thread(() ->
        {
            synchronized (this)
            {
                while (!isFilled)
                {
                    try
                    {
                        this.wait();
                    }
                    catch (InterruptedException e)
                    {
                        Thread.currentThread().interrupt(); //Обработать исключение и снять блокировку
                        return;
                    }
                }
            }

            int sum = 0;
            for (int num : array)
                sum += num;

            System.out.println("Сумма элементов массива: " + sum);
        });


        Thread averageThread = new Thread(() ->
        {
            synchronized (this)
            {
                while (!isFilled)
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

            double sum = 0;
            for (int num : array)
                sum += num;

            double average = sum / size;
            System.out.println("Среднее арифметическое: " + average);
        });

        fillerThread.start();
        sumThread.start();
        averageThread.start();


        try
        {
            fillerThread.join();
            sumThread.join();
            averageThread.join();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }

    }
}