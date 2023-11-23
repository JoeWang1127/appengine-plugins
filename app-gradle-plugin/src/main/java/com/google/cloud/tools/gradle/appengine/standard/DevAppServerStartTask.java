/*
 * Copyright 2016 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.google.cloud.tools.gradle.appengine.standard;

import com.google.cloud.tools.appengine.AppEngineException;
import com.google.cloud.tools.appengine.operations.DevServers;
import com.google.cloud.tools.appengine.operations.cloudsdk.process.LegacyProcessHandler;
import com.google.cloud.tools.appengine.operations.cloudsdk.process.NonZeroExceptionExitListener;
import com.google.cloud.tools.appengine.operations.cloudsdk.process.ProcessHandler;
import com.google.cloud.tools.gradle.appengine.util.io.FileOutputLineListener;
import java.io.File;
import java.io.IOException;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

/** Start the App Engine development server asynchronously. */
public class DevAppServerStartTask extends DefaultTask {

  private RunExtension runConfig;
  private DevServers devServers;
  private File devAppServerLoggingDir;

  public DevAppServerStartTask() {
    // force it to always run (required since we use OutputDirectory)
    this.getOutputs().upToDateWhen(task -> false);
  }

  public void setRunConfig(RunExtension runConfig) {
    this.runConfig = runConfig;
  }

  public void setDevServers(DevServers devServers) {
    this.devServers = devServers;
  }

  public void setDevAppServerLoggingDir(File devAppServerLoggingDir) {
    this.devAppServerLoggingDir = devAppServerLoggingDir;
  }

  @OutputDirectory
  public File getDevAppServerLoggingDir() {
    return devAppServerLoggingDir;
  }

  /** Task entrypoint : start the dev appserver (non-blocking). */
  @TaskAction
  public void startAction() throws AppEngineException, IOException {

    // Add a listener to write to a file for non-blocking starts, this really only works
    // when the gradle daemon is running (which is default for newer versions of gradle)
    File logFile = new File(devAppServerLoggingDir, "dev_appserver.out");
    FileOutputLineListener logFileWriter = new FileOutputLineListener(logFile);

    Logger taskLogger = getLogger();
    ProcessHandler processHandler =
        LegacyProcessHandler.builder()
            .addStdOutLineListener(taskLogger::lifecycle)
            .addStdOutLineListener(logFileWriter)
            .addStdErrLineListener(taskLogger::lifecycle)
            .addStdErrLineListener(logFileWriter)
            .setExitListener(new NonZeroExceptionExitListener())
            .buildDevAppServerAsync(runConfig.getStartSuccessTimeout());

    devServers.newDevAppServer(processHandler).run(runConfig.toRunConfiguration());

    getLogger().lifecycle("Dev App Server output written to : " + logFile.getAbsolutePath());
  }
}
