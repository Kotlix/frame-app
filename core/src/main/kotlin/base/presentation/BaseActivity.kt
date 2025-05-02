package base.presentation

abstract class BaseActivity<
        VS : ViewState,
        VE : ViewEvent,
        VA : ViewAction,
        VM : BaseViewModel<VS, VE, VA>>

{
    abstract val viewModel: VM

    abstract fun onCreate()

    abstract fun renderViewState(viewState: VS)

    abstract fun renderViewEvent(viewEvent: VE)

    protected fun postAction(action: VA) {
        viewModel.postAction(action)
    }
}