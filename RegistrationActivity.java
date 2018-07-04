

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import pcubed.biz.elpro.Helpers.Constants;
import pcubed.biz.elpro.Helpers.Controller;
import pcubed.biz.elpro.Model.Mobile_user;
import pcubed.biz.elpro.R;

public class RegistrationActivity extends BaseActivity {

    private EditText editTextFirstname, editTextLastname, editTextMobileNumber, editTextMobileNumberConfirm, editTextEmail, editTextPassword, editTextConfirmPassword;
    private CheckBox checkBoxTerms, checkBoxShowPassword;
    private TextView textViewTerms;
    private Button buttonSubmit;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        this.context = this;

        initialize();
        setListeners();
    }

    private void initialize(){
        editTextFirstname = (EditText)findViewById(R.id.editTextFirstname);
        editTextLastname = (EditText)findViewById(R.id.editTextLastname);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextMobileNumber = (EditText)findViewById(R.id.editTextMobileNumber);
        editTextMobileNumberConfirm = (EditText)findViewById(R.id.editTextMobileNumberConfirm);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText)findViewById(R.id.editTextConfirmPassword);

        checkBoxTerms = (CheckBox)findViewById(R.id.checkBoxTerms);
        checkBoxShowPassword = (CheckBox)findViewById(R.id.checkBoxShowPassword);
        textViewTerms = (TextView)findViewById(R.id.textViewTerms);

        buttonSubmit = (Button)findViewById(R.id.buttonSubmit);
    }

    private void setListeners(){

        checkBoxShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editTextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    editTextConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                else {
                    editTextPassword.setInputType(129);
                    editTextConfirmPassword.setInputType(129);
                }
            }
        });

        textViewTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controller.getLayoutHelper().showApplicationTermsActivity(v.getContext());
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxTerms.isChecked()) {
                    if (validate()) {
                        Controller.getLayoutHelper().showOTPActivity(v.getContext());
                        finish();
                    }
                }
                else {
                    Controller.showUserMessageCenter(context, getString(R.string.str_please_accept_terms));
                }
            }
        });
    }

    private Boolean validate(){
        Mobile_user user = Mobile_user.deserialize(getPreferenceValue(context, Constants.MOBILE_USER_REGISTER));
        user.setMobile_user_id(getPreferenceValue(context, Constants.MOBILE_USER_ID));

        Boolean validated = Boolean.TRUE;
        if (editTextFirstname.getText().toString().equalsIgnoreCase("")) {
            editTextFirstname.setError(getString(R.string.str_required));
            validated = false;
        }
        else {
            user.setFirstname(editTextFirstname.getText().toString());
        }
        if (editTextLastname.getText().toString().equalsIgnoreCase("")) {
            editTextLastname.setError(getString(R.string.str_required));
            validated = false;
        }
        else {
            user.setLastname(editTextLastname.getText().toString());
        }
        if (editTextMobileNumber.getText().toString().equalsIgnoreCase("")) {
            editTextMobileNumber.setError(getString(R.string.str_required));
            validated = false;
        }
        if (editTextMobileNumber.getText().toString().substring(0,3).equalsIgnoreCase("+27")) {
            user.setMobile_number(editTextMobileNumber.getText().toString().replace("+27", "0"));
        }
        else if (editTextMobileNumber.length() < 10 || editTextMobileNumber.length() > 11) {
            Controller.showUserMessageCenter(context, getString(R.string.str_error_telephone_size));
            validated = false;
        }
        else {
            if (editTextMobileNumberConfirm.getText().toString().equalsIgnoreCase(editTextMobileNumber.getText().toString())) {
                user.setMobile_number(editTextMobileNumber.getText().toString());
            }
            else {
                Controller.showUserMessageCenter(context, getString(R.string.str_error_telephone_mismatch));
                validated = false;
            }
        }
        if (editTextPassword.getText().toString().equalsIgnoreCase("")) {
            editTextPassword.setError(getString(R.string.str_required));
            validated = false;
        }
        if (editTextEmail.getText().toString().equalsIgnoreCase("")) {
            editTextEmail.setError(getString(R.string.str_required));
            validated = false;
        }
        else if (!Controller.isEmailValid(editTextEmail.getText().toString())) {
            Controller.showUserMessageCenter(context, getString(R.string.str_invalid_email));
            validated = false;
        }
        else {
            user.setEmail_address(editTextEmail.getText().toString());
        }
        if (!editTextConfirmPassword.getText().toString().equalsIgnoreCase(editTextPassword.getText().toString()) || editTextConfirmPassword.getText().toString().equalsIgnoreCase("")) {
            editTextConfirmPassword.setError(getString(R.string.str_password_mismatch));
            validated = false;
        }
        else {
            if (editTextPassword.getText().toString().length() <6) {
                Controller.showUserMessageCenter(context, getString(R.string.str_password_length));
                validated = false;
            }
            else {
                user.setPassword(editTextPassword.getText().toString());
            }
        }

        setPreference(context, Constants.MOBILE_USER_REGISTER, user.serialize());

        return validated;
    }

    @Override
    public void onBackPressed() {
        Controller.showUserMessage(context, getString(R.string.str_no_back_press));
    }
}
