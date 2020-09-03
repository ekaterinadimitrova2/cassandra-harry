/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package harry.generators;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import harry.model.OpSelectors;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

public class RandomGeneratorTest
{
    @Test
    public void testShuffleUnshuffle()
    {
        int iterations = 100000;
        Random rnd = new Random();

        for (int i = 1; i < iterations; i++)
        {
            long l = rnd.nextLong();
            Assert.assertEquals(l, PCGFastPure.unshuffle(PCGFastPure.shuffle(l)));
        }
    }

    @Test
    public void testImmutableRng()
    {
        int size = 5;
        for (int stream = 1; stream < 1000000; stream++)
        {
            long[] generated = new long[size];
            OpSelectors.Rng rng = new OpSelectors.PCGFast(1);
            for (int i = 0; i < size; i++)
                generated[i] = rng.randomNumber(i, stream);

            for (int i = 1; i < size; i++)
            {
                Assert.assertEquals(generated[i], rng.next(generated[i - 1], stream));
                Assert.assertEquals(generated[i - 1], rng.prev(generated[i], stream));
                Assert.assertEquals(i - 1, rng.sequenceNumber(generated[i], stream));
            }
        }
    }

    @Test
    public void seekTest()
    {
        // TODO: more examples; randomize
        PcgRSUFast rand = new PcgRSUFast(1, 1);
        long first = rand.next();
        long last = 0;
        for (int i = 0; i < 10; i++)
            last = rand.next();

        rand.advance(-11);
        Assert.assertEquals(first, rand.next());

        rand.advance(9);
        Assert.assertEquals(last, rand.next());
        Assert.assertEquals(first, rand.nextAt(0));
        Assert.assertEquals(last, rand.nextAt(10));
        Assert.assertEquals(-11, rand.distance(first));
    }

    @Test
    public void shuffleUnshuffleTest()
    {
        Random rnd = new Random();
        for (int i = 0; i < 100000; i++)
        {
            long a = rnd.nextLong();
            Assert.assertEquals(a, PCGFastPure.unshuffle(PCGFastPure.shuffle(a)));
        }
    }

    @Test
    public void testIntBetween()
    {
        RandomGenerator rng = new PcgRSUFast(System.currentTimeMillis(), 0);

        int a = 0;
        int b = 50;
        int[] cardinality = new int[b - a];
        for (int i = 0; i < 100000; i++)
        {
            int min = Math.min(a, b);
            int max = Math.max(a, b);
            cardinality[rng.nextInt(min, max - 1) - min]++;
        }

        // Extremely improbable yet possible that some of the values won't be generated
        for (int i = 0; i < cardinality.length; i++)
            Assert.assertTrue(cardinality[i] > 0);
    }
}
