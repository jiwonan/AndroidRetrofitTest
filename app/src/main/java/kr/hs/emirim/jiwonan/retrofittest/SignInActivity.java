package kr.hs.emirim.jiwonan.retrofittest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignInActivity extends AppCompatActivity {

    SharedPreferences LoginUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // 저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        LoginUserInfo = getSharedPreferences("userlogininfo", MODE_PRIVATE);
        // text에 key값이 저장되어있는지 확인. 아무 값도 없으면 ""반환.
        String text = LoginUserInfo.getString("text","");

        // retrofit
        Retrofit retrofit = (new Retrofit.Builder()).baseUrl("http://192.168.9.162:1234").addConverterFactory(GsonConverterFactory.create()).build();
        final RedayService apiService = retrofit.create(RedayService.class);

        final EditText useremailEdit = (EditText)findViewById(R.id.input_email);
        final EditText userpasswordEdit = (EditText)findViewById(R.id.input_password);
        final Button btn = findViewById(R.id.submit);

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String useremail = useremailEdit.getText().toString().trim();
                Log.d("mytag",useremail);
                String userpassword = userpasswordEdit.getText().toString().trim();
                Log.d("mytag",userpassword);

                if(useremail == null) {
                    Toast.makeText(SignInActivity.this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
                    if(userpassword == null) {
                        Toast.makeText(SignInActivity.this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    final Call<LoginResponse> apiCall = apiService.loginUser(useremail, userpassword);
                    apiCall.enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            LoginResponse lr = response.body();
                            Log.d("mytag", lr.toString());

                            if(lr.isSuccess()) {
                                Toast.makeText(SignInActivity.this, "로그인 완료.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(SignInActivity.this, "로그인 실패.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            Log.d("mytag", "fail" + t.getMessage());
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        final EditText useremailEdit = findViewById(R.id.input_email);
        final EditText userpasswordEdit = findViewById(R.id.input_password);

        //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
        SharedPreferences.Editor editor = LoginUserInfo.edit();
        String useremail = useremailEdit.getText().toString(); // 사용자가 입력한 저장할 데이터
        String userpassword = userpasswordEdit.getText().toString();
        editor.putString("email", useremail); // key, value를 이용하여 저장하는 형태
        editor.putString("password", userpassword);

        editor.commit();

        String ee = LoginUserInfo.getString("email", null);
        String pp = LoginUserInfo.getString("password", "null");
    }
}
