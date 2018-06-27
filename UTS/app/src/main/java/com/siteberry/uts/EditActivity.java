package com.siteberry.uts;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.siteberry.UTSDto;
import com.siteberry.dto.RegisteredUser;

public class EditActivity extends AppCompatActivity {
    private EditText editFName,editLName,editEMail,editMobile,editAnswer;
    private AppCompatButton updateDetails;
    private RegisteredUser reg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        editFName = (EditText)findViewById(R.id.edit_fname);
        editLName = (EditText)findViewById(R.id.edit_lname);
        editEMail = (EditText)findViewById(R.id.edit_email);
        editMobile = (EditText)findViewById(R.id.edit_mobile);
        editAnswer = (EditText)findViewById(R.id.edit_answer);
        updateDetails = (AppCompatButton)findViewById(R.id.button_update_details);
        editFName.setText(UTSDto.fName);
        editLName.setText(UTSDto.lName);
        editEMail.setText(UTSDto.userName);
        editMobile.setText(UTSDto.mobileNumber);
        editAnswer.setText(UTSDto);
        updateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ON CLICKING UPDATE PROFILE BUTTON.
                final EditText inputPassword = new EditText(getApplicationContext());
                inputPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                AlertDialog.Builder inputDialog = new AlertDialog.Builder(getApplicationContext(),R.style.AppTheme);
                inputDialog.setView(inputPassword);
                inputDialog.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),"Password is : "+inputPassword.getText().toString(),Toast.LENGTH_SHORT);
                    }
                }).setNegativeButton("Back",null).show();
            }
        });
    }
}
