/*
 * Copyright 2018 Google LLC.
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

package com.google.cloud.tools.appengine.operations;

import com.google.cloud.tools.appengine.AppEngineException;
import com.google.cloud.tools.appengine.operations.cloudsdk.internal.args.GcloudArgs;
import com.google.cloud.tools.appengine.operations.cloudsdk.process.ProcessHandlerException;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** Run various gcloud auth commands. */
public class Auth {
  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]+$", Pattern.CASE_INSENSITIVE);

  private final GcloudRunner runner;

  Auth(GcloudRunner runner) {
    this.runner = Preconditions.checkNotNull(runner);
  }

  /**
   * Logs into the Cloud SDK with a specific user (does not retrigger auth flow if user is already
   * configured for the system).
   *
   * @param user a user email
   * @throws AppEngineException when there is an issue with the auth flow
   */
  public void login(String user) throws AppEngineException {
    Preconditions.checkNotNull(user);
    if (!EMAIL_PATTERN.matcher(user).find()) {
      throw new AppEngineException("Invalid email address: " + user);
    }
    try {
      runner.run(ImmutableList.of("auth", "login", user), null);
    } catch (ProcessHandlerException | IOException ex) {
      throw new AppEngineException(ex);
    }
  }

  /** Launches the gcloud auth login flow. */
  public void login() throws AppEngineException {
    try {
      runner.run(ImmutableList.of("auth", "login"), null);
    } catch (ProcessHandlerException | IOException ex) {
      throw new AppEngineException(ex);
    }
  }

  /**
   * Activates a service account based on a configured json key file.
   *
   * @param jsonFile a service account json key file
   * @throws AppEngineException when there is an issue with the auth flow
   */
  public void activateServiceAccount(Path jsonFile) throws AppEngineException {
    Preconditions.checkArgument(Files.exists(jsonFile), "File does not exist: " + jsonFile);
    try {
      List<String> args = new ArrayList<>(3);
      args.add("auth");
      args.add("activate-service-account");
      args.addAll(GcloudArgs.get("key-file", jsonFile));
      runner.run(args, null);
    } catch (ProcessHandlerException | IOException ex) {
      throw new AppEngineException(ex);
    }
  }
}
