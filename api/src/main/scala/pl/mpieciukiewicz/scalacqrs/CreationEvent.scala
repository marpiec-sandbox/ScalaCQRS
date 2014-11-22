package pl.mpieciukiewicz.scalacqrs

import pl.mpieciukiewicz.scalacqrs.internal.AbstractEvent

trait CreationEvent[T] extends AbstractEvent[T] {

  override final def apply(entity: T): T = {
    throw new IllegalAccessException("This type of event doesn't support modification of existing aggregate!")
  }

}
