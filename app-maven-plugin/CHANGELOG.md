# Change Log
All notable changes to this project will be documented in this file.

## 2.5.1-SNAPSHOT

## 2.5.0

* Update to appengine-plugins-core 0.10.0 that supports GAE java17 and java21 runtimes.

## 2.4.4

* Update to appengine-plugins-core 0.9.9, for automatic Java 17 compatibility when running local devserver [appengine-plugins-core#894](https://github.com/GoogleCloudPlatform/appengine-plugins-core/pull/894).

## 2.4.3

### Changed

* Update to appengine-plugins-core 0.9.8 which includes [a version upgrade for gson to 2.8.9](https://github.com/GoogleCloudPlatform/appengine-plugins-core/pull/889) and fix for [`StringIndexOutOfBoundsException`](https://github.com/GoogleCloudPlatform/appengine-plugins-core/pull/890/files). 

## 2.4.2

### Changed

* Update to appengine-plugins-core 0.9.7 allowing Java 17 jar deployment.


## 2.4.1
### Changed
* Update to appengine-plugins-core 0.9.5 ([#446](../../pull/446))
  * Addresses the security advisories in the old commons-compress library ([appengine-plugins-core#875](
    https://github.com/GoogleCloudPlatform/appengine-plugins-core/pull/875)).

## 2.4.0
### Added
* `appengine.tools.verbosity` option for defining gcloud log verbosity ([#429](../../pull/429))

## 2.3.0
### Changed
* Update to appengine-plugins-core 0.9.0 ([#422](../../pull/422))
  * Includes support for binary artifacts for app.yaml based deployments ([appengine-plugins-core:#840](https://github.com/GoogleCloudPlatform/appengine-plugins-core/issues/840))

### Fixed
* Fix error message when executing `appengine:run` on `app.yaml` based projects ([#423](../../pull/423))

## 2.2.0
### Changed
* Update to appengine-plugins-core 0.8.1 ([#405](../../pull/405))

### Added
* Automatically copy `Class-Path` entries in jar manifest to staging directory ([appengine-plugins-core:#804](https://github.com/GoogleCloudPlatform/appengine-plugins-core/pull/804))

## 2.1.0
### Added
* `gcloudMode` to `appengine.deploy` block ([#394](../../pull/394))

## 2.0.0
See 2.0.0-rc1 - 2.0.0-rc6 for details
### Key Changes
* Dev App Server `v2-alpha` support removed.
* Cloud Sdk
  * `cloudSdkHome`, `cloudSdkVersion`, `serviceAccountKeyFile` added to configuration
  * `appengine:cloudSdkLogin` to trigger cloud sdk login flow
  * `checkCloudSdk` validates version of installed sdk on system
  * Managed Cloud SDK
    * Auto download google cloud sdk if no `cloudSdkHome` provided
    * Skip download in `--offline` mode
    * Do not download appengine components for app.yaml based deployments
* Support for multiple extra files directories: `extraFilesDirectory` -> `extraFilesDirectories`
* Support for app.yaml based standard deployments
* `project` is renamed to `projectId`
* `projectId` and `version` are required parameters. Must be set to a value or to `GCLOUD_CONFIG` to delegate to gcloud.
* `projectId` can be configured for multi-module dev appserver runs
* `deployables` config parameter removed
* `appYamls` config paramter removed
* Added `skip` on plugin configuration to skip all appengine goals.

## 2.0.0-rc6
### Added
* Allow configuring `skip` on plugin configuration ([#379](../../pull/379))

### Changed
* Remove support for `APPENGINE_CONFIG` option in `projectId` and `version` ([#368](../../pull/368))
* Remove support for `v2-alpha` devappserver version ([#378](../../pull/378))

### Fixed
* Actually skip downloading/updating the cloud Sdk in offline mode ([#382](../../pull/382))

## 2.0.0-rc5
### Changed
* `app.yaml` based builds do not delete staging directory when staging ([#361](../../pull/362))
* Update to appengine-plugins-core 0.7.3 ([#361](../../pull/362))
* `extraFileDirectory` -> `extraFileDirectories` ([#353](../../pull/353))
* Only download appengine components of cloud sdk when running compat projects ([#352](../../pull/352))

## 2.0.0-rc4
### Changed
* Old flex build path is now used for all app.yaml (standard and flex) based builds ([#337](../../pull/337))
* Using `project` instead of `projectId` displays deprecation warning ([#337](../../pull/337))
* Update to appengine-plugins-core 0.7.1 ([#344](../../pull/344))

## 2.0.0-rc2
### Added
* New `cloudSdkVersion` parameter to specify desired Cloud SDK version.
* Cloud SDK and java app-engine components are automatically installed when `cloudSdkHome` is not provided. ([#247](../../issues/247))
* Cloud SDK installation is verified when `cloudSdkHome` and `cloudSdkVersion` are configured. ([#248](../../issues/248))
* New `<serviceAccountKeyFile>` configuration parameter, and `appengine:cloudSdkLogin` goal. ([#268](../../issues/268))
* New `appengine:deployAll` goal to deploy application with all valid yaml configs simultaneously. ([#273](../../issues/273), [#277](../../issues/277))
* Can set `projectId` and `version` to `GCLOUD_CONFIG` to delegate to gcloud or `APPENGINE_CONFIG` to delegate to appengine-web.xml. ([#305](../../issues/305))

### Changed
* `appengine:stop` no longer fails if the stop request to server fails, but it will log an error. ([#309](https://github.com/GoogleCloudPlatform/app-maven-plugin/pull/309))
* Upgrade App Engine Plugins Core dependency to 0.5.2.
* `cloudSdkPath` has been replaced with `cloudSdkHome`. ([#257](../../issues/257))
* Remove deprecated `appYamls` parameter. ([#162](../../issues/162))
* Appengine goals no longer fork. Instead of running `mvn appengine:<goal>`, you must either explicitly run
`mvn package appengine:<goal>` or bind the goal to a lifecycle phase in your pom.xml. ([#301](../../issues/301))
* Removed `deployables` parameter. To deploy specific configuration files, use the appropriate deploy goals
(i.e. appengine:deployCron, appengine:deployIndex, etc.) ([#300](../../issues/300)).

## 1.3.2
### Added
* New `<additionalArguments>` parameter to pass additional arguments to Dev App Server ([#219](../../pulls/219)),
relevant pull request in App Engine Plugins Core:
[appengine-plugins-core/433](https://github.com/GoogleCloudPlatform/appengine-plugins-core/pull/433)

### Changed
* Upgrade App Engine Plugins Core dependency to 0.3.9 ([#219](../../pulls/219))

## 1.3.1
### Added
* New `<environment>` parameter to pass environment variables to Dev App Server ([#183](../../pulls/183)),
relevant pull request in App Engine Plugins Core:
[appengine-plugins-core/378](https://github.com/GoogleCloudPlatform/appengine-plugins-core/pull/378)
and [appengine-plugins-core/381](https://github.com/GoogleCloudPlatform/appengine-plugins-core/pull/381)

### Changed
* Upgrade App Engine Plugins Core dependency to 0.3.2 ([#183](../../pulls/183))

## 1.3.0
No changes compared to 1.3.0-rc2.

## 1.3.0-rc2
### Fixed

* Setting appEngineDirectory for GAE Standard projects has no effect ([#173](../../issues/173))

## 1.3.0-rc1
### Added

* New goals to deploy App Engine configuration XMLs/YMLs separately. ([#155](../../issues/155))
* Dev Appserver1 integration ([#158](../../issues/158))
* New parameter `devserverVersion` to change between Java Dev Appserver 1 and 2-alpha for local runs.
* Primitive [User Guide](USER_GUIDE.md)

### Changed

* Javadoc update to indicate which parameters are supported by Java Dev Appserver 1 and 2-alpha. ([#167](../../issues/167))
* Default local dev server is Java Dev Appserver1
* `appYamls` parameter is deprecated in favor of `services`

### Fixed

* :deploy goal should quietly skip non-war projects ([#171](../../issues/85))

## 1.2.1
### Fixed

* "Directories are not supported" issue when deploying ([#144](../../issues/144))
