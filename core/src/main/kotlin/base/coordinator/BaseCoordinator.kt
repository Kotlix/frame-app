package base.coordinator

abstract class BaseCoordinator {
    abstract fun sendCoordinatorEvent(event: CoordinatorEvent)
}