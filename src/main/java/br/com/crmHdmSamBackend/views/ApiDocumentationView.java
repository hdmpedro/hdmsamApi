package br.com.crmHdmSamBackend.views;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;


@Route(value = "api-docs")
@PageTitle("Documentação da API")
@AnonymousAllowed

public class ApiDocumentationView extends VerticalLayout {

    private final VerticalLayout documentationContainer;
    private final Gson gson;
    private JsonObject openApiSpec;

    public ApiDocumentationView() {
        this.gson = new Gson();
        this.documentationContainer = new VerticalLayout();

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(createUploadSection());
        add(documentationContainer);

        documentationContainer.setSizeFull();
        documentationContainer.setPadding(true);
        documentationContainer.getStyle()
                .set("background-color", "#fafafa")
                .set("overflow-y", "auto");
    }

    private Component createUploadSection() {
        VerticalLayout uploadSection = new VerticalLayout();
        uploadSection.setPadding(true);
        uploadSection.getStyle()
                .set("background-color", "#ffffff")
                .set("border-bottom", "1px solid #e0e0e0");

        H2 title = new H2("Documentação de API");
        title.getStyle().set("margin", "0");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("application/json", ".json");
        upload.setMaxFiles(1);
        upload.setDropLabel(new Span("Arraste o arquivo OpenAPI JSON aqui"));
        upload.setUploadButton(new Button("Selecionar arquivo"));

        upload.addSucceededListener(event -> {
            try {
                String content = new BufferedReader(
                        new InputStreamReader(buffer.getInputStream(), StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));

                openApiSpec = gson.fromJson(content, JsonObject.class);
                renderDocumentation();
            } catch (Exception e) {
                documentationContainer.removeAll();
                documentationContainer.add(new Span("Erro ao processar arquivo: " + e.getMessage()));
            }
        });

        uploadSection.add(title, upload);
        return uploadSection;
    }

    private void renderDocumentation() {
        documentationContainer.removeAll();

        if (openApiSpec == null) {
            return;
        }

        documentationContainer.add(createInfoSection());
        documentationContainer.add(createServersSection());
        documentationContainer.add(createPathsSection());
        documentationContainer.add(createSchemasSection());
    }

