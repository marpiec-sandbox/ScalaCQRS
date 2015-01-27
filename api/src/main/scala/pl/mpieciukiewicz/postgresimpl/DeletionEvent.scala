package pl.mpieciukiewicz.postgresimpl

import pl.mpieciukiewicz.postgresimpl.internal.Event

trait DeletionEvent[T] extends Event[T]