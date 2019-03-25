package edu.cnm.deepdive.nasaapod.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import edu.cnm.deepdive.nasaapod.model.entity.Apod;
import edu.cnm.deepdive.nasaapod.model.pojo.ApodWithAccesses;
import edu.cnm.deepdive.util.Date;
import java.util.Collection;
import java.util.List;

/**
 * Declares basic CRUD operations for {@link Apod} instances in the local database, using Room
 * annotations.
 */
@Dao
public interface ApodDao {

  /**
   * Inserts a single {@link Apod} instances into the local database. Any primary or unique key
   * constraint violations will result in the existing records being retained.
   *
   * @param apod {@link Apod} instance to be inserted.
   * @return inserted record ID(s).
   */
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  long insert(Apod apod);

  /**
   * Inserts zero or more {@link Apod} instances into the local database. Any primary or unique key
   * constraint violations will result in the existing records being retained.
   *
   * @param apods {@link Apod} instance(s) (or an array of instances) to be inserted.
   * @return inserted record ID(s).
   */
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  List<Long> insert(Apod... apods);

  /**
   * Inserts zero or more {@link Apod} instances into the local database. Any primary or unique key
   * constraint violations will result in the existing records being retained.
   *
   * @param apods collection of {@link Apod} instance(s) to be inserted.
   * @return inserted record ID(s).
   */
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  List<Long> insert(Collection<Apod> apods);

  /**
   * Selects and returns the single {@link Apod} instance (or null) for the specified {@link Date}.
   *
   * @param date desired {@link Apod} {@link Date}.
   * @return {@link Apod} instance if found in database; <code>null</code> otherwise.
   */
  @Query("SELECT * FROM Apod WHERE date = :date")
  Apod findFirstByDate(Date date);

  /**
   * Selects and returns all {@link Apod} instances in the local database, sorting the result in
   * descending date order.
   *
   * @return all {@link Apod} instances in local database.
   */
  @Query("SELECT * FROM Apod ORDER BY date DESC")
  List<Apod> findAll();

  /**
   * Selects and returns all <code>Apod</code> instances in the local database, each with its
   * related <code>Access</code> instances (i.e. {@link ApodWithAccesses} instances), sorting the
   * result in descending date order.
   *
   * @return joined {@link ApodWithAccesses} instances.
   */
  @Transaction
  @Query("SELECT * FROM Apod ORDER BY date DESC")
  List<ApodWithAccesses> findAllWithAccesses();

  /**
   * Deletes zero or more {@link Apod} instances from local database.
   *
   * @param apods instances of {@link Apod} to be deleted from database.
   * @return number of records deleted.
   */
  @Delete
  int delete(Apod... apods);

  /**
   * Deletes zero or more {@link Apod} instances from local database.
   *
   * @param apods instances of {@link Apod} to be deleted from database.
   * @return number of records deleted.
   */
  @Delete
  int delete(Collection<Apod> apods);

}
