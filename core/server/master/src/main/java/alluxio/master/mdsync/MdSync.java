/*
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */

package alluxio.master.mdsync;

import alluxio.AlluxioURI;
import alluxio.master.file.metasync.SyncResult;
import alluxio.underfs.UfsClient;
import alluxio.underfs.UfsLoadResult;

import java.util.Optional;
import java.util.function.Function;

/**
 * The interactions between different task processing steps is exposed through this
 * standard interface in order to allow changes in the future, for example calling
 * separate components over the network.
 */
public class MdSync {

  TaskTracker mTaskTracker;
  Function<AlluxioURI, UfsClient> mGetClient;

  public MdSync(TaskTracker taskTracker) {
    mTaskTracker = taskTracker;
  }

  void onLoadRequestError(long taskId, long loadId, Throwable t) {
    mTaskTracker.getTask(taskId).ifPresent(
        task -> task.getPathLoadTask().onLoadRequestError(loadId, t));
  }

  void onFailed(long taskId, Throwable t) {
    mTaskTracker.getTask(taskId).ifPresent(task -> task.onFailed(t));
  }

  void onProcessError(long taskId, Throwable t) {
    mTaskTracker.getTask(taskId).ifPresent(task ->
        task.getPathLoadTask().onProcessError(t));
  }

  void onEachResult(long taskId, SyncProcessResult result) {
    mTaskTracker.getTask(taskId).ifPresent(task -> task.nextCompleted(result));
  }

  void onTaskError(long taskId, Throwable t) {
    mTaskTracker.getTask(taskId).ifPresent(task -> mTaskTracker.taskError(taskId, t));
  }

  void onTaskComplete(long taskId, boolean isFile) {
    mTaskTracker.taskComplete(taskId, isFile);
  }

  void onPathLoadComplete(long taskId, boolean isFile, SyncResult result) {
    mTaskTracker.getTask(taskId).ifPresent(task -> task.onComplete(isFile, result));
  }

  public void loadNestedDirectory(long taskId, AlluxioURI path) {
    mTaskTracker.getTask(taskId).ifPresent(
        task -> task.getPathLoadTask().loadNestedDirectory(path));
  }

  Optional<LoadResult> onReceiveLoadRequestOutput(long taskId, long loadId, UfsLoadResult result) {
    return mTaskTracker.getTask(taskId).flatMap(task ->
        task.getPathLoadTask().createLoadResult(loadId, result));
  }

  void onProcessComplete(long taskId, long loadRequestId, SyncProcessResult result) {
    mTaskTracker.getTask(taskId).ifPresent(task ->
        task.getPathLoadTask().onProcessComplete(loadRequestId, result));
  }
}
