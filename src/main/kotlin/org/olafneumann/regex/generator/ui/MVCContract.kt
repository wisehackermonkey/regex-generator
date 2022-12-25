package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.output.CodeGeneratorOptions
import org.olafneumann.regex.generator.ui.model.DisplayModel
import org.olafneumann.regex.generator.recognizer.RecognizerMatch
import org.olafneumann.regex.generator.regex.RecognizerMatchCombinerOptions

interface MVCContract {
    interface View {
        fun applyModel(model: DisplayModel)

        fun showUserGuide(initialStep: Boolean)
    }

    interface Controller {
        fun onFinishedLoading()
        fun onDoneAskingUserForCookies(hasGivenConsent: Boolean)
        fun onUserInputChange(input: String)
        fun onCodeGeneratorOptionsChange(options: CodeGeneratorOptions)
        fun onRecognizerCombinerOptionsChange(options: RecognizerMatchCombinerOptions)
        fun onRecognizerMatchClick(recognizerMatch: RecognizerMatch)
        fun onCopyRegexButtonClick()
        fun onShareButtonClick()

        fun onUndo()
        fun onRedo()

        fun onShowUserGuide(initialStep: Boolean = false)
    }
}
