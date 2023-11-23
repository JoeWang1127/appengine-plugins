/*
 * Copyright 2016 Google LLC.
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

package com.google.cloud.tools.appengine.configuration;

import com.google.cloud.tools.appengine.operations.Deployment;
import com.google.common.base.Preconditions;
import java.nio.file.Path;
import java.util.List;
import javax.annotation.Nullable;

/** Configuration for {@link Deployment#deploy(DeployConfiguration)}. */
public class DeployConfiguration {

  @Nullable private final String bucket;
  private final List<Path> deployables;
  @Nullable private final String gcloudMode;
  @Nullable private final String imageUrl;
  @Nullable private final String projectId;
  @Nullable private final Boolean promote;
  @Nullable private final String server;
  @Nullable private final Boolean stopPreviousVersion;
  @Nullable private final String version;

  private DeployConfiguration(
      @Nullable String bucket,
      List<Path> deployables,
      @Nullable String gcloudMode,
      @Nullable String imageUrl,
      @Nullable String projectId,
      @Nullable Boolean promote,
      @Nullable String server,
      @Nullable Boolean stopPreviousVersion,
      @Nullable String version) {
    this.bucket = bucket;
    this.deployables = deployables;
    this.gcloudMode = gcloudMode;
    this.imageUrl = imageUrl;
    this.projectId = projectId;
    this.promote = promote;
    this.server = server;
    this.stopPreviousVersion = stopPreviousVersion;
    this.version = version;
  }

  /** GCS storage bucket used for staging files associated with deployment. */
  @Nullable
  public String getBucket() {
    return bucket;
  }

  /** Gcloud pre-release mode: like alpha or beta. */
  @Nullable
  public String getGcloudMode() {
    return gcloudMode;
  }

  /** List of deployable target directories or yaml files. */
  public List<Path> getDeployables() {
    return deployables;
  }

  /** Docker image to use during deployment (only for app.yaml deployments). */
  @Nullable
  public String getImageUrl() {
    return imageUrl;
  }

  /** Google Cloud Project ID to deploy to. */
  @Nullable
  public String getProjectId() {
    return projectId;
  }

  /** Promote the deployed version to receive all traffic. */
  @Nullable
  public Boolean getPromote() {
    return promote;
  }

  /** The App Engine server to use. Users typically will never set this value. */
  @Nullable
  public String getServer() {
    return server;
  }

  /** Stop the previous running version when deploying and promote this new version. */
  @Nullable
  public Boolean getStopPreviousVersion() {
    return stopPreviousVersion;
  }

  /** Version to deploy. */
  @Nullable
  public String getVersion() {
    return version;
  }

  public static Builder builder(List<Path> deployables) {
    return new Builder(deployables);
  }

  public static final class Builder {
    @Nullable private String bucket;
    private List<Path> deployables;
    @Nullable private String gcloudMode;
    @Nullable private String imageUrl;
    @Nullable private String projectId;
    @Nullable private Boolean promote;
    @Nullable private String server;
    @Nullable private Boolean stopPreviousVersion;
    @Nullable private String version;

    private Builder(List<Path> deployables) {
      Preconditions.checkNotNull(deployables);
      Preconditions.checkArgument(deployables.size() != 0);

      this.deployables = deployables;
    }

    public DeployConfiguration.Builder bucket(@Nullable String bucket) {
      this.bucket = bucket;
      return this;
    }

    public DeployConfiguration.Builder gcloudMode(@Nullable String gcloudMode) {
      this.gcloudMode = gcloudMode;
      return this;
    }

    public DeployConfiguration.Builder imageUrl(@Nullable String imageUrl) {
      this.imageUrl = imageUrl;
      return this;
    }

    public DeployConfiguration.Builder projectId(@Nullable String projectId) {
      this.projectId = projectId;
      return this;
    }

    public DeployConfiguration.Builder promote(@Nullable Boolean promote) {
      this.promote = promote;
      return this;
    }

    public DeployConfiguration.Builder server(@Nullable String server) {
      this.server = server;
      return this;
    }

    public DeployConfiguration.Builder stopPreviousVersion(@Nullable Boolean stopPreviousVersion) {
      this.stopPreviousVersion = stopPreviousVersion;
      return this;
    }

    public DeployConfiguration.Builder version(@Nullable String version) {
      this.version = version;
      return this;
    }

    /** Build a {@link DeployConfiguration}. */
    public DeployConfiguration build() {
      return new DeployConfiguration(
          this.bucket,
          this.deployables,
          this.gcloudMode,
          this.imageUrl,
          this.projectId,
          this.promote,
          this.server,
          this.stopPreviousVersion,
          this.version);
    }
  }
}
