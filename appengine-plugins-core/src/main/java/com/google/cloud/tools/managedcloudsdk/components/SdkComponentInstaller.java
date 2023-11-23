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

package com.google.cloud.tools.managedcloudsdk.components;

import com.google.cloud.tools.managedcloudsdk.ConsoleListener;
import com.google.cloud.tools.managedcloudsdk.OsInfo;
import com.google.cloud.tools.managedcloudsdk.ProgressListener;
import com.google.cloud.tools.managedcloudsdk.command.CommandCaller;
import com.google.cloud.tools.managedcloudsdk.command.CommandExecutionException;
import com.google.cloud.tools.managedcloudsdk.command.CommandExitException;
import com.google.cloud.tools.managedcloudsdk.command.CommandRunner;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

/** Install an SDK component. */
public class SdkComponentInstaller {

  private final Path gcloudPath;
  private final CommandRunner commandRunner;
  @Nullable private final BundledPythonCopier pythonCopier;

  /** Use {@link #newComponentInstaller} to instantiate. */
  @VisibleForTesting
  SdkComponentInstaller(
      Path gcloudPath, CommandRunner commandRunner, @Nullable BundledPythonCopier pythonCopier) {
    Preconditions.checkArgument(gcloudPath.isAbsolute());
    this.gcloudPath = Preconditions.checkNotNull(gcloudPath);
    this.commandRunner = Preconditions.checkNotNull(commandRunner);
    this.pythonCopier = pythonCopier;
  }

  /**
   * Install a component.
   *
   * @param component component to install
   * @param progressListener listener to action progress feedback
   * @param consoleListener listener to process console feedback
   */
  public void installComponent(
      SdkComponent component, ProgressListener progressListener, ConsoleListener consoleListener)
      throws InterruptedException, CommandExitException, CommandExecutionException {
    installComponents(Collections.singletonList(component), progressListener, consoleListener);
  }

  /**
   * Install components.
   *
   * @param components list of components to install
   * @param progressListener listener to action progress feedback
   * @param consoleListener listener to process console feedback
   */
  public void installComponents(
      List<SdkComponent> components,
      ProgressListener progressListener,
      ConsoleListener consoleListener)
      throws InterruptedException, CommandExitException, CommandExecutionException {

    String message =
        "Installing "
            + components.stream().map(SdkComponent::toString).collect(Collectors.joining(", "));
    progressListener.start(message, ProgressListener.UNKNOWN);

    Map<String, String> environment = null;
    if (pythonCopier != null) {
      environment = pythonCopier.copyPython();
    }

    List<String> command = new ArrayList<>();
    Collections.addAll(command, gcloudPath.toString(), "components", "install");
    components.forEach(component -> command.add(component.toString()));
    command.add("--quiet");

    Path workingDirectory = gcloudPath.getRoot();
    commandRunner.run(command, workingDirectory, environment, consoleListener);
    progressListener.done();
  }

  /**
   * Configure and create a new Component Installer instance.
   *
   * @param gcloudPath full path to gcloud in the Cloud SDK
   * @return a new configured Cloud SDK component installer
   */
  public static SdkComponentInstaller newComponentInstaller(OsInfo.Name osName, Path gcloudPath) {
    switch (osName) {
      case WINDOWS:
        return new SdkComponentInstaller(
            gcloudPath,
            CommandRunner.newRunner(),
            new WindowsBundledPythonCopier(gcloudPath, CommandCaller.newCaller()));
      default:
        return new SdkComponentInstaller(gcloudPath, CommandRunner.newRunner(), null);
    }
  }
}
