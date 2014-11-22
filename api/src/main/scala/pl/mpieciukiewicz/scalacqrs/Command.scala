package pl.mpieciukiewicz.scalacqrs

abstract class Command(val aggregateId: UID, expectedAggregateVersion: Int) {

}
