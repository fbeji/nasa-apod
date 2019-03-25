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
