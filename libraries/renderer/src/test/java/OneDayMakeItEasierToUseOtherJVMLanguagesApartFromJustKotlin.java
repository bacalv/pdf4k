import io.pdf4k.domain.Pdf;
import io.pdf4k.domain.Stationary;
import io.pdf4k.testing.InMemoryRenderer;
import kotlin.Unit;

import java.io.FileOutputStream;

import static io.pdf4k.dsl.PdfBuilder.Companion;

public class OneDayMakeItEasierToUseOtherJVMLanguagesApartFromJustKotlin {
    public static void main(String[] args) throws Exception {
        Pdf pdf = Companion.pdf(null, builder -> {
            builder.section(null, Stationary.Companion.getBlankA4Portrait(), new Stationary[0], page -> {
                page.content(null, content -> {
                    content.paragraph("Hello world!");
                    return Unit.INSTANCE;
                });
                return Unit.INSTANCE;
            });
            return Unit.INSTANCE;
        });
        InMemoryRenderer.INSTANCE.render(pdf, new FileOutputStream("./kaboom.pdf"));
    }
}
