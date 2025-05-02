package base.presentation

import base.coordinator.BaseCoordinator
import base.coordinator.CoordinatorEvent

abstract class BaseViewModel<VS : ViewState, VE : ViewEvent, VA : ViewAction> (
    val coordinator: BaseCoordinator
) {

    abstract fun postAction(action: VA)

    protected fun sendCoordinatorEvent(event: CoordinatorEvent) {
        coordinator.sendCoordinatorEvent(event)
    }
}