package intellij.plugin

import com.intellij.lang.Language
import com.intellij.lang.javascript.JSMinifiedFileUtil
import com.intellij.lang.javascript.index.JavaScriptIndex
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.LanguageSubstitutor


val MY_EXCLUDE = Key.create<Boolean>("my.exclude.minified")

class ExcludeLanguageSubstitutor : LanguageSubstitutor() {
    override fun getLanguage(file: VirtualFile, project: Project): Language? {
        val extension = file.extension
        if (extension == "js") {
            val state = ExcludeSettings.getService(project).state
            val hasCreation = true == file.getUserData(MY_EXCLUDE)
            if (state.disableJs) return PlainTextLanguage.INSTANCE
            if (state.disableJsStubIndexing) {
                if (!hasCreation) {
                    if (true != file.getUserData(JavaScriptIndex.SKIP_STUB_CREATION)) {
                        file.putUserData(JavaScriptIndex.SKIP_STUB_CREATION, true)
                        file.putUserData(MY_EXCLUDE, true)
                    }
                }
            } else {
                file.putUserData(JavaScriptIndex.SKIP_STUB_CREATION, false)
                file.putUserData(MY_EXCLUDE, false)
            }
            if (state.disableJsMinifiedFileIndexing && JSMinifiedFileUtil.isFileContentMinified(file)) {
                return PlainTextLanguage.INSTANCE
            }
        }

        return null
    }
}