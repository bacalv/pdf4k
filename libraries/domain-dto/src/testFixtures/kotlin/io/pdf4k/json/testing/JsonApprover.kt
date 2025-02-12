package io.pdf4k.json.testing

import com.oneeyedmen.okeydoke.junit5.ApprovalsExtension
import java.io.File

fun jsonApprover(sourceRoot: String = "src/test/resources") = ApprovalsExtension(File(sourceRoot), ".json")