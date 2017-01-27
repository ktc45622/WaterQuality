/* BSD 3-Clause License
 *
 * Copyright (c) 2017, Louis Jenkins <LouisJenkinsCS@hotmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *
 *     - Neither the name of Louis Jenkins, Bloomsburg University nor the names of its 
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package Providers;

import Misc.GraphData;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 *
 * @author Louis Jenkins
 * 
 * A test stub to generate random data for each which is fed to be rendered by the graph.
 */
public class StubbedDataProvider implements DataProvider<GraphData> {
    
    private final List<GraphData> data = new ArrayList<>();
    
    private static final int NUM_GENERATE = 1000;

    public StubbedDataProvider() {
        // AtomicReference used because we need to update this
        AtomicReference<Instant> instanceRef = new AtomicReference(Instant.now().truncatedTo(ChronoUnit.HOURS));
        // On construction, we randomly generate this data.
        Random rand = new Random();
        
        // Generate data
        data.addAll(rand.ints(NUM_GENERATE)
                // We generate random integers in parallel
                .parallel()
                // Ensures that they are from 0 to 100
                .map(i -> Math.abs((i % 100)) + 1)
                // Converts them to GraphData.
                .mapToObj(i ->
                    new GraphData(instanceRef.getAndUpdate(currentInstance -> currentInstance.plus(15, ChronoUnit.MINUTES)), i)
                )
                .sorted((i1, i2) -> i1.timestamp.compareTo(i2.timestamp))
                .collect(Collectors.toList())
        );
    }

    @Override
    public List<GraphData> get() {
        return data;
    }
    
}
