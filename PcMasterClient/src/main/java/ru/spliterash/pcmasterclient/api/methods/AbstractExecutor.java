package ru.spliterash.pcmasterclient.api.methods;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.Nullable;
import ru.spliterash.pcmasterclient.Main;
import ru.spliterash.pcmasterclient.Utils;
import ru.spliterash.pcmasterclient.api.MethodExecuteException;
import ru.spliterash.pcmasterclient.api.RequestManager;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;

public abstract class AbstractExecutor<Request, Response> {
    private ReadOnlyObjectWrapper<Response> cachedResponse = new ReadOnlyObjectWrapper<>();
    @Getter
    private long lastUpdate = 0;
    private final static int index = "ru.spliterash.pcmasterclient.api.methods.".length();

    public String getMethodName() {
        String fullName = getClass().getPackage().getName().toLowerCase();

        return (fullName.substring(index));
    }

    public ReadOnlyObjectProperty<Response> responseProperty() {
        return cachedResponse.getReadOnlyProperty();
    }


    private void executeSyncNoCheck(Request request, @Nullable BiConsumer<Response, Throwable> consumer, Node... nodes) {
        Response result = null;
        Exception ex = null;
        try {
            result = execute(request);
        } catch (Exception e) {
            ex = e;
        }
        if (result != null || ex != null) {
            Response finalResult = result;
            Exception finalEx = ex;
            Platform.runLater(() -> {
                if (nodes.length > 0)
                    for (Node node : nodes) {
                        node.setDisable(false);
                    }
                if (consumer != null)
                    consumer.accept(finalResult, finalEx);
            });
        }
    }

    public void executeSync(Request request, BiConsumer<Response, Throwable> consumer, Node... nodes) {
        if (Platform.isFxApplicationThread()) {
            for (Node node : nodes) {
                node.setDisable(true);
            }
            RequestManager.getInstance().runTaskAsync(() -> executeSyncNoCheck(request, consumer, nodes));
        } else {
            if (nodes.length > 0)
                Platform.runLater(() -> {
                    for (Node node : nodes) {
                        node.setDisable(true);
                    }
                });
            executeSyncNoCheck(request, consumer, nodes);
        }
    }

    public Response execute(Request request) throws IOException, MethodExecuteException {
        System.out.println("Query run in " + Thread.currentThread().getName());
        Main main = Main.getMain();
        Gson gson = main.getGson();
        String token = main.getSettings().getToken();
        String postUrl = Main.getMain().getSettings().getServerUrl() + "/api/" + getMethodName();
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(postUrl);
        StringEntity postingString = new StringEntity(gson.toJson(request), StandardCharsets.UTF_8);
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        if (token != null)
            post.setHeader("Authorization", "Bearer " + token);
        HttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(post);
        } catch (HttpHostConnectException ex) {
            Platform.runLater(() -> {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("No internet connection");
                alert.setHeaderText("Похоже у вас отсутсвует интернет соединение");
                alert.setContentText("Программа не может работать без интернета, пожалуйста восстановите подключение и попробуйте снова");
                alert.setOnCloseRequest(e -> Platform.exit());
                alert.show();
            });

            return null;
        }
        try (StringWriter writer = new StringWriter()) {
            IOUtils.copy(httpResponse.getEntity().getContent(), writer, StandardCharsets.UTF_8);
            String rawJson = writer.toString();
            JsonObject element = gson.fromJson(rawJson, JsonObject.class);
            if (element == null) {
                throw new MethodExecuteException(
                        MethodExecuteException.ExceptionCode.SERVER_ERROR,
                        "Сервер возратил пустой результат",
                        "Сообщите об этом в нашу службу поддержки");
            }
            if (element.has("error")) {
                throw gson.fromJson(element, MethodExecuteException.class);
            } else {
                try {
                    Response response = getJson(element, gson);    //Устанавливаем синхронно, чтобы слушатели не выкинули эксепшн
                    Utils.runSyncAction(() -> cachedResponse.set(response));
                    lastUpdate = System.currentTimeMillis();
                    return response;

                } catch (Exception ex) {
                    ex.printStackTrace();
                    return null;
                }

            }
        }
    }

    @SuppressWarnings("unchecked")
    private Response getJson(JsonElement json, Gson gson) {
        Class<Response> clazz = (Class<Response>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[1];
        return gson.fromJson(json, clazz);
    }

    public void updateCacheNow(Request request, Node... nodes) {
        executeSync(request, null, nodes);
    }

    public void updateCache(Request request, Node... nodes) {
        long a = System.currentTimeMillis() - lastUpdate;
        if (cachedResponse.get() == null || a > 1000 * 60 * 10) {
            updateCacheNow(request, nodes);
        }
    }

    /**
     * Обнуляем результат в памяти
     */
    public void reset() {
        cachedResponse.set(null);
        //Возможно это костыль, но это единственный способ чтобы отвязать листенеры
        cachedResponse = new ReadOnlyObjectWrapper<>();
        lastUpdate = 0;
    }
}
