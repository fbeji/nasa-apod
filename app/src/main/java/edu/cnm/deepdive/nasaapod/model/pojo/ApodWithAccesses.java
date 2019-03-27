package edu.cnm.deepdive.nasaapod.model.pojo;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;
import edu.cnm.deepdive.nasaapod.model.entity.Access;
import edu.cnm.deepdive.nasaapod.model.entity.Apod;
import java.util.List;

/**
 * Encapsulates a join between the {@link Apod} and {@link Access} entities. Each instance of this
 * class contains an embedded instance of {@link Apod}, as well as a list of the associated {@link
 * Access} instances.
 */
public class ApodWithAccesses {

  @Embedded
  private Apod apod;

  @Relation(parentColumn = "apod_id", entityColumn = "apod_id")
  private List<Access> accesses;

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
   * Returns the list of {@link Access} instances related to the embedded {@link Apod} instance
   * returned by {@link #getApod()}.
   *
   * @return related {@link Access} instances.
   */
  public List<Access> getAccesses() {
    return accesses;
  }

  /**
   * Sets the list of {@link Access} instances related to the embedded {@link Apod} instance. This
   * method is invoked exclusively by Room, when creating instances of this class based on a join.
   *
   * @param accesses related {@link Access} instances.
   */
  public void setAccesses(List<Access> accesses) {
    this.accesses = accesses;
  }

}
