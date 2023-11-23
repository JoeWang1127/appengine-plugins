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

package com.google.cloud.tools.managedcloudsdk.install;

import com.google.cloud.tools.managedcloudsdk.ConsoleListener;
import com.google.cloud.tools.managedcloudsdk.OsInfo;
import com.google.cloud.tools.managedcloudsdk.ProgressListener;
import com.google.cloud.tools.managedcloudsdk.Version;
import com.google.cloud.tools.managedcloudsdk.command.CommandExecutionException;
import com.google.cloud.tools.managedcloudsdk.command.CommandExitException;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.annotation.Nullable;

/** Install an SDK by downloading, extracting and if necessary installing. */
public class SdkInstaller {

  private static final Logger logger = Logger.getLogger(SdkInstaller.class.getName());

  private final FileResourceProviderFactory fileResourceProviderFactory;
  private final ExtractorFactory extractorFactory;
  private final DownloaderFactory downloaderFactory;
  @Nullable private final InstallerFactory installerFactory;
  private final Map<String, String> environmentVariables;

  /** Use {@link #newInstaller} to instantiate. */
  SdkInstaller(
      FileResourceProviderFactory fileResourceProviderFactory,
      DownloaderFactory downloaderFactory,
      ExtractorFactory extractorFactory,
      @Nullable InstallerFactory installerFactory) {
    this(
        fileResourceProviderFactory,
        downloaderFactory,
        extractorFactory,
        installerFactory,
        Collections.emptyMap());
  }

  /** Use {@link #newInstaller} to instantiate. */
  @VisibleForTesting
  SdkInstaller(
      FileResourceProviderFactory fileResourceProviderFactory,
      DownloaderFactory downloaderFactory,
      ExtractorFactory extractorFactory,
      @Nullable InstallerFactory installerFactory,
      Map<String, String> environmentVariables) {
    this.fileResourceProviderFactory = fileResourceProviderFactory;
    this.downloaderFactory = downloaderFactory;
    this.extractorFactory = extractorFactory;
    this.installerFactory = installerFactory;
    this.environmentVariables = environmentVariables;
  }

  /** Download and install a new Cloud SDK. */
  public Path install(
      final ProgressListener progressListener, final ConsoleListener consoleListener)
      throws IOException, InterruptedException, SdkInstallerException, CommandExecutionException,
          CommandExitException {

    FileResourceProvider fileResourceProvider =
        fileResourceProviderFactory.newFileResourceProvider();

    // Cleanup, remove old downloaded archive if exists
    if (Files.isRegularFile(fileResourceProvider.getArchiveDestination())) {
      logger.info("Removing stale archive: " + fileResourceProvider.getArchiveDestination());
      Files.delete(fileResourceProvider.getArchiveDestination());
    }

    // Cleanup, remove old SDK directory if exists
    if (Files.exists(fileResourceProvider.getArchiveExtractionDestination())) {
      logger.info(
          "Removing stale install: " + fileResourceProvider.getArchiveExtractionDestination());

      MoreFiles.deleteRecursively(
          fileResourceProvider.getArchiveExtractionDestination(),
          RecursiveDeleteOption.ALLOW_INSECURE);
    }

    progressListener.start("Installing Cloud SDK", installerFactory != null ? 300 : 200);

    // download and verify
    Downloader downloader =
        downloaderFactory.newDownloader(
            fileResourceProvider.getArchiveSource(),
            fileResourceProvider.getArchiveDestination(),
            progressListener.newChild(100));
    downloader.download();
    if (!Files.isRegularFile(fileResourceProvider.getArchiveDestination())) {
      throw new SdkInstallerException(
          "Download succeeded but valid archive not found at "
              + fileResourceProvider.getArchiveDestination());
    }

    try {
      // extract and verify
      extractorFactory
          .newExtractor(
              fileResourceProvider.getArchiveDestination(),
              fileResourceProvider.getArchiveExtractionDestination(),
              progressListener.newChild(100))
          .extract();
      if (!Files.isDirectory(fileResourceProvider.getExtractedSdkHome())) {
        throw new SdkInstallerException(
            "Extraction succeeded but valid sdk home not found at "
                + fileResourceProvider.getExtractedSdkHome());
      }
    } catch (UnknownArchiveTypeException e) {
      // fileResourceProviderFactory.newFileResourceProvider() creates a fileResourceProvider that
      // returns either .tar.gz or .zip for getArchiveDestination().
      throw new RuntimeException(e);
    }

    // install if necessary
    if (installerFactory != null) {
      installerFactory
          .newInstaller(
              fileResourceProvider.getExtractedSdkHome(),
              progressListener.newChild(100),
              consoleListener,
              environmentVariables)
          .install();
    }

    // verify final state
    if (!Files.isRegularFile(fileResourceProvider.getExtractedGcloud())) {
      throw new SdkInstallerException(
          "Installation succeeded but gcloud executable not found at "
              + fileResourceProvider.getExtractedGcloud());
    }

    progressListener.done();
    return fileResourceProvider.getExtractedSdkHome();
  }

  /**
   * Configure and create a new Installer instance.
   *
   * @param managedSdkDirectory directory where the Cloud SDK will be installed
   * @param version version of the Cloud SDK to install
   * @param osInfo target operating system for installation
   * @param userAgentString user agent string for https requests
   * @param usageReporting enable client side usage reporting on gcloud
   * @param environmentVariables map of additional environment variables to be passed to the
   *     installer process (proxy settings, etc.)
   * @return a new configured Cloud SDK Installer
   */
  public static SdkInstaller newInstaller(
      Path managedSdkDirectory,
      Version version,
      OsInfo osInfo,
      String userAgentString,
      boolean usageReporting,
      Map<String, String> environmentVariables) {
    return SdkInstaller.newInstaller(
        managedSdkDirectory,
        version,
        osInfo,
        userAgentString,
        usageReporting,
        null,
        environmentVariables);
  }

  /**
   * Configure and create a new Installer instance.
   *
   * @param managedSdkDirectory directory where the Cloud SDK will be installed
   * @param version version of the Cloud SDK to install
   * @param osInfo target operating system for installation
   * @param userAgentString user agent string for https requests
   * @param usageReporting enable client side usage reporting on gcloud
   * @param environmentVariables map of additional environment variables to be passed to the
   *     installer process (proxy settings, etc.)
   * @param overrideComponents gcloud components to install instead of the defaults
   * @return a new configured Cloud SDK Installer
   */
  public static SdkInstaller newInstaller(
      Path managedSdkDirectory,
      Version version,
      OsInfo osInfo,
      String userAgentString,
      boolean usageReporting,
      @Nullable Set<String> overrideComponents,
      Map<String, String> environmentVariables) {
    DownloaderFactory downloaderFactory = new DownloaderFactory(userAgentString);
    ExtractorFactory extractorFactory = new ExtractorFactory();

    InstallerFactory installerFactory =
        version == Version.LATEST
            ? new InstallerFactory(osInfo, usageReporting, overrideComponents)
            : null;

    FileResourceProviderFactory fileResourceProviderFactory =
        new FileResourceProviderFactory(version, osInfo, managedSdkDirectory);

    return new SdkInstaller(
        fileResourceProviderFactory,
        downloaderFactory,
        extractorFactory,
        installerFactory,
        environmentVariables);
  }
}
