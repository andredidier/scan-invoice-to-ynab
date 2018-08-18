package com.lealdidier.ynab;

import com.lealdidier.media.MapMedia;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("XML Transaction")
public class XmlTransactionTest {


    @DisplayName("Parsing and output to Map Media")
    @Test
    public void verifyJSONMedia() throws IOException {
        Function<String, URL> resource = getClass()::getResource;
        Function<URL, String> getContents = url -> {
            try {
                return IOUtils.toString(url);
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        };
        Function<String, JSONObject> contentsToJson = JSONObject::new;
        Function<URL, JSONObject> jsonUrlToJson = contentsToJson.compose(getContents);

        Transaction t = new UrlXmlInvoiceTransaction(resource.apply("/invoice1.xml"));
        Map<String, String> m = new HashMap<>();
        m.put("ynabJson", "json");
        MapMedia mapMedia = new MapMedia(m);
        t.saveTo(mapMedia);

        assertEquals(jsonUrlToJson.compose(resource).apply("/json1.js").toString(),
                mapMedia.map().get("json").toString());
    }
}
