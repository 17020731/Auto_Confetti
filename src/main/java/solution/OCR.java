package solution;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

public class OCR {
    public String getTextRecognized(String imageSave) {

        String str = "";
        try {
            ITesseract image = new Tesseract();
            image.setDatapath("F:/BaitaplonOOP/HackConfetti");
            image.setLanguage("vie");

            str = image.doOCR(new File("F:\\BaitaplonOOP\\HackConfetti\\" + imageSave));
//            System.out.println(str);
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        return str;
    }

}
