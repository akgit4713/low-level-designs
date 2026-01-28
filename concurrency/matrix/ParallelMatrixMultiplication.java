package concurrency.matrix;

import java.util.*;
import java.util.concurrent.*;

/**
 * ================================================================================
 * PROBLEM 6: PARALLEL MATRIX MULTIPLICATION
 * ================================================================================
 * 
 * Requirements:
 * 1. Multiply two matrices using multiple threads
 * 2. Achieve parallel processing for better performance
 * 3. Handle various matrix sizes efficiently
 * 
 * Key Approaches:
 * 1. Row-wise parallelization - Each thread computes subset of rows
 * 2. Cell-wise parallelization - Each thread computes one cell
 * 3. Block-wise parallelization - Divide into sub-matrices (cache-friendly)
 * 4. ForkJoinPool - Recursive divide-and-conquer
 * 
 * Time Complexity:
 * - Sequential: O(n³)
 * - Parallel with p threads: O(n³/p) + overhead
 * 
 * Space Complexity: O(n²) for result matrix
 * 
 * Interview Tips:
 * - Discuss trade-offs between parallelization strategies
 * - Mention cache locality (block-wise is better for large matrices)
 * - Know when parallelization helps (overhead for small matrices)
 */
public class ParallelMatrixMultiplication {
    
    // ==================== APPROACH 1: Row-wise Parallelization ====================
    
    /**
     * Parallel matrix multiplication using row-wise distribution.
     * Each thread computes a portion of the result matrix rows.
     * 
     * Pros:
     * - Simple to implement
     * - Good load balancing if rows are evenly divisible
     * 
     * Cons:
     * - May not utilize cache optimally for large matrices
     */
    public static int[][] multiplyRowWise(int[][] A, int[][] B, int numThreads) 
            throws InterruptedException {
        
        int rowsA = A.length;
        int colsA = A[0].length;
        int colsB = B[0].length;
        
        // Validate dimensions: A is m×n, B is n×p → C is m×p
        if (colsA != B.length) {
            throw new IllegalArgumentException(
                "Matrix dimensions don't match: A cols (" + colsA + ") != B rows (" + B.length + ")");
        }
        
        int[][] C = new int[rowsA][colsB];
        
        // Create threads, each handling a portion of rows
        Thread[] threads = new Thread[numThreads];
        int rowsPerThread = (rowsA + numThreads - 1) / numThreads;  // Ceiling division
        
        for (int t = 0; t < numThreads; t++) {
            final int startRow = t * rowsPerThread;
            final int endRow = Math.min(startRow + rowsPerThread, rowsA);
            
            threads[t] = new Thread(() -> {
                for (int i = startRow; i < endRow; i++) {
                    for (int j = 0; j < colsB; j++) {
                        int sum = 0;
                        for (int k = 0; k < colsA; k++) {
                            sum += A[i][k] * B[k][j];
                        }
                        C[i][j] = sum;
                    }
                }
            });
            threads[t].start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        return C;
    }
    
    // ==================== APPROACH 2: ExecutorService with Callable ====================
    
    /**
     * Using ExecutorService for better thread management.
     * Returns Future for each row computation.
     */
    public static int[][] multiplyWithExecutor(int[][] A, int[][] B, int numThreads) 
            throws InterruptedException, ExecutionException {
        
        int rowsA = A.length;
        int colsA = A[0].length;
        int colsB = B[0].length;
        
        int[][] C = new int[rowsA][colsB];
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Void>> futures = new ArrayList<>();
        
        // Submit task for each row
        for (int i = 0; i < rowsA; i++) {
            final int row = i;
            
            Future<Void> future = executor.submit(() -> {
                for (int j = 0; j < colsB; j++) {
                    int sum = 0;
                    for (int k = 0; k < colsA; k++) {
                        sum += A[row][k] * B[k][j];
                    }
                    C[row][j] = sum;
                }
                return null;
            });
            futures.add(future);
        }
        
        // Wait for all tasks to complete
        for (Future<Void> future : futures) {
            future.get();
        }
        
        executor.shutdown();
        return C;
    }
    
    // ==================== APPROACH 3: ForkJoinPool (Divide and Conquer) ====================
    
    /**
     * Using ForkJoinPool for recursive parallelization.
     * Better for very large matrices due to work-stealing algorithm.
     */
    public static int[][] multiplyWithForkJoin(int[][] A, int[][] B) {
        int rowsA = A.length;
        int colsB = B[0].length;
        int[][] C = new int[rowsA][colsB];
        
        ForkJoinPool pool = ForkJoinPool.commonPool();
        pool.invoke(new MatrixMultiplyTask(A, B, C, 0, rowsA));
        
        return C;
    }
    
    /**
     * RecursiveAction for ForkJoin-based matrix multiplication.
     */
    static class MatrixMultiplyTask extends RecursiveAction {
        private static final int THRESHOLD = 64;  // Minimum rows for parallel execution
        
        private final int[][] A, B, C;
        private final int startRow, endRow;
        
        MatrixMultiplyTask(int[][] A, int[][] B, int[][] C, int startRow, int endRow) {
            this.A = A;
            this.B = B;
            this.C = C;
            this.startRow = startRow;
            this.endRow = endRow;
        }
        
        @Override
        protected void compute() {
            int numRows = endRow - startRow;
            
            if (numRows <= THRESHOLD) {
                // Base case: compute sequentially
                computeSequential();
            } else {
                // Recursive case: split into subtasks
                int midRow = startRow + numRows / 2;
                
                MatrixMultiplyTask leftTask = new MatrixMultiplyTask(A, B, C, startRow, midRow);
                MatrixMultiplyTask rightTask = new MatrixMultiplyTask(A, B, C, midRow, endRow);
                
                // Fork left, compute right in current thread, then join
                leftTask.fork();
                rightTask.compute();
                leftTask.join();
            }
        }
        
        private void computeSequential() {
            int colsA = A[0].length;
            int colsB = B[0].length;
            
            for (int i = startRow; i < endRow; i++) {
                for (int j = 0; j < colsB; j++) {
                    int sum = 0;
                    for (int k = 0; k < colsA; k++) {
                        sum += A[i][k] * B[k][j];
                    }
                    C[i][j] = sum;
                }
            }
        }
    }
    
    // ==================== APPROACH 4: Block-wise (Cache Optimized) ====================
    
    /**
     * Block-wise multiplication for better cache performance.
     * Divides matrices into blocks and processes each block.
     * 
     * Why blocks?
     * - Better cache locality (blocks fit in L1/L2 cache)
     * - Reduces cache misses for large matrices
     */
    public static int[][] multiplyBlockWise(int[][] A, int[][] B, int blockSize, int numThreads) 
            throws InterruptedException {
        
        int n = A.length;  // Assuming square matrices for simplicity
        int[][] C = new int[n][n];
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<?>> futures = new ArrayList<>();
        
        // Iterate over blocks
        for (int bi = 0; bi < n; bi += blockSize) {
            for (int bj = 0; bj < n; bj += blockSize) {
                final int blockI = bi;
                final int blockJ = bj;
                
                futures.add(executor.submit(() -> {
                    // Compute block C[bi:bi+blockSize, bj:bj+blockSize]
                    for (int bk = 0; bk < n; bk += blockSize) {
                        // Mini matrix multiplication within blocks
                        int maxI = Math.min(blockI + blockSize, n);
                        int maxJ = Math.min(blockJ + blockSize, n);
                        int maxK = Math.min(bk + blockSize, n);
                        
                        for (int i = blockI; i < maxI; i++) {
                            for (int j = blockJ; j < maxJ; j++) {
                                int sum = 0;
                                for (int k = bk; k < maxK; k++) {
                                    sum += A[i][k] * B[k][j];
                                }
                                synchronized (C) {  // Need sync since multiple threads update C
                                    C[i][j] += sum;
                                }
                            }
                        }
                    }
                }));
            }
        }
        
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        
        executor.shutdown();
        return C;
    }
    
    // ==================== Sequential (Baseline for Comparison) ====================
    
    public static int[][] multiplySequential(int[][] A, int[][] B) {
        int rowsA = A.length;
        int colsA = A[0].length;
        int colsB = B[0].length;
        
        int[][] C = new int[rowsA][colsB];
        
        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                int sum = 0;
                for (int k = 0; k < colsA; k++) {
                    sum += A[i][k] * B[k][j];
                }
                C[i][j] = sum;
            }
        }
        
