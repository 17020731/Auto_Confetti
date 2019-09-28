package solution;

import com.jfoenix.controls.JFXButton;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class Controller implements Initializable {

    private static final String USER_AGENT = "Mozilla/5.0";
    //    RubberBandSelection rubberBandSelection;
    private OCR ocr = new OCR();

    private static final String QUESTION_IMAGE = "question.png";
    private static final String ANSWER_A_IMAGE = "anwserA.png";
    private static final String ANSWER_B_IMAGE = "answerB.png";
    private static final String ANSWER_C_IMAGE = "answerC.png";

    private static String URL = "https://www.google.com/search?query=";
    private static String URL_WIKI = "https://vi.wikipedia.org/wiki/";

    //    private int A = 0, B = 0, C = 0;
    private Bounds bound_ques = new Rectangle(15, 615, 511, 153).getBoundsInParent();
    private Bounds bound_ans1 = new Rectangle(33, 770, 475, 60).getBoundsInParent();
    private Bounds bound_ans2 = new Rectangle(33, 853, 475, 60).getBoundsInParent();
    private Bounds bound_ans3 = new Rectangle(33, 938, 475, 60).getBoundsInParent();


    private String[] arrWords = {"nào", "của", "là", "đâu", "ở", "từ", "theo", "sau đây", "trong", "có", "được", "gì", "tại", "các", "đâu là"};
    private List<String> words;

    // Buttons controll
    @FXML
    JFXButton btn_setup, btn_result, btn_sleep, btn_exit;

    //Pane SET UP
    @FXML
    AnchorPane container, setupPane, resultPane;

    //Image to Detect Question, Answer A, Answer B, Answer C
    @FXML
    ImageView img_ques, img_ansA, img_ansB, img_ansC;
    @FXML
    ImageView open1, open2;

    //Show text detected from image (OCR)
    @FXML
    Label lbTime;

    //Button set up crop image
    @FXML
    JFXButton quesSetup, ans1Setup, ans2Setup, ans3Setup;

    @FXML
    BarChart barChart1, barChart2;

    @FXML
    JFXButton btn_start;

    WebView webView = new WebView();
    WebEngine webEngine = webView.getEngine();


    //Process event when click the Menu Items
    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getTarget() == btn_setup) {
            setupPane.setVisible(true);
            resultPane.setVisible(false);

            open1.setVisible(true);
            open2.setVisible(false);
        } else if (event.getTarget() == btn_result) {
            resultPane.setVisible(true);
            setupPane.setVisible(false);

            open1.setVisible(false);
            open2.setVisible(true);

            webEngine.load(URL);

            webView.setPrefSize(1037, 777);
            resultPane.getChildren().add(webView);
        } else if (event.getTarget() == btn_sleep) {
            setupPane.setVisible(false);
        } else if (event.getTarget() == btn_exit) {
            System.exit(1);
        }
    }

    //Format Special Characters
    public String formatText(String str) {
        String text = "";
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '²') text += '2';
            else if (str.charAt(i) == 'º') text += 'o';
            else if (str.charAt(i) == '—') text += '-';
            else text += str.charAt(i);
        }
        return text;
    }

    //Tokenize the text after OCR
    private String tokenize(String str) {
        StringTokenizer tokenizer = new StringTokenizer(str);
        String[] arr = new String[tokenizer.countTokens()];
        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            arr[i++] = tokenizer.nextToken();
        }

        List<String> arrayList = new ArrayList<String>(Arrays.asList(arr));
        arrayList = arrayList.stream()
                .filter(code -> !words.contains(code))
                .collect(Collectors.toList());

        String rs = "";
        for (String s : arrayList) {
            rs += " " + s;
        }
        StringBuilder sb = new StringBuilder(rs);
        if (sb.indexOf("?") != -1)
            sb.deleteCharAt(sb.indexOf("?"));

        rs = sb.toString().trim();
        System.out.println(rs);
        return rs;
    }

    //Execute OCR
    public String detectText(final String imageURL) {
        String result = ocr.getTextRecognized(imageURL);
        result = tokenize(formatText(result));
        return result;
    }

    //Crop 3 images of three answer
    public void cropImageFromScreen(Bounds bounds, String imageSave, ImageView img) {

        int width = (int) bounds.getWidth();
        int height = (int) bounds.getHeight();

        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        parameters.setViewport(new Rectangle2D(bounds.getMinX(), bounds.getMinY(), width, height));

        WritableImage wi = new WritableImage(width, height);


        File file = new File("F:\\BaitaplonOOP\\HackConfetti\\Screenshot.png");
        ImageView background = new ImageView(new Image(file.toURI().toString()));
        Image image = background.snapshot(parameters, wi);
        img.setImage(image);

        File outputFile = new File(imageSave);
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bImage, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Encoding question, three answers to set up URL for SEARCH
    public String encodeURL(String text) {
        String encodedURL = "";
        try {
            encodedURL = URLEncoder.encode(text, "utf-8");
        } catch (UnsupportedEncodingException e) {
            System.out.print(e.getMessage());
        }
        return encodedURL;
    }

    //ScreenShot to OCR image
    public void screenShot() {
        try {
            BufferedImage image = new Robot().createScreenCapture(new java.awt.Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

            // Store screenshot on Specific location
            ImageIO.write(image, "png", new File("Screenshot.png"));

            cropImageFromScreen(bound_ques, QUESTION_IMAGE, img_ques);

            cropImageFromScreen(bound_ans1, ANSWER_A_IMAGE, img_ansA);

            cropImageFromScreen(bound_ans2, ANSWER_B_IMAGE, img_ansB);

            cropImageFromScreen(bound_ans3, ANSWER_C_IMAGE, img_ansC);

        } catch (AWTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Count the number of occurrences of a word
    public static int countSubstring(String str, String child) {
        int count = 0, fromIndex = 0;

        str = str.toLowerCase();
        child = child.toLowerCase();
        while ((fromIndex = str.indexOf(child, fromIndex)) != -1) {
            count++;
            fromIndex++;
        }
        return count;
    }

    //Show result Google by BarChart
    public void showResultGoogle(String url, String ques, String ans1, String ans2, String ans3) {


        int A, B, C;
//        System.out.println(sendGet(url).length());

        logn(url + ques + ans1 + ans2 + ans3);
        logn(url + encodeURL(ques) + encodeURL(ans1) + encodeURL(ans2) + encodeURL(ans3));
        String response = sendGet(url + encodeURL(ques) + encodeURL(ans1) + encodeURL(ans2) + encodeURL(ans3));

//        logn(url + encodeURL(ques) + encodeURL(ans1));
//        String response1 = sendGet(url + encodeURL(ques) + encodeURL(ans1));
//
//        logn(url + encodeURL(ques) + encodeURL(ans2));
//        String response2 = sendGet(url + encodeURL(ques) + encodeURL(ans2));
//
//        logn(url + encodeURL(ques) + encodeURL(ans3));
//        String response3 = sendGet(url + encodeURL(ques) + encodeURL(ans3));

//        System.out.println(response);
        A = countSubstring(response, ans1);
//        A+= countSubstring(response1, ans1);

        B = countSubstring(response, ans2);
//        B += countSubstring(response2, ans2);

        C = countSubstring(response, ans3);
//        C += countSubstring(response3, ans3);
        System.out.println("Result: " + "A:" + A + " B:" + B + " C:" + C);

        barChart1.getData().clear();
        barChart1.setBarGap(10);

        XYChart.Series<String, Number> dataSeries1 = new XYChart.Series<String, Number>();
        dataSeries1.setName("A: " + A + " B: " + B + " C: " + C);

        dataSeries1.getData().add(new XYChart.Data<String, Number>("Đáp án A", A));
        dataSeries1.getData().add(new XYChart.Data<String, Number>("Đáp án B", B));
        dataSeries1.getData().add(new XYChart.Data<String, Number>("Đáp án C", C));

        barChart1.getData().add(dataSeries1);

    }

    //Replace chart ' ' to '_' for search Wikipedia
    public String covertAnswer(String answer) {
        answer = answer.replace(' ', '_');
        logn(answer);
        return answer;
    }

    //Show result Wiki by BarChart
    public void showResultWiki(String url, String ques, String ans1, String ans2, String ans3) {

        int A, B, C;
        ans1 = covertAnswer(ans1);
        ans2 = covertAnswer(ans2);
        ans3 = covertAnswer(ans3);

        System.out.println(url + encodeURL(ans1));
        System.out.println(url + encodeURL(ans2));
        System.out.println(url + encodeURL(ans3));

        String response1 = sendGet(url + encodeURL(ans1));
        String response2 = sendGet(url + encodeURL(ans2));
        String response3 = sendGet(url + encodeURL(ans3));

        A = countSubstring(response1, ans1);
        B = countSubstring(response2, ans2);
        C = countSubstring(response3, ans3);

        barChart2.getData().clear();
        barChart2.setBarGap(10);

        XYChart.Series<String, Number> dataSeries = new XYChart.Series<String, Number>();
        dataSeries.setName("A: " + A + " B: " + B + " C: " + C);

        dataSeries.getData().add(new XYChart.Data<String, Number>("Đáp án A", A));
        dataSeries.getData().add(new XYChart.Data<String, Number>("Đáp án B", B));
        dataSeries.getData().add(new XYChart.Data<String, Number>("Đáp án C", C));

        barChart2.getData().add(dataSeries);


    }

    // HTTP GET request
    private static String sendGet(String url) {

        StringBuffer response = null;
        try {
            URL obj = new URL(url);

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            int responseCode = con.getResponseCode();
            System.out.println("GET Response Code :: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                System.out.println("Length: " + response.length());
                in.close();

                // print result
//                System.out.println(response.toString());
            } else {
                System.out.println("GET request not worked");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.toString().trim();
    }


    //Run TOOLS
    private void run() {
        log("Starting programs: ........");
        logn("Done!!");

        log("Screenshot background: ......");
        screenShot();
        logn("Done!!");

        logn("Loading question ... Done!!");
        String QUESTION_TEXT = detectText(QUESTION_IMAGE);

        log("Loading answer A ...");
        String A_TEXT = detectText(ANSWER_A_IMAGE);

        log("Loading answer B ...");
        String B_TEXT = detectText(ANSWER_B_IMAGE);

        log("Loading answer C ...");
        String C_TEXT = detectText(ANSWER_C_IMAGE);


        logn("Get data HTML from URL ..... ");

        showResultGoogle(URL, QUESTION_TEXT, " " + A_TEXT, " " + B_TEXT, " " + C_TEXT);

        //test
//        showResultWiki(URL_WIKI, A_TEXT, B_TEXT, C_TEXT);
        //reset count results
    }

    //
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        words = Arrays.asList(arrWords);
        resultPane.getChildren().add(webView);

        btn_start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                double startTime = System.currentTimeMillis();
                run();
                double endTime = System.currentTimeMillis();
                System.out.println("Time excuted: " + (endTime - startTime) / 1000 + "s");
                lbTime.setText("Time excuted: " + (endTime - startTime) / 1000 + "s");
            }
        });
    }


//    @FXML
//    private void setupImageToDetect(ActionEvent event){
//        if (event.getTarget() == quesSetup){
//            getBoundsImage(QUESTION_IMAGE, img_ques, labelQues);
//
//        }
//        else if(event.getTarget() == ans1Setup){
//            getBoundsImage(ANSWER_A_IMAGE, img_ansA, labelAns1);
//
//        }
//        else if(event.getTarget() == ans2Setup){
//            getBoundsImage(ANSWER_B_IMAGE, img_ansB, labelAns2);
//
//        }
//        else if(event.getTarget() == ans3Setup){
//            getBoundsImage(ANSWER_C_IMAGE, img_ansC, labelAns3);
//
//        }
//
//    }
//
//    public void detectTextForLabel(final String imageSave, final Label label){
//        String result = ocr.getTextRecognized(imageSave);
//        result = tokenize(formatText(result));
//        label.setText(result);
//
//
//    }
//    public void getBoundsImage(final String imageSave, final ImageView img, final Label label){
//        rubberBandSelection = new RubberBandSelection(setupPane);
//        final ContextMenu contextMenu = new ContextMenu();
//        MenuItem cropMenuItem = new MenuItem("Crop");
//
//        cropMenuItem.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                Bounds selectionBounds = rubberBandSelection.getBounds();
//                System.out.println(selectionBounds);
//                cropImage(selectionBounds, imageSave, img);
//                detectTextForLabel(imageSave, label);
//            }
//        });
//
//        contextMenu.getItems().add(cropMenuItem);
//
//        setupPane.setOnMousePressed(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                if (event.isSecondaryButtonDown()) {
//                    contextMenu.show(setupPane, event.getScreenX(), event.getScreenY());
//                }
//            }
//        });
//
//    }
//    public void cropImage(Bounds bounds, String imageSave, ImageView img){
//
//        int width = (int) bounds.getWidth();
//        int height = (int) bounds.getHeight();
//
//        SnapshotParameters parameters = new SnapshotParameters();
//        parameters.setFill(Color.TRANSPARENT);
//        parameters.setViewport(new Rectangle2D( bounds.getMinX()+229, bounds.getMinY(), width, height));
//
//        WritableImage wi = new WritableImage(width, height);
//
//
//        Image image = setupPane.snapshot(parameters, wi);
//        img.setImage(image);
//
//        File outputFile = new File(imageSave);
//        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
//        try {
//            ImageIO.write(bImage, "png", outputFile);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    /**
     * Drag rectangle with mouse cursor in order to get selection bounds
     */
//    public static class RubberBandSelection {
//
//        final DragContext dragContext = new DragContext();
//        Rectangle rect = new Rectangle();
//
//        AnchorPane group;
//
//
//        public Bounds getBounds() {
//            return rect.getBoundsInParent();
//        }
//
//        public RubberBandSelection( AnchorPane group) {
//
//            this.group = group;
//
//            rect = new Rectangle(0,0,0,0);
//            rect.setStroke(Color.BLUE);
////            rect.setStrokeWidth(1);
//            rect.setStrokeLineCap(StrokeLineCap.ROUND);
//            rect.setFill(Color.LIGHTBLUE.deriveColor(0, 1.2, 1, 0));
//
//            group.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
//            group.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
//            group.addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedEventHandler);
//
//        }
//
//        EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {
//
//            @Override
//            public void handle(MouseEvent event) {
//
//                if( event.isSecondaryButtonDown())
//                    return;
//
//                // remove old rect
//                rect.setX(0);
//                rect.setY(0);
//                rect.setWidth(0);
//                rect.setHeight(0);
//
//                group.getChildren().remove( rect);
//
//
//                // prepare new drag operation
//                dragContext.mouseAnchorX = event.getX();
//                dragContext.mouseAnchorY = event.getY();
//
//                rect.setX(dragContext.mouseAnchorX);
//                rect.setY(dragContext.mouseAnchorY);
//                rect.setWidth(0);
//                rect.setHeight(0);
//
//                group.getChildren().add( rect);
//
//            }
//        };
//
//        EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
//
//            @Override
//            public void handle(MouseEvent event) {
//
//                if( event.isSecondaryButtonDown())
//                    return;
//
//                double offsetX = event.getX() - dragContext.mouseAnchorX;
//                double offsetY = event.getY() - dragContext.mouseAnchorY;
//
//                if( offsetX > 0)
//                    rect.setWidth( offsetX);
//                else {
//                    rect.setX(event.getX());
//                    rect.setWidth(dragContext.mouseAnchorX - rect.getX());
//                }
//
//                if( offsetY > 0) {
//                    rect.setHeight( offsetY);
//                } else {
//                    rect.setY(event.getY());
//                    rect.setHeight(dragContext.mouseAnchorY - rect.getY());
//                }
//            }
//        };
//
//
//        EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>() {
//
//            @Override
//            public void handle(MouseEvent event) {
//
//                if( event.isSecondaryButtonDown())
//                    return;
//
//                // remove rectangle
//                // note: we want to keep the ruuberband selection for the cropping => code is just commented out
//                /*
//                rect.setX(0);
//                rect.setY(0);
//                rect.setWidth(0);
//                rect.setHeight(0);
//
//                group.getChildren().remove( rect);
//                */
//
//            }
//        };
//        private static final class DragContext {
//
//            public double mouseAnchorX;
//            public double mouseAnchorY;
//
//        }
//    }
    private void log(String message) {
        System.out.print(message);
    }

    private void logn(String message) {
        log(message + "\n");
    }
}


