/*
 * Copyright 2017 Google LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.tools.managedcloudsdk.command;

import javax.annotation.Nullable;

/** Exception thrown when a command failed to execute completely. */
public class CommandExecutionException extends Exception {

  @Nullable private final String errorLog;

  public CommandExecutionException(Throwable cause) {
    super(cause);
    this.errorLog = null;
  }

  /**
   * Creates a new command execution exception.
   *
   * @param message failure details; typically output of stdout and stderr
   * @param cause root exception
   */
  public CommandExecutionException(String message, Throwable cause) {
    super(message, cause);
    this.errorLog = null;
  }

  /**
   * Creates a new command execution exception.
   *
   * @param message failure details; typically output of stdout and stderr
   * @param cause root exception
   * @param errorLog command output on stdout and stderr
   */
  public CommandExecutionException(String message, Throwable cause, String errorLog) {
    super(message, cause);
    this.errorLog = errorLog;
  }

  @Nullable
  public String getErrorLog() {
    return errorLog;
  }
}
