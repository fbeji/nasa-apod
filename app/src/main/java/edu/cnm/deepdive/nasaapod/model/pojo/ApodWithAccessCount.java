package edu.cnm.deepdive.nasaapod.model.pojo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;
import edu.cnm.deepdive.nasaapod.model.entity.Access;
import edu.cnm.deepdive.nasaapod.model.entity.Apod;
import java.util.List;

/**
 * Encapsulates a join between the {@link Apod} and a <code>GROUP BY</code> query of related {@link
 * Access} entities. Each instance of this class contains an embedded instance of {@link Apod}, and
 * a count of the related {@link Access} instances.
 */
public class ApodWithAccessCount {

  @Embedded
  private Apod apod;

  @ColumnInfo(name = "access_count")
  private int accessCount;

  /**
   * Returns the embedded {@link Apod} instance.
   *
   * @return reference to embedded {@link Apod} instance.
   */
  public Apod getApod() {
    return apod;
  }

  /**
   * Sets the reference to the embedded {@link Apod} instance. This method is invoked exclusively by
   * Room, when creating instances of this class based on a join.
   *
   * @param apod reference to embedded {@link Apod} instance.
   */
  public void setApod(Apod apod) {
    this.apod = apod;
  }

  /**
   * Returns a count of the {@link Access} entity instances related to the embedded {@link Apod}
   * instance.
   *
   * @return count of related {@link Access} entity instances.
   */
  public int getAccessCount() {
    return accessCount;
  }

  /**
   * Sets the count of the {@link Access} entity instances related to the embedded {@link Apod}
   * instance. This method is invoked exclusively by Room, when creating instances of this class
   * based on a join.
   *
   * @param accessCount count of related {@link Access} entity instances.
   */
  public void setAccessCount(int accessCount) {
    this.accessCount = accessCount;
  }

}
