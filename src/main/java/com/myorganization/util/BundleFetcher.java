package com.myorganization.util;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.instance.model.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;

/**
 * Helpful class that, given an {@link IGenericClient} and a {@link Bundle}, can iterate through all of the 'next' links
 * of the original Bundle and will return an aggregate Bundle containing all of the results that matched the search.
 */
public class BundleFetcher {
    private static final Logger LOG = LoggerFactory.getLogger(BundleFetcher.class);

    private final IGenericClient client;
    private final Bundle startingBundle;

    /**
     * Constructor.
     * @param theClient the client
     * @param theStartingBundle the bundle to start with
     * @throws NullPointerException if any of the parameters are {@code null}
     */
    public BundleFetcher (final IGenericClient theClient, final Bundle theStartingBundle) {
        Objects.requireNonNull(theClient, "theClient cannot be null");
        Objects.requireNonNull(theStartingBundle, "theOriginalBundle cannot be null");
        client = theClient;
        startingBundle = theStartingBundle;
    }

    /**
     * Iterates through and calls all of the 'next' links and aggregates all of the resources into one bundle.
     * @return an aggregated bundle of all of the resources that matched the search
     */
    public Bundle fetchAll () {
        Bundle aggregatedBundle = startingBundle.copy();
        Bundle partialBundle = startingBundle;
        LOG.debug(String.format("Original bundle search matched %d total resources(s) in the search and %d resource(s) are in this bundle.", startingBundle.getTotal(), startingBundle.getEntry().size()));

        while (partialBundle.getLink(Bundle.LINK_NEXT) != null) {
            partialBundle = client.loadPage().next(partialBundle).execute();
            LOG.debug(String.format("Got the next bundle. This 'next' bundle had %d resources(s) in it.", partialBundle.getEntry().size()));
            aggregatedBundle.getEntry().addAll(partialBundle.getEntry());
        }

        if (aggregatedBundle.getTotal() != aggregatedBundle.getEntry().size()) {
            LOG.error(String.format("Counts didn't match! Expected %d resource(s) but the bundle only had %d resource(s)!", aggregatedBundle.getTotal(), aggregatedBundle.getEntry().size()));
        }

        // Clear links off of this bundle since they aren't really valid at this point
        aggregatedBundle.getLink().clear();

        return aggregatedBundle;
    }
}
