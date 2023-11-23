/*
 * Copyright 2018 Google LLC. All Rights Reserved.
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

package com.google.cloud.tools.gradle.appengine.core;

import com.google.cloud.tools.appengine.AppEngineException;
import com.google.cloud.tools.appengine.operations.Gcloud;
import org.gradle.api.tasks.TaskAction;

public class CloudSdkLoginTask extends GcloudTask {

  private Gcloud gcloud;

  public void setGcloud(Gcloud gcloud) {
    this.gcloud = gcloud;
  }

  /** Login by delegating to gcloud auth login. */
  @TaskAction
  public void login() throws AppEngineException {
    gcloud.newAuth(CloudSdkOperations.getDefaultHandler(getLogger())).login();
  }
}
