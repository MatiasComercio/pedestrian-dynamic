package ar.edu.itba.ss.pedestriandynamic.models;

import org.immutables.builder.Builder;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(
        typeAbstract = "*Abs",
        typeImmutable = "*",
        get = ""
)
public abstract class TargetAbs {

  @Builder.Parameter
  public abstract Vector2D position();
}
