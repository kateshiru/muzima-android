package com.muzima.controller;

import com.muzima.api.model.Concept;
import com.muzima.api.model.Encounter;
import com.muzima.api.model.Observation;
import com.muzima.api.service.ConceptService;
import com.muzima.api.service.EncounterService;
import com.muzima.api.service.LastSyncTimeService;
import com.muzima.api.service.ObservationService;
import com.muzima.service.SntpService;
import org.junit.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ObservationControllerGetEncountersTest {
    @Test
    public void shouldInflateConcept() throws Exception, ObservationController.LoadObservationException {
        ObservationService observationService = mock(ObservationService.class);
        ConceptService conceptService = mock(ConceptService.class);
        EncounterService encounterService = mock(EncounterService.class);
        LastSyncTimeService lastSyncTimeService = mock(LastSyncTimeService.class);
        SntpService sntpService = mock(SntpService.class);
        final String patientUuid = "patient1";
        final String encounterUuid = "encounterUuid";
        final String conceptUuid = "c1";
        ArrayList<Observation> observations = new ArrayList<Observation>();
        Observation observation = new Observation(){{
            setConcept(new Concept(){{
                setUuid(conceptUuid);
            }});
            setEncounter(new Encounter(){{
                setUuid(encounterUuid);
            }});
        }};
        observations.add(observation);
        when(observationService.getObservationsByPatient(patientUuid)).thenReturn(observations);

        ObservationController observationController = new ObservationController(observationService, conceptService, encounterService, lastSyncTimeService, sntpService);
        observationController.getEncountersWithObservations(patientUuid);

        verify(conceptService).getConceptByUuid(conceptUuid);
        verify(encounterService).getEncounterByUuid(encounterUuid);
    }
}