    private Component createInfoSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "8px")
                .set("margin-bottom", "20px")
                .set("box-shadow", "0 1px 3px rgba(0,0,0,0.1)");

        JsonObject info = openApiSpec.getAsJsonObject("info");
        if (info == null) {
            return section;
        }

        H2 title = new H2(getStringValue(info, "title", "API Documentation"));
        title.getStyle().set("margin-top", "0");

        Span version = new Span("Versão: " + getStringValue(info, "version", "1.0.0"));
        version.getStyle()
                .set("background-color", "#e3f2fd")
                .set("padding", "4px 12px")
                .set("border-radius", "12px")
                .set("font-size", "0.875rem")
                .set("color", "#1976d2");

        Paragraph description = new Paragraph(getStringValue(info, "description", ""));
        description.getStyle().set("color", "#666");

        section.add(title, version, description);
        return section;
    }

    private Component createServersSection() {
        if (!openApiSpec.has("servers")) {
            return new Span();
        }

        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "8px")
                .set("margin-bottom", "20px")
                .set("box-shadow", "0 1px 3px rgba(0,0,0,0.1)");

        H3 title = new H3("Servidores");
        section.add(title);

        JsonArray servers = openApiSpec.getAsJsonArray("servers");
        for (JsonElement serverElement : servers) {
            JsonObject server = serverElement.getAsJsonObject();
            String url = getStringValue(server, "url", "");
            String desc = getStringValue(server, "description", "");

            VerticalLayout serverLayout = new VerticalLayout();
            serverLayout.setPadding(false);
            serverLayout.setSpacing(false);

            Span urlSpan = new Span(url);
            urlSpan.getStyle()
                    .set("font-family", "monospace")
                    .set("font-weight", "bold")
                    .set("color", "#2e7d32");

            Span descSpan = new Span(desc);
            descSpan.getStyle().set("color", "#666").set("font-size", "0.875rem");

            serverLayout.add(urlSpan, descSpan);
            section.add(serverLayout);
        }

        return section;
    }

    private Component createPathsSection() {
        if (!openApiSpec.has("paths")) {
            return new Span();
        }

        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(true);

        H3 title = new H3("Endpoints");
        title.getStyle()
                .set("padding", "0 16px")
                .set("margin-bottom", "16px");
        section.add(title);

        JsonObject paths = openApiSpec.getAsJsonObject("paths");
        for (Map.Entry<String, JsonElement> pathEntry : paths.entrySet()) {
            String path = pathEntry.getKey();
            JsonObject pathItem = pathEntry.getValue().getAsJsonObject();

            section.add(createPathItem(path, pathItem));
        }

        return section;
    }

    private Component createPathItem(String path, JsonObject pathItem) {
        VerticalLayout pathLayout = new VerticalLayout();
        pathLayout.setPadding(false);
        pathLayout.setSpacing(false);

        for (Map.Entry<String, JsonElement> methodEntry : pathItem.entrySet()) {
            String method = methodEntry.getKey().toUpperCase();

            if (method.equals("PARAMETERS") || method.equals("SERVERS") ||
                    method.equals("SUMMARY") || method.equals("DESCRIPTION")) {
                continue;
            }

            JsonObject operation = methodEntry.getValue().getAsJsonObject();
            pathLayout.add(createOperation(method, path, operation));
        }

        return pathLayout;
    }

    private Component createOperation(String method, String path, JsonObject operation) {
        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);

        String summary = getStringValue(operation, "summary", "");
        String description = getStringValue(operation, "description", "");

        if (!summary.isEmpty()) {
            Paragraph summaryPara = new Paragraph(summary);
            summaryPara.getStyle().set("margin", "0 0 8px 0").set("font-weight", "500");
            content.add(summaryPara);
        }

        if (!description.isEmpty()) {
            Paragraph descPara = new Paragraph(description);
            descPara.getStyle().set("margin", "0 0 12px 0").set("color", "#666");
            content.add(descPara);
        }

        if (operation.has("parameters")) {
            content.add(createParametersSection(operation.getAsJsonArray("parameters")));
        }

        if (operation.has("requestBody")) {
            content.add(createRequestBodySection(operation.getAsJsonObject("requestBody")));
        }

        if (operation.has("responses")) {
            content.add(createResponsesSection(operation.getAsJsonObject("responses")));
        }

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setPadding(true);
        header.getStyle()
                .set("cursor", "pointer")
                .set("border-radius", "8px 8px 0 0");

        Span methodBadge = new Span(method);
        methodBadge.getStyle()
                .set("padding", "4px 12px")
                .set("border-radius", "4px")
                .set("font-weight", "bold")
                .set("font-size", "0.75rem")
                .set("min-width", "70px")
                .set("text-align", "center");

        switch (method) {
            case "GET":
                methodBadge.getStyle()
                        .set("background-color", "#e3f2fd")
                        .set("color", "#1976d2");
                header.getStyle().set("background-color", "#f5f5f5");
                break;
            case "POST":
                methodBadge.getStyle()
                        .set("background-color", "#e8f5e9")
                        .set("color", "#388e3c");
                header.getStyle().set("background-color", "#f5f5f5");
                break;
            case "PUT":
                methodBadge.getStyle()
                        .set("background-color", "#fff3e0")
                        .set("color", "#f57c00");
                header.getStyle().set("background-color", "#f5f5f5");
                break;
            case "DELETE":
                methodBadge.getStyle()
                        .set("background-color", "#ffebee")
                        .set("color", "#d32f2f");
                header.getStyle().set("background-color", "#f5f5f5");
                break;
            case "PATCH":
                methodBadge.getStyle()
                        .set("background-color", "#f3e5f5")
                        .set("color", "#7b1fa2");
                header.getStyle().set("background-color", "#f5f5f5");
                break;
            default:
                methodBadge.getStyle()
                        .set("background-color", "#e0e0e0")
                        .set("color", "#424242");
                header.getStyle().set("background-color", "#f5f5f5");
        }

        Span pathSpan = new Span(path);
        pathSpan.getStyle()
                .set("font-family", "monospace")
                .set("font-size", "0.95rem")
                .set("margin-left", "12px");

        header.add(methodBadge, pathSpan);

        Details details = new Details(header, content);
        details.setWidthFull();
        details.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "8px")
                .set("margin-bottom", "12px")
                .set("box-shadow", "0 1px 3px rgba(0,0,0,0.1)");

        return details;
    }

    private Component createParametersSection(JsonArray parameters) {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(true);

        H4 title = new H4("Parâmetros");
        title.getStyle().set("margin", "12px 0 8px 0");
        section.add(title);

        for (JsonElement paramElement : parameters) {
            JsonObject param = paramElement.getAsJsonObject();

            HorizontalLayout paramLayout = new HorizontalLayout();
            paramLayout.setAlignItems(Alignment.CENTER);
            paramLayout.getStyle()
                    .set("padding", "8px 12px")
                    .set("background-color", "#fafafa")
                    .set("border-radius", "4px")
                    .set("margin-bottom", "4px");

            String name = getStringValue(param, "name", "");
            String in = getStringValue(param, "in", "");
            boolean required = param.has("required") && param.get("required").getAsBoolean();
            String type = getParameterType(param);
            String desc = getStringValue(param, "description", "");

            Span nameSpan = new Span(name);
            nameSpan.getStyle()
                    .set("font-family", "monospace")
                    .set("font-weight", "bold")
                    .set("color", "#d32f2f");

            Span inSpan = new Span(in);
            inSpan.getStyle()
                    .set("font-size", "0.75rem")
                    .set("color", "#666")
                    .set("background-color", "#e0e0e0")
                    .set("padding", "2px 6px")
                    .set("border-radius", "3px")
                    .set("margin-left", "8px");

            Span typeSpan = new Span(type);
            typeSpan.getStyle()
                    .set("font-size", "0.75rem")
                    .set("color", "#1976d2")
                    .set("margin-left", "8px");

            VerticalLayout detailsLayout = new VerticalLayout();
            detailsLayout.setPadding(false);
            detailsLayout.setSpacing(false);

            HorizontalLayout mainInfo = new HorizontalLayout(nameSpan, inSpan, typeSpan);
            mainInfo.setAlignItems(Alignment.CENTER);
            mainInfo.setSpacing(false);

            if (required) {
                Span requiredSpan = new Span("obrigatório");
                requiredSpan.getStyle()
                        .set("font-size", "0.75rem")
                        .set("color", "#d32f2f")
                        .set("margin-left", "8px")
                        .set("font-weight", "500");
                mainInfo.add(requiredSpan);
            }

            detailsLayout.add(mainInfo);

            if (!desc.isEmpty()) {
                Span descSpan = new Span(desc);
                descSpan.getStyle()
                        .set("font-size", "0.875rem")
                        .set("color", "#666")
                        .set("margin-top", "4px");
                detailsLayout.add(descSpan);
            }

            paramLayout.add(detailsLayout);
            section.add(paramLayout);
        }

        return section;
    }

    private Component createRequestBodySection(JsonObject requestBody) {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(true);

        H4 title = new H4("Request Body");
        title.getStyle().set("margin", "12px 0 8px 0");
        section.add(title);

        String desc = getStringValue(requestBody, "description", "");
        if (!desc.isEmpty()) {
            Paragraph descPara = new Paragraph(desc);
            descPara.getStyle().set("color", "#666").set("margin", "0 0 8px 0");
            section.add(descPara);
        }

        if (requestBody.has("content")) {
            JsonObject content = requestBody.getAsJsonObject("content");
            for (Map.Entry<String, JsonElement> entry : content.entrySet()) {
                String mediaType = entry.getKey();
                JsonObject mediaTypeObj = entry.getValue().getAsJsonObject();

                Span mediaTypeSpan = new Span(mediaType);
                mediaTypeSpan.getStyle()
                        .set("font-family", "monospace")
                        .set("font-size", "0.875rem")
                        .set("color", "#1976d2")
                        .set("background-color", "#e3f2fd")
                        .set("padding", "4px 8px")
                        .set("border-radius", "4px");
                section.add(mediaTypeSpan);

                if (mediaTypeObj.has("schema")) {
                    section.add(createSchemaDisplay(mediaTypeObj.getAsJsonObject("schema")));
                }
            }
        }

        return section;
    }

    private Component createResponsesSection(JsonObject responses) {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(true);

        H4 title = new H4("Respostas");
        title.getStyle().set("margin", "12px 0 8px 0");
        section.add(title);

        for (Map.Entry<String, JsonElement> entry : responses.entrySet()) {
            String statusCode = entry.getKey();
            JsonObject response = entry.getValue().getAsJsonObject();

            VerticalLayout responseLayout = new VerticalLayout();
            responseLayout.setPadding(true);
            responseLayout.getStyle()
                    .set("background-color", "#fafafa")
                    .set("border-radius", "4px")
                    .set("border-left", "4px solid " + getStatusColor(statusCode))
                    .set("margin-bottom", "8px");

            HorizontalLayout statusLayout = new HorizontalLayout();
            statusLayout.setAlignItems(Alignment.CENTER);

            Span statusSpan = new Span(statusCode);
            statusSpan.getStyle()
                    .set("font-weight", "bold")
                    .set("font-family", "monospace")
                    .set("color", getStatusColor(statusCode));

            String desc = getStringValue(response, "description", "");
            Span descSpan = new Span(desc);
            descSpan.getStyle()
                    .set("margin-left", "12px")
                    .set("color", "#666");

            statusLayout.add(statusSpan, descSpan);
            responseLayout.add(statusLayout);

            if (response.has("content")) {
                JsonObject content = response.getAsJsonObject("content");
                for (Map.Entry<String, JsonElement> contentEntry : content.entrySet()) {
                    String mediaType = contentEntry.getKey();
                    JsonObject mediaTypeObj = contentEntry.getValue().getAsJsonObject();

                    Span mediaTypeSpan = new Span(mediaType);
                    mediaTypeSpan.getStyle()
                            .set("font-family", "monospace")
                            .set("font-size", "0.875rem")
                            .set("color", "#666")
                            .set("margin-top", "8px");
                    responseLayout.add(mediaTypeSpan);

                    if (mediaTypeObj.has("schema")) {
                        responseLayout.add(createSchemaDisplay(mediaTypeObj.getAsJsonObject("schema")));
                    }
                }
            }

            section.add(responseLayout);
        }

        return section;
    }

    private Component createSchemaDisplay(JsonObject schema) {
        TextArea schemaArea = new TextArea();
        schemaArea.setWidthFull();
        schemaArea.setReadOnly(true);
        schemaArea.setValue(gson.toJson(schema));
        schemaArea.getStyle()
                .set("font-family", "monospace")
                .set("font-size", "0.875rem")
                .set("margin-top", "8px");
        schemaArea.setHeight("150px");

        return schemaArea;
    }

    private Component createSchemasSection() {
        if (!openApiSpec.has("components")) {
            return new Span();
        }

        JsonObject components = openApiSpec.getAsJsonObject("components");
        if (!components.has("schemas")) {
            return new Span();
        }

        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "8px")
                .set("margin-top", "20px")
                .set("box-shadow", "0 1px 3px rgba(0,0,0,0.1)");

        H3 title = new H3("Schemas");
        section.add(title);

        JsonObject schemas = components.getAsJsonObject("schemas");
        for (Map.Entry<String, JsonElement> entry : schemas.entrySet()) {
            String schemaName = entry.getKey();
            JsonObject schema = entry.getValue().getAsJsonObject();

            Details schemaDetails = new Details();
            schemaDetails.setSummaryText(schemaName);
            schemaDetails.setWidthFull();
            schemaDetails.getStyle()
                    .set("margin-bottom", "8px")
                    .set("border", "1px solid #e0e0e0")
                    .set("border-radius", "4px")
                    .set("padding", "8px");

            schemaDetails.setContent(createSchemaDisplay(schema));
            section.add(schemaDetails);
        }

        return section;
    }

    private String getStringValue(JsonObject obj, String key, String defaultValue) {
        if (obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsString();
        }
        return defaultValue;
    }

    private String getParameterType(JsonObject param) {
        if (param.has("schema")) {
            JsonObject schema = param.getAsJsonObject("schema");
            if (schema.has("type")) {
                return schema.get("type").getAsString();
            }
        }
        return "string";
    }

    private String getStatusColor(String statusCode) {
        if (statusCode.startsWith("2")) {
            return "#388e3c";
        } else if (statusCode.startsWith("3")) {
            return "#1976d2";
        } else if (statusCode.startsWith("4")) {
            return "#f57c00";
        } else if (statusCode.startsWith("5")) {
            return "#d32f2f";
        }
        return "#666";
    }
}