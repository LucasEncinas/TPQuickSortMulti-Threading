package test;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class QuickSortMutliThreading {

    // QuickSort con multi-threading
    static class QuickSortMultiThreading extends RecursiveTask<Void> {
        int start, end;
        int[] arr;

        //realiza la particion del array
        private int partition(int start, int end, int[] arr) {
            int i = start, j = end;
            int pivoted = new Random().nextInt(j - i + 1) + i; // Corrección en la elección del pivote

            //intercambia el pivote con el ultimo elemento
            int t = arr[j];
            arr[j] = arr[pivoted];
            arr[pivoted] = t;
            j--;

            //particionamiento
            while (i <= j) {
                if (arr[i] <= arr[end]) {
                    i++;
                    continue;
                }

                if (arr[j] >= arr[end]) {
                    j--;
                    continue;
                }

                t = arr[j];
                arr[j] = arr[i];
                arr[i] = t;
                j--;
                i++;
            }

            //coloca el pivote en la posicion correcta
            t = arr[j + 1];
            arr[j + 1] = arr[end];
            arr[end] = t;
            return j + 1;
        }
        
        //constructor de la clase
        public QuickSortMultiThreading(int start, int end, int[] arr) {
            this.arr = arr;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Void compute() {
            if (start >= end)
                return null;
            
            //encuentra la particion
            int p = partition(start, end, arr);
            
            //crea tareas para las subpartes del array
            QuickSortMultiThreading left = new QuickSortMultiThreading(start, p - 1, arr);
            QuickSortMultiThreading right = new QuickSortMultiThreading(p + 1, end, arr);
            
            //ejecuta la tarea del lado izquierdo en un nuevo hilo
            left.fork();
            //ejecuta la tarea del lado derecho en el hilo actual
            right.compute();
            //espera a que termine la tarea del lado izquierdo
            left.join();

            return null;
        }
    }

    // QuickSort recursivo
    public static void quickSortRecursive(int[] arr, int start, int end) {
        if (start < end) {
            int p = partition(arr, start, end);
            quickSortRecursive(arr, start, p - 1);
            quickSortRecursive(arr, p + 1, end);
        }
    }
    //Metodo de particion para el quicksort recursivo
    private static int partition(int[] arr, int start, int end) {
        int pivoted = new Random().nextInt(end - start + 1) + start;
        int t = arr[end];
        arr[end] = arr[pivoted];
        arr[pivoted] = t;

        int pivot = arr[end];
        int i = start - 1;
        for (int j = start; j < end; j++) {
            if (arr[j] < pivot) {
                i++;
                t = arr[i];
                arr[i] = arr[j];
                arr[j] = t;
            }
        }
        t = arr[i + 1];
        arr[i + 1] = arr[end];
        arr[end] = t;
        return i + 1;
    }

    // Método principal para comparar tiempos
    public static void main(String[] args) {
        int n = 10000000; // tamaño del array
        int[] arr = new int[n];
        Random rand = new Random();
        
        //Llenar el array con valores aleatorios
        for (int i = 0; i < n; i++) {
            arr[i] = rand.nextInt(100000);
        }

        int[] arrCopy = arr.clone();

        // Medir tiempo para QuickSort multi-threading
        ForkJoinPool pool = ForkJoinPool.commonPool();
        long startTime = System.nanoTime();
        pool.invoke(new QuickSortMultiThreading(0, n - 1, arr));
        long endTime = System.nanoTime();
        long multiThreadTime = endTime - startTime;

        // Medir tiempo para QuickSort recursivo
        startTime = System.nanoTime();
        quickSortRecursive(arrCopy, 0, n - 1);
        endTime = System.nanoTime();
        long recursiveTime = endTime - startTime;

        // Imprimir resultados
        System.out.println("Tiempo de ejecución QuickSort multi-threading: " + multiThreadTime / 1e6 + " ms");
        System.out.println("Tiempo de ejecución QuickSort recursivo: " + recursiveTime / 1e6 + " ms");
    }
}