        return C;
    }
    
    // ==================== Utility Methods ====================
    
    public static int[][] generateRandomMatrix(int rows, int cols) {
        Random random = new Random();
        int[][] matrix = new int[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = random.nextInt(10);
            }
        }
        
        return matrix;
    }
    
    public static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }
    
    public static boolean areMatricesEqual(int[][] A, int[][] B) {
        if (A.length != B.length || A[0].length != B[0].length) {
            return false;
        }
        
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                if (A[i][j] != B[i][j]) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    // ==================== Main Test ====================
    
    public static void main(String[] args) throws Exception {
        System.out.println("=== Parallel Matrix Multiplication Test ===\n");
        
        // Small test for correctness
        int[][] A = {
            {1, 2, 3},
            {4, 5, 6}
        };
        
        int[][] B = {
            {7, 8},
            {9, 10},
            {11, 12}
        };
        
        System.out.println("Matrix A (2x3):");
        printMatrix(A);
        System.out.println("\nMatrix B (3x2):");
        printMatrix(B);
        
        int[][] C = multiplySequential(A, B);
        System.out.println("\nResult C = A × B (2x2):");
        printMatrix(C);
        
        // Performance test
        System.out.println("\n--- Performance Comparison ---\n");
        
        int size = 500;
        int numThreads = 4;
        
        int[][] largeA = generateRandomMatrix(size, size);
        int[][] largeB = generateRandomMatrix(size, size);
        
        // Sequential
        long start = System.currentTimeMillis();
        int[][] seqResult = multiplySequential(largeA, largeB);
        long seqTime = System.currentTimeMillis() - start;
        System.out.println("Sequential (" + size + "x" + size + "): " + seqTime + " ms");
        
        // Row-wise parallel
        start = System.currentTimeMillis();
        int[][] rowResult = multiplyRowWise(largeA, largeB, numThreads);
        long rowTime = System.currentTimeMillis() - start;
        System.out.println("Row-wise parallel (" + numThreads + " threads): " + rowTime + " ms");
        System.out.println("  Speedup: " + String.format("%.2f", (double)seqTime/rowTime) + "x");
        System.out.println("  Correct: " + areMatricesEqual(seqResult, rowResult));
        
        // Executor-based
        start = System.currentTimeMillis();
        int[][] execResult = multiplyWithExecutor(largeA, largeB, numThreads);
        long execTime = System.currentTimeMillis() - start;
        System.out.println("Executor-based (" + numThreads + " threads): " + execTime + " ms");
        System.out.println("  Speedup: " + String.format("%.2f", (double)seqTime/execTime) + "x");
        System.out.println("  Correct: " + areMatricesEqual(seqResult, execResult));
        
        // ForkJoin
        start = System.currentTimeMillis();
        int[][] fjResult = multiplyWithForkJoin(largeA, largeB);
        long fjTime = System.currentTimeMillis() - start;
        System.out.println("ForkJoin: " + fjTime + " ms");
        System.out.println("  Speedup: " + String.format("%.2f", (double)seqTime/fjTime) + "x");
        System.out.println("  Correct: " + areMatricesEqual(seqResult, fjResult));
        
        System.out.println("\nAll tests completed!");
    }
}

