package io.pdf4k.testing

import com.oneeyedmen.okeydoke.BinaryApprover
import com.oneeyedmen.okeydoke.SourceOfApproval

class PdfApprover(testName: String, sourceOfApproval: SourceOfApproval) : BinaryApprover(testName, sourceOfApproval)
