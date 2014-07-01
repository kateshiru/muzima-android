package com.muzima.view.forms;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import com.muzima.MuzimaApplication;
import com.muzima.api.model.Form;
import com.muzima.api.model.Patient;
import com.muzima.controller.FormController;
import com.muzima.model.BaseForm;
import com.muzima.model.FormWithData;
import com.muzima.utils.Constants;

public class FormViewIntent extends Intent {
    public FormViewIntent(Activity activity, FormWithData form) {
        this(activity, form, form.getPatient());
    }

    public FormViewIntent(Activity activity, BaseForm form, Patient patient) {
        this(activity,form,patient,getEncounterFormDiscriminatorBasedOnFormType(activity, form));
    }

    public FormViewIntent(Activity activity, BaseForm form, Patient patient, String discriminator) {
        super(activity, getClassBasedOnFormType(activity, form));
        putExtra(HTMLFormWebViewActivity.FORM, form);
        putExtra(HTMLFormWebViewActivity.PATIENT, patient);
        putExtra(HTMLFormWebViewActivity.DISCRIMINATOR, discriminator);
    }


    private static Class getClassBasedOnFormType(Activity activity, BaseForm form) {
        FormController formController = ((MuzimaApplication) activity.getApplication()).getFormController();
        try {
            if (formController.getFormTemplateByUuid(form.getFormUuid()).isHTMLForm()) {
                return HTMLFormWebViewActivity.class;
            }
        } catch (FormController.FormFetchException e) {
            Log.e("FormIntent", "Error while identifying form to load it in WebView");
        }
        return FormWebViewActivity.class;
    }

    private static String getEncounterFormDiscriminatorBasedOnFormType(Activity activity, BaseForm baseForm){
        FormController formController = ((MuzimaApplication) activity.getApplication()).getFormController();
        try {
            Form form = formController.getFormByUuid(baseForm.getFormUuid());
            return form.getDiscriminator();
        } catch (FormController.FormFetchException e) {
            Log.e("FormViewIntent", "Error while identifying form to load it in WebView", e);
        }
        return Constants.FORM_XML_DISCRIMINATOR_ENCOUNTER;
    }
}
