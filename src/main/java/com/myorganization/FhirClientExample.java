package com.myorganization;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.client.ServerValidationModeEnum;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import org.hl7.fhir.instance.model.Bundle;
import org.hl7.fhir.instance.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.instance.model.OperationOutcome;
import org.hl7.fhir.instance.model.OperationOutcome.OperationOutcomeIssueComponent;
import org.hl7.fhir.instance.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Super basic FHIR client example using HAPI FHIR.
 */
public class FhirClientExample {
    private static final Logger LOG = LoggerFactory.getLogger(FhirClientExample.class);

    private static final FhirContext FHIR_CONTEXT = FhirContext.forDstu2Hl7Org();
    static {
        // Disable server validation (don't pull the server's metadata first) to allow for interaction with incomplete FHIR servers.
        FHIR_CONTEXT.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
        /*
          Using a lenient parser (which is the default).
          Could also be even more lenient and not care if there are errors on invalid values,
          but that could lead to a loss of data, so I don't recommend that:
          FHIR_CONTEXT.setParserErrorHandler(new LenientErrorHandler().setErrorOnInvalidValue(false));
         */
    }
    private static final String GENERIC_ERROR_MESSAGE = "An unexpected error occurred.";

    public static void main (String[] args) {
        // Getting the base URL from the command line argument.
        if (args.length == 0) {
            throw new IllegalStateException("The base URL for the FHIR server must be specified as an argument. " +
                    "For example: http://fhirtest.uhn.ca/baseDstu2");
        }
        String baseUrl = args[0];
        LOG.debug(String.format("Base URL is %s", baseUrl));
        IGenericClient client = FHIR_CONTEXT.newRestfulGenericClient(baseUrl);

        try {
            // Example searching for patients with a specific family name.
            Bundle results = client
                    .search()
                    .forResource(Patient.class)
                    .where(new StringClientParam(Patient.SP_FAMILY).matches().value("reynolds"))
                    .returnBundle(Bundle.class)
                    .execute();

            LOG.info(String.format("Found %d patients.", results.getEntry().size()));
            // Log the IDs of the patients that were returned.
            for (BundleEntryComponent bec : results.getEntry()) {
                Patient p = (Patient)bec.getResource();
                LOG.info(String.format("ID of found patient is %s", p.getIdElement().getIdPart()));
            }
        } catch (BaseServerResponseException bsr) {
            LOG.error(String.format("A FHIR error occurred!: %s", bsr.getMessage()));

            OperationOutcome oo = (OperationOutcome)bsr.getOperationOutcome();

            /*
             * For each of the operation outcome's issues, we'll log an error message.
             * We'll use the first of the following that is defined in each issue:
             *   OperationOutcome.issue.details.text
             *   OperationOutcome.issue.diagnostics
             *   A generic error message
             */
            List<String> messagesFromOperationOutcome = new ArrayList<>();
            for (OperationOutcomeIssueComponent ooic : oo.getIssue()) {
                String messageForIssue;
                if (ooic.getDetails().getText() != null) {
                    messageForIssue = ooic.getDetails().getText();
                } else if (ooic.getDiagnostics() != null) {
                    messageForIssue = ooic.getDiagnostics();
                } else {
                    messageForIssue = GENERIC_ERROR_MESSAGE;
                }
                messagesFromOperationOutcome.add(messageForIssue);
            }

            if (!messagesFromOperationOutcome.isEmpty()) {
                LOG.error("Here are the error messages from each of the operation outcome issues:");
                messagesFromOperationOutcome.forEach(LOG::error);
            }

        } catch (Exception e) {
            LOG.error("Something really bad happened!", e);
        }
    }
}
