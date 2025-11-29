package com.example.Mutantes.service;

public class TestRunner {
    public static void main(String[] args) {
        MutantDetector detector = new MutantDetector();

        // Test 1: testMutantExactlyTwoSequences
        System.out.println("=== Test 1: Exactamente 2 secuencias ===");
        String[] dna1 = {
            "AAAATG",
            "TGCAGT",
            "GCTTCC",
            "TTTAGG",
            "GTAGTC",
            "AGTCAC"
        };
        int count1 = detector.countMutantSequences(dna1);
        boolean result1 = detector.isMutant(dna1);
        System.out.println("Secuencias encontradas: " + count1);
        System.out.println("Es mutante: " + result1);
        System.out.println();

        // Test 2: testSequencesInCorners
        System.out.println("=== Test 2: Secuencias en esquinas ===");
        String[] dna2 = {
            "AAAATG",
            "TGCAGT",
            "GCTTCC",
            "CCCAGG",
            "GTAGTC",
            "AGTCGG"
        };
        int count2 = detector.countMutantSequences(dna2);
        boolean result2 = detector.isMutant(dna2);
        System.out.println("Secuencias encontradas: " + count2);
        System.out.println("Es mutante: " + result2);
        System.out.println();

        // Test 3: testLargeMatrixHuman
        System.out.println("=== Test 3: Matriz grande (50x50) ===");
        int size = 50;
        String[] dna3 = new String[size];

        for (int i = 0; i < size; i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < size; j++) {
                char[] bases = {'A', 'T', 'C', 'G'};
                row.append(bases[(i + j) % 4]);
            }
            dna3[i] = row.toString();
        }

        // Mostrar primeras filas para debug
        for (int i = 0; i < 5; i++) {
            System.out.println("Fila " + i + ": " + dna3[i].substring(0, 20) + "...");
        }

        int count3 = detector.countMutantSequences(dna3);
        boolean result3 = detector.isMutant(dna3);
        System.out.println("Secuencias encontradas: " + count3);
        System.out.println("Es mutante: " + result3);
    }
}

