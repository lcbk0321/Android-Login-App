package com.example.q.loginmongonodejs;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.q.loginmongonodejs.Retrofit.IMyService;
import com.example.q.loginmongonodejs.Retrofit.RetrofitClient;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    TextView text_create_account;
    MaterialEditText edit_login_phonenumber, edit_login_password;
    Button btn_login;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init service

        Retrofit retrofitClient = RetrofitClient.getInstance();

        iMyService = retrofitClient.create(IMyService.class);

        edit_login_phonenumber = (MaterialEditText) findViewById(R.id.edit_phonenumber);
        edit_login_password = (MaterialEditText) findViewById(R.id.edit_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(edit_login_phonenumber.getText().toString(),edit_login_password.getText().toString());
            }
        });

        text_create_account = (TextView) findViewById(R.id.txt_create_account);
        text_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View register_layout = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.register_layout, null);

                new MaterialStyledDialog.Builder(MainActivity.this)
                        .setIcon(R.drawable.ic_phone)
                        .setTitle("REGISTRATION")
                        .setDescription("Please fill all fields")
                        .setCustomView(register_layout)
                        .setNegativeText("CANCEL")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveText("REGISTER")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                MaterialEditText edit_register_phonenumber = (MaterialEditText) register_layout.findViewById(R.id.edit_phonenumber);
                                MaterialEditText edit_register_name = (MaterialEditText) register_layout.findViewById(R.id.edit_phonenumber);
                                MaterialEditText edit_register_password = (MaterialEditText) register_layout.findViewById(R.id.edit_password);

                                if(TextUtils.isEmpty(edit_register_phonenumber.getText().toString())){
                                    Toast.makeText(MainActivity.this, "Phone Number cannot be null or empty", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if(TextUtils.isEmpty(edit_register_name.getText().toString())){
                                    Toast.makeText(MainActivity.this, "Name cannot be null or empty", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if(TextUtils.isEmpty(edit_register_password.getText().toString())){
                                    Toast.makeText(MainActivity.this, "password cannot be null or empty", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                registerUser (edit_register_phonenumber.getText().toString(),
                                        edit_register_name.getText().toString(),
                                        edit_register_password.getText().toString());

                            }
                        }).show();
            }
        });
    }

    private void registerUser(String phonenumber, String name , String password) {
        compositeDisposable.add(iMyService.registerUser(phonenumber,name, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        Toast.makeText(MainActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void loginUser (String phonenumber, String password){
        if(TextUtils.isEmpty(phonenumber)){
            Toast.makeText(this, "Phone Number cannot be null or empty", Toast.LENGTH_SHORT).show();
            return;
        }

        compositeDisposable.add(iMyService.loginUser(phonenumber,password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        Toast.makeText(MainActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                    }
                }));
    }
}
