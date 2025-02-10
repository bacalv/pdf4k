package io.pdf4k.testing

import com.oneeyedmen.okeydoke.*
import com.oneeyedmen.okeydoke.internal.IO
import com.oneeyedmen.okeydoke.sources.FileSystemSourceOfApproval
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.extension.*
import org.junit.jupiter.api.extension.ExtensionContext.Namespace
import java.io.File
import java.lang.reflect.Method

class PdfApproverExtension : ParameterResolver, BeforeTestExecutionCallback, AfterTestExecutionCallback {
    override fun beforeTestExecution(context: ExtensionContext) {
        val password = context.testMethod.map {
            it.annotations.filterIsInstance<PdfPassword>().firstOrNull()?.value ?: ""
        }.orElse("")
        store(context).put(STORE_KEY, with(context) {
            PdfApprover(
                nameFor(requiredTestClass, requiredTestMethod, displayName),
                PDFSourceOfApproval(File(DEFAULT_SOURCE_ROOT, Sources.pathForPackage(requiredTestClass.getPackage())), password)
            )
        })
    }

    override fun afterTestExecution(context: ExtensionContext) {
        context.executionException.takeUnless { it.isPresent }?.let { _ ->
            (store(context)[STORE_KEY] as PdfApprover).takeUnless { it.satisfactionChecked() }?.assertSatisfied()
        }
    }

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext?) =
        PdfApprover::class.java == parameterContext.parameter.type

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext) =
        if (PdfApprover::class.java == parameterContext.parameter.type) {
            store(extensionContext)[STORE_KEY]
        } else null

    private fun store(context: ExtensionContext): ExtensionContext.Store =
        context.getStore(Namespace.create(context.requiredTestClass.name, context.requiredTestMethod.name))

    private class PDFSourceOfApproval(dir: File, val password: String) :
        FileSystemSourceOfApproval(dir, dir, ".pdf", Reporters.fileSystemReporter()) {
        override fun <T : Any?> checkActualAgainstApproved(
            testName: String,
            serializer: Serializer<T>,
            checker: Checker<T>
        ) {
            val actual = IO.readResource(actualFor(testName), serializer) as ByteArray?
            val approved =
                IO.readResource(approvedFor(testName), serializer) as ByteArray?
            if (actual == null && approved != null) {
                fail<Unit>("Actual was null")
            }
            if (actual != null && approved == null) {
                fail<Unit>("No approved file found")
            }
            if ( actual != null && approved != null) {
                PdfAssert.assertEquals(approved, actual, password)
            }
        }
    }

    private companion object {
        val DEFAULT_SOURCE_ROOT = System.getProperty("pdf.approver.source-root") ?: "src/test/resources"
        const val STORE_KEY = "pdf.approver"

        fun nameFor(testClass: Class<*>, testMethod: Method, displayName: String): String {
            val className = testClass.getAnnotation(Name::class.java)?.value ?: testClass.simpleName
            val methodName = testMethod.getAnnotation(Name::class.java)?.value ?: testMethod.name
            val display = if (displayName.startsWith(methodName)) "" else ".$displayName"
            return "$className.$methodName$display"
        }
    }
}