package test

import io.voidx.html.exception.NoOutputException
import io.voidx.html.main
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ElementGenTests {
    @Test
    fun element_gen_main_coverage() {
        val tempDir = Files.createTempDirectory("element_gen_test").toFile()
        try {
            // Success case
            main(arrayOf("--output", tempDir.absolutePath))
            assertEquals(tempDir.listFiles()?.isNotEmpty(), true)

            // Missing --output
            assertFailsWith<NoOutputException> {
                main(arrayOf("something"))
            }

            // Empty args
            assertFailsWith<NoOutputException> {
                main(arrayOf())
            }
        } finally {
            tempDir.deleteRecursively()
        }
    }
}
