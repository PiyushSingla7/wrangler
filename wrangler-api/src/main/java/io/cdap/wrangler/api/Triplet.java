/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

 package io.cdap.wrangler.api;

 import io.cdap.wrangler.api.annotations.Public;
 
 /**
  * A triplet consisting of three elements - first, second, and third.
  *
  * This class provides immutable access to elements of the triplet.
  *
  * @param <F> The type of the first element.
  * @param <S> The type of the second element.
  * @param <T> The type of the third element.
  */
 @Public
 public final class Triplet<F, S, T> {
     /**
      * The first element of the triplet.
      */
     private final F first;
 
     /**
      * The second element of the triplet.
      */
     private final S second;
 
     /**
      * The third element of the triplet.
      */
     private final T third;
 
     /**
      * Constructs a new {@code Triplet} with the specified elements.
      *
      * @param first The first element of the triplet.
      * @param second The second element of the triplet.
      * @param third The third element of the triplet.
      */
     public Triplet(final F first, final S second, final T third) {
         this.first = first;
         this.second = second;
         this.third = third;
     }
 
     /**
      * Returns the first element of the triplet.
      *
      * @return The first element of the triplet.
      */
     public final F getFirst() {
         return first;
     }
 
     /**
      * Returns the second element of the triplet.
      *
      * @return The second element of the triplet.
      */
     public final S getSecond() {
         return second;
     }
 
     /**
      * Returns the third element of the triplet.
      *
      * @return The third element of the triplet.
      */
     public final T getThird() {
         return third;
     }
 }