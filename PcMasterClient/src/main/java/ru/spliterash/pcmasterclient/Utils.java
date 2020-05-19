package ru.spliterash.pcmasterclient;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import ru.spliterash.pcmasterclient.api.RequestManager;
import ru.spliterash.pcmasterclient.api.models.ComponentType;
import ru.spliterash.pcmasterclient.api.models.Supplier;
import ru.spliterash.pcmasterclient.interfaces.ExceptionSupplier;
import ru.spliterash.pcmasterclient.interfaces.ServerData;
import ru.spliterash.pcmasterclient.view.admin.pages.reports.Report;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Utils {
    public static void showServerError(Throwable throwable) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(throwable.getMessage());
        alert.setTitle("Server error");
        StringWriter writerStr = new StringWriter();
        PrintWriter writer = new PrintWriter(writerStr);
        throwable.printStackTrace(writer);
        alert.setContentText(writerStr.toString());
        alert.show();
    }

    public static void showWaitAlert(Alert.AlertType type, String title, String head, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(head);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.initOwner(Main.getMain().getMainStage());
        alert.showAndWait();
    }

    public static IntegerBinding createDataBinding(SingleSelectionModel<? extends ServerData> selection) {
        return new IntegerBinding() {
            {
                super.bind(selection.selectedItemProperty());
            }

            @Override
            protected int computeValue() {
                ServerData selected = selection.getSelectedItem();
                if (selected != null)
                    return selection.getSelectedItem().getId();
                else
                    return -1;
            }
        };
    }

    /**
     * Создаёт BooleanBinding из множества BooleanProperty
     *
     * @return Биндинг который возращает true только если все Property true
     */
    public static BooleanBinding andProperty(Set<BooleanProperty> set) {
        return new BooleanBinding() {
            {
                set.forEach(this::bind);
            }

            @Override
            protected boolean computeValue() {
                return set.stream().allMatch(ObservableBooleanValue::get);
            }
        };
    }

    public static BooleanBinding orProperty(Set<BooleanProperty> set) {
        return new BooleanBinding() {
            {
                set.forEach(this::bind);
            }

            @Override
            protected boolean computeValue() {
                return set.stream().anyMatch(ObservableBooleanValue::get);
            }
        };
    }

    public static void runSyncAction(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }

    }

    public static <T extends Parent> void loadFXML(T component) {
        String fileName = component.getClass().getSimpleName() + ".fxml";
        loadFXML(component, fileName);
    }

    public static String getMonthName(int month) {
        Month m = Month.of(month + 1);
        return m.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());
    }

    public static <T extends Parent> void loadFXML(T component, URL resource) {
        FXMLLoader loader = new FXMLLoader(resource);
        loader.setRoot(component);
        loader.setControllerFactory(theClass -> component);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static <T extends Parent> void loadFXML(T component, String filename) {
        String fileName = component.getClass().getSimpleName() + ".fxml";
        loadFXML(component, component.getClass().getResource(fileName));
    }

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

    public static String dateToString(Date date) {
        return dateFormat.format(date);
    }

    public static <T> void runAsyncAction(ExceptionSupplier<T> asyncAction, BiConsumer<T, Throwable> syncAction) {
        RequestManager.getInstance().runTaskAsync(() -> {
            T result = null;
            Throwable throwable = null;
            try {
                result = asyncAction.get();
            } catch (Throwable ex) {
                throwable = ex;
            }
            T finalResult = result;
            Throwable finalThrowable = throwable;
            Platform.runLater(() -> syncAction.accept(finalResult, finalThrowable));
        });
    }

    public static BufferedImage getBuffered(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public static void enableCache(Node node) {
        node.setCache(true);
        node.setCacheHint(CacheHint.SPEED);
    }

    public static javafx.scene.image.Image getImage(InputStream image, int x, int y) throws IOException {
        return getImage(ImageIO.read(image), x, y);
    }

    public static javafx.scene.image.Image getImage(BufferedImage image, int x, int y) {
        image = Utils.getBuffered(image.getScaledInstance(x, y, Image.SCALE_SMOOTH));
        return SwingFXUtils.toFXImage(image, null);

    }

    public static javafx.scene.image.Image getFxImage(String link, int x, int y) throws IOException {
        URL url = new URL(link);
        URLConnection connection = url.openConnection();
        //Некоторые сайты шлют нафиг если нету useragent
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        InputStream stream = connection.getInputStream();
        return getImage(stream, x, y);
    }

    public static javafx.scene.image.Image getFxImageSave(String link, int x, int y) {
        try {
            return getFxImage(link, x, y);
        } catch (IOException e) {
            e.printStackTrace();
            return ImageManager.BROKEN.getResizedFx(x, y);
        }
    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static void setImage(String link, ImageView view) {
        setImage(link, view, null);
    }

    public static void setImage(String link, ImageView view, Consumer<javafx.scene.image.Image> afterSet) {
        runSyncAction(() -> {
            view.setImage(ImageManager.LOAD.getResizedFx((int) view.getFitWidth(), -1));
            runAsyncAction(() -> getFxImageSave(link, (int) view.getFitWidth(), -1),
                    (image, throwable) ->
                    {
                        if (image != null) {
                            if (afterSet != null)
                                afterSet.accept(image);
                            view.setImage(image);
                        }
                        if (throwable != null) {
                            Utils.showServerError(throwable);
                        }
                    });
        });

    }

    public static void loadImage(String link, int width, Consumer<javafx.scene.image.Image> afterSet) {
        runSyncAction(() -> {
                    afterSet.accept(ImageManager.LOAD.getResizedFx((int) width, -1));
                    runAsyncAction(() -> getFxImageSave(link, width, -1),
                            (image, throwable) ->
                            {
                                if (image != null) {
                                    afterSet.accept(image);
                                }
                               /* if (throwable != null) {
                                    Utils.showServerError(throwable);
                                }*/
                            });
                }
        );
    }

    public static void centerImage(ImageView imageView) {
        javafx.scene.image.Image img = imageView.getImage();
        if (img != null) {
            double w;
            double h;

            double ratioX = imageView.getFitWidth() / img.getWidth();
            double ratioY = imageView.getFitHeight() / img.getHeight();

            double reduceCoeff;
            reduceCoeff = Math.min(ratioX, ratioY);

            w = img.getWidth() * reduceCoeff;
            h = img.getHeight() * reduceCoeff;

            imageView.setX((imageView.getFitWidth() - w) / 2);
            imageView.setY((imageView.getFitHeight() - h) / 2);

        }
    }

    public static void setAnchorProperty(Node... nodes) {
        for (Node node : nodes) {
            AnchorPane.setRightAnchor(node, 0D);
            AnchorPane.setLeftAnchor(node, 0D);
            AnchorPane.setTopAnchor(node, 0D);
            AnchorPane.setBottomAnchor(node, 0D);
        }
    }

    public static ObservableValue<? extends Number> createIntBind(StringProperty property) {
        return new IntegerBinding() {
            {
                super.bind(property);
            }

            @Override
            protected int computeValue() {
                try {
                    return Integer.parseInt(property.get());
                } catch (Exception ex) {
                    return -1;
                }
            }
        };
    }
}
