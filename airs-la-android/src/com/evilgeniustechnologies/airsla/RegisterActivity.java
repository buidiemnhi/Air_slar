package com.evilgeniustechnologies.airsla;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.evilgeniustechnologies.airsla.base.ServiceActivity;
import com.evilgeniustechnologies.airsla.services.ServiceReceiver;
import com.evilgeniustechnologies.airsla.services.SubmitService;
import com.evilgeniustechnologies.airsla.utilities.DataValidator;
import com.evilgeniustechnologies.airsla.utilities.DialogManager;

import java.util.ArrayList;

/**
 * Created by benjamin on 2/17/14.
 */
public class RegisterActivity extends ServiceActivity
implements AdapterView.OnItemSelectedListener {
    private final String FIRST = "first";
    private final String LAST = "last";
    private final String EMAIL = "email";
    private final String VERIFY = "verify";
    private final String GENDER = "gender";
    private final String BIRTH = "birth";
    private final String CAREER = "career";
    private final String ADDRESS = "address";
    private final String VISION = "vision";
    private final String AFFECTED = "affected";
    private final String INTEREST = "interest";
    private final String CERTIFY = "certify";

    private String firstName = null;
    private String lastName = null;
    private String email = null;
    private String verifyEmail = null;
    private String gender = null;
    private String birth = null;
    private String career = null;
    private String address = null;
    private String visionImpairment = null;
    private String whoAffected = null;
    private ArrayList<String> interests = new ArrayList<String>();
    private boolean certify = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        // Populates 'Where you live' field
        Spinner addressSelector = (Spinner) findViewById(R.id.address);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.american_states, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addressSelector.setAdapter(adapter);

        // Listen to spinner's movement
        addressSelector.setOnItemSelectedListener(this);

        address = getResources().getStringArray(R.array.american_states)[0];

        if (savedInstanceState != null) {
            firstName = savedInstanceState.getString(FIRST);
            lastName = savedInstanceState.getString(LAST);
            email = savedInstanceState.getString(EMAIL);
            verifyEmail = savedInstanceState.getString(VERIFY);
            gender = savedInstanceState.getString(GENDER);
            birth = savedInstanceState.getString(BIRTH);
            career = savedInstanceState.getString(CAREER);
            address = savedInstanceState.getString(ADDRESS);
            visionImpairment = savedInstanceState.getString(VISION);
            whoAffected = savedInstanceState.getString(AFFECTED);
            interests = savedInstanceState.getStringArrayList(INTEREST);
            certify = savedInstanceState.getBoolean(CERTIFY);
        }

        // Display dialog if any
        DialogManager.showDialog(this);

        final Spinner spinner = (Spinner) findViewById(R.id.address);
        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                spinner.requestFocus();
                spinner.requestFocusFromTouch();
                return false;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(FIRST, firstName);
        outState.putString(LAST, lastName);
        outState.putString(EMAIL, email);
        outState.putString(VERIFY, verifyEmail);
        outState.putString(GENDER, gender);
        outState.putString(BIRTH, birth);
        outState.putString(CAREER, career);
        outState.putString(ADDRESS, address);
        outState.putString(VISION, visionImpairment);
        outState.putString(AFFECTED, whoAffected);
        outState.putStringArrayList(INTEREST, interests);
        outState.putBoolean(CERTIFY, certify);
        super.onSaveInstanceState(outState);
    }

    public void onGenderChecked(View v) {
        boolean checked = ((RadioButton) v).isChecked();

        switch (v.getId()) {
            case R.id.male:
                if (checked) {
                    gender = getResources().getString(R.string.male);
                }
                break;
            case R.id.female:
                if (checked) {
                    gender = getResources().getString(R.string.female);
                }
                break;
            case R.id.transgender:
                if (checked) {
                    gender = getResources().getString(R.string.transgender);
                }
                break;
        }
    }

    public void onCareerChecked(View v) {
        boolean checked = ((RadioButton) v).isChecked();

        switch (v.getId()) {
            case R.id.employed:
                if (checked) {
                    career = getResources().getString(R.string.employed);
                }
                break;
            case R.id.student:
                if (checked) {
                    career = getResources().getString(R.string.student);
                }
                break;
            case R.id.retired:
                if (checked) {
                    career = getResources().getString(R.string.retired);
                }
                break;
            case R.id.unemployed:
                if (checked) {
                    career = getResources().getString(R.string.unemployed);
                }
                break;
            case R.id.other:
                if (checked) {
                    career = getResources().getString(R.string.other);
                }
                break;
        }
    }

    public void onVisionImpairmentChecked(View v) {
        boolean checked = ((RadioButton) v).isChecked();

        switch (v.getId()) {
            case R.id.albinism:
                if (checked) {
                    visionImpairment = getResources().getString(R.string.albinism);
                }
                break;
            case R.id.cataracts:
                if (checked) {
                    visionImpairment = getResources().getString(R.string.cataracts);
                }
                break;
            case R.id.childhood_blindness:
                if (checked) {
                    visionImpairment = getResources().getString(R.string.childhood_blindness);
                }
                break;
            case R.id.corneal_opacity:
                if (checked) {
                    visionImpairment = getResources().getString(R.string.corneal_opacity);
                }
                break;
            case R.id.diabetic_retinopathy:
                if (checked) {
                    visionImpairment = getResources().getString(R.string.diabetic_retinopathy);
                }
                break;
            case R.id.dyslexia:
                if (checked) {
                    visionImpairment = getResources().getString(R.string.dyslexia);
                }
                break;
            case R.id.glaucoma:
                if (checked) {
                    visionImpairment = getResources().getString(R.string.glaucoma);
                }
                break;
            case R.id.lebers_amaurosis:
                if (checked) {
                    visionImpairment = getResources().getString(R.string.lebers_amaurosis);
                }
                break;
            case R.id.macular_degeneration:
                if (checked) {
                    visionImpairment = getResources().getString(R.string.macular_degeneration);
                }
                break;
            case R.id.neurological_vision_impairment:
                if (checked) {
                    visionImpairment = getResources().getString(R.string.neurological_vision_impairment);
                }
                break;
            case R.id.onchocerciasis:
                if (checked) {
                    visionImpairment = getResources().getString(R.string.onchocerciasis);
                }
                break;
            case R.id.optic_atrophy:
                if (checked) {
                    visionImpairment = getResources().getString(R.string.optic_atrophy);
                }
                break;
            case R.id.retinal_detachment:
                if (checked) {
                    visionImpairment = getResources().getString(R.string.retinal_detachment);
                }
                break;
            case R.id.retinitis_pigmentosa:
                if (checked) {
                    visionImpairment = getResources().getString(R.string.retinitis_pigmentosa);
                }
                break;
            case R.id.stargardts_disease:
                if (checked) {
                    visionImpairment = getResources().getString(R.string.stargardts_disease);
                }
                break;
            case R.id.trachoma:
                if (checked) {
                    visionImpairment = getResources().getString(R.string.trachoma);
                }
                break;
            case R.id.traumatic_head_injury:
                if (checked) {
                    visionImpairment = getResources().getString(R.string.traumatic_head_injury);
                }
                break;
            case R.id.vision_care_worker:
                if (checked) {
                    visionImpairment = getResources().getString(R.string.vision_care_worker);
                }
                break;
            case R.id.other_vision_impairment:
                if (checked) {
                    visionImpairment = getResources().getString(R.string.other_vision_impairment);
                }
                break;
        }
    }

    public void onWhoAffectedChecked(View v) {
        boolean checked = ((RadioButton) v).isChecked();

        switch (v.getId()) {
            case R.id.client:
                if (checked) {
                    whoAffected = getResources().getString(R.string.client);
                }
                break;
            case R.id.family_member:
                if (checked) {
                    whoAffected = getResources().getString(R.string.family_member);
                }
                break;
            case R.id.friend:
                if (checked) {
                    whoAffected = getResources().getString(R.string.friend);
                }
                break;
            case R.id.student:
                if (checked) {
                    whoAffected = getResources().getString(R.string.student);
                }
                break;
            case R.id.yourself:
                if (checked) {
                    whoAffected = getResources().getString(R.string.yourself);
                }
                break;
        }
    }

    public void onInterestedChecked(View v) {
        boolean checked = ((CheckBox) v).isChecked();

        switch (v.getId()) {
            case R.id.assistive_technology:
                if (checked) {
                    interests.add(getResources().getString(R.string.assistive_technology));
                } else {
                    interests.remove(getResources().getString(R.string.assistive_technology));
                }
                break;
            case R.id.cooking_food:
                if (checked) {
                    interests.add(getResources().getString(R.string.cooking_food));
                } else {
                    interests.remove(getResources().getString(R.string.cooking_food));
                }
                break;
            case R.id.diabetes:
                if (checked) {
                    interests.add(getResources().getString(R.string.diabetes));
                } else {
                    interests.remove(getResources().getString(R.string.diabetes));
                }
                break;
            case R.id.entertainment:
                if (checked) {
                    interests.add(getResources().getString(R.string.entertainment));
                } else {
                    interests.remove(getResources().getString(R.string.entertainment));
                }
                break;
            case R.id.health:
                if (checked) {
                    interests.add(getResources().getString(R.string.health));
                } else {
                    interests.remove(getResources().getString(R.string.health));
                }
                break;
            case R.id.interviews:
                if (checked) {
                    interests.add(getResources().getString(R.string.interviews));
                } else {
                    interests.remove(getResources().getString(R.string.interviews));
                }
                break;
            case R.id.kids_programs:
                if (checked) {
                    interests.add(getResources().getString(R.string.kids_programs));
                } else {
                    interests.remove(getResources().getString(R.string.kids_programs));
                }
                break;
            case R.id.science:
                if (checked) {
                    interests.add(getResources().getString(R.string.science));
                } else {
                    interests.remove(getResources().getString(R.string.science));
                }
                break;
            case R.id.seminars_on_vision:
                if (checked) {
                    interests.add(getResources().getString(R.string.seminars_on_vision));
                } else {
                    interests.remove(getResources().getString(R.string.seminars_on_vision));
                }
                break;
            case R.id.sports:
                if (checked) {
                    interests.add(getResources().getString(R.string.sports));
                } else {
                    interests.remove(getResources().getString(R.string.sports));
                }
                break;
            case R.id.travel:
                if (checked) {
                    interests.add(getResources().getString(R.string.travel));
                } else {
                    interests.remove(getResources().getString(R.string.travel));
                }
                break;
            case R.id.vision_rehabilitation:
                if (checked) {
                    interests.add(getResources().getString(R.string.vision_rehabilitation));
                } else {
                    interests.remove(getResources().getString(R.string.vision_rehabilitation));
                }
                break;
            case R.id.vision_development:
                if (checked) {
                    interests.add(getResources().getString(R.string.vision_development));
                } else {
                    interests.remove(getResources().getString(R.string.vision_development));
                }
                break;
            case R.id.other_interested:
                if (checked) {
                    interests.add(getResources().getString(R.string.other_interested));
                } else {
                    interests.remove(getResources().getString(R.string.other_interested));
                }
                break;
        }
    }

    public void onCertifyChecked(View v) {
        boolean checked = ((CheckBox) v).isChecked();

        switch (v.getId()) {
            case R.id.certify:
                certify = checked;
                break;
        }
    }

    public void onSubmit(View v) {
        EditText firstNameField = (EditText) findViewById(R.id.first_name);
        EditText lastNameField = (EditText) findViewById(R.id.last_name);
        EditText emailField = (EditText) findViewById(R.id.email_address);
        EditText verifyEmailField = (EditText) findViewById(R.id.verify_email);
        EditText birthField = (EditText) findViewById(R.id.birth);

        firstName = firstNameField.getText().toString();
        lastName = lastNameField.getText().toString();
        email = emailField.getText().toString();
        verifyEmail = verifyEmailField.getText().toString();
        birth = birthField.getText().toString();

        if (DataValidator.validateRegister(this, firstName, lastName, email, verifyEmail,
                gender, birth, career, address, visionImpairment, whoAffected, interests, certify)) {
            // Start service
            Intent service = new Intent(this, SubmitService.class);
            service.putExtra(ServiceReceiver.RECEIVER, receiver);
            service.putExtra(SubmitService.TASK, SubmitService.REGISTER);
            service.putExtra(SubmitService.FIRST, firstName);
            service.putExtra(SubmitService.LAST, lastName);
            service.putExtra(SubmitService.EMAIL, email);
            service.putExtra(SubmitService.VERIFY, verifyEmail);
            service.putExtra(SubmitService.GENDER, gender);
            service.putExtra(SubmitService.BIRTH, birth);
            service.putExtra(SubmitService.CAREER, career);
            service.putExtra(SubmitService.ADDRESS, address);
            service.putExtra(SubmitService.VISION, visionImpairment);
            service.putExtra(SubmitService.AFFECTED, whoAffected);
            service.putExtra(SubmitService.INTEREST, interests);
            startService(service);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        address = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onReceiveResult(int task, Bundle data) {
        DataValidator.processRegisterResponse(this,
                data.getString(SubmitService.RESPONSE));
    }
}