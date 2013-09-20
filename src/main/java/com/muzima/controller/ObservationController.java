package com.muzima.controller;

import com.muzima.api.model.Concept;
import com.muzima.api.model.Observation;
import com.muzima.api.service.ConceptService;
import com.muzima.api.service.ObservationService;
import com.muzima.model.observation.Concepts;
import com.muzima.utils.CustomColor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObservationController {

    private ObservationService observationService;
    private ConceptService conceptService;
    private Map<String, Integer> conceptColors;

    public ObservationController(ObservationService observationService, ConceptService conceptService) {
        this.observationService = observationService;
        this.conceptService = conceptService;
        conceptColors = new HashMap<String, Integer>();
    }

    public Concepts getConceptWithObservations(String patientUuid) throws LoadObservationException {
        try {
            return groupByConcepts(observationService.getObservationsByPatient(patientUuid));
        } catch (IOException e) {
            throw new LoadObservationException(e);
        }
    }

    private void inflateConcepts(List<Observation> observationsByPatient) throws IOException {
        Map<String, Concept> conceptCache = new HashMap<String, Concept>();

        for (Observation observation : observationsByPatient) {
            Concept concept = observation.getConcept();
            String conceptUuid = concept.getUuid();
            if (!conceptCache.containsKey(conceptUuid)) {
                Concept conceptByUuid = conceptService.getConceptByUuid(conceptUuid);
                conceptCache.put(conceptUuid, conceptByUuid);
            }
            observation.setConcept(conceptCache.get(conceptUuid));
        }
    }

    public int getConceptColor(String uuid) {
        if (!conceptColors.containsKey(uuid)) {
            conceptColors.put(uuid, CustomColor.getOrderedColor(conceptColors.size()));
        }
        return conceptColors.get(uuid);
    }

    public void replaceObservations(List<String> patientUuids, List<Observation> allObservations) throws ReplaceObservationException {
        try {
            for (String patientUuid : patientUuids) {
                List<Observation> observationsByPatient = observationService.getObservationsByPatient(patientUuid);
                observationService.deleteObservations(observationsByPatient);
            }
            observationService.saveObservations(allObservations);
        } catch (IOException e) {
            throw new ReplaceObservationException(e);
        }
    }

    public List<Observation> downloadObservations(String patientUuid, String conceptUuid) throws DownloadObservationException {
        try {
            return observationService.downloadObservationsByPatientAndConcept(patientUuid, conceptUuid);
        } catch (IOException e) {
            throw new DownloadObservationException(e);
        }
    }

    public Concepts searchObservations(String term, String patientUuid) throws LoadObservationException {
        try {
            return groupByConcepts(observationService.searchObservations(patientUuid, term));
        } catch (IOException e) {
            throw new LoadObservationException(e);
        }
    }

    private Concepts groupByConcepts(List<Observation> observations) throws IOException {
        inflateConcepts(observations);
        return new Concepts(observations);
    }


    public static class LoadObservationException extends Throwable {
        public LoadObservationException(Throwable e) {
            super(e);
        }
    }

    public static class ReplaceObservationException extends Throwable {
        public ReplaceObservationException(Throwable e) {
            super(e);
        }
    }

    public static class DownloadObservationException extends Throwable {
        public DownloadObservationException(Throwable e) {
            super(e);
        }
    }

}
