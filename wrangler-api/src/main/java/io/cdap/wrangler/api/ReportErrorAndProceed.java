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

 /**
  * Exception thrown when the record needs to be emitted to the error collector.
  * This class is similar to {@link ErrorRowException}, but with the difference
  * that it reports the error and continues processing.
  */
 public final class ReportErrorAndProceed extends Exception {
 
     /**
      * Message indicating why the record errored.
      */
     private final String message;
 
     /**
      * Code associated with the error message.
      */
     private final int code;
 
     /**
      * Constructs a new {@link ReportErrorAndProceed} exception with the specified message and code.
      *
      * @param message The message describing why the record errored.
      * @param code The code associated with the error message.
      */
     public ReportErrorAndProceed(final String message, final int code) {
         super(message);
         this.message = message;
         this.code = code;
     }
 
     /**
      * Returns the message describing why the record errored.
      *
      * @return The error message.
      */
     public final String getMessage() {
         return message;
     }
 
     /**
      * Returns the code associated with the error message.
      *
      * @return The error code.
      */
     public final int getCode() {
         return code;
     }
 }