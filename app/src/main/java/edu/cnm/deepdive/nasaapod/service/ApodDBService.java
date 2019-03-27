/*
 *  Copyright 2019 Nicholas Bennett & Deep Dive Coding
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.cnm.deepdive.nasaapod.service;

import android.support.annotation.Nullable;
import edu.cnm.deepdive.android.BaseFluentAsyncTask;
import edu.cnm.deepdive.nasaapod.model.ApodDB;
import edu.cnm.deepdive.nasaapod.model.entity.Access;
import edu.cnm.deepdive.nasaapod.model.entity.Apod;
import edu.cnm.deepdive.nasaapod.model.pojo.ApodWithAccessCount;
import edu.cnm.deepdive.nasaapod.model.pojo.ApodWithAccesses;
import edu.cnm.deepdive.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides a service layer for accessing the {@link ApodDB} Room/SQLite database. Each operation is
 * implemented as a static nested class that extends {@link BaseFluentAsyncTask}.
 */
public final class ApodDBService {

  private ApodDBService() {
  }

  /**
   * Implements an asynchronous <code>INSERT</code> of an {@link Apod} instance into the local
   * database.
   */
  public static class InsertApodTask
      extends BaseFluentAsyncTask<Apod, Void, Apod, Apod> {

    @Override
    protected Apod perform(Apod... apods) {
      Apod apod = apods[0];
      long id = ApodDB.getInstance().getApodDao().insert(apods[0]);
      apod.setId(id);
      return apod;
    }

  }

  /**
   * Implements an asynchronous <code>SELECT</code> of a single {@link Apod} instance from the local
   * database.
   */
  public static class SelectApodTask extends BaseFluentAsyncTask<Date, Void, Apod, Apod> {

    @Override
    protected Apod perform(Date... dates) {
      Apod apod = ApodDB.getInstance().getApodDao().findFirstByDate(dates[0]);
      if (apod == null) {
        throw new TaskException();
      }
      return apod;
    }

  }

  /**
   * Implements an asynchronous <code>SELECT</code> of all {@link Apod} instances (sorted in
   * descending date order) from the local database.
   */
  public static class SelectAllApodTask
      extends BaseFluentAsyncTask<Void, Void, List<Apod>, List<Apod>> {

    @Override
    protected List<Apod> perform(Void... voids) {
      return ApodDB.getInstance().getApodDao().findAll();
    }

  }

  /**
   * Implements an asynchronous <code>SELECT</code> of all {@link ApodWithAccesses} instances, each
   * containing an embedded {@link Apod} instance and a list of related {@link Access} instances.
   */
  public static class SelectAllApodWithAccessesTask
      extends BaseFluentAsyncTask<Void, Void, List<ApodWithAccesses>, List<ApodWithAccesses>> {

    @Override
    protected List<ApodWithAccesses> perform(Void... voids) {
      return ApodDB.getInstance().getApodDao().findAllWithAccesses();
    }

  }

  /**
   * Implements an asynchronous <code>SELECT</code> of all {@link ApodWithAccessCount} instances,
   * each containing an embedded {@link Apod} instance and a count of related {@link Access}
   * instances.
   */
  public static class SelectAllApodWithAccessCountTask
      extends BaseFluentAsyncTask<Void, Void, List<ApodWithAccessCount>, List<ApodWithAccessCount>> {

    @Override
    protected List<ApodWithAccessCount> perform(Void... voids) {
      return ApodDB.getInstance().getApodDao().findAllWithAccessCount();
    }

  }

  /**
   * Implements an asynchronous <code>SELECT</code> of the most recently accessed {@link Apod}
   * instances (sorted in descending date order) from the local database.
   */
  public static class SelectMRUApodTask extends BaseFluentAsyncTask<Integer, Void, List<Apod>, List<Apod>> {

    @Nullable
    @Override
    protected List<Apod> perform(Integer... params) throws TaskException {
      return ApodDB.getInstance().getApodDao().findMRU(params[0]);
    }

  }

  /**
   * Implements an asynchronous <code>DELETE</code> of one or more {@link Access} instances from the
   * local database.
   */
  public static class DeleteApodTask extends BaseFluentAsyncTask<Apod, Void, Void, Void> {

    @Nullable
    @Override
    protected Void perform(Apod... apods) throws TaskException {
      ApodDB.getInstance().getApodDao().delete(apods);
      return null;
    }

  }

  /**
   * Implements an asynchronous <code>INSERT</code> of one {@link Access} instance, referencing the
   * specified {@link Apod} instance, into the local database.
   */
  public static class InsertAccessTask
      extends BaseFluentAsyncTask<Apod, Void, Apod, Apod> {

    @Nullable
    @Override
    protected Apod perform(Apod... apods) throws TaskException {
      Apod apod = apods[0];
      Access access = new Access();
      access.setApodId(apod.getId());
      ApodDB.getInstance().getAccessDao().insert(access);
      return apod;
    }

  }

}
