package me.ste.library.datagen

import net.minecraft.data.DataGenerator
import java.nio.file.Path

interface DataGeneratorExtensions {
    var steveslib_outputFolder: Path?

    companion object {
        fun withOutputPath(generator: DataGenerator, path: Path, runnable: Runnable) {
            val old = generator.outputFolder
            (generator as DataGeneratorExtensions).steveslib_outputFolder = path
            runnable.run()
            generator.steveslib_outputFolder = old
        }
    }
}