package pl.mpieciukiewicz.scalacqrs

import pl.mpieciukiewicz.scalacqrs.internal.AbstractEvent

trait ModificationEvent[T] extends AbstractEvent[T] {
  override final def apply(): T = {
    throw new IllegalAccessException("This type of event require aggregate that can be modified!")
  }
}
