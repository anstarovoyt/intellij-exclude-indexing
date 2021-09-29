package intellij.plugin

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project


class ExcludeState {
    var disableJs: Boolean = false
    var disableJsStubIndexing: Boolean = false
    var disableJsMinifiedFileIndexing: Boolean = false
}

@State(name = "IndexingExlcude", storages = [Storage("indexing-exclude.xml")])
class ExcludeSettings : PersistentStateComponent<ExcludeState> {

    companion object {
        fun getService(project: Project): ExcludeSettings {
            return project.getService(ExcludeSettings::class.java)
        }
    }

    private var state = ExcludeState()

    override fun getState(): ExcludeState {
        return state
    }

    override fun loadState(state: ExcludeState) {
        this.state = state
    }
}