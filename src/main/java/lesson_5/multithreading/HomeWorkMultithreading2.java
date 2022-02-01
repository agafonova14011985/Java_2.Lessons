package lesson_5.multithreading;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class HomeWorkMultithreading2 {
    public static final int SIZE = 10000000;
    private static final int  H = SIZE / 2;

    public static void main(String[] args) {

        //запускаем без смещения, что б прогнать в одном потоке полностью весь массив
        float [] data1 = createArray(SIZE);
        measureTime(() -> sequentialMethod(data1), "sequentialMethod");

        //массив разбивается по формуле  смещения во 2 есть
        float [] data2 = createArray(SIZE);
        measureTime(() -> parallelMethod(data2), "parallelMethod");
        System.out.println("Arrays are equal/ сравнение массивов : " + Arrays.equals(data1,data2));
            }

    private static void sequentialMethod(float[] data){
       sequentialMethod(data,0);
    }

    //получает массив и смещение(для корректной обработки массива) и в цикле прогоняет массив по методу с формулой
    private static void sequentialMethod (float[] data, int offset){
        for (int index = 0; index < data.length; index++){
            float currentValue = data[index];
        data[index] = computeValue(index + offset, currentValue);
    }}

    private static void parallelMethod(float[] data){
        float[] par1 = Arrays.copyOfRange(data,0, H);
        float[] par2 = Arrays.copyOfRange(data, H, data.length);

        Thread thread1 = new Thread(()-> sequentialMethod(par1, 0));
        Thread thread2 = new Thread(()->sequentialMethod(par2, H));

        //запуск потоков
         thread1.start();
         thread2.start();

         //прежде чем склеивать массивы обратно ждем обработку потоков
         try {
             thread1.join();
             thread2.join();
         }catch (InterruptedException e){
             System.err.println("ups........");
             e.printStackTrace();
             return;
         }

         //склейка
         System.arraycopy(par1, 0, data,0, H);
        System.arraycopy(par2, 0, data,H, H);
    }


    //создание массива заполненного 1
    private static float[] createArray(int size){
        float[] data = new float[size];
        Arrays.fill(data, 1.0f);
        return data;
    }


    //считает значение для конкретной ячейки массива
    //
    private static float computeValue(int index, float currentValue){
        return (float)
                (currentValue + Math.sin(0.2f + index / 5.0)
                        * Math.cos(0.2f * index / 5.0) * Math.cos(0.4f * index / 2.0f));

    }

    //метод засекает время
    private static void measureTime(Runnable action, String actionName){
        long start = System.nanoTime();
        action.run();
        long finish = System.nanoTime();
        long duration = finish - start;
        System.out.println("Method: " + actionName + " it took time " + TimeUnit.NANOSECONDS.toMillis(duration) + " ms");
    }

}
