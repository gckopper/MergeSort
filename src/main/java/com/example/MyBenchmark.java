/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.APPEND;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class MyBenchmark {

    private final long seed = 42;

    private int[] array;

    private int tracker = 0;

    Path file = Path.of(
            "trocas.txt");

    @Param({
            "ORDERED", // Ordered
            "REVERSED", // Reversed
            "RANDOM" // Random
    })
    private Order order;

    @Param({
            "128",
            "256",
            "512",
            "1000",
            "1024",
            "2048",
            "4096",
            "8192",
            "10000",
            "16384",
            "32768",
            "65536",
            "100000",
            "131072",
            "262144",
            "524288",
            "1000000",
            "1048576",
            "2097152",
            "4194304",
    })
    private int size;

    @Setup(Level.Invocation)
    public void setup() {
        tracker = 0;
        array = new int[size];
        switch (order) {
            case ORDERED:
                for (int i = 0; i < array.length; i++) {
                    array[i] = i;
                }
                break;
            case REVERSED:
                for (int i = 0; i < array.length; i++) {
                    array[i] = array.length - i - 1;
                }
                break;
            case RANDOM:
                for (int i = 0; i < array.length; i++) {
                    array[i] = i;
                }
                Random rng = new Random(seed);
                List<Integer> integerArray = Arrays.stream(array).boxed().collect(Collectors.toList());
                Collections.shuffle(integerArray, rng);
                array = integerArray.stream().mapToInt(Integer::intValue).toArray();
                break;
            default:
                System.err.println("This is not supposed to happen");
                break;
        }

    }

    @Benchmark
    @Warmup(iterations = 100, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 100, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    public void testChanges() {
        tracker = MergeSort.sort(array);
    }

    @TearDown(Level.Trial)
    public void check(BenchmarkParams b) {
        StringBuilder dados = new StringBuilder(b.getBenchmark());
        dados.append(',');
        dados.append(size);
        dados.append(',');
        dados.append(order.toString());
        dados.append(',');
        dados.append(tracker);
        dados.append('\n');
        try {
            Files.writeString(file, dados.toString(), CREATE, APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws RunnerException, IOException {
        Options opt = new OptionsBuilder()
                .include(MyBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}
