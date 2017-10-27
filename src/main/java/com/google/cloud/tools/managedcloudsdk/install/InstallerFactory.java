/*
 * Copyright 2017 Google Inc.
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

package com.google.cloud.tools.managedcloudsdk.install;

import com.google.cloud.tools.managedcloudsdk.MessageListener;
import com.google.cloud.tools.managedcloudsdk.OsType;
import com.google.cloud.tools.managedcloudsdk.process.CommandExecutorFactory;
import java.nio.file.Path;

/** {@link Installer} Factory. */
final class InstallerFactory {

  private final OsType os;
  private final boolean usageReporting;

  /**
   * Creates a new factory.
   *
   * @param os the operating system of the computer this script is running on
   * @param usageReporting enable or disable client side usage reporting {@code true} is enabled,
   *     {@code false} is disabled
   */
  public InstallerFactory(OsType os, boolean usageReporting) {
    this.os = os;
    this.usageReporting = usageReporting;
  }

  /**
   * Returns a new {@link Installer} instance.
   *
   * @param installedSdkRoot path to the Cloud SDK directory
   * @param messageListener listener on installer script output
   * @return a {@link Installer} instance.
   */
  public Installer<? extends InstallScriptProvider> newInstaller(
      Path installedSdkRoot, MessageListener messageListener) {

    return new Installer<>(
        installedSdkRoot,
        getInstallScriptProvider(),
        usageReporting,
        messageListener,
        new CommandExecutorFactory());
  }

  private InstallScriptProvider getInstallScriptProvider() {
    switch (os) {
      case WINDOWS:
        return new WindowsInstallScriptProvider();
      case MAC:
      case LINUX:
        return new UnixInstallScriptProvider();
      default:
        throw new IllegalStateException("Unexpected OSType: " + os.name());
    }
  }
}