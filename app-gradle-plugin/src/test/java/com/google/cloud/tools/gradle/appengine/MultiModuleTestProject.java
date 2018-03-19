/*
 * Copyright (c) 2018 Google Inc. All Right Reserved.
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

package com.google.cloud.tools.gradle.appengine;

import com.google.cloud.tools.gradle.appengine.standard.AppEngineStandardPlugin;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.gradle.api.Project;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.testfixtures.ProjectBuilder;

/** Test helper to create multimodule appengine projects. */
public class MultiModuleTestProject {

  private final File projectRoot;
  private final List<String> modules = new ArrayList<>();

  public MultiModuleTestProject(File projectRoot) {
    this.projectRoot = projectRoot;
  }

  public MultiModuleTestProject addModule(String moduleName) {
    modules.add(moduleName);
    return this;
  }

  /**
   * Build and evaluate multi-module project.
   *
   * @return root project
   */
  public Project build() {
    Project rootProject = ProjectBuilder.builder().withProjectDir(projectRoot).build();
    for (String module : modules) {
      Project p = ProjectBuilder.builder().withName(module).withParent(rootProject).build();
      p.getPluginManager().apply(JavaPlugin.class);
      p.getPluginManager().apply(WarPlugin.class);
      p.getPluginManager().apply(AppEngineStandardPlugin.class);
    }
    ((ProjectInternal) rootProject).evaluate();
    return rootProject;
  }
}