/**
 * ================================================================================
 * FOLLOW-UP: Strassen's Algorithm (O(n^2.807))
 * ================================================================================
 * 
 * For very large matrices, Strassen's algorithm provides better asymptotic
 * complexity by reducing multiplications at the cost of more additions.
 * 
 * Note: In practice, due to overhead, Strassen's is only faster for n > 500-1000
 */
class StrassenMatrixMultiplication {
    
    public static int[][] multiply(int[][] A, int[][] B) {
        int n = A.length;
        
        // Base case
        if (n <= 64) {
            return ParallelMatrixMultiplication.multiplySequential(A, B);
        }
        
        // Divide matrices into quadrants
        int newSize = n / 2;
        
        int[][] a11 = new int[newSize][newSize];
        int[][] a12 = new int[newSize][newSize];
        int[][] a21 = new int[newSize][newSize];
        int[][] a22 = new int[newSize][newSize];
        
        int[][] b11 = new int[newSize][newSize];
        int[][] b12 = new int[newSize][newSize];
        int[][] b21 = new int[newSize][newSize];
        int[][] b22 = new int[newSize][newSize];
        
        // Split matrices
        split(A, a11, 0, 0);
        split(A, a12, 0, newSize);
        split(A, a21, newSize, 0);
        split(A, a22, newSize, newSize);
        
        split(B, b11, 0, 0);
        split(B, b12, 0, newSize);
        split(B, b21, newSize, 0);
        split(B, b22, newSize, newSize);
        
        // Strassen's 7 multiplications (can be parallelized)
        int[][] m1 = multiply(add(a11, a22), add(b11, b22));
        int[][] m2 = multiply(add(a21, a22), b11);
        int[][] m3 = multiply(a11, subtract(b12, b22));
        int[][] m4 = multiply(a22, subtract(b21, b11));
        int[][] m5 = multiply(add(a11, a12), b22);
        int[][] m6 = multiply(subtract(a21, a11), add(b11, b12));
        int[][] m7 = multiply(subtract(a12, a22), add(b21, b22));
        
        // Calculate result quadrants
        int[][] c11 = add(subtract(add(m1, m4), m5), m7);
        int[][] c12 = add(m3, m5);
        int[][] c21 = add(m2, m4);
        int[][] c22 = add(subtract(add(m1, m3), m2), m6);
        
        // Combine quadrants
        int[][] C = new int[n][n];
        join(c11, C, 0, 0);
        join(c12, C, 0, newSize);
        join(c21, C, newSize, 0);
        join(c22, C, newSize, newSize);
        
        return C;
    }
    
    private static int[][] add(int[][] A, int[][] B) {
        int n = A.length;
        int[][] C = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] + B[i][j];
            }
        }
        return C;
    }
    
    private static int[][] subtract(int[][] A, int[][] B) {
        int n = A.length;
        int[][] C = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] - B[i][j];
            }
        }
        return C;
    }
    
    private static void split(int[][] P, int[][] C, int iB, int jB) {
        for (int i = 0; i < C.length; i++) {
            for (int j = 0; j < C.length; j++) {
                C[i][j] = P[iB + i][jB + j];
            }
        }
    }
    
    private static void join(int[][] C, int[][] P, int iB, int jB) {
        for (int i = 0; i < C.length; i++) {
            for (int j = 0; j < C.length; j++) {
                P[iB + i][jB + j] = C[i][j];
            }
        }
    }
}
