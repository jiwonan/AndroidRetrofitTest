package kr.hs.emirim.jiwonan.retrofittest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrofit.Builder();
        Retrofit retrofit = (new Retrofit.Builder()).baseUrl("http://192.168.9.162:1234").addConverterFactory(GsonConverterFactory.create()).build();
        final RedayService apiService = retrofit.create(RedayService.class);

        final Button btn = findViewById(R.id.submit);
        final EditText usernameEdit = findViewById(R.id.username);
        final EditText passwordEdit = findViewById(R.id.input_password);
        final EditText emailEdit = findViewById(R.id.email);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                String email = emailEdit.getText().toString();

                Call<User> apiCall = apiService.createUser(username, password, email);
                // 서버에 요청할 준비
                apiCall.enqueue(new Callback<User>() {
                    // enqueue 요청
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        // call 요청 정보, response 응답 정보
                        User user = response.body();
                        Log.d("mytag", user.toString());

                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.d("mytag", "fail" +  t.getMessage());

                    }
                });
            }
        });


    }
}
