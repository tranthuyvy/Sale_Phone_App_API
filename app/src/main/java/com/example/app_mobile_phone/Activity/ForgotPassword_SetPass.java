package com.example.app_mobile_phone.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_mobile_phone.Model.ChangePassword;
import com.example.app_mobile_phone.R;
import com.example.app_mobile_phone.Retrofit.ApiService;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword_SetPass extends AppCompatActivity {
    Button btnEnterNewPassword;
    EditText txtEnterNewPassword, txtEnterReNewPassword;
    ImageButton btnPrevious;
    boolean passwordVisible, rePasswordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_set_pass);
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnEnterNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPassword = txtEnterNewPassword.getText().toString().trim();
                String reNewPassword = txtEnterReNewPassword.getText().toString().trim();
                if (newPassword.length() == 0) {
                    openDialogSetPass(false, "Please enter a new password!");
                    return;
                }
                if (reNewPassword.length() == 0) {
                    openDialogSetPass(false, "Please re-enter new password!");
                    return;
                }

                if (newPassword.length() < 6) {
                    openDialogSetPass(false, "Password needs 6 or more characters");
                    return;
                }

                if (newPassword.equals(reNewPassword) == false) {
                    openDialogSetPass(false, "Two passwords are not the same! Please re-enter");
                    return;
                }

                handleLoginApi();
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // Show password
        txtEnterNewPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int Right = 2;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    if(motionEvent.getRawX() >= txtEnterNewPassword.getRight() - txtEnterNewPassword.getCompoundDrawables()[Right].getBounds().width()){
                        if(passwordVisible){
                            txtEnterNewPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.show_password, 0);
                            txtEnterNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        }else{
                            txtEnterNewPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide_password, 0);
                            txtEnterNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                    }
                }
                return false;
            }
        });

        // Show RePassword
        txtEnterReNewPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int Right = 2;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    if(motionEvent.getRawX() >= txtEnterReNewPassword.getRight() - txtEnterReNewPassword.getCompoundDrawables()[Right].getBounds().width()){
                        if(rePasswordVisible){
                            txtEnterReNewPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.show_password, 0);
                            txtEnterReNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            rePasswordVisible = false;
                        }else{
                            txtEnterReNewPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide_password, 0);
                            txtEnterReNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            rePasswordVisible = true;
                        }
                    }
                }
                return false;
            }
        });
    }

    public void handleLoginApi() {
        String email = ForgotPassword_EnterEmail.emailInput;
        String password = txtEnterNewPassword.getText().toString().trim();
        if (email.length() == 0) {
            openDialogSetPass(false, "Please go back to entering your email address!");
            return;
        }
        if (!email.contains("@gmail.com")) {
            openDialogSetPass(false, "Invalid email input!\nPlease return to the step of entering the email address.");
            return;
        }
        LoadingDialog loadingDialog = new LoadingDialog(ForgotPassword_SetPass.this);
        loadingDialog.startLoadingDialog();
        Log.e("email", email);
        Log.e("txtPassword", password);
        ChangePassword changePassword = new ChangePassword(email, password);
        ApiService.apiService.postChangePassByEmail(changePassword).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        openDialogSetPass(true, "Password change successful!");
                    }
                } else {
                    try {
                        String strResponseBody = "";
                        strResponseBody = response.errorBody().string();
                        JSONObject messageObject = new JSONObject(strResponseBody);
                        openDialogSetPass(false, "Password change failed!" + messageObject.get("message"));
                        Log.v("Error code 400", response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                loadingDialog.closeLoadingDialog();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void addControls() {
        btnEnterNewPassword = findViewById(R.id.btnEnterNewPassword);
        txtEnterNewPassword = findViewById(R.id.txtEnterNewPassword);
        txtEnterReNewPassword = findViewById(R.id.txtEnterReNewPassword);
        btnPrevious = findViewById(R.id.btnPrevious);
    }


    public void openDialogSetPass(Boolean isSuccess, String text_content) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_message);
        dialog.setCancelable(true);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        window.setAttributes(windowAttributes);

        Button btnDialogOke = dialog.findViewById(R.id.btnDialogOke);
        TextView txtDialogContent = dialog.findViewById(R.id.txtDialogContent);
        txtDialogContent.setText(text_content);
        btnDialogOke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSuccess) {
                    Intent intent = new Intent(ForgotPassword_SetPass.this, Login.class);
                    startActivity(intent);
                } else {
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }
}