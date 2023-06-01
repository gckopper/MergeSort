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

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class MyBenchmark {

    private final long seed = 42;

    private int[] array;

    private int tracker = 0;

    @Param({
            "ORDERED", // Ordered
            "REVERSED", // Reversed
            "RANDOM" // Random
    })
    private Order order;

    @Param({
            "10",
            "50",
            "100",
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

    // @Benchmark
    public void testMethod() {
        MergeSort.sort(array);
    }

    @Benchmark
    @Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
    public void testChanges() {
        tracker = MergeSort2.sort(array);
    }

    @TearDown
    public void check() {
        System.out.printf("Number of changes to the array: %d\n", tracker);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MyBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}
