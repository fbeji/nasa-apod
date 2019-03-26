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
package edu.cnm.deepdive.nasaapod.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import edu.cnm.deepdive.nasaapod.model.entity.Access;
import java.util.Collection;
import java.util.List;

@Dao
public interface AccessDao {

  /**
   * Inserts one {@link Access} instance into the local database. Any primary key or
   * foreign key constraint violations will result in the existing records being retained.
   *
   * @param access {@link Access} instance to be inserted.
   * @return list of inserted record ID(s).
   */
  @Insert
  long insert(Access access);

  /**
   * Inserts zero or more {@link Access} instances into the local database. Any primary key or
   * foreign key constraint violations will result in the existing records being retained.
   *
   * @param accesses 0 or more {@link Access} instance(s) (or an array of them) to be inserted.
   * @return list of inserted record ID(s).
   */
  @Insert
  List<Long> insert(Access... accesses);

  /**
   * Inserts zero or more {@link Access} instances into the local database. Any primary key or
   * foreign key constraint violations will result in the existing records being retained.
   *
   * @param accesses collection of {@link Access} instance(s) to be inserted.
   * @return list of inserted record ID(s).
   */
  @Insert
  List<Long> insert(Collection<Access> accesses);

}
