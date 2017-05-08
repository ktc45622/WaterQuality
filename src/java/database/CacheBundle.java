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
package database;

import com.google.common.collect.ImmutableBiMap;

/**
 * Implementation of an RCU in-memory cache for commonly-used database objects.
 * 
 * RCU is a synchronization strategy that significantly increases performance of reads while
 * adding a harsh penalty to writes. Reads are essentially free (and provides a wait-free
 * guarantee on accesses). RCU differs from Reader-Writer Locks by allowing concurrent writes
 * to occur during reads. The algorithm is simple, and is composed of 3 stages:
 * 
 * 1) Read: Reading the current value
 * 2) Copy: Cloning the original read value and modifying it
 * 3) Update: Performing a CAS on the value with the new modified value.
 * 
 * Values for CacheBundle are immutable, as such provide some extremely high-performance
 * optimizations and makes this possible. Modifying multiple values in the bundle is trivial,
 * and reusing any unmodified values is possible as they are immutable.
 * @author Louis Jenkins
 */
public class CacheBundle {
    /**
    * Mappings of database parameter ids to their respective names.
    */
   ImmutableBiMap<Long, String> paramIdToName = ImmutableBiMap.of();

   /**
    * Mappings of remote database parameter ids to their respective remote sources.
    */
   ImmutableBiMap<Long, Long> paramIdToRemoteSource = ImmutableBiMap.of();
}
