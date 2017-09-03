package com.myorganization.util;

import ca.uhn.fhir.rest.client.IGenericClient;
import org.hl7.fhir.instance.model.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;

/**
 * Helpful class that, given an {@link IGenericClient} and a {@link Bundle}, can iterate through all of the 'next' links
 * of the original Bundle and will return an aggregate Bundle containing all of the results that matched the search.
 */
public final class BundleFetcher {
    private static final Logger LOG = LoggerFactory.getLogger(BundleFetcher.class);

    private final IGenericClient theClient;
    private final Bundle originalBundle;

    private BundleFetcher (final IGenericClient theClient, final Bundle originalBundle) {
        this.theClient = theClient;
        this.originalBundle = originalBundle;
    }

    /**
     * Creates a new {@link BundleFetcher} instance from the given client and original bundle.
     * @param theClient the client (must not be null)
     * @param originalBundle the original bundle to start from (must not be null)
     * @return a new BundleFetcher instance
     */
    public static BundleFetcher startingWith (final IGenericClient theClient, final Bundle originalBundle) {
        Objects.requireNonNull(theClient, "theClient cannot be null");
        Objects.requireNonNull(originalBundle, "originalBundle cannot be null");
        return new BundleFetcher(theClient, originalBundle);
    }

    /**
     * Fetches all of the results by iterating through all of the {@value Bundle#LINK_NEXT} links.
     * @return a {@link Bundle} containing all of the resources that matched the search
     */
    public Bundle fetchAll () {
        Bundle aggregatedBundle = originalBundle.copy();
        Bundle partialBundle = originalBundle;
        LOG.debug(String.format("Original bundle search matched %d total resources(s) in the search and %d resource(s) are in this bundle.", originalBundle.getTotal(), originalBundle.getEntry().size()));

        while (partialBundle.getLink(Bundle.LINK_NEXT) != null) {
            partialBundle = theClient.loadPage().next(partialBundle).execute();
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
