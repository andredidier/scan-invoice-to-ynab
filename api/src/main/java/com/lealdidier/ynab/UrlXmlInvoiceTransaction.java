package com.lealdidier.ynab;

import com.amazonaws.util.IOUtils;
import com.lealdidier.io.XmlUrlInputStreamSupplier;
import com.lealdidier.media.Media;
import com.lealdidier.ynab.nfce.NfceXmlToJson;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.function.Supplier;

public class UrlXmlInvoiceTransaction implements Transaction {
    private final URL url;
    private final Supplier<InputStream> xslStreamSupplier;

    public UrlXmlInvoiceTransaction(URL url, Supplier<InputStream> xslStreamSupplier) {
        this.url = url;
        this.xslStreamSupplier = xslStreamSupplier;
    }

    public UrlXmlInvoiceTransaction(URL url) {
        this(url, () -> UrlXmlInvoiceTransaction.class.getResourceAsStream("nfce/nfce-pe-to-json.xsl"));
    }

    private String xmlContents() {
        try {
            return IOUtils.toString(url.openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public JSONObject ynabJsonContents() {
        return new NfceXmlToJson(xslStreamSupplier).compose(new XmlUrlInputStreamSupplier()).apply(url);
    }

    @Override
    public <T> void saveTo(Media<T> media) throws IOException {
        media.writeFields(
                media.create("url", () -> url),
                media.create("xml", () -> xmlContents()),
                media.create("ynabJson", () -> ynabJsonContents()));
    }
}
