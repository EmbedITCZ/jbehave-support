package org.jbehavesupport.core.test.app;

import static org.apache.commons.lang3.Validate.isTrue;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import org.jbehavesupport.core.test.app.oxm.Error;
import org.jbehavesupport.core.test.app.oxm.NameRequest;
import org.jbehavesupport.core.test.app.oxm.NameResponse;
import org.jbehavesupport.core.test.app.oxm.ObjectFactory;
import org.jbehavesupport.core.test.app.oxm.Relative;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@RequiredArgsConstructor
public class NameEndpoint {
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    final ResourceLoader resourceLoader;

    @PayloadRoot(namespace = "http://jbehavesupport.org/definitions", localPart = "NameRequest")
    @ResponsePayload
    public NameResponse nameRequest(@RequestPayload NameRequest nameRequest) {
        isTrue(nameRequest.getName() != null, "name must be filled for this request");
        NameResponse nameResponse = OBJECT_FACTORY.createNameResponse();
        switch (nameRequest.getName()) {
            case "test":
                return okResponse(nameResponse);
            case "error":
                return errorResponse(nameResponse);
            case "simpleJB":
                return processSimpleJaxb(nameRequest, nameResponse);
            case "inlayJB":
                return processNestedJaxb(nameRequest, nameResponse);
            case "complexJB":
                return processComplexJaxb(nameRequest, nameResponse);
            case "file":
                return processFile(nameRequest, nameResponse);
            default:
                throw new IllegalArgumentException("Unsupported behaviour for: " + nameRequest.getName());
        }
    }

    private NameResponse processFile(final NameRequest nameRequest, final NameResponse nameResponse) {
        isTrue(nameRequest.getPhotoInfo() != null, "photo info must be present");
        byte[] photo = nameRequest.getPhotoInfo().getPhoto();
        isTrue(photo != null, "photo must be present");

        String filePath = nameRequest.getPhotoInfo().getPath();
        if (filePath != null) {
            nameResponse.setTestResponse(validateFile(photo, filePath));
        } else {
            nameResponse.setTestResponse("Base 64 valid");
        }
        return nameResponse;
    }

    private String validateFile(final byte[] photo, final String filePath) {
        try {
            File targetFile = new File(filePath);
            if (targetFile.isFile()) {
                byte[] expected = FileUtils.readFileToByteArray(targetFile);
                if (Arrays.equals(photo, expected)) {
                    return "file validated";
                }
            }
            return "file not valid";
        } catch (IOException e) {
            throw new UncheckedIOException("validation failed", e);
        }
    }

    private NameResponse processComplexJaxb(final NameRequest nameRequest, final NameResponse nameResponse) {
        return nameResponse.withTestResponse(nameRequest.getPhoneContact().getValue().getNumber());
    }

    private NameResponse processNestedJaxb(final NameRequest nameRequest, final NameResponse nameResponse) {
        if (nameRequest.getAddressList().getAddressInfo().stream()
            .allMatch(i -> i.getLivingSince() != null)) {
            nameResponse.setError(getError("OK"));
        } else {
            nameResponse.setError(getError("ERR112"));
        }
        return nameResponse;
    }

    private NameResponse processSimpleJaxb(final NameRequest nameRequest, final NameResponse nameResponse) {
        if (nameRequest.getPassDate().isNil()) {
            nameResponse.setFirstName("resurrected");
        } else if (nameRequest.getPassDate().getValue() != null) {
            nameResponse.setFirstName("dead");
        }
        return nameResponse.withError(getError("OK"));
    }

    private NameResponse errorResponse(final NameResponse nameResponse) {
        return nameResponse.withError(getError("ERR111"));
    }

    private NameResponse okResponse(final NameResponse nameResponse) {
        return nameResponse
            .withFirstName("John")
            .withLastName("Doe")
            .withAge(33)
            .withMarried(true)
            .withParent(Boolean.TRUE)
            .withError(getError("OK"))
            .withTestResponse("<![CDATA[<us><me>robot</me><you></you></us>]]>")
            .withRelatives(new Relative()
                    .withName("Sarah")
                    .withRelation("Daughter"),
                new Relative()
                    .withName("Kamil")
                    .withRelation("Son"));
    }

    private Error getError(String errorCode) {
        Error error = new Error();
        error.setCode(errorCode);
        return error;
    }

}
