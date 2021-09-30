package intellij.plugin

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.fileTypes.ex.FileTypeManagerEx
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBRadioButton
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import javax.swing.ButtonGroup
import javax.swing.JComponent
import javax.swing.JPanel

class ExcludeConfigurable(private val project: Project) : Configurable {

    private val myDefault = JBRadioButton("Default")
    private val myDisableJavaScript = JBRadioButton("Ignore JavaScript files for the project")
    private val myDisableIndexing = JBRadioButton("Disable indexing of JavaScript files for the project")
    private val myDisableIndexingMinified = JBRadioButton("Ignore minified JavaScript files for the project")

    override fun createComponent(): JComponent {
        val builder = FormBuilder.createFormBuilder()
        builder.addComponent(myDefault)
        builder.addComponent(myDisableJavaScript)
        builder.addComponent(myDisableIndexing)
        builder.addComponent(myDisableIndexingMinified)
        val buttonGroup = ButtonGroup()
        buttonGroup.add(myDefault)
        buttonGroup.add(myDisableJavaScript)
        buttonGroup.add(myDisableIndexing)
        buttonGroup.add(myDisableIndexingMinified)
        
        val wrapper = JPanel(BorderLayout())
        wrapper.add(builder.panel, BorderLayout.NORTH)

        return wrapper
    }

    override fun reset() {
        val state = ExcludeSettings.getService(project).state
        myDisableJavaScript.isSelected = state.disableJs
        myDisableIndexing.isSelected = state.disableJsStubIndexing
        myDisableIndexingMinified.isSelected = state.disableJsMinifiedFileIndexing
        myDefault.isSelected = !state.disableJs && !state.disableJsStubIndexing && !state.disableJsMinifiedFileIndexing
    }

    override fun apply() {
        val modified = isModified
        val state = ExcludeSettings.getService(project).state
        state.disableJs = myDisableJavaScript.isSelected
        state.disableJsStubIndexing = myDisableIndexing.isSelected
        state.disableJsMinifiedFileIndexing = myDisableIndexingMinified.isSelected

        if (modified) {
            WriteAction.run<RuntimeException> {
                FileTypeManagerEx.getInstanceEx().fireFileTypesChanged()
            }
        }
    }

    override fun isModified(): Boolean {
        val state = ExcludeSettings.getService(project).state

        return state.disableJs != myDisableJavaScript.isSelected ||
                state.disableJsStubIndexing != myDisableIndexing.isSelected ||
                state.disableJsMinifiedFileIndexing != myDisableIndexingMinified.isSelected
    }

    override fun getDisplayName(): String {
        return "Index Excluder"
    }
}